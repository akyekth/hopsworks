package se.kth.hopsworks.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.AccessControlException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.kth.bbc.activity.ActivityFacade;
import se.kth.bbc.project.Project;
import se.kth.bbc.project.fb.Inode;
import se.kth.bbc.project.fb.InodeFacade;
import se.kth.hopsworks.dataset.Dataset;
import se.kth.hopsworks.dataset.DatasetFacade;
import io.hops.gvod.io.resources.items.FileInfo;
import io.hops.gvod.io.resources.items.ManifestJson;
import se.kth.hopsworks.hdfs.fileoperations.DistributedFsService;
import se.kth.hopsworks.hdfs.fileoperations.DistributedFileSystemOps;
import se.kth.hopsworks.hdfsUsers.controller.HdfsUsersController;
import se.kth.hopsworks.meta.db.InodeBasicMetadataFacade;
import se.kth.hopsworks.meta.db.TemplateFacade;
import se.kth.hopsworks.meta.entity.InodeBasicMetadata;
import se.kth.hopsworks.meta.entity.Template;
import se.kth.hopsworks.meta.exception.DatabaseException;
import se.kth.hopsworks.rest.AppException;
import se.kth.hopsworks.util.Settings;
import se.kth.hopsworks.user.model.Users;

/**
 * Contains business logic pertaining DataSet management.
 * <p/>
 * @author stig
 */
@Stateless
public class DatasetController {

    private static final Logger logger = Logger.getLogger(DatasetController.class.
            getName());
    @EJB
    private InodeFacade inodes;
    @EJB
    private TemplateFacade templates;
    @EJB
    private DatasetFacade datasetFacade;
    @EJB
    private ActivityFacade activityFacade;
    @EJB
    private InodeBasicMetadataFacade inodeBasicMetaFacade;
    @EJB
    private HdfsUsersController hdfsUsersBean;
    @EJB
    private InodeFacade inodeFacade;
    @EJB
    private DistributedFsService dfs;

    /**
     * Create a new DataSet. This is, a folder right under the project home
     * folder.
     * <p/>
     * @param user The creating Users. Cannot be null.
     * @param project The project under which to create the DataSet. Cannot be
     * null.
     * @param dataSetName The name of the DataSet being created. Cannot be null
     * and must satisfy the validity criteria for a folder name.
     * @param datasetDescription The description of the DataSet being created.
     * Can be null.
     * @param templateId The id of the metadata template to be associated with
     * this DataSet.
     * @param searchable Defines whether the dataset can be indexed or not (i.e.
     * whether it can be visible in the search results or not)
     * @param globallyVisible
     * @param dfso
     * @param udfso
     * @throws NullPointerException If any of the given parameters is null.
     * @throws IllegalArgumentException If the given DataSetDTO contains invalid
     * folder names, or the folder already exists.
     * @throws IOException if the creation of the dataset failed.
     * @see FolderNameValidator.java
     */
    public Dataset createDataset(Users user, Project project, String dataSetName,
            String datasetDescription, int templateId, boolean searchable,
            boolean globallyVisible, DistributedFileSystemOps dfso,
            DistributedFileSystemOps udfso)
            throws IOException {
        //Parameter checking.
        if (user == null) {
            throw new NullPointerException(
                    "A valid user must be passed upon DataSet creation. Received null.");
        } else if (project == null) {
            throw new NullPointerException(
                    "A valid project must be passed upon DataSet creation. Received null.");
        } else if (dataSetName == null) {
            throw new NullPointerException(
                    "A valid DataSet name must be passed upon DataSet creation. Received null.");
        }
        try {
            FolderNameValidator.isValidName(dataSetName);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Invalid folder name for DataSet.", e);
        }
        //Logic
        boolean success;
        String dsPath = File.separator + Settings.DIR_ROOT + File.separator
                + project.getName();
        dsPath = dsPath + File.separator + dataSetName;
        Inode parent = inodes.getProjectRoot(project.getName());
        Inode ds = inodes.findByParentAndName(parent, dataSetName);

        if (ds != null) {
            throw new IllegalStateException(
                    "Invalid folder name for DataSet creation. "
                    + ResponseMessages.FOLDER_NAME_EXIST);
        }
        String username = hdfsUsersBean.getHdfsUserName(project, user);
        //Permission hdfs dfs -chmod 750 or 755
        FsAction global = (globallyVisible ? FsAction.READ_EXECUTE
                : FsAction.NONE);
        FsAction group = (globallyVisible ? FsAction.ALL
                : FsAction.READ_EXECUTE);
        FsPermission fsPermission = new FsPermission(FsAction.ALL,
                group, global, globallyVisible);
        success = createFolder(dsPath, templateId, username, fsPermission, dfso,
                udfso);
        if (success) {
            //set the dataset meta enabled. Support 3 level indexing
            dfso.setMetaEnabled(dsPath);
            try {

                ds = inodes.findByParentAndName(parent, dataSetName);
                Dataset newDS = new Dataset(ds, project);
                newDS.setSearchable(searchable);

                if (datasetDescription != null) {
                    newDS.setDescription(datasetDescription);
                }
                datasetFacade.persistDataset(newDS);
                activityFacade.persistActivity(ActivityFacade.NEW_DATA + dataSetName, project, user);
                // creates a dataset and adds user as owner.
                hdfsUsersBean.addDatasetUsersGroups(user, project, newDS, dfso);
                return newDS;
            } catch (Exception e) {
                IOException failed = new IOException("Failed to create dataset at path "
                        + dsPath + ".", e);
                try {
                    dfso.rm(new Path(dsPath), true);//if dataset persist fails rm ds folder.
                    throw failed;
                } catch (IOException ex) {
                    throw new IOException(
                            "Failed to clean up properly on dataset creation failure", ex);
                }
            }
        } else {
            throw new IOException("Could not create the directory at " + dsPath);
        }
    }

    /**
     * Create a directory under an existing DataSet. With the same permission as
     * the parent.
     * <p/>
     * @param user creating the folder
     * @param project The project under which the directory is being created.
     * Cannot be null.
     * @param datasetName The name of the DataSet under which the folder is
     * being created. Must be an existing DataSet.
     * @param dsRelativePath The DataSet-relative path to be created. E.g. if
     * the full path is /Projects/projectA/datasetB/folder1/folder2/folder3, the
     * DataSetRelative path is /folder1/folder2/folder3. Must be a valid path;
     * all the folder names on it must be valid and it cannot be null.
     * @param templateId The id of the template to be associated with the newly
     * created directory.
     * @param description The description of the directory
     * @param searchable Defines if the directory can be searched upon
     * @param dfso
     * @param udfso
     * @throws java.io.IOException If something goes wrong upon the creation of
     * the directory.
     * @throws IllegalArgumentException If:
     * <ul>
     * <li>Any of the folder names on the given path does not have a valid name
     * or
     * </li>
     * <li>The dataset with given name does not exist or </li>
     * <li>Such a folder already exists. </li>
     * </ul>
     * @see FolderNameValidator.java
     * @throws NullPointerException If any of the non-null-allowed parameters is
     * null.
     */
    public void createSubDirectory(Users user, Project project, String datasetName,
            String dsRelativePath, int templateId, String description,
            boolean searchable, DistributedFileSystemOps dfso,
            DistributedFileSystemOps udfso) throws IOException {

        //Preliminary
        while (dsRelativePath.startsWith("/")) {
            dsRelativePath = dsRelativePath.substring(1);
        }
        String[] relativePathArray = dsRelativePath.split(File.separator); //The array representing the DataSet-relative path
        String fullPath = "/" + Settings.DIR_ROOT + "/" + project.getName() + "/"
                + datasetName + "/" + dsRelativePath;
        //Parameter checking
        if (project == null) {
            throw new NullPointerException(
                    "Cannot create a directory under a null project.");
        } else if (datasetName == null) {
            throw new NullPointerException(
                    "Cannot create a directory under a null DataSet.");
        } else if (dsRelativePath == null) {
            throw new NullPointerException(
                    "Cannot create a directory for a null path.");
        } else if (dsRelativePath.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot create a directory for an empty path.");
        } else if (datasetName.isEmpty()) {
            throw new IllegalArgumentException("Invalid dataset: emtpy name.");
        } else {
            //Check every folder name for validity.
            for (String s : relativePathArray) {
                try {
                    FolderNameValidator.isValidName(s);
                } catch (ValidationException e) {
                    throw new IllegalArgumentException("Invalid folder name on the path: "
                            + s + "Reason: " + e.getLocalizedMessage());
                }
            }
        }
        //Check if the given folder already exists
        if (inodes.existsPath(fullPath)) {
            throw new IllegalArgumentException("The given path already exists.");
        }

        //Check if the given dataset exists.
        Inode projectRoot = inodes.getProjectRoot(project.getName());

        String parentPath = fullPath;
        // strip any trailing '/' in the pathname
        while (parentPath != null && parentPath.length() > 0 && parentPath.charAt(parentPath.length() - 1) == '/') {
            parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
        // parent path prefixes the last '/' in the pathname
        parentPath = parentPath.substring(0, parentPath.lastIndexOf("/"));
        Inode parent = inodes.getInodeAtPath(parentPath);
        if (parent == null) {
            throw new IllegalArgumentException("Path for parent folder does not exist: "
                    + parentPath + " under " + project.getName());
        }

        String username = hdfsUsersBean.getHdfsUserName(project, user);
        //Now actually create the folder
        boolean success = this.createFolder(fullPath, templateId, username, null,
                dfso, udfso);

        //if the folder was created successfully, persist basic metadata to it -
        //description and searchable attribute
        if (success) {
            //get the folder name. The last part of fullPath
            String pathParts[] = fullPath.split("/");
            String folderName = pathParts[pathParts.length - 1];

            //find the corresponding inode
            Inode folder = this.inodes.findByParentAndName(parent, folderName);
            InodeBasicMetadata basicMeta = new InodeBasicMetadata(folder, description,
                    searchable);
            this.inodeBasicMetaFacade.addBasicMetadata(basicMeta);
        }
    }

    /**
     * Deletes a folder recursively as the given user.
     * <p>
     * @param path
     * @param user
     * @param project
     * @param udfso
     * @return
     * @throws java.io.IOException
     */
    public boolean deleteDataset(String path, Users user, Project project,
            DistributedFileSystemOps udfso) throws
            IOException {
        boolean success;
        Path location = new Path(path);
        success = udfso.rm(location, true);
        return success;
    }

    /**
     * Change permission of the folder in path. This is performed with the
     * username of the user in the given project.
     * <p>
     * @param path
     * @param user
     * @param project
     * @param pemission
     * @param udfso
     * @throws IOException
     */
    public void changePermission(String path, Users user, Project project,
            FsPermission pemission, DistributedFileSystemOps udfso) throws
            IOException {

        Path location = new Path(path);
        udfso.setPermission(location, pemission);
    }

    /**
     * Creates a folder in HDFS at the given path, and associates a template
     * with that folder.
     * <p/>
     * @param path The full HDFS path to the folder to be created (e.g.
     * /Projects/projectA/datasetB/folder1/folder2).
     * @param template The id of the template to be associated with the created
     * folder.
     * @return
     * @throws IOException
     */
    private boolean createFolder(String path, int template, String username,
            FsPermission fsPermission, DistributedFileSystemOps dfso,
            DistributedFileSystemOps udfso) throws IOException {
        boolean success = false;
        Path location = new Path(path);
        DistributedFileSystemOps dfs;
        if (fsPermission == null) {
            fsPermission = dfso.getParentPermission(location);
        }
        try {
            //create the folder in the file system
            if (username == null) {
                dfs = dfso;
            } else {
                dfs = udfso;
            }
            success = dfs.mkdir(location, fsPermission);
            if (success) {
                dfs.setPermission(location, fsPermission);
            }
            if (success && template != 0 && template != -1) {
                //Get the newly created Inode.
                Inode created = inodes.getInodeAtPath(path);
                Template templ = templates.findByTemplateId(template);
                if (templ != null) {
                    templ.getInodes().add(created);
                    //persist the relationship table
                    templates.updateTemplatesInodesMxN(templ);
                }
            }
        } catch (AccessControlException ex) {
            throw new AccessControlException(ex);
        } catch (IOException ex) {
            throw new IOException("Could not create the directory at " + path, ex);
        } catch (DatabaseException e) {
            throw new IOException("Could not attach template to folder. ", e);
        }
        return success;
    }

    public ManifestJson createAndPersistManifestJson(String dsPath, String description, String name, String email, Project project) throws AppException {
        
        ManifestJson manifestJson = new ManifestJson();
        manifestJson.setDatasetName(name);
        manifestJson.setDatasetDescription(description);
        manifestJson.setKafkaSupport(false);
        manifestJson.setFileInfos(new LinkedList<FileInfo>());
        Inode inode = inodeFacade.getInodeAtPath(dsPath);
        List<Inode> inodekids = inodeFacade.getChildren(inode);
        List<String> childrenOfDataset = new ArrayList<>(inodekids.size());
        for (Inode i : inodekids) {
            if (!i.isDir()) {
                childrenOfDataset.add(i.getInodePK().getName());
            } else {
                throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                        ResponseMessages.ONLY_SINGLE_LEVEL_PUBLIC_DATASETS);
            }
        }
        
        for(String child : childrenOfDataset){
            
            FileInfo fileInfo = null;
            
            if(!isAvro(child)){
                fileInfo = new FileInfo();
                fileInfo.setFileName(child);
                if(childrenOfDataset.contains(child + ".avro")){
                    String hdfsPath = dsPath + child + ".avro";
                    fileInfo.setSchema(new String(this.readJsonFromHdfs(hdfsPath)));
                    fileInfo.setLength(this.getLength(dsPath + child));
                }else{
                    fileInfo.setSchema("");
                    fileInfo.setLength(this.getLength(dsPath + child));
                }
                
            }
            if(fileInfo != null){
                manifestJson.getFileInfos().add(fileInfo);
            }
        }
        for (FileInfo f : manifestJson.getFileInfos()) {
            if (!f.getSchema().equals("")) {
                manifestJson.setKafkaSupport(true);
                break;
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        manifestJson.setCreatorDate(dateFormat.format(date));
        manifestJson.setCreatorEmail(email);

        //TODO other schemas
        manifestJson.setMetaDataJsons(new LinkedList<String>());

        this.writeManifestJsonToHdfs(manifestJson, dsPath + "manifest.json");

        return manifestJson;
    }

    public byte [] readJsonFromHdfs(String pathToJson) {

        long jsonLength = dfs.getDfsOps().getlength(pathToJson);
        if(jsonLength != -1){
            FSDataInputStream fdi = null;
            try {
                fdi = dfs.getDfsOps().open(new Path(pathToJson));
                byte[] manifestByte = new byte[(int) jsonLength];
                fdi.readFully(manifestByte);
                return manifestByte;
            } catch (IOException ex) {
                Logger.getLogger(DatasetController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if(fdi != null){
                    try {
                        fdi.close();
                    } catch (IOException ex) {
                        Logger.getLogger(DatasetController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        return null;
    }

    private void writeManifestJsonToHdfs(ManifestJson manifest, String path) {

        FSDataOutputStream out = null;
        try {
            byte[] manifestBytes = getManifestByte(manifest);
            out = dfs.getDfsOps().create(path);
            out.write(manifestBytes);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(DatasetController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(DatasetController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public byte[] getManifestByte(ManifestJson manifest) {
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(manifest);
        byte[] jsonByte;
        try {
            jsonByte = jsonString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        if (jsonByte.length > 1200) {
            throw new RuntimeException("manifest is too long");
        }
        return jsonByte;
    }
    
    public ManifestJson getManifestJSON(byte[] jsonByte) {
        String jsonString;
        try {
            jsonString = new String(jsonByte, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        Gson gson = new GsonBuilder().create();
        ManifestJson manifest = gson.fromJson(jsonString, ManifestJson.class);
        return manifest;
    }
    
    private boolean isAvro(String s){
        String remove_spaces = s.replaceAll(" ","");
        String [] split = remove_spaces.split("\\.");
        if(split.length == 2){
            return split[1].equals("avro");
        }else{
            return false;
        }
    }
    
    private boolean isCsv(String s){
        return true;
    }

    private long getLength(String pathToFile) {
        return dfs.getDfsOps().getlength(pathToFile);
    }
    
    public void makeDatasetPublicAndImmutable(Dataset ds, String id){
        
        ds.setPublicDs(true);
        ds.setPublicDsId(id);
        ds.setEditable(false);
        datasetFacade.merge(ds);
    }
}
