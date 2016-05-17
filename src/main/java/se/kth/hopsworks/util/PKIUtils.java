package se.kth.hopsworks.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;


public class PKIUtils {

    final static String PASSWORD = "changeit";
    
    final static Logger logger = Logger.getLogger(PKIUtils.class.getName());

    public static String signWithServerCertificate(String csr) throws IOException, InterruptedException {

        if(verifyCSR(csr)) {
           return signCSR(csr);
        } 
        return null;
    }
    
    private static boolean verifyCSR(String csr) {
        //TODO
        return false;
    }

    //Attention, expects .csr as a formatted String!
    private static String signCSR(String csr) throws IOException, InterruptedException{
        File csrFile = File.createTempFile(System.getProperty("java.io.tmpdir"), ".csr");
        FileUtils.writeStringToFile(csrFile, csr);

        File generatedCertFile = File.createTempFile(System.getProperty("java.io.tmpdir"), ".cert.pem");
        
        logger.info("Signing CSR...");
        List<String> cmds = new ArrayList<>();
        
        cmds.add("openssl");
        cmds.add("ca");
        cmds.add("-batch");
        cmds.add("-config");
        cmds.add(Settings.CA_DIR +"openssl.cnf");
        cmds.add("-passin");
        cmds.add("pass:"+PASSWORD);
        cmds.add("-extensions");
        cmds.add("usr_cert");
        cmds.add("-days");
        cmds.add("365");
        cmds.add("-notext");
        cmds.add("-md");
        cmds.add("sha256");
        cmds.add("-in");
        cmds.add(csrFile.getAbsolutePath());
        cmds.add("-out");
        cmds.add(generatedCertFile.getAbsolutePath());
        
        Process process = new ProcessBuilder(cmds).directory(new File("/usr/bin/")).
                redirectErrorStream(true).start();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                process.getInputStream(), Charset.forName("UTF8")));
        String line;
        while ((line = br.readLine()) != null) {
            logger.info(line);
        }
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("Failed to sign certificate.");
        }
        logger.info("Singned certificate.");
        return FileUtils.readFileToString(generatedCertFile);
    }
}
