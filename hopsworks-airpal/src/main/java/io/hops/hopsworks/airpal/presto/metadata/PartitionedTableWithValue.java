package io.hops.hopsworks.airpal.presto.metadata;

import io.hops.hopsworks.airpal.presto.Table;
import io.hops.hopsworks.airpal.presto.hive.HivePartition;
import com.google.common.base.Optional;
import lombok.Value;

@Value
public class PartitionedTableWithValue {

  private final Table table;
  private final Optional<HivePartition> partition;
  private final String value;
}
