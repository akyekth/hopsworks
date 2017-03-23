package io.hops.hopsworks.api.airpal.resources;

//import io.hops.hopsworks.api.airpal.api.Job;
//import io.hops.hopsworks.api.airpal.api.JobState;
//import io.hops.hopsworks.api.airpal.core.AirpalUser;
//import io.hops.hopsworks.api.airpal.core.AuthorizationUtil;
//import io.hops.hopsworks.api.airpal.core.execution.ExecutionClient;
//import io.hops.hopsworks.api.airpal.core.store.history.JobHistoryStore;
//import io.hops.hopsworks.api.airpal.presto.PartitionedTable;
//import io.hops.hopsworks.api.airpal.presto.Table;
//import com.facebook.presto.client.Column;
//import com.google.common.base.Optional;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.Iterables;
//import com.google.common.collect.Ordering;
//import com.google.inject.Inject;
//import org.secnod.shiro.jaxrs.Auth;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import java.util.Collections;
//import java.util.List;
import java.util.UUID;

//import static io.hops.hopsworks.api.airpal.resources.QueryResource.JOB_ORDERING;
@Path("/queries")
@Produces({MediaType.APPLICATION_JSON})
public class QueriesResource {

//  private final JobHistoryStore jobHistoryStore;
//  private final ExecutionClient executionClient;
//  @Inject
//  public QueriesResource(
//          JobHistoryStore jobHistoryStore,
//          ExecutionClient executionClient) {
//    this.jobHistoryStore = jobHistoryStore;
//    this.executionClient = executionClient;
//  }
  @GET
  public Response getQueries(
      //          @Auth AirpalUser user,
      @QueryParam("results") int numResults) {
    //          @QueryParam("table") List<PartitionedTable> tables
//    Iterable<Job> recentlyRun;
//    int results = Optional.of(numResults).or(200);
//
//    if (tables.size() < 1) {
//      recentlyRun = jobHistoryStore.getRecentlyRun(results);
//    } else {
//      recentlyRun = jobHistoryStore.getRecentlyRun(
//              results,
//              Iterables.transform(tables,
//                      new PartitionedTable.PartitionedTableToTable()));
//    }
//
//    ImmutableList.Builder<Job> filtered = ImmutableList.builder();
//    for (Job job : recentlyRun) {
//      if (job.getTablesUsed().isEmpty() && (job.getState() == JobState.FAILED)) {
//        filtered.add(job);
//        continue;
//      }
//      for (Table table : job.getTablesUsed()) {
//        if (AuthorizationUtil.isAuthorizedRead(user, table)) {
//          filtered.add(new Job(
//                  job.getUser(),
//                  job.getQuery(),
//                  job.getUuid(),
//                  job.getOutput(),
//                  job.getQueryStats(),
//                  job.getState(),
//                  Collections.<Column>emptyList(),
//                  Collections.<Table>emptySet(),
//                  job.getQueryStartedDateTime(),
//                  job.getError(),
//                  job.getQueryFinishedDateTime()));
//        }
//      }
//    }
//
//    List<Job> sortedResult = Ordering
//            .natural()
//            .nullsLast()
//            .onResultOf(JOB_ORDERING)
//            .reverse()
//            .immutableSortedCopy(filtered.build());
//    return Response.ok(sortedResult).build();
    return Response.status(Response.Status.OK).build();
  }

  @DELETE
  @Path("/{uuid}")
  public Response cancelQuery(
      //          @Auth AirpalUser user,
      @PathParam("uuid") UUID uuid) {
    boolean success = true; //executionClient.cancelQuery(user, uuid);
    if (success) {
      return Response.ok().build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }
}
