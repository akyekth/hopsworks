package io.hops.hopsworks.airpal.api.event;

import io.hops.hopsworks.airpal.api.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class JobFinishedEvent implements JobEvent {

  @JsonProperty
  private final Job job;

}
