package io.hops.gvod.resources.items;

import io.hops.gvod.resources.HDFSResource;
import java.util.Map;

/**
 *
 * @author jsvhqr
 */
public class ExtendedDetails {

  private Map<String, HDFSResource> hdfsDetails;
  private Map<String, KafkaResource> kafkaDetails;

  public ExtendedDetails() {
  }

  public ExtendedDetails(Map<String, HDFSResource> hdfsDetails,
          Map<String, KafkaResource> kafkaDetails) {
    this.hdfsDetails = hdfsDetails;
    this.kafkaDetails = kafkaDetails;
  }

  public Map<String, HDFSResource> getHdfsDetails() {
    return hdfsDetails;
  }

  public void setHdfsDetails(Map<String, HDFSResource> hdfsDetails) {
    this.hdfsDetails = hdfsDetails;
  }

  public Map<String, KafkaResource> getKafkaDetails() {
    return kafkaDetails;
  }

  public void setKafkaDetails(Map<String, KafkaResource> kafkaDetails) {
    this.kafkaDetails = kafkaDetails;
  }

}
