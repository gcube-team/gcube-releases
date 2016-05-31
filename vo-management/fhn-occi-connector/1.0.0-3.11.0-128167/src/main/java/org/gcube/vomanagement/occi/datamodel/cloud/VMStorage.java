package org.gcube.vomanagement.occi.datamodel.cloud;

public class VMStorage {

  private String id;
  private String name;
  private String deviceId;
  private String status;
  private String endpoint;

  private Storage storage;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Storage getStorage() {
    return storage;
  }

  public void setStorage(Storage storage) {
    this.storage = storage;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
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
