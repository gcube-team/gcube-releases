package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WfRoleDetails implements Serializable {
  private String id;
  private String displayName;
  
  public WfRoleDetails() {
    new WfRoleDetails("0", "");
  }

  public WfRoleDetails(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; } 
}
