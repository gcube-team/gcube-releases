package org.gcube.dataanalysis.dataminer.poolmanager.ansible.model;

import java.util.List;
import java.util.Vector;

public class Playbook {

  private String hostGroupName;
  
  private List<String> roles;
  
  private String remote_user;
  
  public Playbook() {
    this.roles = new Vector<>();
  }
  
  public void addRole(String role) {
    roles.add(role);
  }
  
  public void applyTo(String hostGroupName) {
    this.hostGroupName = hostGroupName;
  }

  public String getHostGroupName() {
    return hostGroupName;
  }

  public List<String> getRoles() {
    return new Vector<>(roles);
  }

public String getRemote_user() {
	return remote_user;
}

public void setRemote_user(String remote_user) {
	this.remote_user = remote_user;
}

public void setHostGroupName(String hostGroupName) {
	this.hostGroupName = hostGroupName;
}

public void setRoles(List<String> roles) {
	this.roles = roles;
}
  
}
