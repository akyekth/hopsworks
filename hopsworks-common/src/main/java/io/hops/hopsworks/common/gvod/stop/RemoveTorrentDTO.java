package io.hops.hopsworks.common.gvod.stop;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RemoveTorrentDTO {

  private String torrentId;

  public RemoveTorrentDTO() {
  }

  public String getTorrentId() {
    return torrentId;
  }

  public void setTorrentId(String torrentId) {
    this.torrentId = torrentId;
  }

}
