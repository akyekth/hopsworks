package io.hops.gvod.resources;

/**
 *
 * @author jsvhqr
 */
public class HopsResource {

  private final int projectId;

  public HopsResource(int projectId) {
    this.projectId = projectId;
  }

  public int getProjectId() {
    return projectId;
  }
}
