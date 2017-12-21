package org.gcube.data.spd.model.service.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CompleteJobStatus {
	
	@XmlElement
	private int completedEntries;
	
	@XmlElement
	private JobStatus status;
	
	@XmlElement(name="subNodesStatus")
	private List<NodeStatus> subNodes= new ArrayList<NodeStatus>();

	@XmlElement
	private Calendar endDate;
	
	@XmlElement
	private Calendar startDate;
	
	public int getCompletedEntries() {
		return completedEntries;
	}

	public JobStatus getStatus() {
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

	public void setCompletedEntries(int completedEntries) {
		this.completedEntries = completedEntries;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public void setSubNodes(List<NodeStatus> subNodes) {
		this.subNodes = subNodes;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		return "CompleteJobStatus [completedEntries=" + completedEntries
				+ ", status=" + status + ", subNodes=" + subNodes
				+ ", endDate=" + endDate + ", startDate=" + startDate + "]";
	}
	
	
	
}
