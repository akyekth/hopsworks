package io.hops.hopssite.io.identity;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IdentificationJSON {

  private String clusterId;

  public IdentificationJSON(String clusterId) {
    this.clusterId = clusterId;
  }

  public IdentificationJSON() {

  }

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }

}
