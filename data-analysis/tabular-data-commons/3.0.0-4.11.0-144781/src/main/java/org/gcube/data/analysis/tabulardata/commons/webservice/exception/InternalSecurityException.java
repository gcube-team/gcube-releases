package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class InternalSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7215024037335025841L;
	
	private TabularDataExceptionBean faultInfo ;
	
	protected InternalSecurityException() {
	}
	
	public InternalSecurityException(String cause) {
		this.faultInfo = new TabularDataExceptionBean("invocation not valid");
	}
	
	public InternalSecurityException(Throwable cause) {
		this.faultInfo = new TabularDataExceptionBean("invocation not valid", cause);
	}
	
	public InternalSecurityException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public InternalSecurityException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
