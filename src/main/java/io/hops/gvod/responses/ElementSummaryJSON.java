/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.responses;

import io.hops.gvod.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class ElementSummaryJSON {
    
    private String fileName;
    private TorrentId torrentId;
    private String torrentStatus;
    
    public ElementSummaryJSON(String name, TorrentId torrentId, String status) {
        this.fileName = name;
        this.torrentId = torrentId;
        this.torrentStatus = status;
    }

    public ElementSummaryJSON() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TorrentId getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(TorrentId torrentId) {
        this.torrentId = torrentId;
    }

    public String getTorrentStatus() {
        return torrentStatus;
    }

    public void setTorrentStatus(String torrentStatus) {
        this.torrentStatus = torrentStatus;
    }

    
}
