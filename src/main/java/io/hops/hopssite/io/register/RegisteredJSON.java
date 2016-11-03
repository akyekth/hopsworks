package io.hops.hopssite.io.register;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegisteredJSON {

  private String clusterId;

  public RegisteredJSON() {

  }

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

}
