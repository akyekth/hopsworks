package io.hops.hopsworks.api.airpal.resources;

import io.hops.hopsworks.airpal.core.AirpalUser;
import io.hops.hopsworks.airpal.core.AuthorizationUtil;
import io.swagger.annotations.Api;
import lombok.Value;
import org.secnod.shiro.jaxrs.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "UserResource", description = "User Resource")
public class UserResource {

  @GET
  public Response getUserInfo(@Auth AirpalUser user) {
    if (user == null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.ok(
          new UserInfo(
              user.getUserName(),
              new ExecutionPermissions(
                  AuthorizationUtil.isAuthorizedWrite(user, "hive", "airpal", "any"),
                  true,
                  user.getAccessLevel())
          )).build();
    }
  }

  @Value
  private static class UserInfo {

    private final String name;
    private final ExecutionPermissions executionPermissions;
  }

  @Value
  public static class ExecutionPermissions {

    private final boolean canCreateTable;
    private final boolean canCreateCsv;
    private final String accessLevel;
  }
}
