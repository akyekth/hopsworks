package io.hops.hopsworks.api.airpal.output.persistors;

import io.hops.hopsworks.api.airpal.Job;
import io.hops.hopsworks.api.airpal.output.PersistentJobOutput;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersistorFactory {

  private final CSVPersistorFactory csvPersistorFactory;

  public Persistor getPersistor(Job job, PersistentJobOutput jobOutput) {
    switch (jobOutput.getType()) {
      case "csv":
        return csvPersistorFactory.getPersistor(job, jobOutput);
      case "hive":
        return new HiveTablePersistor(jobOutput);
      default:
        throw new IllegalArgumentException();
    }
  }
}
