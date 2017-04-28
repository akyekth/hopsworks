package io.hops.hopsworks.api.airpal.resources;

//import io.hops.hopsworks.api.airpal.core.AirpalUser;
//import io.hops.hopsworks.api.airpal.core.AuthorizationUtil;
import io.swagger.annotations.Api;
//import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.Response;
//import org.secnod.shiro.jaxrs.Auth;

@Path("/sample")
@Api(value = "SampleResource",
  description = "sample Resource")
public class SampleResource {

  @Path("/s1/{name}")
  @Produces(MediaType.TEXT_PLAIN)
  @GET
  public String getSamples(@PathParam("name") String name) {
    return name;
  }

  @Path("/s2/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public String getSamples1(@PathParam("name") String name) {
    return name;
  }

}
