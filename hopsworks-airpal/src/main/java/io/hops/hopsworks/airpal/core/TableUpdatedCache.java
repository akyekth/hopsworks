package io.hops.hopsworks.airpal.core;

import io.hops.hopsworks.airpal.presto.PartitionedTable;
import io.hops.hopsworks.airpal.presto.Table;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

public interface TableUpdatedCache {

  public DateTime get(Table table);

  public Map<PartitionedTable, DateTime> getAllPresent(List<? extends Table> tables);

  public Map<PartitionedTable, DateTime> getAll(List<Table> tables);
}
