package io.hops.hopsworks.api.airpal.resources;

import io.swagger.annotations.Api;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
@Api(value = "RedirectRootResource", description = "Redirect Root  Resource")
public class RedirectRootResource {

  @GET
  public Response redirectToApp() {
    return Response.temporaryRedirect(URI.create("/app"))
        .status(Response.Status.MOVED_PERMANENTLY)
        .build();
  }
}
