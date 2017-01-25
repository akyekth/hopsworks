package io.hops.hopsworks.api.elastic;

import io.hops.hopsworks.api.filter.AllowedRoles;
import io.hops.hopsworks.api.filter.NoCacheResponse;
import io.hops.hopsworks.common.dao.dataset.Dataset;
import io.hops.hopsworks.common.dao.dataset.DatasetFacade;
import io.hops.hopsworks.common.dao.hdfs.inode.Inode;
import io.hops.hopsworks.common.dao.hdfs.inode.InodeFacade;
import io.hops.hopsworks.common.dao.project.Project;
import io.hops.hopsworks.common.dao.project.ProjectFacade;
import io.hops.hopsworks.common.dao.user.Users;
import io.hops.hopsworks.common.dao.user.security.ua.UserManager;
import io.hops.hopsworks.common.dataset.DatasetController;
import io.hops.hopsworks.common.dataset.FilePreviewDTO;
import io.hops.hopsworks.common.exception.AppException;
import io.hops.hopsworks.common.hdfs.DistributedFileSystemOps;
import io.hops.hopsworks.common.hdfs.DistributedFsService;
import io.hops.hopsworks.common.project.MoreInfoDTO;
import io.swagger.annotations.Api;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/publicSearch")
@RequestScoped
@Api(value = "Public search",
        description = "Public search service")
public class PublicSearchService {

  @EJB
  private ProjectFacade projectFacade;
  @EJB
  private UserManager userManager;
  @EJB
  private DatasetFacade datasetFacade;
  @EJB
  private DistributedFsService dfs;
  @EJB
  private DatasetController datasetController;
  @EJB
  private InodeFacade inodes;
  @EJB
  private NoCacheResponse noCacheResponse;

  @GET
  @Path("/getMoreInfo/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AllowedRoles(roles = {AllowedRoles.ALL})
  public Response getMoreInfo(@PathParam("id") Integer id) throws AppException {
    MoreInfoDTO info = null;
    if (id != null) {
      info = datasetInfo(id);
    }
    if (info == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              "Dataset not found.");
    }
    return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).entity(
            info).build();
  }

  @GET
  @Path("/readme/{path: .+}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getReadme(@PathParam("path") String path) throws AppException {
    if (path == null) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              "No path given.");
    }
    String[] parts = path.split(File.separator);
    if (parts.length < 5) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              "Should specify full path.");
    }
    Project proj = projectFacade.findByName(parts[2]);
    Dataset ds = datasetFacade.findByNameAndProjectId(proj, parts[3]);
    if (ds != null && (!ds.isSearchable() || !ds.isPublicDs())) {
      throw new AppException(Response.Status.BAD_REQUEST.getStatusCode(),
              "Readme not accessable.");
    }
    DistributedFileSystemOps dfso = dfs.getDfsOps();
    FilePreviewDTO filePreviewDTO;
    try {
      filePreviewDTO = datasetController.getReadme(path, dfso);
    } catch (IOException ex) {
      filePreviewDTO = new FilePreviewDTO();
      filePreviewDTO.setContent("No README file found for this dataset.");
      return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).
              entity(
                      filePreviewDTO).build();
    }
    return noCacheResponse.getNoCacheResponseBuilder(Response.Status.OK).entity(
            filePreviewDTO).build();
  }

  private MoreInfoDTO datasetInfo(Integer inodeId) {
    Inode inode = inodes.findById(inodeId);
    if (inode == null) {
      return null;
    }
    List<Dataset> ds = datasetFacade.findByInode(inode);
    if (ds == null) {
      return null;
    }
    for (Dataset d : ds) {
      if (!d.isShared() && !d.isPublicDs()) {
        return null;
      }
    }
    MoreInfoDTO info = new MoreInfoDTO(inode);
    Users user = userManager.getUserByUsername(info.getUser());
    info.setUser(user.getFname() + " " + user.getLname());
    info.setSize(inodes.getSize(inode));
    info.setPath(inodes.getPath(inode));
    return info;
  }
}
