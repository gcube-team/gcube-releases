package org.gcube.dataanalysis.dataminer.poolmanager.ansible.model;

import java.util.Collection;
import java.util.Vector;

public class HostGroup {

  private String name;

  private Collection<AnsibleHost> hosts;

  public HostGroup(String name) {
    this.name = name;
    this.hosts = new Vector<>();
  }

  public void addHost(AnsibleHost h) {
    this.hosts.add(h);
  }

  public String getName() {
    return this.name;
  }

  public Collection<AnsibleHost> getHosts() {
    return new Vector<>(this.hosts);
  }

}
