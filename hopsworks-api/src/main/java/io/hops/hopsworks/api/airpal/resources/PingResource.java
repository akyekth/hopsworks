package io.hops.hopsworks.api.airpal.resources;

import io.swagger.annotations.Api;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/ping")
@Api(value = "PingResource", description = "Ping Resource")
public class PingResource {

  @GET
  public Response ping() {
    return Response.status(Response.Status.OK).entity("PONG").build();
  }
}
