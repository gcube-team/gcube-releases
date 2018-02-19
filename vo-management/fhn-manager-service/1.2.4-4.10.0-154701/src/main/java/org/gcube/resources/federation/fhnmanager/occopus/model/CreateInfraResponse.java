package org.gcube.resources.federation.fhnmanager.occopus.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateInfraResponse {
	
	String infraid;

	public String getInfraid() {
		return infraid;
	}

	public void setInfraid(String infraid) {
		this.infraid = infraid;
	}
	
	
}
