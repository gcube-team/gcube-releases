package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class TemplateNotCompatibleException extends Exception {
		

	/**
	 * 
	 */
	private static final long serialVersionUID = -4674761982558636516L;

	private TabularDataExceptionBean faultInfo ;
	
	protected TemplateNotCompatibleException() {
	}
	
	public TemplateNotCompatibleException(Throwable cause) {
		this.faultInfo = new TabularDataExceptionBean("template not compatible with tabularResource", cause);
	}
	
	public TemplateNotCompatibleException(String message) {
		this.faultInfo = new TabularDataExceptionBean(message);
	}
	
	public TemplateNotCompatibleException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		this.faultInfo = faultInfo;
	}
		
	public TemplateNotCompatibleException(String message, TabularDataExceptionBean faultInfo){
		this.faultInfo = faultInfo;
	}
	
	public TabularDataExceptionBean getFaultInfo(){
		return faultInfo;
	}

}
