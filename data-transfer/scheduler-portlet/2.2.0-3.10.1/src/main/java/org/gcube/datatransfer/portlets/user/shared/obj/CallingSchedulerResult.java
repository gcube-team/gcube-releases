package org.gcube.datatransfer.portlets.user.shared.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;

 //this is the place for storing the result of calling some operation of
//Scheduler Service

public class CallingSchedulerResult implements JsonSerializable{
	
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
	
	
	public CallingSchedulerResult(){
		this.transferid="";
		this.status="";
		this.errors=new ArrayList<String>();
		this.errors.add("");
		this.monitorResult="";
		this.cancelResult="";
		this.schedulerOutcomes="";
		this.printResult="";
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

	public static Serializer createSerializer(){
		   return GWT.create(Serializer.class);
	}
	
}
