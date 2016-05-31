package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMAbstractResource {

	@XmlElement(namespace = TYPES_NAMESPACE)
	private String abstractResourceId;
	
	
	@XmlElement(namespace = TYPES_NAMESPACE)
	private SMResource resource;

	public SMAbstractResource() {
		super();

	}

	public SMAbstractResource(String abstractResourceId, SMResource resource) {
		this.abstractResourceId = abstractResourceId;
		this.resource = resource;
	}

	public String abstractResourceId() {
		return abstractResourceId;
	}

	public void abstractResourceId(String abstractResourceId) {
		this.abstractResourceId = abstractResourceId;
	}

	public SMResource resource() {
		return resource;
	}

	public void resource(SMResource resource) {
		this.resource = resource;
	}

	@Override
	public String toString() {
		return "SMAbstractResource [abstractResourceId=" + abstractResourceId
				+ ", resource=" + resource + "]";
	}
}
