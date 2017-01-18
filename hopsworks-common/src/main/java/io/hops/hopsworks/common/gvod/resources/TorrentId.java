package io.hops.hopsworks.common.gvod.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TorrentId {

  private String val;

  public TorrentId() {
  }

  public TorrentId(String val) {
    this.val = val;
  }

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

}
