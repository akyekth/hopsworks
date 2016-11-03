package io.hops.gvod.contents;

/**
 *
 * @author jsvhqr
 */
public class HopsContentsReqJSON {

  private Integer projectId;

  public HopsContentsReqJSON() {
  }

  public HopsContentsReqJSON(Integer projectId) {
    this.projectId = projectId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }
}
