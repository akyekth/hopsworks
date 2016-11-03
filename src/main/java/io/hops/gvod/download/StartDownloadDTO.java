package io.hops.gvod.download;

import io.hops.gvod.resources.items.AddressJSON;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class StartDownloadDTO {

  private String destinationDatasetName;

  private String publicDatasetId;

  private int projectId;

  private List<AddressJSON> partners;

  public StartDownloadDTO() {
  }

  public StartDownloadDTO(String destinationDatasetName, String publicDatasetId,
          int projectId, List<AddressJSON> partners) {
    this.destinationDatasetName = destinationDatasetName;
    this.publicDatasetId = publicDatasetId;
    this.projectId = projectId;
    this.partners = partners;
  }

  public String getDestinationDatasetName() {
    return destinationDatasetName;
  }

  public void setDestinationDatasetName(String destinationDatasetName) {
    this.destinationDatasetName = destinationDatasetName;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public List<AddressJSON> getPartners() {
    return partners;
  }

  public void setPartners(List<AddressJSON> partners) {
    this.partners = partners;
  }

  public String getPublicDatasetId() {
    return publicDatasetId;
  }

  public void setPublicDatasetId(String publicDatasetId) {
    this.publicDatasetId = publicDatasetId;
  }

}
