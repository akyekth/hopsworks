package io.hops.hopsworks.api.airpal.output.persistors;

import io.hops.hopsworks.api.airpal.Job;
import io.hops.hopsworks.api.airpal.output.PersistentJobOutput;
import io.hops.hopsworks.api.airpal.output.builders.JobOutputBuilder;
import io.hops.hopsworks.api.airpal.core.execution.QueryExecutionAuthorizer;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.net.URI;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class HiveTablePersistor
    implements Persistor {

  private static final Splitter TABLE_SPLITTER = Splitter.on(".").omitEmptyStrings();
  private final URI jobURI;

  public HiveTablePersistor(PersistentJobOutput jobOutput) {
    this.jobURI = jobOutput.getLocation();
  }

  @Override
  public boolean canPersist(QueryExecutionAuthorizer authorizer) {
    List<String> locationParts = TABLE_SPLITTER.splitToList(jobURI.toString());
    checkState(locationParts.size() == 2, "destination hive table did not have schema and table components");
    return authorizer.isAuthorizedWrite("hive", Iterables.getFirst(locationParts, ""), 
        Iterables.getLast(locationParts));
  }

  @Override
  public URI persist(JobOutputBuilder outputBuilder, Job job) {
    return null;
  }
}
