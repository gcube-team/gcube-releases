package org.gcube.data.analysis.tabulardata.commons.webservice.exception;


import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class RuleNotApplicableException extends TabularDataException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7412219694189757537L;

	private TabularDataExceptionBean faultInfo ;
	
	public RuleNotApplicableException(String name, String message) {
		this.faultInfo = new TabularDataExceptionBean("rule with id "+name+" not applicable : "+message);
	}
	
	public RuleNotApplicableException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public RuleNotApplicableException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}

