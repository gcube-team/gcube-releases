package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RollbackTaskInfo extends TaskInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4366371145895102818L;
	
	private List<Long> historyStepsToRemove;
	
	@SuppressWarnings("unused")
	private RollbackTaskInfo(){}
	
	public RollbackTaskInfo(String submitter, long tabularResourceId, List<Long> historyStepsToRemove) {
		super(submitter, tabularResourceId);
		this.historyStepsToRemove = historyStepsToRemove;
	}

	@Override
	public boolean isResubmittable() {
		return false;
	}
	
	@Override
	public TaskType getType() {
		return TaskType.ROLLBACK;
	}

	public List<Long> getHistoryStepsToRemove() {
		return historyStepsToRemove;
	}

	
}
