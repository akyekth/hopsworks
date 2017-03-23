package io.hops.hopsworks.api.airpal.output.persistors;

import io.hops.hopsworks.api.airpal.Job;
import io.hops.hopsworks.api.airpal.output.builders.JobOutputBuilder;
import io.hops.hopsworks.api.airpal.core.execution.QueryExecutionAuthorizer;

import java.net.URI;

public interface Persistor {

  public boolean canPersist(QueryExecutionAuthorizer authorizer);

  URI persist(JobOutputBuilder outputBuilder, Job job);
}
