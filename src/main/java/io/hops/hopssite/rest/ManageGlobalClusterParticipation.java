package io.hops.hopssite.rest;

import io.hops.gvod.resources.items.AddressJSON;
import io.hops.hopssite.io.register.RegisterJSON;
import io.hops.hopssite.io.populardatasets.PopularDatasetJSON;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import io.hops.gvod.resources.items.ManifestJSON;
import io.hops.hopssite.io.identity.IdentificationJSON;
import io.hops.hopssite.io.ping.PingedJSON;
import io.hops.hopssite.io.register.RegisteredJSON;
import io.hops.hopssite.io.register.RegisteredClusterJSON;
import javax.mail.Session;
import se.kth.hopsworks.util.Settings;

@Startup
@Singleton
public class ManageGlobalClusterParticipation {

  private List<RegisteredClusterJSON> registeredClusters = null;
  private List<PopularDatasetJSON> popularDatasets = null;
  private WebTarget webTarget = null;
  private Client client = null;

  @Resource
  private SessionContext context;

  @EJB
  private Settings settings;

  @Resource(lookup = "mail/BBCMail")
  private Session mailSession;

  @PostConstruct
  private void init() {
    DoTimeout();
  }

  @Timeout
  private void TimeoutOcurred() {

    if (webTarget != null && client != null) {
      doTimeoutStuff();
    } else {
      client = javax.ws.rs.client.ClientBuilder.newClient();
      webTarget = client.target(settings.getBASE_URI_HOPS_SITE()).path(
              "myresource");
      doTimeoutStuff();
    }

  }

  private void doTimeoutStuff() {
    if (settings.getCLUSTER_ID() != null) {
      PingAndGetPopularDatasets();
    } else {
      TryToRegister();
    }
  }

  private void TryToRegister() {

    String id = null;
    try {
      AddressJSON gvodEndpoint = settings.getGVOD_UDP_ENDPOINT();
      if (gvodEndpoint != null) {
        id = RegisterRest(settings.getELASTIC_PUBLIC_RESTENDPOINT(),
                mailSession.getProperty("mail.from"), settings.getCLUSTER_CERT(),
                gvodEndpoint);
      }
    } catch (ClientErrorException ex) {

    } finally {
      if (id != null) {
        settings.setCLUSTER_ID(id);
      }
      DoTimeout();
    }

  }

  private void PingAndGetPopularDatasets() {

    List<RegisteredClusterJSON> pingResponse = null;
    List<PopularDatasetJSON> popularDatasetsResponse = null;
    try {
      pingResponse = PingRest(settings.getCLUSTER_ID());
      popularDatasetsResponse = getPopularDatasets(settings.getCLUSTER_ID());
    } catch (ClientErrorException ex) {

    } finally {
      if (pingResponse != null) {
        this.registeredClusters = pingResponse;
      }
      if (popularDatasetsResponse != null) {
        this.popularDatasets = popularDatasetsResponse;
      }
      DoTimeout();
    }

  }

  private List<RegisteredClusterJSON> PingRest(String clusterId) {
    WebTarget resource = webTarget.path("ping");

    Response r = resource.request().accept(MediaType.APPLICATION_JSON).put(
            Entity.json(new IdentificationJSON(clusterId)));

    if (r != null && r.getStatus() == 200) {
      return r.readEntity(PingedJSON.class).getClusters();
    } else {
      return null;
    }
  }

  private List<PopularDatasetJSON> getPopularDatasets(String clusterId) {

    WebTarget resource = webTarget.path("populardatasets");

    Response r = resource.request().accept(MediaType.APPLICATION_JSON).put(
            Entity.json(new IdentificationJSON(clusterId)));

    if (r != null && r.getStatus() == 200) {
      return r.readEntity(new GenericType<List<PopularDatasetJSON>>() {
      });
    } else {
      return null;
    }
  }

  private String RegisterRest(String searchEndpoint, String email, String cert,
          AddressJSON gvodEndpoint) {

    WebTarget resource = webTarget.path("register");

    Response r = resource.request().accept(MediaType.APPLICATION_JSON).post(
            Entity.json(new RegisterJSON(searchEndpoint, gvodEndpoint, email,
                    cert)));

    if (r != null && r.getStatus() == 200) {
      return r.readEntity(RegisteredJSON.class).getClusterId();
    } else {
      return null;
    }

  }

  private void DoTimeout() {

    long duration = 60000;
    TimerConfig timerConfig = new TimerConfig();
    timerConfig.setPersistent(false);
    context.getTimerService().createSingleActionTimer(duration, timerConfig);

  }

  public void notifyHopsSiteAboutNewDataset(ManifestJSON manifestJson,
          String publicDsId, int leeches, int seeds) {

    WebTarget resource = webTarget.path("populardatasets");

    resource.request().accept(MediaType.APPLICATION_JSON).post(Entity.json(
            new PopularDatasetJSON(manifestJson, publicDsId, leeches, seeds)));

  }

  public List<RegisteredClusterJSON> getRegisteredClusters() {
    return registeredClusters;
  }

  public List<PopularDatasetJSON> getPopularDatasets() {
    return popularDatasets;
  }

}
