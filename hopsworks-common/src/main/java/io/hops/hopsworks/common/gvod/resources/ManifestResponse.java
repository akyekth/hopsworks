package io.hops.hopsworks.common.gvod.resources;

import javax.ws.rs.core.Response;

public class ManifestResponse {

  private ManifestJSON manifest;
  private Response response;

  public ManifestResponse(ManifestJSON manifest, Response response) {
    this.manifest = manifest;
    this.response = response;
  }

  public ManifestJSON getManifest() {
    return manifest;
  }

  public void setManifest(ManifestJSON manifest) {
    this.manifest = manifest;
  }

  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

}
