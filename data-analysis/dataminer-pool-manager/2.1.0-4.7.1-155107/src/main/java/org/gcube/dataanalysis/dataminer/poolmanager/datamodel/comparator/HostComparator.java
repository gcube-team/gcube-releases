package org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator;

import java.util.Comparator;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;

public class HostComparator implements Comparator<Host> {

  @Override
  public int compare(Host h1, Host h2) {
    int out = h1.getDomain().getName().compareTo(h2.getDomain().getName());
    if(out!=0)
      return out;
    return h1.getName().compareTo(h2.getName());
  }

}
