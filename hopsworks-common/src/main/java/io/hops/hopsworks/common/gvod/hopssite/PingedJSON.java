package io.hops.hopsworks.common.gvod.hopssite;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PingedJSON {

  private List<RegisteredClusterJSON> clusters;

  public PingedJSON() {

  }

  public PingedJSON(List<RegisteredClusterJSON> clusters) {
    this.clusters = clusters;
  }

  public List<RegisteredClusterJSON> getClusters() {
    return clusters;
  }

  public void setClusters(List<RegisteredClusterJSON> clusters) {
    this.clusters = clusters;
  }

}
