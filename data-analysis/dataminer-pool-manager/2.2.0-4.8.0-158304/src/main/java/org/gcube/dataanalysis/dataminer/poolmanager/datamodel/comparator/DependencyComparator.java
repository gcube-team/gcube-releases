package org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator;

import java.util.Comparator;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;

public class DependencyComparator implements Comparator<Dependency> {

  @Override
  public int compare(Dependency a1, Dependency a2) {
    int out = a1.getType().compareTo(a2.getType());
    if(out!=0)
      return out;
    return a1.getName().compareTo(a2.getName());
  }
  

}
