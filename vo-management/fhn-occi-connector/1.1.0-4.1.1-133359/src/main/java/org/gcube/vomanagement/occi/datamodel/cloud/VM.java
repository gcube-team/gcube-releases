package org.gcube.vomanagement.occi.datamodel.cloud;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class VM {

  private String id;
  private String name;
  private int cores;
  private Long memory;
  private String status;
  private URI endpoint;
  private String provider;
  private String hostname;

  private List<VMStorage> storage;
  private List<VMNetwork> networks;

  public VM() {
    this.storage = new Vector<>();
    this.networks = new Vector<>();
  }

  public Collection<VMNetwork> getNetworks() {
    return new ArrayList<>(this.networks);
  }

  public void addNetwork(VMNetwork network) {
    this.networks.add(network);
  }

  public void addStorage(VMStorage storages) {
    this.storage.add(storages);
  }

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

  public int getCores() {
    return cores;
  }

  public void setCores(int cores) {
    this.cores = cores;
  }

  public Long getMemory() {
    return memory;
  }

  public void setMemory(Long memory) {
    this.memory = memory;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Return the size of all disks attached to this VM.
   * @return the size of all disks.
   */
  public Long getDiskSize() {
    Long size = 0L;
    for (VMStorage s : this.storage) {
      if (s.getStorage() != null && s.getStorage().getSize() != null) {
        size += s.getStorage().getSize();
      }
    }
    return size;
  }

  public URI getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(URI endpoint) {
    this.endpoint = endpoint;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public List<VMStorage> getStorage() {
    return storage;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

}
