/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.upload;

import io.hops.gvod.resources.HDFSEndpoint;
import io.hops.gvod.resources.HDFSResource;
import io.hops.gvod.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class UploadGVoDJSON {
    
    
    private TorrentId torrentId;
    private String torrentName;
    private HDFSResource manifestHDFSResource;
    private HDFSEndpoint hdfsEndpoint;

    public UploadGVoDJSON(TorrentId torrentId, String torrentName, HDFSResource manifestHDFSResource, HDFSEndpoint hdfsEndpoint) {
        this.torrentId = torrentId;
        this.torrentName = torrentName;
        this.manifestHDFSResource = manifestHDFSResource;
        this.hdfsEndpoint = hdfsEndpoint;
    }

    public UploadGVoDJSON() {
    }

    public String getTorrentName() {
        return torrentName;
    }

    public void setTorrentName(String torrentName) {
        this.torrentName = torrentName;
    }
    
    public TorrentId getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(TorrentId torrentId) {
        this.torrentId = torrentId;
    }

    public HDFSResource getManifestHDFSResource() {
        return manifestHDFSResource;
    }

    public void setManifestHDFSResource(HDFSResource manifestHDFSResource) {
        this.manifestHDFSResource = manifestHDFSResource;
    }

    public HDFSEndpoint getHdfsEndpoint() {
        return hdfsEndpoint;
    }

    public void setHdfsEndpoint(HDFSEndpoint hdfsEndpoint) {
        this.hdfsEndpoint = hdfsEndpoint;
    }
    
    
    
}
