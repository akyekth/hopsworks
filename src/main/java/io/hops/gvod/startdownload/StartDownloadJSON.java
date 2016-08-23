/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.startdownload;

import io.hops.gvod.resources.HDFSEndpoint;
import io.hops.gvod.resources.HDFSResource;
import io.hops.gvod.resources.items.AddressJSON;
import io.hops.gvod.resources.items.TorrentId;
import java.util.List;

/**
 *
 * @author jsvhqr
 */
public class StartDownloadJSON {
    
    
    private TorrentId torrentId;
    private String torrentName;
    private HDFSResource manifestHDFSResource;
    private List<AddressJSON> partners;
    private HDFSEndpoint hdfsEndpoint;

    public StartDownloadJSON(TorrentId torrentId, String torrentName, HDFSResource manifestHDFSResource, List<AddressJSON> partners, HDFSEndpoint hdfsEndpoint) {
        this.torrentId = torrentId;
        this.torrentName = torrentName;
        this.manifestHDFSResource = manifestHDFSResource;
        this.partners = partners;
        this.hdfsEndpoint = hdfsEndpoint;
    }

    
    
    public StartDownloadJSON() {
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

    public List<AddressJSON> getPartners() {
        return partners;
    }

    public void setPartners(List<AddressJSON> partners) {
        this.partners = partners;
    }

    public HDFSEndpoint getHdfsEndpoint() {
        return hdfsEndpoint;
    }

    public void setHdfsEndpoint(HDFSEndpoint hdfsEndpoint) {
        this.hdfsEndpoint = hdfsEndpoint;
    }
    
    
    
}
