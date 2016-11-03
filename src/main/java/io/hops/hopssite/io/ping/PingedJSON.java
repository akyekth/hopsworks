package io.hops.hopssite.io.ping;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import io.hops.hopssite.io.register.RegisteredClusterJSON;

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
