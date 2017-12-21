package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class NoSuchOperationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TabularDataExceptionBean faultInfo ;
	
	public NoSuchOperationException(long id) {
		this.faultInfo = new TabularDataExceptionBean("An operation with id "+id+" does not exists");
	}
	
	public NoSuchOperationException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchOperationException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
