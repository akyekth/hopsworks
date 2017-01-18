package io.hops.hopsworks.common.kafka;

import io.hops.hopsworks.common.dao.certificates.CertsFacade;
import io.hops.hopsworks.common.dao.certificates.UserCerts;
import io.hops.hopsworks.common.dao.project.Project;
import io.hops.hopsworks.common.util.Settings;
import java.io.File;
import java.io.FileOutputStream;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class KafkaController {

  @EJB
  private CertsFacade userCerts;

  public String getKafkaCertPaths(Project project) {
    UserCerts userCert = userCerts.findUserCert(project.getName(), project.
            getOwner().getUsername());
    //Check if the user certificate was actually retrieved
    if (userCert.getUserCert() != null
            && userCert.getUserCert().length > 0
            && userCert.getUserKey() != null
            && userCert.getUserKey().length > 0) {

      File certDir = new File(Settings.KAFKA_TMP_CERT_STORE_LOCAL);

      if (!certDir.exists()) {
        try {
          certDir.mkdir();
        } catch (Exception ex) {

        }
      }
      try {
        FileOutputStream fos = new FileOutputStream(
                Settings.KAFKA_TMP_CERT_STORE_LOCAL + "/keystore.jks");
        fos.write(userCert.getUserCert());
        fos.close();

        fos = new FileOutputStream(Settings.KAFKA_TMP_CERT_STORE_LOCAL
                + "/truststore.jks");
        fos.write(userCert.getUserKey());
        fos.close();

      } catch (Exception e) {

      }

    } else {
      return null;
    }

    return Settings.KAFKA_TMP_CERT_STORE_LOCAL;
  }

}
