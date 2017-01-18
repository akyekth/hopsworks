package io.hops.hopsworks.common.gvod.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorDescJSON {

  private String details;

  public ErrorDescJSON(String details) {
    this.details = details;
  }

  public ErrorDescJSON() {
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
