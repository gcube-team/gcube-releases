package org.gcube.data.spd.stubs.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodeStatus {

	@XmlElement
	private String scientificName;
	
	@XmlElement
	private String status;

	public String getScientificName() {
		return scientificName;
	}

	public String getStatus() {
		return status;
	}

	
}
