package io.hops.gvod.contents;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DetailsRequestDTO {

  private String torrentId;
  private int index;

  public DetailsRequestDTO() {
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getTorrentId() {
    return torrentId;
  }

  public void setTorrentId(String torrentId) {
    this.torrentId = torrentId;
  }

}
