package io.hops.gvod.download;

import io.hops.gvod.resources.HDFSEndpoint;
import io.hops.gvod.resources.KafkaEndpoint;
import io.hops.gvod.resources.items.ExtendedDetails;
import io.hops.gvod.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class HopsTorrentAdvanceDownload {

  private TorrentId torrentId;
  private KafkaEndpoint kafkaEndpoint;
  private HDFSEndpoint hdfsEndpoint;
  private ExtendedDetails extendedDetails;

  public HopsTorrentAdvanceDownload() {
  }

  public HopsTorrentAdvanceDownload(TorrentId torrentId,
          KafkaEndpoint kafkaEndpoint, HDFSEndpoint hdfsEndpoint,
          ExtendedDetails extendedDetails) {
    this.torrentId = torrentId;
    this.kafkaEndpoint = kafkaEndpoint;
    this.hdfsEndpoint = hdfsEndpoint;
    this.extendedDetails = extendedDetails;
  }

  public TorrentId getTorrentId() {
    return torrentId;
  }

  public void setTorrentId(TorrentId torrentId) {
    this.torrentId = torrentId;
  }

  public KafkaEndpoint getKafkaEndpoint() {
    return kafkaEndpoint;
  }

  public void setKafkaEndpoint(KafkaEndpoint kafkaEndpoint) {
    this.kafkaEndpoint = kafkaEndpoint;
  }

  public ExtendedDetails getExtendedDetails() {
    return extendedDetails;
  }

  public void setExtendedDetails(ExtendedDetails extendedDetails) {
    this.extendedDetails = extendedDetails;
  }

  public HDFSEndpoint getHdfsEndpoint() {
    return hdfsEndpoint;
  }

  public void setHdfsEndpoint(HDFSEndpoint hdfsEndpoint) {
    this.hdfsEndpoint = hdfsEndpoint;
  }

}
