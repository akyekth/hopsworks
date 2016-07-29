/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.hopsworks.gvod.io.resources.items;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class ManifestJson {
    
    private String datasetName;
    
    private String datasetDescription;
    
    private String creatorEmail;
    
    private String creatorDate;
    
    private boolean kafkaSupport;
    
    private List<FileInfo> fileInfos;
    
    private List<String> metaDataJsons;

    public ManifestJson() {
    }

    public ManifestJson(String datasetName, String datasetDescription, String creatorEmail, String creatorDate, boolean kafkaSupport, List<FileInfo> fileInfos, List<String> metaDataJsons) {
        this.datasetName = datasetName;
        this.datasetDescription = datasetDescription;
        this.creatorEmail = creatorEmail;
        this.creatorDate = creatorDate;
        this.kafkaSupport = kafkaSupport;
        this.fileInfos = fileInfos;
        this.metaDataJsons = metaDataJsons;
    } 

    public List<FileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public List<String> getMetaDataJsons() {
        return metaDataJsons;
    }

    public void setMetaDataJsons(List<String> metaDataJsons) {
        this.metaDataJsons = metaDataJsons;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetDescription() {
        return datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorDate() {
        return creatorDate;
    }

    public void setCreatorDate(String creatorDate) {
        this.creatorDate = creatorDate;
    }

    public boolean isKafkaSupport() {
        return kafkaSupport;
    }

    public void setKafkaSupport(boolean kafkaSupport) {
        this.kafkaSupport = kafkaSupport;
    }
    
    
    
    
    
    
}