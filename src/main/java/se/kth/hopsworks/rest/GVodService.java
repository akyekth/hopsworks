/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.rest;

import java.io.File;
import java.io.IOException;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.json.simple.parser.ParseException;
import se.kth.bbc.project.Project;
import se.kth.bbc.project.ProjectFacade;
import se.kth.hopsworks.controller.KafkaController;
import se.kth.hopsworks.controller.ResponseMessages;
import se.kth.hopsworks.filters.AllowedRoles;
import se.kth.hopsworks.gvod.io.download.FrontentJsonForHdfsDownload;
import se.kth.hopsworks.controller.GVodController;
import se.kth.hopsworks.gvod.io.download.FrontendJsonForHdfsKafkaDownload;
import se.kth.hopsworks.gvod.io.download.DownloadRequest;
import se.kth.hopsworks.gvod.io.resources.items.ManifestJson;
import se.kth.hopsworks.users.UserFacade;
import se.kth.hopsworks.util.Settings;

/**
 *
 * @author jsvhqr
 */
@Path("/gvod")
@RolesAllowed({"HOPS_ADMIN", "HOPS_USER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@TransactionAttribute(TransactionAttributeType.NEVER)
public class GVodService {

    @EJB
    private UserFacade userFacade;

    @EJB
    private Settings settings;
    @EJB
    private NoCacheResponse noCacheResponse;
    @EJB
    private GVodController gvodController;
    @EJB
    private ProjectFacade projectFacade;
    @EJB
    private KafkaController kafkaController;
    
    @GET
    @Path("downloadrequest")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
    public Response downloadRequest(@Context SecurityContext sc,
            @Context HttpServletRequest req, DownloadRequest downloadRequest) throws IOException, AppException, ParseException {

        if (settings.getCLUSTER_ID() == null) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
        }
        if (settings.getGVOD_UDP_ENDPOINT() == null) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.GVOD_OFFLINE);
        }

        ManifestJson response = gvodController.downloadRequest(
                downloadRequest.getPublicDatasetId(), 
                settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME, 
                userFacade.findByEmail(sc.getUserPrincipal().getName()), 
                projectFacade.find(downloadRequest.getProjectId()), 
                downloadRequest.getDatasetName());

        if (response != null) {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).entity(response).build();
        } else {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.EXPECTATION_FAILED).entity(null).build();
        }
    }
    
    @PUT
    @Path("downloadhdfs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
    public Response downloadDatasetHdfs(@Context SecurityContext sc,
            @Context HttpServletRequest req, FrontentJsonForHdfsDownload frontentJsonForHdfsDownload) throws IOException, AppException {

        if (settings.getCLUSTER_ID() == null) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
        }
        if (settings.getGVOD_UDP_ENDPOINT() == null) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.GVOD_OFFLINE);
        }

        String response = gvodController.downloadHdfs(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME,
                projectFacade.find(frontentJsonForHdfsDownload.getProjectId()),
                userFacade.findByEmail(sc.getUserPrincipal().getName()),
                frontentJsonForHdfsDownload.getDatasetId(),
                frontentJsonForHdfsDownload.getPartners(),
                frontentJsonForHdfsDownload.getDatasetName());

        if (response != null) {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).entity(response).build();
        } else {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.EXPECTATION_FAILED).entity(null).build();
        }
    }

    @PUT
    @Path("downloadkafka")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
    public Response downloadDatasetKafka(@Context SecurityContext sc,
            @Context HttpServletRequest req, FrontendJsonForHdfsKafkaDownload frontendJsonForHdfsKafkaDownload) throws IOException, AppException {
        
        if (settings.getCLUSTER_ID() == null) {
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
        }
        if(settings.getGVOD_UDP_ENDPOINT() == null){
            throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
                    ResponseMessages.GVOD_OFFLINE);
        }

        Project project = projectFacade.find(frontendJsonForHdfsKafkaDownload.getProjectId());
        String certPath = kafkaController.getKafkaCertPaths(project);

        String response = gvodController.downloadKafka(settings.getHadoopConfDir() + File.separator + Settings.DEFAULT_HADOOP_CONFFILE_NAME,
                project,
                userFacade.findByEmail(sc.getUserPrincipal().getName()),
                frontendJsonForHdfsKafkaDownload.getDatasetId(),
                frontendJsonForHdfsKafkaDownload.getPartners(),
                req.getSession().getId(),
                frontendJsonForHdfsKafkaDownload.getTopicName(),
                certPath + "/keystore.jks",
                certPath + "/truststore.jks",
                settings.getKafkaConnectStr(),
                "http://" + settings.getDOMAIN() + ":" + settings.getRestPort(),
                settings.getDOMAIN(),
                frontendJsonForHdfsKafkaDownload.getDatasetName());

        if (response != null) {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).entity(response).build();
        } else {
            return noCacheResponse.getNoCacheResponseBuilder(Response.Status.EXPECTATION_FAILED).entity(null).build();
        }
    }
}
