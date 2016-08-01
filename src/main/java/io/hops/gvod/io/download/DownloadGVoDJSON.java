/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.io.download;

import io.hops.gvod.io.resources.HDFSEndpoint;
import io.hops.gvod.io.resources.KafkaEndpoint;
import io.hops.gvod.io.resources.items.ExtendedDetails;
import io.hops.gvod.io.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class DownloadGVoDJSON {

    private TorrentId torrentId;
    private KafkaEndpoint kafkaEndpoint;
    private HDFSEndpoint hdfsEndpoint;
    private ExtendedDetails extendedDetails;

    public DownloadGVoDJSON() {
    }
    
    public DownloadGVoDJSON(TorrentId torrentId, KafkaEndpoint kafkaEndpoint, HDFSEndpoint hdfsEndpoint, ExtendedDetails extendedDetails) {
        this.torrentId = torrentId;
        this.kafkaEndpoint = kafkaEndpoint;
        this.hdfsEndpoint = hdfsEndpoint;
        this.extendedDetails = extendedDetails;
    }

    public TorrentId getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(TorrentId torrentId) {
        this.torrentId = torrentId;
    }

    public KafkaEndpoint getKafkaEndpoint() {
        return kafkaEndpoint;
    }

    public void setKafkaEndpoint(KafkaEndpoint kafkaEndpoint) {
        this.kafkaEndpoint = kafkaEndpoint;
    }

    public ExtendedDetails getExtendedDetails() {
        return extendedDetails;
    }

    public void setExtendedDetails(ExtendedDetails extendedDetails) {
        this.extendedDetails = extendedDetails;
    }

    public HDFSEndpoint getHdfsEndpoint() {
        return hdfsEndpoint;
    }

    public void setHdfsEndpoint(HDFSEndpoint hdfsEndpoint) {
        this.hdfsEndpoint = hdfsEndpoint;
    }
    
    
    
    
}
