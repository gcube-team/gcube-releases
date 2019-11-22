package org.gcube.dataanalysis.dataminer.poolmanager.ansible.model;

import java.util.Collection;
import java.util.Vector;

public class Inventory {

  private Collection<HostGroup> groups;

  public Inventory() {
    this.groups = new Vector<>();
  }

  public void addGroup(HostGroup group) {
    this.groups.add(group);
  }

  public void addHost(AnsibleHost h, String groupName) {
    this.getGroup(groupName).addHost(h);
  }

  private HostGroup getGroup(String groupName) {
    for (HostGroup hg : this.groups) {
      if (groupName.equals(hg.getName())) {
        return hg;
      }
    }
    HostGroup hg = new HostGroup(groupName);
    this.groups.add(hg);
    return hg;
  }

  public Collection<HostGroup> getHostGroups() {
    return new Vector<>(this.groups);
  }

}
