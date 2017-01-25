package io.hops.hopsworks.common.gvod;

import io.hops.hopsworks.common.gvod.hopssite.IdentificationJSON;
import io.hops.hopsworks.common.gvod.hopssite.PingedJSON;
import io.hops.hopsworks.common.gvod.hopssite.PopularDatasetJSON;
import io.hops.hopsworks.common.gvod.hopssite.RegisterJSON;
import io.hops.hopsworks.common.gvod.hopssite.RegisteredClusterJSON;
import io.hops.hopsworks.common.gvod.hopssite.RegisteredJSON;
import io.hops.hopsworks.common.gvod.resources.AddressJSON;
import io.hops.hopsworks.common.gvod.resources.ManifestJSON;
import io.hops.hopsworks.common.util.Settings;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.mail.Session;

@Startup
@Singleton
public class ManageGlobalClusterParticipation {

  private final static Logger logger = Logger.getLogger(
          ManageGlobalClusterParticipation.class.
          getName());
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
    logger.log(Level.INFO, "Trying to register to: {0}", webTarget.getUri());
    RegisterJSON reg = new RegisterJSON(searchEndpoint, gvodEndpoint, email,
            cert);
    Response r = resource.request().accept(MediaType.APPLICATION_JSON).post(
            Entity.json(reg));
    logger.log(Level.INFO, "Trying to register with payload: {0}", reg);
    if (r != null && r.getStatus() == 200) {
      logger.log(Level.INFO, "Registered with: {0} ", webTarget.getUri());
      return r.readEntity(RegisteredJSON.class).getClusterId();
    } else {
      if (r != null){
        logger.log(Level.INFO, "Recived response: {0}", r.getStatus());
      }
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
