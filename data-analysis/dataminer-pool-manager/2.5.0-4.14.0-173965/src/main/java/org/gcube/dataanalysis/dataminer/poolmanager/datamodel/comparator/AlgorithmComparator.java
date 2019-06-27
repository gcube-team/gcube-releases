package org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator;

import java.util.Comparator;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;

public class AlgorithmComparator implements Comparator<Algorithm> {

  @Override
  public int compare(Algorithm a1, Algorithm a2) {
    return a1.getName().compareTo(a2.getName());
  }
  

}
