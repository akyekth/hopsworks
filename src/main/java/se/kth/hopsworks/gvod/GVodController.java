/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.gvod;

import com.owlike.genson.Genson;
import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import se.kth.bbc.project.Project;
import se.kth.bbc.project.ProjectFacade;
import se.kth.hopsworks.controller.DatasetController;
import se.kth.hopsworks.user.model.Users;
import se.kth.hopsworks.users.UserFacade;
import se.kth.hopsworks.util.Settings;

/**
 *
 * @author jsvhqr
 */
@Stateless
public class GVodController {
    
    @EJB
    Settings settings;
    
    @EJB
    DatasetController datasetController;
    
    @EJB
    ProjectFacade projectFacade;
    
    @EJB
    UserFacade userFacade;
    
    private WebTarget webTarget = null;
    private Client rest_client = null;
    private final Genson genson = new Genson();

    public String uploadToGVod(String hdfsXMLPath, String path, String datasetName, String username, String publicDsId) {
        
        UploadGVoDJson uploadGVoDJson = new UploadGVoDJson(new HdfsResource(hdfsXMLPath,path,datasetName,username), new TorrentId(publicDsId));
        
        String restToSend = genson.serialize(uploadGVoDJson);
        
        rest_client = ClientBuilder.newClient();
        
        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("torrent/hops/upload");
        
        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(restToSend, MediaType.APPLICATION_JSON), Response.class);
        
        if(r != null && r.getStatus() == 200){
            return r.readEntity(String.class);
        }else{
            return null;
        }
    }
    
    public String downloadKafka() {
        return null;
    }
    
    public String downloadHdfs(String hdfsXMLPath,int projectId, String datasetName, String username, String publicDsId, String partners) throws IOException  {
        
        Project project = projectFacade.find(projectId);
        
        Users user = userFacade.findByUsername(username);
        
        String dsPath = datasetController.createDatasetForDownload(user, project, datasetName, datasetName, publicDsId);
        
        DownloadGVoDJson downloadGVoDJson = new DownloadGVoDJson(new HdfsResource(hdfsXMLPath, dsPath,datasetName,username),null,new TorrentId(publicDsId), partners);
        
        String restToSend = genson.serialize(downloadGVoDJson);
        
        rest_client = ClientBuilder.newClient();
        
        webTarget = rest_client.target(settings.getGVOD_REST_ENDPOINT()).path("torrent/hops/upload");
        
        Response r = webTarget.request().accept(MediaType.APPLICATION_JSON).put(Entity.entity(restToSend, MediaType.APPLICATION_JSON), Response.class);
        
        if(r != null && r.getStatus() == 200){
            return r.readEntity(String.class);
        }else{
            return null;
        }
    }
    
}
