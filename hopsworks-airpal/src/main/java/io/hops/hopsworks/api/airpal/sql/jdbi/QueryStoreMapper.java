package io.hops.hopsworks.api.airpal.sql.jdbi;

import io.hops.hopsworks.api.airpal.queries.FeaturedQuery;
import io.hops.hopsworks.api.airpal.queries.SavedQuery;
import io.hops.hopsworks.api.airpal.queries.UserSavedQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Slf4j
public class QueryStoreMapper implements ResultSetMapper<SavedQuery> {

  private final ObjectMapper objectMapper;

  public QueryStoreMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public SavedQuery map(int index, ResultSet r, StatementContext ctx)
      throws SQLException {
    try {
      return new UserSavedQuery(
          objectMapper.readValue(r.getString("query"), FeaturedQuery.QueryWithPlaceholders.class),
          r.getString("user"),
          r.getString("name"),
          r.getString("description"),
          null,
          UUID.fromString(r.getString("uuid")),
          false);
    } catch (IOException e) {
      log.error("Caught exception mapping SavedQuery", e);
    }

    return null;
  }
}
