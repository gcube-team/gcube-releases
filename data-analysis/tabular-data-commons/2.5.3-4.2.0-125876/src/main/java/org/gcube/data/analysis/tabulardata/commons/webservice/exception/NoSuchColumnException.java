package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

public class NoSuchColumnException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1109476374032388875L;

	private TabularDataExceptionBean faultInfo ;
	
	
	public NoSuchColumnException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchColumnException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}


}
