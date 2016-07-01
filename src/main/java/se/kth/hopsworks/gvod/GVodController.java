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
import org.json.JSONArray;
import se.kth.bbc.project.Project;
import se.kth.bbc.project.ProjectFacade;
import se.kth.hopsworks.controller.DatasetController;
import se.kth.hopsworks.gvod.GVodUploadInput;
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

    public String uploadToGVod(String restEndpoint, String path, String datasetName, String username, String publicDsId) {
        
        String [] splitURL = restEndpoint.split(":");
        
        GVodUploadInput gvodUploadInput = new GVodUploadInput(new HdfsResource(splitURL[0],splitURL[1],path,datasetName,username), new TorrentId(publicDsId));
        
        String restToSend = genson.serialize(gvodUploadInput);
        
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
    
    public String downloadHdfs(String restEndpoint,int projectId, String datasetName, String username, String publicDsId, JSONArray partners) throws IOException {
        
        Project project = projectFacade.find(projectId);
        
        Users user = userFacade.findByUsername(username);
        
        String dsPath = datasetController.createDatasetForDownload(user, project, datasetName, datasetName, publicDsId);
        
        String [] splitURL = restEndpoint.split(":");
        
        GVodHdfsDownloadInput gvodHdfsDownloadInput = new GVodHdfsDownloadInput(new HdfsResource(splitURL[0],splitURL[1],dsPath,datasetName,username),new TorrentId(publicDsId), partners);
        
        String restToSend = genson.serialize(gvodHdfsDownloadInput);
        
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
