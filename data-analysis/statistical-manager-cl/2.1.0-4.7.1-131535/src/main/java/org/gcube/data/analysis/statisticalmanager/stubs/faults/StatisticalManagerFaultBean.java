package org.gcube.data.analysis.statisticalmanager.stubs.faults;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StatisticalManagerFaultBean {

	
	protected String message;

	@XmlJavaTypeAdapter(ThrowableAdapter.class)
	protected Throwable cause;
	
	protected StatisticalManagerFaultBean() {
	}

	public StatisticalManagerFaultBean(String message) {
		super();
		this.message = message;
	}

	public StatisticalManagerFaultBean(String message, Throwable cause) {
		super();
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
