package io.hops.hopsworks.common.gvod.hopssite;

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
