package org.gcube.dataanalysis.dataminer.poolmanager.datamodel;

import java.util.Collection;
import java.util.Vector;

public class Cluster {

  /**
   * The set of hosts belonging to the cluster.
   */
  private Collection<Host> hosts;

  /**
   * A name for this cluster.
   */
  private String name;

  /**
   * A description of this cluster.
   */
  private String description;

  /**
   * The set of algorithms deployed on this cluster (i.e. on all its hosts)
   */
  private Collection<AlgorithmSet> algoSets;
  
  public Cluster() 
  {
    this.hosts = new Vector<>();
    this.algoSets = new Vector<>();
  }
  
  public void addAlgorithmSet(AlgorithmSet set) 
  {
    this.algoSets.add(set);
  }

  public void addHost(Host host)
  {
    this.hosts.add(host);
  }

  public Collection<Host> getHosts() 
  {
    return hosts;
  }

  public String getName() 
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription() 
  {
    return description;
  }

  public void setDescription(String description) 
  {
    this.description = description;
  }

  public Collection<AlgorithmSet> getAlgorithmSets() 
  {
    return algoSets;
  }
  
  public String toString() {
    String out = "Cluster: "+this.name+"\n";
    for(Host h:this.getHosts()) {
      out+="  "+h+"\n";
    }
    return out;
  }

}
