package org.gcube.dataanalysis.ewe.notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import scala.actors.threadpool.Arrays;

public class NotificationHelper {

  private Calendar startTime;

  private String taskId;

  private String scope;
  
  private Exception executionException;

  public NotificationHelper() {
  }

  public void setStartTime(Calendar startTime) {
    this.startTime = startTime;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  private String getSubjectHeader() {
    return "[BlueBRIDGE - Dataminer]";
  }
  
  private String getSpecificVREName() {
    if(this.scope!=null) {
      String[] parts = this.scope.split("/");
      if(parts.length>=3) {
        return StringUtils.join(Arrays.copyOfRange(parts, 3, parts.length), "/");
      }
    }
    return this.scope;
  }

  private boolean isError() {
    return this.executionException!=null;
  }
  
  public void setExecutionException(Exception executionException) {
    this.executionException = executionException;
  }

  private String getSuccessSubject() {
    return String.format("%s Results for your experiment '%s' are ready", this.getSubjectHeader(), this.taskId);
  }

  private String getFailedSubject() {
    return String.format("%s An error occurred while executing your experiment '%s'", this.getSubjectHeader(), this.taskId);
  }
  
  private String getFormattedStartTime() {
    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    return sdf.format(this.startTime.getTime());
  }
  
  private String getSuccessBody() {
    String message = String.format("Your experiment '%s' submitted on %s in the '%s' VRE completed successfully.", this.taskId, this.getFormattedStartTime(), this.getSpecificVREName());
    message+="\n\nYou can retrieve experiment results under the '/DataMiner' e-Infrastructure Workspace folder or from the DataMiner interface.";
    return message;
  }

  private String getFailedBody() {
    String message = String.format("An error occurred while executing your experiment '%s' submitted on %s in the '%s' VRE.", this.taskId, this.getFormattedStartTime(), this.getSpecificVREName());
    message+= "\n\nHere are the error details:\n\n" + this.executionException;
    return message;
  }

  public String getSubject() {
    if(this.isError()) {
      return this.getFailedSubject();
    } else {
      return this.getSuccessSubject();
    }
  }
  
  public String getBody() {
    if(this.isError()) {
      return this.getFailedBody();
    } else {
      return this.getSuccessBody();
    }
  }
  
}
