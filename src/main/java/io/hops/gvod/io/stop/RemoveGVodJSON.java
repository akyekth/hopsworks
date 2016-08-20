/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.io.stop;

import javax.xml.bind.annotation.XmlRootElement;
import io.hops.gvod.io.resources.items.TorrentId;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class RemoveGVodJSON {
    
      private TorrentId torrentId;

    public RemoveGVodJSON() {
    }

    public RemoveGVodJSON(TorrentId torrentId) {
        this.torrentId = torrentId;
    }

    public TorrentId getTorrentId() {
        return torrentId;
    }

    public void setTorrentId(TorrentId torrentId) {
        this.torrentId = torrentId;
    }
      
      
    
    
}
