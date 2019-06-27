package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class NoSuchTabularResourceException extends TabularDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 352871398364588526L;

	
	
	
	public NoSuchTabularResourceException() {
		super();
	}

	private TabularDataExceptionBean faultInfo ;
	
	public NoSuchTabularResourceException(long id) {
		this.faultInfo = new TabularDataExceptionBean("A tabular resource with the given id does not exists");
	}
	
	public NoSuchTabularResourceException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchTabularResourceException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
