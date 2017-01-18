package io.hops.hopsworks.common.gvod.hopssite;

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
