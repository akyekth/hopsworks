package io.hops.hopsworks.common.dao.project.service;

public enum ProjectServiceEnum {

  ZEPPELIN("Zeppelin"),
  SSH("Ssh"),
  KAFKA("Kafka"),
  WORKFLOWS("Workflows"),
  TENSORFLOW("Tensorflow"),
  HISTORY("History"),
  //  BIOBANKING("Biobanking"),
  //  CHARON("Charon"),
  JOBS("Jobs"),
  P2P("P2p");

  private final String readable;

  private ProjectServiceEnum(String readable) {
    this.readable = readable;
  }

  @Override
  public String toString() {
    return readable;
  }

}
