package io.hops.hopsworks.api.airpal.sql.dao;

import io.hops.hopsworks.api.airpal.Job;
import com.hubspot.rosetta.jdbi.RosettaBinder;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface JobDAO {

  @SqlUpdate(
      "INSERT INTO jobs (query, user, uuid, queryStats, state, columns, query_finished, query_started, error) "
      + "VALUES (" + ":query, " + ":user, " + ":uuid, " + ":queryStats, " + ":state, " + ":columns, "
      + ":queryFinished, " + ":queryStarted, " + ":error)")
  @GetGeneratedKeys
  long createJob(
      @RosettaBinder Job job
  );
}
