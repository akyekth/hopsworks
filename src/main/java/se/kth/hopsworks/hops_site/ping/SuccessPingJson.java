/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.hops_site.ping;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import se.kth.hopsworks.hops_site.register.RegisteredClusters;

/**
 *
 * @author jsvhqr
 */
public class SuccessPingJson {
    
    private List<RegisteredClusters> clusters;
    public SuccessPingJson(){
        
    }
    
    @XmlElement(name = "clusters")
    public List<RegisteredClusters> getClusters() {
        return clusters;
    }

    public void setClusters(List<RegisteredClusters> clusters) {
        this.clusters = clusters;
    }
    
    
    
}
