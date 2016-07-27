package org.gcube.vremanagement.executor.exception.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.vremanagement.executor.api.types.adapter.ThrowableAdapter;


/**
 * @author Lucio Leii (ISTI - CNR)
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExceptionBean {

	protected String message;

	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable cause;
	
	protected ExceptionBean(){}
	
	public ExceptionBean(String message){
		this.message = message;
	}
	
	public ExceptionBean(String message, Throwable cause){
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
