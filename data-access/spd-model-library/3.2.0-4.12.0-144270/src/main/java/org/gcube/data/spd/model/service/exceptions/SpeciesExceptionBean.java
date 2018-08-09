package org.gcube.data.spd.model.service.exceptions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpeciesExceptionBean {

	protected String message;

	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable cause;
	
	protected SpeciesExceptionBean(){}
	
	public SpeciesExceptionBean(String message){
		this.message = message;
	}
	
	public SpeciesExceptionBean(String message, Throwable cause){
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