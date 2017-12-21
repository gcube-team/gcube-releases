package org.gcube.dataanalysis.dataminer.poolmanager.util;

//import scala.actors.threadpool.Arrays;

public abstract class NotificationHelper {
  
//  private Exception executionException;


	

//  private boolean isError() {
//    return this.executionException!=null;
//  }
  
//  public void setExecutionException(Exception executionException) {
//    this.executionException = executionException;
//  }

  public abstract String getSuccessSubject();


  public abstract String getFailedSubject();

  
  public String getSuccessBody(String info) {
    String message = String.format("The installation of the algorithm is completed successfully.");
    message+="\n\nYou can retrieve experiment results under the '/DataMiner' e-Infrastructure Workspace folder or from the DataMiner interface.\n\n"+ info;
    return message;
  }

  public String getFailedBody(String message) {
    String body = String.format("An error occurred while deploying your algorithm");
    body+= "\n\nHere are the error details:\n\n" + message;
    return body;
  }

//  public String getSuccessBodyRelease(String info) {
//	    String message = String.format("SVN REPOSITORY CORRECTLY UPDATED.");
//	    message+="\n\n The CRON job will install the algorithm in the target VRE \n\n"+ info;
//	    return message;
//	  }
//  
//  public String getFailedBodyRelease(String info) {
//	    String message = String.format("SVN REPOSITORY UPDATE FAILED.");
//	    message+="\n\n The CRON job will NOT be able to install the algorithm in the target VRE \n\n"+ info;
//	    return message;
//	  }
  
//  public String getSubject() {
//    if(this.isError()) {
//      return this.getFailedSubject();
//    } else {
//      return this.getSuccessSubject();
//    }
//  }
//  
//  public String getBody() {
//    if(this.isError()) {
//      return this.getFailedBody();
//    } else {
//      return this.getSuccessBody();
//    }
//  }
  
}
