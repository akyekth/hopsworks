/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.hops.gvod.io.contents;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */

@XmlRootElement
public class ContentsRequestDTO {
    
    private int projectId;

    public ContentsRequestDTO(int projectId) {
        this.projectId = projectId;
    }

    public ContentsRequestDTO() {
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
    
    
    
}
