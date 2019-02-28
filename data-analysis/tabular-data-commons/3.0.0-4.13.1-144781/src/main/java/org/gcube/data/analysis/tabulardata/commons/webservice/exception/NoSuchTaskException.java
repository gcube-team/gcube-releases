package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;


@WebFault
public class NoSuchTaskException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6203418926222758026L;

	private TabularDataExceptionBean faultInfo ;
	
	public NoSuchTaskException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchTaskException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public NoSuchTaskException(String taskId){
		this.faultInfo = new TabularDataExceptionBean("task "+taskId+" not found");
		
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}
}
