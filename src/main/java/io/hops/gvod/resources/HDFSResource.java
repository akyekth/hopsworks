package io.hops.gvod.resources;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jsvhqr
 */
@XmlRootElement
public class HDFSResource {

  private String dirPath;
  private String fileName;

  public HDFSResource() {
  }

  public HDFSResource(String dirPath, String fileName) {
    this.dirPath = dirPath;
    this.fileName = fileName;
  }

  public String getDirPath() {
    return dirPath;
  }

  public void setDirPath(String dirPath) {
    this.dirPath = dirPath;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

}
