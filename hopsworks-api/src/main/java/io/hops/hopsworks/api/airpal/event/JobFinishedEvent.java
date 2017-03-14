package io.hops.hopsworks.api.airpal.event;

import io.hops.hopsworks.api.airpal.Job;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data 
public class JobFinishedEvent implements JobEvent {
          
       @JsonProperty
       private final Job job;

   
}
