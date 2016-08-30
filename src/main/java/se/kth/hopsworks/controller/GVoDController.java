/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.controller;

import com.google.gson.Gson;
import io.hops.gvod.contents.HopsContentsReqJSON;
import io.hops.gvod.contents.TorrentExtendedStatusJSON;
import io.hops.gvod.download.DownloadGVoDJSON;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import se.kth.bbc.project.Project;
import io.hops.gvod.startdownload.StartDownloadJSON;
import io.hops.gvod.resources.HDFSEndpoint;
import io.hops.gvod.resources.HDFSResource;
import io.hops.gvod.resources.KafkaEndpoint;
import io.hops.gvod.resources.items.AddressJSON;
import io.hops.gvod.resources.items.ExtendedDetails;
import io.hops.gvod.resources.items.KafkaResource;
import io.hops.gvod.resources.items.ManifestJSON;
import io.hops.gvod.resources.items.ManifestResponse;
import io.hops.gvod.resources.items.TorrentId;
import io.hops.gvod.responses.ErrorDescJSON;
import io.hops.gvod.responses.HopsContentsSummaryJSON;
import io.hops.gvod.responses.SuccessJSON;
import io.hops.gvod.upload.UploadGVoDJSON;
import se.kth.hopsworks.hdfs.fileoperations.DistributedFileSystemOps;
import se.kth.hopsworks.hdfs.fileoperations.DistributedFsService;
import se.kth.hopsworks.hdfsUsers.controller.HdfsUsersController;
import se.kth.hopsworks.rest.AppException;
import se.kth.hopsworks.user.model.Users;
import se.kth.hopsworks.util.Settings;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.json.JSONException;
import org.json.JSONObject;
import se.kth.hopsworks.dataset.Dataset;

/**
 *
 * @author jsvhqr
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.NEVER)
public class GVoDController {

    @EJB
    Settings settings;

    @EJB
    DatasetController datasetController;

    @EJB
    private DistributedFsService dfs;

    @EJB
    private HdfsUsersController hdfsUsersBean;

    private WebTarget webTarget = null;
    private Client rest_client = null;

    public String uploadToGVod(String projectName, String sourceDatasetName, String username, String datasetPath) {

        UploadGVoDJSON uploadGVoDJSON = new UploadGVoDJSON(
                new TorrentId(Settings.getPublicDatasetId(settings.getCLUSTER_ID(), projectName, sourceDatasetName)),
                sourceDatasetName,
                new HDFSResource(datasetPath, Settings.MANIFEST_NAME),
                new HDFSEndpoint(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME, username)
        );
        rest_client = ClientBuilder.newClient();

        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("torrent/hops/upload/xml");

        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(uploadGVoDJSON, MediaType.APPLICATION_JSON), Response.class);

        if (r != null && r.getStatus() == 200) {
            return r.readEntity(SuccessJSON.class).getDetails();
        } else if (r != null) {
            return r.readEntity(ErrorDescJSON.class).getDetails();
        } else {
            return null;
        }
    }

    public ManifestResponse startDownload(String publicDsId, Users user, Project project, String destinationDatasetName, List<AddressJSON> partners) throws AppException {

        DistributedFileSystemOps dfso = null;
        DistributedFileSystemOps udfso = null;
        Dataset ds = null;
        ManifestResponse to_ret;
        String username = hdfsUsersBean.getHdfsUserName(project, user);
        try {
            dfso = dfs.getDfsOps();
            if (username != null) {
                udfso = dfs.getDfsOps(username);
            }
            ds = datasetController.createDataset(user, project, destinationDatasetName, "", -1, true, false, dfso, udfso);

        } catch (NullPointerException c) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), c.
                    getLocalizedMessage());

        } catch (IllegalArgumentException e) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    "Failed to create dataset: " + e.getLocalizedMessage());
        } catch (IOException e) {
            throw new AppException(Response.Status.INTERNAL_SERVER_ERROR.
                    getStatusCode(), "Failed to create dataset: " + e.
                    getLocalizedMessage());
        } finally {
            if (dfso != null) {
                dfso.close();
            }
            if (udfso != null) {
                udfso.close();
            }
        }

        StartDownloadJSON startDownloadJSON = new StartDownloadJSON(
                new TorrentId(publicDsId),
                destinationDatasetName,
                new HDFSResource(Settings.getProjectPath(project.getName()) + File.separator + destinationDatasetName + File.separator, "manifest.json"),
                partners,
                new HDFSEndpoint(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME, username)
        );

        rest_client = ClientBuilder.newClient();

        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("/torrent/hops/download/start/xml");

        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(startDownloadJSON, MediaType.APPLICATION_JSON), Response.class);

        String pathToManifest = Settings.getProjectPath(project.getName()) + File.separator + destinationDatasetName + File.separator + Settings.MANIFEST_NAME;

        ManifestJSON manifestJson = null;

        if (r != null && r.getStatus() == 200) {
            byte[] jsonBytes = datasetController.readJsonFromHdfs(pathToManifest);
            manifestJson = datasetController.getManifestJSON(jsonBytes);
            ds.setDescription(manifestJson.getDatasetDescription());
            datasetController.makeDatasetPublicAndImmutable(ds, publicDsId);
            to_ret = new ManifestResponse(manifestJson, r);

        } else {
            try {
                dfso = dfs.getDfsOps();
                datasetController.deleteDataset(Settings.getProjectPath(project.getName()) + File.separator + destinationDatasetName + File.separator,
                        user,
                        project,
                        dfso);
            } catch (Exception e) {
                throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(), e.
                        getLocalizedMessage());
            } finally {
                if (dfso != null) {
                    dfso.close();
                }
            }
            to_ret = new ManifestResponse(manifestJson, r);
        }
        
        return to_ret;
    }

    public Response download(KafkaEndpoint kafkaEndpoint, String username, String publicDsId, String dsPath, JSONObject fileTopics, String sessiodId) {

        if (kafkaEndpoint != null) { // perform kafka download

            Map<String, HDFSResource> hdfsResources = new HashMap<>();
            Map<String, KafkaResource> kafkaResources = new HashMap<>();

            Iterator<String> iter = fileTopics.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    String value = (String) fileTopics.get(key);
                    if (!value.equals("")) {
                        kafkaResources.put(key, new KafkaResource(sessiodId, value));
                        hdfsResources.put(key, new HDFSResource(dsPath, key));
                    } else {
                        hdfsResources.put(key, new HDFSResource(dsPath, key));
                    }
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            
        
            
            DownloadGVoDJSON downloadGVodJSON = new DownloadGVoDJSON(
                    new TorrentId(publicDsId),
                    kafkaEndpoint,
                    new HDFSEndpoint(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME, username),
                    new ExtendedDetails(hdfsResources, kafkaResources)
            );

            rest_client = ClientBuilder.newClient();

            webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("/torrent/hops/download/advance/xml");
            
            
            Gson gson = new Gson();
            
            String json = gson.toJson(downloadGVodJSON);
            
            Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON), Response.class);

            if (r != null && r.getStatus() == 200) {
                return r;
            } else if(r!= null) {
                return r;
            }else{
                return null;
            }

        } else { // only hdfs

            Map<String, HDFSResource> hdfsResources = new HashMap<>();
            
            Iterator<String> iter = fileTopics.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    hdfsResources.put(key, new HDFSResource(dsPath, key));
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            
            DownloadGVoDJSON downloadGVodJSON = new DownloadGVoDJSON(
                    new TorrentId(publicDsId),
                    null,
                    new HDFSEndpoint(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME, username),
                    new ExtendedDetails(hdfsResources, new HashMap<String,KafkaResource>())
            );

            rest_client = ClientBuilder.newClient();

            webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("/torrent/hops/download/advance/xml");
            
            Gson gson = new Gson();
            
            String json = gson.toJson(downloadGVodJSON);

            Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON), Response.class);

            if (r != null && r.getStatus() == 200) {
                return r;
            } else if (r != null) {
                return r;
            } else {
                return null;
            }

        }

    }

    public String removeUpload(String publicDsId) {

        TorrentId torrentId = new TorrentId(publicDsId);

        rest_client = ClientBuilder.newClient();

        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("torrent/hops/stop");
        
        Gson gson = new Gson();
            
        String json = gson.toJson(torrentId);

        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON), Response.class);

        if (r != null && r.getStatus() == 200) {
            return r.readEntity(SuccessJSON.class).getDetails();
        } else if (r != null) {
            return r.readEntity(ErrorDescJSON.class).getDetails();
        } else {
            return null;
        }

    }

    public HopsContentsSummaryJSON getContents(int projectId) {

        HopsContentsReqJSON hopsContentsReqJSON = new HopsContentsReqJSON(projectId);

        rest_client = ClientBuilder.newClient();

        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("/library/contents");

        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(hopsContentsReqJSON, MediaType.APPLICATION_JSON), Response.class);

        if (r != null && r.getStatus() == 200) {
            return r.readEntity(HopsContentsSummaryJSON.class);
        } else {
            return null;
        }
    }

    public TorrentExtendedStatusJSON getDetails(String torrentId) {
        
        TorrentId torrent = new TorrentId(torrentId);

        rest_client = ClientBuilder.newClient();

        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("/library/extended");

        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.entity(torrent, MediaType.APPLICATION_JSON), Response.class);

        if (r != null && r.getStatus() == 200) {
            return r.readEntity(TorrentExtendedStatusJSON.class);
        } else {
            return null;
        }
        
    }
}
