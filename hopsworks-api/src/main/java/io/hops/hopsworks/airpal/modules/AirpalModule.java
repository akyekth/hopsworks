package io.hops.hopsworks.airpal.modules;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.model.EncryptionMaterialsProvider;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.Provider;
import io.airlift.http.client.HttpClient;
import io.airlift.units.DataSize;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
//import java.util.Collections;

import com.google.common.base.Strings;
import com.google.inject.name.Names;
import io.airlift.configuration.ConfigDefaults;
import io.airlift.configuration.ConfigurationFactory;
import io.airlift.http.client.HttpClientConfig;
import io.airlift.units.Duration;
import org.skife.jdbi.v2.DBI;
import javax.inject.Named;
import java.net.URI;//
import java.util.concurrent.TimeUnit;
import io.hops.hopsworks.airpal.airlift.http.client.ForQueryInfoClient;
import java.util.Collections;

import io.hops.hopsworks.api.airpal.resources.SampleResource;
import io.hops.hopsworks.api.airpal.resources.TablesResource;
import io.hops.hopsworks.airpal.api.output.PersistentJobOutputFactory;
import io.hops.hopsworks.airpal.api.output.builders.OutputBuilderFactory;
import io.hops.hopsworks.airpal.api.output.persistors.CSVPersistorFactory;
import io.hops.hopsworks.airpal.api.output.persistors.PersistorFactory;
import io.hops.hopsworks.airpal.core.AirpalUserFactory;
//import io.hops.hopsworks.airpal.core.execution.ExecutionClient;
//import io.hops.hopsworks.airpal.core.health.PrestoHealthCheck;
import io.hops.hopsworks.airpal.core.store.files.ExpiringFileStore;
import io.hops.hopsworks.airpal.core.store.history.JobHistoryStore;
import io.hops.hopsworks.airpal.core.store.history.JobHistoryStoreDAO;
import io.hops.hopsworks.airpal.core.store.jobs.ActiveJobsStore;
import io.hops.hopsworks.airpal.core.store.jobs.InMemoryActiveJobsStore;
import io.hops.hopsworks.airpal.core.store.queries.QueryStore;
import io.hops.hopsworks.airpal.core.store.queries.QueryStoreDAO;
import io.hops.hopsworks.airpal.core.store.usage.CachingUsageStore;
import io.hops.hopsworks.airpal.core.store.usage.SQLUsageStore;
import io.hops.hopsworks.airpal.core.store.usage.UsageStore;
import io.hops.hopsworks.airpal.presto.ClientSessionFactory;
import io.hops.hopsworks.airpal.presto.ForQueryRunner;
//import io.hops.hopsworks.airpal.presto.QueryInfoClient;
//import io.hops.hopsworks.airpal.presto.QueryInfoClient.BasicQueryInfo;
import io.hops.hopsworks.airpal.presto.metadata.ColumnCache;
import io.hops.hopsworks.airpal.presto.metadata.PreviewTableCache;
import io.hops.hopsworks.airpal.presto.metadata.SchemaCache;
import io.hops.hopsworks.airpal.sql.beans.TableRow;
import io.hops.hopsworks.airpal.sql.jdbi.QueryStoreMapper;
import io.hops.hopsworks.airpal.sql.jdbi.URIArgumentFactory;
import io.hops.hopsworks.airpal.sql.jdbi.UUIDArgumentFactory;
//@Slf4j
import io.hops.hopsworks.airpal.AirpalConfiguration;
//import static io.hops.hopsworks.airpal.presto.QueryRunner.QueryRunnerFactory;
import static io.airlift.http.client.HttpClientBinder.httpClientBinder;
import io.hops.hopsworks.airpal.presto.QueryRunner;
import io.hops.hopsworks.airpal.sql.DbType;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
//import static io.airlift.json.JsonCodec.jsonCodec;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
public class AirpalModule implements Module {

  private static final ConfigDefaults<HttpClientConfig> HTTP_CLIENT_CONFIG_DEFAULTS = d -> new HttpClientConfig()
    .setConnectTimeout(new Duration(10, TimeUnit.SECONDS));

  private AirpalConfiguration config = new AirpalConfiguration();
  
  
  @Override
  public void configure(Binder binder) {
//   
//    binder.bind(ExecuteResource.class).in(Scopes.SINGLETON);
//    binder.bind(QueryResource.class).in(Scopes.SINGLETON);
//    binder.bind(HealthResource.class).in(Scopes.SINGLETON);
//    binder.bind(PingResource.class).in(Scopes.SINGLETON);
//    binder.bind(SessionResource.class).in(Scopes.SINGLETON);
//    binder.bind(SSEEventSourceServlet.class).in(Scopes.SINGLETON);
//    binder.bind(FilesResource.class).in(Scopes.SINGLETON);
//    binder.bind(ResultsPreviewResource.class).in(Scopes.SINGLETON);
//    binder.bind(S3FilesResource.class).in(Scopes.SINGLETON);
//    binder.bind(ObjectifyFilter.class).in(Singleton.class);
    binder.bind(TablesResource.class).in(Singleton.class);
    binder.bind(SampleResource.class).in(Singleton.class);
    httpClientBinder(binder).bindHttpClient("query-info", ForQueryInfoClient.class)
      .withConfigDefaults(HTTP_CLIENT_CONFIG_DEFAULTS);

    httpClientBinder(binder).bindHttpClient("query-runner", ForQueryRunner.class)
      .withConfigDefaults(HTTP_CLIENT_CONFIG_DEFAULTS);
    binder.bind(String.class).annotatedWith(Names.named("createTableDestinationSchema")).
      toInstance("airpal");
    binder.bind(String.class).annotatedWith(Names.named("s3Bucket")).
      toInstance(Strings.nullToEmpty(config.getS3Bucket()));

    binder.bind(PersistentJobOutputFactory.class).in(Singleton.class);

    binder.bind(JobHistoryStore.class).to(JobHistoryStoreDAO.class).in(Singleton.class);

  }

//  @Singleton
//  @Provides
//  public ConfigurationFactory provideConfigurationFactory() {
//    return new ConfigurationFactory(Collections.<String, String>emptyMap());
//  }
  /**
   * Providing the Objectify object via a Provider to ensure transaction management.
   */
//  @Provides
//  @Singleton
//  Objectify provideObjectify() {
//    return ofy();
//  }
  @Singleton
  @Provides
  public DbType provideDbType() {
    String driverClass = "com.mysql.jdbc.Driver";//config.getDataSourceFactory().getDriverClass();
    if (driverClass.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
      return DbType.MySQL;
    } else if (driverClass.equalsIgnoreCase("org.h2.Driver")) {
      return DbType.H2;
    } else {
      return DbType.Default;
    }
  }

  @Singleton
  @Provides
  public DBI provideDBI(ObjectMapper objectMapper)
    throws ClassNotFoundException {
    //final DBIFactory factory = new DBIFactory();
    //final DBI dbi = factory.build(environment, config.getDataSourceFactory(), provideDbType().name());

    final DBI dbi = new DBI("jdbc:mysql://bbc2.sics.se:24013/airpal",
      "kthfs", "kthfs");
    dbi.registerMapper(new TableRow.TableRowMapper(objectMapper));
    dbi.registerMapper(new QueryStoreMapper(objectMapper));
    dbi.registerArgumentFactory(new UUIDArgumentFactory());
    dbi.registerArgumentFactory(new URIArgumentFactory());

    return dbi;
  }

  @Singleton
  @Provides
  public ConfigurationFactory provideConfigurationFactory() {
    return new ConfigurationFactory(Collections.<String, String>emptyMap());
  }

  @Named("coordinator-uri")
  @Provides
  public URI providePrestoCoordinatorURI() throws URISyntaxException {
    //if(== null)
    return config.getPrestoCoordinator();
  }

  @Singleton
  @Named("default-catalog")
  @Provides
  public String provideDefaultCatalog() {
    return config.getPrestoCatalog();
  }

  @Provides
  @Singleton
  public ClientSessionFactory provideClientSessionFactory(@Named("coordinator-uri") Provider<URI> uriProvider) {
    return new ClientSessionFactory(uriProvider,
      config.getPrestoUser(),
      config.getPrestoSource(),
      config.getPrestoCatalog(),
      config.getPrestoSchema(),
      config.isPrestoDebug(),
      null);
  }

  @Provides
  public QueryRunner.QueryRunnerFactory provideQueryRunner(ClientSessionFactory sessionFactory,
    @ForQueryRunner HttpClient httpClient) {
    return new QueryRunner.QueryRunnerFactory(sessionFactory, httpClient);
  }

  /**
   *
   * @param httpClient
   * @return
   */
//  @Provides
//  public QueryInfoClient provideQueryInfoClient(@ForQueryInfoClient HttpClient httpClient) {
//    return new QueryInfoClient(httpClient, jsonCodec(BasicQueryInfo.class));
//  }
  @Singleton
  @Provides
  public SchemaCache provideSchemaCache(QueryRunner.QueryRunnerFactory queryRunnerFactory,
    @Named("presto") ExecutorService executorService) {
    final SchemaCache cache = new SchemaCache(queryRunnerFactory, executorService);
    cache.populateCache(config.getPrestoCatalog());

    return cache;
  }

  @Singleton
  @Provides
  public ColumnCache provideColumnCache(QueryRunner.QueryRunnerFactory queryRunnerFactory,
    @Named("presto") ExecutorService executorService) {
    return new ColumnCache(queryRunnerFactory,
      new Duration(5, TimeUnit.MINUTES),
      new Duration(60, TimeUnit.MINUTES),
      executorService);
  }

  @Singleton
  @Provides
  public PreviewTableCache providePreviewTableCache(QueryRunner.QueryRunnerFactory queryRunnerFactory,
    @Named("presto") ExecutorService executorService) {
    return new PreviewTableCache(queryRunnerFactory,
      new Duration(20, TimeUnit.MINUTES),
      executorService,
      100);
  }

  @Singleton
  @Named("event-bus")
  @Provides
  public ExecutorService provideEventBusExecutorService() {
    return Executors.newCachedThreadPool(SchemaCache.daemonThreadsNamed("event-bus-%d"));
  }

  @Singleton
  @Named("presto")
  @Provides
  public ExecutorService provideCompleterExecutorService() {
    return Executors.newCachedThreadPool(SchemaCache.daemonThreadsNamed("presto-%d"));
  }

  @Singleton
  @Named("hive")
  @Provides
  public ScheduledExecutorService provideTableCacheUpdater() {
    return Executors.newSingleThreadScheduledExecutor();
  }

  @Singleton
  @Named("sse")
  @Provides
  public ExecutorService provideSSEExecutorService() {
    return Executors.newCachedThreadPool(SchemaCache.daemonThreadsNamed("sse-%d"));
  }

  @Singleton
  @Provides
  public EventBus provideEventBus(@Named("event-bus") ExecutorService executor) {
    return new AsyncEventBus(executor);
  }

  @Provides
  @Nullable
  public AWSCredentials provideAWSCredentials() {
    if ((config.getS3AccessKey() == null) || (config.getS3SecretKey() == null)) {
      return null;
    } else {
      return new BasicAWSCredentials(config.getS3AccessKey(),
        config.getS3SecretKey());
    }
  }

  @Singleton
  @Provides
  @Nullable
  public AmazonS3 provideAmazonS3Client(@Nullable AWSCredentials awsCredentials,
    @Nullable EncryptionMaterialsProvider encryptionMaterialsProvider) {
    if (awsCredentials == null) {
      if (encryptionMaterialsProvider == null) {
        return new AmazonS3Client(new InstanceProfileCredentialsProvider());
      } else {
        return new AmazonS3EncryptionClient(new InstanceProfileCredentialsProvider(), encryptionMaterialsProvider);
      }
    }

    if (encryptionMaterialsProvider == null) {
      return new AmazonS3Client(awsCredentials);
    } else {
      return new AmazonS3EncryptionClient(awsCredentials, encryptionMaterialsProvider);
    }
  }

  @Nullable
  @Singleton
  @Provides
  private EncryptionMaterialsProvider provideEncryptionMaterialsProvider() {
    String empClassName = config.getS3EncryptionMaterialsProvider();
    if (empClassName != null) {
      try {
        Class<?> empClass = Class.forName(empClassName);
        Object instance = empClass.newInstance();
        if (instance instanceof EncryptionMaterialsProvider) {
          return (EncryptionMaterialsProvider) instance;
        } else {
          throw new IllegalArgumentException("Class " + empClassName + " must implement EncryptionMaterialsProvider");
        }
      } catch (Exception x) {
        throw new RuntimeException("Unable to initialize EncryptionMaterialsProvider class " + empClassName + ": " + x,
          x);
      }
    }

    return null;
  }

  @Singleton
  @Provides
  public UsageStore provideUsageCache(DBI dbi) {
    UsageStore delegate = new SQLUsageStore(config.getUsageWindow(), dbi, provideDbType());

    return new CachingUsageStore(delegate, org.joda.time.Duration.standardMinutes(6));
  }

  @Provides
  public QueryStore provideQueryStore(DBI dbi) {
    return dbi.onDemand(QueryStoreDAO.class);
  }

  @Provides
  @Singleton
  public AirpalUserFactory provideAirpalUserFactory() {
    return new AirpalUserFactory(config.getPrestoSchema(), org.joda.time.Duration.standardMinutes(15), "default");
  }

  @Provides
  @Singleton
  public ActiveJobsStore provideActiveJobsStore() {
    return new InMemoryActiveJobsStore();
  }

  @Provides
  @Singleton
  public ExpiringFileStore provideExpiringFileStore() {
    return new ExpiringFileStore(new DataSize(100, DataSize.Unit.MEGABYTE));
  }

  @Provides
  @Singleton
  public CSVPersistorFactory provideCSVPersistorFactory(ExpiringFileStore fileStore, AmazonS3 s3Client, @Named(
    "s3Bucket") String s3Bucket) {
    return new CSVPersistorFactory(config.isUseS3(), s3Client, s3Bucket, fileStore, config.isCompressedOutput());
  }

  @Provides
  @Singleton
  public PersistorFactory providePersistorFactory(CSVPersistorFactory csvPersistorFactory) {
    return new PersistorFactory(csvPersistorFactory);
  }

  @Provides
  @Singleton
  public OutputBuilderFactory provideOutputBuilderFactory() {
    long maxFileSizeInBytes = Math.round(Math.floor(config.getMaxOutputSize().getValue(DataSize.Unit.BYTE)));
    return new OutputBuilderFactory(maxFileSizeInBytes, config.isCompressedOutput());
  }

 

}
