package io.hops.hopsworks.common.gvod.status;

import javax.xml.bind.annotation.XmlRootElement;
import io.hops.hopsworks.common.gvod.resources.TorrentId;

@XmlRootElement
public class StatusGVoDJSON {

  private TorrentId torrentId;
  private String datasetPath;

  public StatusGVoDJSON() {
  }

  public StatusGVoDJSON(String datasetPath, TorrentId torrentId) {
    this.torrentId = torrentId;
    this.datasetPath = datasetPath;
  }

  public TorrentId getTorrentId() {
    return torrentId;
  }

  public void setTorrentId(TorrentId torrentId) {
    this.torrentId = torrentId;
  }

  public String getDatasetPath() {
    return datasetPath;
  }

  public void setDatasetPath(String datasetPath) {
    this.datasetPath = datasetPath;
  }
}
