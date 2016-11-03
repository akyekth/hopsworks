package io.hops.gvod.contents;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class ContentsRequestDTO {

  private int projectId;

  public ContentsRequestDTO(int projectId) {
    this.projectId = projectId;
  }

  public ContentsRequestDTO() {
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

}
