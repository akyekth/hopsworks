package io.hops.hopsworks.common.gvod.responses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SuccessJSON {

  private String details;

  public SuccessJSON() {
    this.details = "";
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}
