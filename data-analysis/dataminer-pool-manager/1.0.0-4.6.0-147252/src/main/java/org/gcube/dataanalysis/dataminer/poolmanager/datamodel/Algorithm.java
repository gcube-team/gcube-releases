package org.gcube.dataanalysis.dataminer.poolmanager.datamodel;

import java.util.Collection;
import java.util.Vector;


public class Algorithm  {

	
	
	
  private String name;

  private String description;

  private String category;
  
  private String clazz;
  
  private String algorithmType;
  
  private String skipJava;
  
  private String packageURL;
  
  private Collection<Action> actions;

  private Collection<Dependency> dependencies;

  public Algorithm() {
    this.actions = new Vector<>();
    this.dependencies = new Vector<>();
    Dependency p = new Dependency();
  }
  
  public void addDependency(Dependency dep) {
    this.dependencies.add(dep);
  }

  public void addAction(Action action) {
    this.actions.add(action);
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

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Collection<Action> getActions() {
    return actions;
  }

  public Collection<Dependency> getDependencies() {
    return dependencies;
  }
  
  public void setDependencies(Collection<Dependency> deps) {
    this.dependencies = deps;
  }
  
  public String toString() {
    String out = "Algorithm: " + this.getName()+"\n";
    out+="  Class Name: " + this.getClazz()+"\n";
    out+="  Description: " + this.getDescription()+"\n";
    out+="  Dependencies: " + this.getDependencies()+"\n";
    return out;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public String getPackageURL() {
    return packageURL;
  }

  public void setPackageURL(String packageURL) {
    this.packageURL = packageURL;
  }

  public String getAlgorithmType() {
    return algorithmType;
  }

  public void setAlgorithmType(String algorithmType) {
    this.algorithmType = algorithmType;
  }

  public String getSkipJava() {
    return skipJava;
  }

  public void setSkipJava(String skipJava) {
    this.skipJava = skipJava;
  }



}
