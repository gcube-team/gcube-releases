package org.gcube.datatransfer.scheduler.library.outcome;

import java.io.Serializable;
import java.util.List;
import com.thoughtworks.xstream.XStream;

 //this is the place for storing the result of calling some operation of
//Scheduler Service

public class CallingSchedulerResult implements Serializable{
	private static final long serialVersionUID = 1L;

	
	private String transferid;
	private String status;
	
	private List<String> errors;
	
	//if it is for monitor
	private String monitorResult;
	//if it is for cancel
	private String cancelResult;
	//if it is for getting the outcomes
	private String schedulerOutcomes;
	//if it is for the printing
	private String printResult;
	
	protected static XStream xstream = new XStream();
	
	public CallingSchedulerResult(){
		this.transferid=null;
		this.status=null;
		this.errors=null;
		this.monitorResult=null;
		this.cancelResult=null;
		this.schedulerOutcomes=null;
		this.printResult=null;
	}
	
	
	public String getTransferid() {
		return transferid;
	}
	public void setTransferid(String transferid) {
		this.transferid = transferid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public String getCancelResult() {
		return cancelResult;
	}
	public void setCancelResult(String cancelResult) {
		this.cancelResult = cancelResult;
	}
	public String getMonitorResult() {
		return monitorResult;
	}
	public void setMonitorResult(String monitorResult) {
		this.monitorResult = monitorResult;
	}
	public String getSchedulerOutcomes() {
		return schedulerOutcomes;
	}
	public void setSchedulerOutcomes(String schedulerOutcomes) {
		this.schedulerOutcomes = schedulerOutcomes;
	}
	public String getPrintResult() {
		return printResult;
	}
	public void setPrintResult(String printResult) {
		this.printResult = printResult;
	}
	
	public String toXML(){
		return xstream.toXML(this);
	}
	
	
}
