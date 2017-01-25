package io.hops.hopsworks.common.gvod.resources;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddressJSON {

  private String ip;
  private int port;
  private int id;

  public AddressJSON() {

  }

  public AddressJSON(String ip, int port, int id) {
    this.ip = ip;
    this.port = port;
    this.id = id;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public int getId() {
    return id;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "AddressJSON{" + "ip=" + ip + ", port=" + port + ", id=" + id + '}';
  }

}
