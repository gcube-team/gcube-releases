package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public abstract class TabularDataException extends Exception{

	protected TabularDataExceptionBean faultInfo ;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8239662677465850962L;

	public TabularDataException(){}
	
	public TabularDataException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public TabularDataException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}
	
}
