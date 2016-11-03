package io.hops.gvod.resources;

public class HopsResource {

  private final int projectId;

  public HopsResource(int projectId) {
    this.projectId = projectId;
  }

  public int getProjectId() {
    return projectId;
  }
}
