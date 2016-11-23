package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class ExecutionFailedException extends TabularDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5462176452440573811L;

	public ExecutionFailedException() {
		super();
	}

	private TabularDataExceptionBean faultInfo ;
	
	public ExecutionFailedException(String msg) {
		this.faultInfo = new TabularDataExceptionBean(msg);
	}
	
	public ExecutionFailedException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public ExecutionFailedException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
