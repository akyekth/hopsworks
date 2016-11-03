package io.hops.gvod.stop;

import javax.xml.bind.annotation.XmlRootElement;
import io.hops.gvod.resources.items.TorrentId;

@XmlRootElement
public class RemoveGVodJSON {

  private TorrentId torrentId;

  public RemoveGVodJSON() {
  }

  public RemoveGVodJSON(TorrentId torrentId) {
    this.torrentId = torrentId;
  }

  public TorrentId getTorrentId() {
    return torrentId;
  }

  public void setTorrentId(TorrentId torrentId) {
    this.torrentId = torrentId;
  }

}
