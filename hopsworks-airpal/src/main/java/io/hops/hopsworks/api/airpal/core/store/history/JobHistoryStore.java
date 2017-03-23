package io.hops.hopsworks.api.airpal.core.store.history;

import io.hops.hopsworks.api.airpal.Job;
import io.hops.hopsworks.api.airpal.presto.Table;

import java.util.List;

public interface JobHistoryStore {

  public List<Job> getRecentlyRun(long maxResults);

  public List<Job> getRecentlyRun(long maxResults, Table table1, Table... otherTables);

  public List<Job> getRecentlyRun(long maxResults, Iterable<Table> tables);

  public List<Job> getRecentlyRunForUser(String user, long maxResults);

  public List<Job> getRecentlyRunForUser(String user, long maxResults, Iterable<Table> tables);

  public void addRun(Job job);
}
