package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlType(name="SMError",namespace=TYPES_NAMESPACE)
public class SMError extends SMResource {

	/**
	 * 
	 */
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String message;

	public SMError() {
		super();
	}

	public SMError(String message) {
		this.message = message;
	}

	public String message() {
		return message;
	}

	public void smessage(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMError [message=");
		builder.append(message);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	
	
	
}