
package io.hops.hopsworks.api.airpal.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class AirpalConfiguration {
  private String DB_DRIVER;
  private String Db_pwd;
  private String Db_username;
  private String DB_URL;
  private URL prestoCoordinator;
  public String getPropvalues() throws IOException{
  Properties prop = new Properties();
	InputStream input = getClass().getResourceAsStream("/config.properties");
   if(input != null)
   {
    // load a properties file
		prop.load(input);
    DB_DRIVER = prop.getProperty(driverClass);
    
       
    } 
   return null;
  }

  
}
