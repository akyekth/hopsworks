package io.hops.gvod.download;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONObject;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class DownloadDTO {

  private String destinationDatasetName;

  private int projectId;

  private String publicDatasetId;

  private List<String> partners;

  private String topics;

  public DownloadDTO() {
  }

  public DownloadDTO(String destinationDatasetName, int projectId,
          String publicDatasetId, List<String> partners, String topics) {
    this.destinationDatasetName = destinationDatasetName;
    this.projectId = projectId;
    this.publicDatasetId = publicDatasetId;
    this.partners = partners;
    this.topics = topics;
  }

  public String getDestinationDatasetName() {
    return destinationDatasetName;
  }

  public void setDestinationDatasetName(String destinationDatasetName) {
    this.destinationDatasetName = destinationDatasetName;
  }

  public String getPublicDatasetId() {
    return publicDatasetId;
  }

  public void setPublicDatasetId(String publicDatasetId) {
    this.publicDatasetId = publicDatasetId;
  }

  public int getProjectId() {
    return projectId;
  }

  public List<String> getPartners() {
    return partners;
  }

  public String getTopics() {
    return topics;
  }

  public JSONObject getJSONTopics() {
    return new JSONObject(topics);
  }

  public void setTopics(String topics) {
    this.topics = topics;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public void setPartners(List<String> partners) {
    this.partners = partners;
  }

}
