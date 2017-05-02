package org.gcube.dataanalysis.dataminer.poolmanager.datamodel;

public class Host {

  private String name;
  
  private Domain domain;

  public Host() {
  }

  public String getFullyQualifiedName() {
    if(this.domain!=null && this.domain.getName()!=null)
      return this.getName()+"."+this.getDomain().getName();
    else
      return this.getName();
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Domain getDomain() {
    return domain;
  }

  public void setDomain(Domain domain) {
    this.domain = domain;
  }
  
//  public String toString() {
//    return this.name + "@" + this.domain;
//  }
  
public String toString() {
return this.name;
}

}
