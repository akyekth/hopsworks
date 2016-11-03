package io.hops.gvod.stop;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
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
