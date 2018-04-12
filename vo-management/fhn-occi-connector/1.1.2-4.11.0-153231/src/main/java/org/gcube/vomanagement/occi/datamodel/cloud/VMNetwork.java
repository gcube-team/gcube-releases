package org.gcube.vomanagement.occi.datamodel.cloud;

public class VMNetwork {

  private String id;
  private Network network;
  private String name;
  private String address;
  private String iface;
  private String mac;
  private String status;
  private String endpoint;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Network getNetwork() {
    return network;
  }

  public void setNetwork(Network network) {
    this.network = network;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getIface() {
    return iface;
  }

  public void setInterface(String iface) {
    this.iface = iface;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

}
