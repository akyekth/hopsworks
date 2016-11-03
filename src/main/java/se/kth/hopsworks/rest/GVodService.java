package se.kth.hopsworks.rest;

import io.hops.gvod.contents.ContentsRequestDTO;
import io.hops.gvod.contents.DetailsRequestDTO;
import io.hops.gvod.contents.TorrentExtendedStatusJSON;
import java.io.File;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import se.kth.bbc.project.Project;
import se.kth.bbc.security.ua.UserManager;
import se.kth.hopsworks.controller.KafkaController;
import se.kth.hopsworks.controller.ResponseMessages;
import se.kth.hopsworks.filters.AllowedRoles;
import io.hops.gvod.download.DownloadDTO;
import se.kth.hopsworks.controller.GVoDController;
import se.kth.hopsworks.controller.ProjectController;
import io.hops.gvod.download.StartDownloadDTO;
import io.hops.gvod.resources.KafkaEndpoint;
import io.hops.gvod.resources.items.ManifestResponse;
import io.hops.gvod.responses.ErrorDescJSON;
import io.hops.gvod.responses.HopsContentsSummaryJSON;
import io.hops.gvod.responses.SuccessJSON;
import io.hops.gvod.stop.RemoveTorrentDTO;
import se.kth.hopsworks.hdfsUsers.controller.HdfsUsersController;
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
