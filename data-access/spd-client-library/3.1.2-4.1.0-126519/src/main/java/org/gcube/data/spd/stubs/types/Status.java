package org.gcube.data.spd.stubs.types;

import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {
	
	@XmlElement
	private int completedEntries;
	
	@XmlElement
	private String status;
	
	@XmlElement(name="subNodesStatus")
	private List<NodeStatus> subNodes;

	@XmlElement
	private Calendar endDate;
	
	@XmlElement
	private Calendar startDate;
	
	public int getCompletedEntries() {
		return completedEntries;
	}

	public String getStatus() {
		return status;
	}

	public List<NodeStatus> getSubNodes() {
		return subNodes;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public Calendar getStartDate() {
		return startDate;
	}
	
}
