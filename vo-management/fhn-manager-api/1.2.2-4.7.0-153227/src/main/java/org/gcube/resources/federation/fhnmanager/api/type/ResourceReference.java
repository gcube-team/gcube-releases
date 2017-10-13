package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlAttribute;

public class ResourceReference<T extends FHNResource> {

	// @XmlAttribute
	private String refId;

	public ResourceReference() {

	}
	
	public ResourceReference(String resId) {
		this.refId = resId;
	}

	@XmlAttribute
	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public ResourceReference(T resource) {
		this.refId = resource.getId();
	}
}
