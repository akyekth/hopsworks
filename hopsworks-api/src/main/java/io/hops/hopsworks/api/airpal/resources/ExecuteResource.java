package io.hops.hopsworks.api.airpal.resources;

import io.hops.hopsworks.airpal.core.AirpalUser;
import io.hops.hopsworks.airpal.core.AuthorizationUtil;
import io.hops.hopsworks.airpal.core.execution.ExecutionClient;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import io.swagger.annotations.Api;
import lombok.Data;
import org.secnod.shiro.jaxrs.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.UUID;

@Path("/execute")
@Api(value = "ExecuteResource", description = "Execute Resource")
public class ExecuteResource {

  private ExecutionClient executionClient;

  @Inject
  public ExecuteResource(ExecutionClient executionClient) {
    this.executionClient = executionClient;
  }

//  @PUT
//  @Produces(MediaType.APPLICATION_JSON)
//  @Consumes(MediaType.APPLICATION_JSON)
//  public Response executeQuery(@Auth AirpalUser user, 
//         ExecutionRequest request) throws IOException {
//
//    if (user != null) {
//      final UUID queryUuid = executionClient.runQuery(
//          request,
//          user,
//          user.getDefaultSchema(),
//          user.getQueryTimeout());
//
//      return Response.ok(new ExecutionSuccess(queryUuid)).build();
//    }
//    return Response.status(Response.Status.NOT_FOUND)
//        .entity(new ExecutionError("No Airpal user found"))
//        .build();
//  }

  @GET
  @Path("permissions")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPermissions(@Auth AirpalUser user) {
    if (user == null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.ok(new ExecutionPermissions(
          AuthorizationUtil.isAuthorizedWrite(user, "hive", "airpal", "any"),
          true,
          user.getUserName(),
          user.getAccessLevel()
      )).build();
    }
  }

  @Data
  public static class ExecutionSuccess {

    @JsonProperty
    public final UUID uuid;
  }

  @Data
  public static class ExecutionError {

    @JsonProperty
    public final String message;
  }

  @Data
  public static class ExecutionPermissions {

    private final boolean canCreateTable;
    private final boolean canCreateCsv;
    private final String userName;
    private final String accessLevel;
  }
}
