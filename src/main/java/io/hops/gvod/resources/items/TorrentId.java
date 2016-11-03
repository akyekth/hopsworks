package io.hops.gvod.resources.items;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
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
