package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OccopusNode {

	String resource_address;
	String state;
	String infratemplate;

	public OccopusNode() {

	}

	public String getResource_address() {
		return resource_address;
	}

	public void setResource_address(String resource_address) {
		this.resource_address = resource_address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getInfratemplate() {
		return infratemplate;
	}

	public void setInfratemplate(String infratemplate) {
		this.infratemplate = infratemplate;
	}

}
