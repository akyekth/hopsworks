package io.hops.hopsworks.airpal.api.output.persistors;

import io.hops.hopsworks.airpal.api.Job;
import io.hops.hopsworks.airpal.api.output.builders.JobOutputBuilder;
import io.hops.hopsworks.airpal.core.execution.QueryExecutionAuthorizer;

import java.net.URI;

public interface Persistor {

  public boolean canPersist(QueryExecutionAuthorizer authorizer);

  URI persist(JobOutputBuilder outputBuilder, Job job);
}
