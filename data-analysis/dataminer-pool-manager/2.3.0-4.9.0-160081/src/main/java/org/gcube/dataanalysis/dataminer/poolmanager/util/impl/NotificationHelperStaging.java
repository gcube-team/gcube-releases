package org.gcube.dataanalysis.dataminer.poolmanager.util.impl;

import org.gcube.dataanalysis.dataminer.poolmanager.util.NotificationHelper;

//import scala.actors.threadpool.Arrays;

public class NotificationHelperStaging extends NotificationHelper {
  
//  private Exception executionException;



  private String getSubjectHeader() {
    return "[DataMinerGhostStagingInstallationRequestReport]";
  }


  @Override
  public String getSuccessSubject() {
    return this.getSubjectHeader()+" is SUCCESS";
  }


 
  
  @Override
  public String getFailedSubject() {
    return String.format(this.getSubjectHeader()+" is FAILED");
  }
    
  
}
