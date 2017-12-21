package org.gcube.data.analysis.statisticalmanager.stubs.types;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_WSDL_NAMESPACE;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.statisticalmanager.stubs.types.schema.SMPagedRequest;

@XmlRootElement(namespace = TYPES_WSDL_NAMESPACE)
public class SMImportersRequest extends SMPagedRequest {
	@XmlElement()
	private String objectType;

	@XmlElement()
	private String user;

	public SMImportersRequest() {
		super();
	}

	public SMImportersRequest(String template, String user) {
		this.objectType = template;
		this.user = user;
	}

	public void user(String user) {
		this.user = user;
	}

	public String user() {
		return user;
	}

	public void objectType(String objectType) {
		this.objectType = objectType;
	}

	public String objectType() {
		return objectType;
	}
}
