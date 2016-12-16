package org.gcube.data.analysis.tabulardata.commons.webservice.exception.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.adapters.ThrowableAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TabularDataExceptionBean {

	protected String message;

	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable cause;
	
	protected TabularDataExceptionBean(){}
	
	public TabularDataExceptionBean(String message){
		this.message = message;
	}
	
	public TabularDataExceptionBean(String message, Throwable cause){
		this.message = message;
		this.cause = cause;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}
	
}
