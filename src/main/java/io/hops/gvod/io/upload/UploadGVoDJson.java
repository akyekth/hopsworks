/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.io.upload;

import io.hops.gvod.io.resources.HDFSEndpoint;
import io.hops.gvod.io.resources.HDFSResource;
import io.hops.gvod.io.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class UploadGVoDJson {
    
    
    private TorrentId torrentId;
    private HDFSResource manifestHDFSResource;
    private HDFSEndpoint hdfsEndpoint;

    public UploadGVoDJson(TorrentId torrentId, HDFSResource manifestHDFSResource, HDFSEndpoint hdfsEndpoint) {
        this.torrentId = torrentId;
        this.manifestHDFSResource = manifestHDFSResource;
        this.hdfsEndpoint = hdfsEndpoint;
    }

    public UploadGVoDJson() {
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
