/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.hopssite.io.register;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class RegisteredJson {
    
    private String clusterId;
    
    public RegisteredJson(){
        
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }
    
}
