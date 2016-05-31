package org.gcube.data.analysis.tabulardata.commons.webservice.exception;

import javax.xml.ws.WebFault;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans.TabularDataExceptionBean;

@WebFault
public class NoSuchTemplateException extends TabularDataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 352871398364588526L;

		
		
	public NoSuchTemplateException(long id) {
		super();
		this.faultInfo = new TabularDataExceptionBean("A template with the given id does not exists");
	}
	
	public NoSuchTemplateException(String message, TabularDataExceptionBean faultInfo, Throwable cause){
		super(message, faultInfo, cause);
	}
		
	public NoSuchTemplateException(String message, TabularDataExceptionBean faultInfo){
		super(message, faultInfo);
	}



}
