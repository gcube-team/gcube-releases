package org.gcube.dataanalysis.dataminer.poolmanager.datamodel;

import java.util.Collection;
import java.util.Vector;

public class AlgorithmSet {

  private String name;

  private Collection<Algorithm> algorithms;

  public AlgorithmSet() {
    this.algorithms = new Vector<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection<Algorithm> getAlgorithms() {
    return new Vector<>(algorithms);
  }

  public void addAlgorithm(Algorithm algoritm) {
    this.algorithms.add(algoritm);
  }

  public Boolean hasAlgorithm(Algorithm algorithm) {
    for (Algorithm a : this.algorithms) {
      if (a.getName().equals(algorithm.getName())) {
        return true;
      }
    }
    return false;
  }
  
  public String toString() {
    String out = "ALGOSET: " + this.name + "\n";
    for(Algorithm a:this.algorithms) {
      out+=a+"\n";
    }
    return out;
  }

}
