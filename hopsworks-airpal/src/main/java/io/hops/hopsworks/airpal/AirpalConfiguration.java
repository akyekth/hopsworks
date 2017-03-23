package io.hops.hopsworks.airpal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.airlift.units.DataSize;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
//import io.dropwizard.flyway.FlywayFactory;
import io.dropwizard.util.Duration;
//import java.net.MalformedURLException;
import lombok.Getter;
import lombok.Setter;
import org.secnod.dropwizard.shiro.ShiroConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirpalConfiguration  {

//  private final static String DBUNAME = "kthfs";
//  private final static String DBPWD = "kthfs";
//  private final static URI DBURI;
//
//  static {
//    java.net.URI temp = null;
//    try {
//      temp = new java.net.URI("jdbc:mysql://localhost:3306/airpal");
//    } catch (URISyntaxException ex) {
//      Logger.getLogger(AirpalConfiguration.class.getName()).log(Level.SEVERE, null, ex);
//    }
//    DBURI = temp;
//  }
  @Getter
  @Setter
  @JsonProperty
  private URI prestoCoordinator = null;

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private String prestoUser = "airpalChangeMe";

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private String prestoSource = "airpal";

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private String prestoCatalog = "hive";

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private String prestoSchema = "default";

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private boolean prestoDebug = false;

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private Duration usageWindow = Duration.hours(6);

  @Getter
  @Setter
  @JsonProperty
  private String s3SecretKey;

  @Getter
  @Setter
  @JsonProperty
  private String s3AccessKey;

  @Getter
  @Setter
  @JsonProperty
  private String s3Bucket;

  @Getter
  @Setter
  @JsonProperty
  private String s3EncryptionMaterialsProvider;

  @Getter
  @Setter
  @JsonProperty
  private String createTableDestinationSchema = "airpal";

  @Getter
  @Setter
  private DataSize bufferSize = DataSize.valueOf("512kB");

  @Getter
  @Setter
  @JsonProperty
  @NotNull
  private DataSize maxOutputSize = DataSize.valueOf("1GB");

  @Valid
  @NotNull
  private DataSourceFactory dataSourceFactory = new DataSourceFactory();

  @Getter
  @Setter
  @Valid
  @JsonProperty
  @NotNull
  private ShiroConfiguration shiro;

  @Getter
  @Setter
  @Valid
  @JsonProperty
  @NotNull
  private boolean useS3 = false;

  @Getter
  @Setter
  @Valid
  @JsonProperty
  @NotNull
  private boolean compressedOutput = false;

//  @JsonProperty("dataSourceFactory")
//  public DataSourceFactory getDataSourceFactory() throws URISyntaxException {
//
//    DatabaseConfiguration databaseConfiguration = Datasource.create(DBURI, DBUNAME, DBPWD);
//    dataSourceFactory = (DataSourceFactory) databaseConfiguration.getDataSourceFactory(null);
//    return dataSourceFactory;
//  }
//
//  @JsonProperty("dataSourceFactory")
//  public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
//    this.dataSourceFactory = dataSourceFactory;
//  }
}
