package org.gcube.dataanalysis.dataminer.poolmanager.datamodel;

public class Dependency {

  private String name;

  private String type;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String toString() {
    return this.type+":"+this.name;
  }

}
