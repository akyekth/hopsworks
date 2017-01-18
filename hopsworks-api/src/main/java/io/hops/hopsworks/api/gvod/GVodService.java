package io.hops.hopsworks.api.gvod;

import java.io.File;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import io.hops.hopsworks.common.kafka.KafkaController;
import io.hops.hopsworks.common.gvod.GVoDController;
import io.hops.hopsworks.api.filter.AllowedRoles;
import io.hops.hopsworks.api.filter.NoCacheResponse;
import io.hops.hopsworks.common.constants.message.ResponseMessages;
import io.hops.hopsworks.common.dao.project.Project;
import io.hops.hopsworks.common.dao.user.security.ua.UserManager;
import io.hops.hopsworks.common.exception.AppException;
import io.hops.hopsworks.common.gvod.contents.ContentsRequestDTO;
import io.hops.hopsworks.common.gvod.contents.DetailsRequestDTO;
import io.hops.hopsworks.common.gvod.contents.TorrentExtendedStatusJSON;
import io.hops.hopsworks.common.gvod.download.DownloadDTO;
import io.hops.hopsworks.common.gvod.download.StartDownloadDTO;
import io.hops.hopsworks.common.gvod.resources.KafkaEndpoint;
import io.hops.hopsworks.common.gvod.resources.ManifestResponse;
import io.hops.hopsworks.common.gvod.responses.ErrorDescJSON;
import io.hops.hopsworks.common.gvod.responses.HopsContentsSummaryJSON;
import io.hops.hopsworks.common.gvod.responses.SuccessJSON;
import io.hops.hopsworks.common.gvod.stop.RemoveTorrentDTO;
import io.hops.hopsworks.common.hdfs.HdfsUsersController;
import io.hops.hopsworks.common.project.ProjectController;
import io.hops.hopsworks.common.util.Settings;
import io.swagger.annotations.Api;
import javax.ejb.Stateless;

@Path("/gvod")
@RolesAllowed({"HOPS_ADMIN", "HOPS_USER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateless
@Api(value = "GVod", description = "GVod service")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class GVodService {

  @EJB
  private Settings settings;
  @EJB
  private NoCacheResponse noCacheResponse;
  @EJB
  private GVoDController gvodController;
  @EJB
  private ProjectController projectController;
  @EJB
  private KafkaController kafkaController;
  @EJB
  private UserManager userBean;
  @EJB
  private HdfsUsersController hdfsUsersBean;

  @PUT
  @Path("startdownload")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response startDownload(@Context SecurityContext sc,
          @Context HttpServletRequest req, StartDownloadDTO startDownloadDTO)
          throws AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    ManifestResponse response = gvodController.startDownload(startDownloadDTO.
            getPublicDatasetId(),
            userBean.getUserByEmail(sc.getUserPrincipal().getName()),
            projectController.findProjectById(startDownloadDTO.getProjectId()),
            startDownloadDTO.getDestinationDatasetName(),
            startDownloadDTO.getPartners());
    if (response.getResponse().getStatus() == 200) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(response.getManifest()).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(response.getResponse().
                      readEntity(ErrorDescJSON.class)).build();
    }
  }

  @PUT
  @Path("downloadhdfs")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response downloadDatasetHdfs(@Context SecurityContext sc,
          @Context HttpServletRequest req, DownloadDTO downloadDTO) throws
          AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    Project project = projectController.findProjectById(downloadDTO.
            getProjectId());
    Response response = gvodController.download(null,
            hdfsUsersBean.getHdfsUserName(project, userBean.getUserByEmail(sc.
                    getUserPrincipal().getName())),
            downloadDTO.getPublicDatasetId(),
            Settings.getProjectPath(project.getName()) + File.separator
            + downloadDTO.getDestinationDatasetName(),
            downloadDTO.getJSONTopics(),
            null);

    if (response != null && response.getStatus() == 200) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(response.readEntity(SuccessJSON.class)).build();
    } else if (response != null) {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(response.readEntity(
                      ErrorDescJSON.class)).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(null).build();
    }
  }

  @PUT
  @Path("downloadkafka")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response downloadDatasetKafka(@Context SecurityContext sc,
          @Context HttpServletRequest req, DownloadDTO downloadDTO) throws
          AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    Project project = projectController.findProjectById(downloadDTO.
            getProjectId());
    String certPath = kafkaController.getKafkaCertPaths(project);
    Response response = gvodController.download(new KafkaEndpoint(settings.
            getKafkaConnectStr(), "http://" + settings.getDOMAIN() + ":"
            + settings.getRestPort(), settings.getDOMAIN(), String.valueOf(
                    downloadDTO.getProjectId()), certPath + "/keystore.jks",
            certPath + "/truststore.jks"),
            hdfsUsersBean.getHdfsUserName(project, userBean.getUserByEmail(sc.
                    getUserPrincipal().getName())),
            downloadDTO.getPublicDatasetId(),
            Settings.getProjectPath(project.getName()) + File.separator
            + downloadDTO.getDestinationDatasetName(),
            downloadDTO.getJSONTopics(),
            req.getSession().getId());
    if (response != null && response.getStatus() == 200) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(response.readEntity(SuccessJSON.class)).build();
    } else if (response != null) {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(response.readEntity(
                      ErrorDescJSON.class)).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(null).build();
    }
  }

  @PUT
  @Path("contents")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response getContents(@Context SecurityContext sc,
          @Context HttpServletRequest req, ContentsRequestDTO contentsRequestDTO)
          throws AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    HopsContentsSummaryJSON hopsContentsSummaryJSON = gvodController.
            getContents(contentsRequestDTO.getProjectId());
    if (hopsContentsSummaryJSON != null) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(hopsContentsSummaryJSON).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(null).build();
    }
  }

  @PUT
  @Path("details")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response getExtendedDetails(@Context SecurityContext sc,
          @Context HttpServletRequest req, DetailsRequestDTO detailsRequestDTO)
          throws AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    TorrentExtendedStatusJSON torrentExtendedStatusJSON = gvodController.
            getDetails(detailsRequestDTO.getTorrentId());
    if (torrentExtendedStatusJSON != null) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(torrentExtendedStatusJSON).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(null).build();
    }
  }

  @PUT
  @Path("remove")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.DATA_SCIENTIST, AllowedRoles.DATA_OWNER})
  public Response remove(@Context SecurityContext sc,
          @Context HttpServletRequest req, RemoveTorrentDTO removeTorrentDTO)
          throws AppException {
    if (settings.getCLUSTER_ID() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.NOT_REGISTERD_WITH_HOPS_SITE);
    }
    if (settings.getGVOD_UDP_ENDPOINT() == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              ResponseMessages.GVOD_OFFLINE);
    }
    String response = gvodController.removeUpload(removeTorrentDTO.
            getTorrentId());
    if (response != null) {
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(response).build();
    } else {
      return noCacheResponse.getNoCacheResponseBuilder(
              Response.Status.EXPECTATION_FAILED).entity(null).build();
    }
  }
}
