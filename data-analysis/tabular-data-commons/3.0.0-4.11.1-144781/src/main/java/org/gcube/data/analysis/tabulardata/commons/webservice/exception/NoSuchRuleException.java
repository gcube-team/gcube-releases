package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class NoSuchRuleException extends TabularDataException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3939395342553741915L;
	
	private TabularDataExceptionBean faultInfo ;
	
	public NoSuchRuleException(long id) {
		this.faultInfo = new TabularDataExceptionBean("A rule with id "+id+" does not exists");
	}
	
	public NoSuchRuleException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public NoSuchRuleException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
