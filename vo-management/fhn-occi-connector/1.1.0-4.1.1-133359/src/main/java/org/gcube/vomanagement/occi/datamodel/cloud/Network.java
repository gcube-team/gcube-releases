package org.gcube.vomanagement.occi.datamodel.cloud;

import java.net.URI;

public class Network {

  private String id;
  private String description;
  private String name;
  private String status;
  private String allocation;
  private String gateway;
  private String address;
  private URI endpoint;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAllocation() {
    return allocation;
  }

  public void setAllocation(String allocation) {
    this.allocation = allocation;
  }

  public String getGateway() {
    return gateway;
  }

  public void setGateway(String gateway) {
    this.gateway = gateway;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public URI getEndpoint() {
    return this.endpoint;
  }

  public void setEndpoint(URI endpoint) {
    this.endpoint = endpoint;
  }

}
