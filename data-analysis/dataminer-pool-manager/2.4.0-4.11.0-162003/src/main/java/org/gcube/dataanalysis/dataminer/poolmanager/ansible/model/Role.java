package org.gcube.dataanalysis.dataminer.poolmanager.ansible.model;

import java.util.Collection;
import java.util.Vector;

public class Role {

  /**
   * The name of the role
   */
  private String name;
  
  private Collection<RoleFile> tasks;

  private Collection<RoleFile> meta;
  
  public Role() {
    this.tasks = new Vector<>();
    this.meta = new Vector<>();
  }

  public Role(String name) {
    this();
    this.name = name;
  }
  
  public void addTaskFile(RoleFile tf) {
    this.tasks.add(tf);
  }

  public void addMeta(RoleFile tf) {
    this.meta.add(tf);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public Collection<RoleFile> getTaskFiles() {
    return new Vector<>(this.tasks);
  }
  
  public Collection<RoleFile> getMeta() {
    return new Vector<>(this.meta);
  }
  
}
