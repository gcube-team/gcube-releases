package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class HistoryNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1389866430425201495L;
	
	private TabularDataExceptionBean faultInfo ;
	
	protected HistoryNotFoundException() {
	}
	
	public HistoryNotFoundException(String cause) {
		this.faultInfo = new TabularDataExceptionBean("invocation not valid");
	}
	
	public HistoryNotFoundException(Throwable cause) {
		this.faultInfo = new TabularDataExceptionBean("invocation not valid", cause);
	}
	
	public HistoryNotFoundException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public HistoryNotFoundException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}
	

}
