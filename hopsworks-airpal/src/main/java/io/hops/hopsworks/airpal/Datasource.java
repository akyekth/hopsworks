
package io.hops.hopsworks.airpal;

//import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.DatabaseConfiguration;
import java.net.URI;
import java.net.URISyntaxException;

public class Datasource {//implements DatabaseConfiguration {

//  private static URI dburi;
//  private static String uname;
//  private static String dbpwd;
//  private static DatabaseConfiguration databaseConfiguration;
//
//  public static DatabaseConfiguration create(URI uri, String uname, String pwd) throws URISyntaxException {
//    Datasource.dburi = uri;
//    Datasource.uname = uname;
//    Datasource.dbpwd = pwd;
//
//    DatabaseConfiguration databaseConfiguration = null;
//
//    databaseConfiguration = new DatabaseConfiguration() {
//      DataSourceFactory dataSourceFactory;
//
//      @Override
//      public DataSourceFactory getDataSourceFactory(Configuration configuration) {
//        if (dataSourceFactory != null) {
//          return dataSourceFactory;
//        }
//        DataSourceFactory dsf = new DataSourceFactory();
//        dsf.setUser(uname);
//        dsf.setPassword(dbpwd);
//        dsf.setUrl(dburi.toString());
//        dsf.setDriverClass("com.mysql.jdbc.Driver");
//        dataSourceFactory = dsf;
//        return dsf;
//      }
//    };
//
//    return databaseConfiguration;
//  }
//
//  @Override
//  public DataSourceFactory getDataSourceFactory(Configuration configuration) {
//    //LOGGER.info("Getting DataSourceFactory");
//    if (databaseConfiguration == null) {
//      throw new IllegalStateException("You must first call DatabaseConfiguration.create(dbUrl)");
//    }
//    return (DataSourceFactory) databaseConfiguration.getDataSourceFactory(null);
//  }

}
