package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.*;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
@XmlType(name ="SMTable",namespace=TYPES_NAMESPACE)
public class SMTable extends SMResource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2374336849304877611L;
	/**
	 * 
	 */
	/**
	 * 
	 */
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String template;

	public SMTable() {
		super();
		
	}

	public SMTable(String template) {

		this.template = template;
	}

	public void template(String template) {
		this.template = template;
	}

	public String template() {
		return template;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMTable [template=");
		builder.append(template);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	

	
}
