package org.gcube.datatransfer.portlets.user.client.obj;


import java.util.Date;
 
import com.google.gwt.i18n.client.DateTimeFormat;
 
public class AgentStat { 
 
  private String id;
  private String endpoint;
  private String ongoing;
  private String failed;
  private String succesful;
  private String canceled;
  private String total;

public AgentStat() {
  }

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getEndpoint() {
	return endpoint;
}

public void setEndpoint(String endpoint) {
	this.endpoint = endpoint;
}

public String getOngoing() {
	return ongoing;
}

public void setOngoing(String ongoing) {
	this.ongoing = ongoing;
}

public String getFailed() {
	return failed;
}

public void setFailed(String failed) {
	this.failed = failed;
}

public String getSuccesful() {
	return succesful;
}

public void setSuccesful(String succesful) {
	this.succesful = succesful;
}

public String getCanceled() {
	return canceled;
}

public void setCanceled(String canceled) {
	this.canceled = canceled;
}

public String getTotal() {
	return total;
}

public void setTotal(String total) {
	this.total = total;
}
 

}