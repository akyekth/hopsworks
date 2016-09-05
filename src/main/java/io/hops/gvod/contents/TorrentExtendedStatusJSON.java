/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.contents;

import io.hops.gvod.resources.items.TorrentId;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class TorrentExtendedStatusJSON {
    
    private TorrentId torrentId;
    private String torrentStatus;
    private int downloadSpeed;
    private double percentageCompleted;

    public TorrentExtendedStatusJSON(TorrentId torrentId, String torrentStatus, int downloadSpeed,
            double percentageCompleted) {
        this.torrentId = torrentId;
        this.torrentStatus = torrentStatus;
        this.downloadSpeed = downloadSpeed;
        this.percentageCompleted = percentageCompleted;
    }

    public TorrentExtendedStatusJSON() {
    }
    
    public String getTorrentStatus() {
        return torrentStatus;
    }

    public void setTorrentStatus(String torrentStatus) {
        this.torrentStatus = torrentStatus;
    }

    public int getDownloadSpeed() {
        return downloadSpeed;
    }

    public void setDownloadSpeed(int downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public TorrentId getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(TorrentId torrentId) {
        this.torrentId = torrentId;
    }

    public double getPercentageCompleted() {
        return percentageCompleted;
    }

    public void setPercentageCompleted(double percentageCompleted) {
        this.percentageCompleted = percentageCompleted;
    }
    
}
