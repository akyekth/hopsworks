package io.hops.hopsworks.api.airpal.core.store.queries;

import io.hops.hopsworks.api.airpal.queries.SavedQuery;
import io.hops.hopsworks.api.airpal.queries.UserSavedQuery;
import io.hops.hopsworks.api.airpal.core.AirpalUser;
import io.hops.hopsworks.api.airpal.presto.PartitionedTable;

import java.util.List;
import java.util.UUID;

public interface QueryStore {

  public List<SavedQuery> getSavedQueries(AirpalUser airpalUser);

  public List<SavedQuery> getSavedQueries(AirpalUser airpalUser, List<PartitionedTable> tables);

  public boolean saveQuery(UserSavedQuery query);

  public boolean deleteSavedQuery(AirpalUser airpalUser, UUID queryUUID);

  public SavedQuery getSavedQuery(UUID queryUUID);
}
