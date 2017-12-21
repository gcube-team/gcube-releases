package org.gcube.data.analysis.statisticalmanager.stubs.types;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMPagedRequest;

@XmlType(name="SMCreatedTablesRequest")

public class SMCreatedTablesRequest extends SMPagedRequest implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "SMCreatedTablesRequest [template=" + template + ", toString()="
				+ super.toString() + "]";
	}

	@XmlElement()
	private String template;

	public SMCreatedTablesRequest() {
		super();

	}

	public SMCreatedTablesRequest(String template) {
		this.template = template;
	}

	public void template(String template) {
		this.template = template;
	}

	public String template() {
		return template;
	}

}