package io.hops.hopsworks.common.gvod.hopssite;

import io.hops.hopsworks.common.gvod.resources.AddressJSON;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisterJSON {

  private String searchEndpoint;

  private AddressJSON gvodEndpoint;

  private String email;

  private String cert;

  public RegisterJSON(String searchEndpoint, AddressJSON gvodEndpoint,
          String email, String cert) {
    this.searchEndpoint = searchEndpoint;
    this.gvodEndpoint = gvodEndpoint;
    this.email = email;
    this.cert = cert;
  }

  public RegisterJSON() {
  }

  public void setSearchEndpoint(String searchEndpoint) {
    this.searchEndpoint = searchEndpoint;
  }

  public void setGvodEndpoint(AddressJSON gvodEndpoint) {
    this.gvodEndpoint = gvodEndpoint;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setCert(String cert) {
    this.cert = cert;
  }

  public String getSearchEndpoint() {
    return searchEndpoint;
  }

  public AddressJSON getGvodEndpoint() {
    return gvodEndpoint;
  }

  public String getEmail() {
    return email;
  }

  public String getCert() {
    return cert;
  }

}
