package org.gcube.data.spd.model.service.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodeStatus {

	@XmlElement
	private String scientificName;
	
	@XmlElement
	private JobStatus status;
	
	public NodeStatus(String scientificName, JobStatus status) {
		super();
		this.scientificName = scientificName;
		this.status = status;
	}
	
	protected NodeStatus() {
		super();
	}

	public String getScientificName() {
		return scientificName;
	}

	public JobStatus getStatus() {
		return status;
	}

	
}
