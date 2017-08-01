package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationTaskInfo extends TaskInfo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3646676453081308341L;

	@SuppressWarnings("unused")
	private OperationTaskInfo(){}
	
	public OperationTaskInfo(String submitter, long tabularResourceId) {
		super(submitter, tabularResourceId);
	}

	@Override
	public boolean isResubmittable() {
		return true;
	}

	@Override
	public TaskType getType() {
		return TaskType.OPERATION;
	}

	
	
}
