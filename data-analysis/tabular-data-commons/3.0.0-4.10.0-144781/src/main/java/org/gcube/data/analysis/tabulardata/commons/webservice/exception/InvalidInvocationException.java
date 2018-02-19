package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class InvalidInvocationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4160619096202052576L;

	private TabularDataExceptionBean faultInfo ;
	
	protected InvalidInvocationException() {
	}
	
	public InvalidInvocationException(Throwable cause) {
		this.faultInfo = new TabularDataExceptionBean("invocation not valid", cause);
	}
	
	public InvalidInvocationException(String reason, Throwable cause) {
		this.faultInfo = new TabularDataExceptionBean(reason, cause);
	}
	
	public InvalidInvocationException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public InvalidInvocationException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}
	

}
