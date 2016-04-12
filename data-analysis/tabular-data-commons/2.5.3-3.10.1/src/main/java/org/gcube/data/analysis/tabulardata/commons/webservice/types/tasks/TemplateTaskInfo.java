package org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateTaskInfo extends TaskInfo {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4412018865057665592L;
	
	private long refenceTemplateId;
		
	@SuppressWarnings("unused")
	private TemplateTaskInfo(){}
	
	
	public TemplateTaskInfo(String submitter, long tabularResourceId, long refenceTemplateId) {
		super(submitter, tabularResourceId);
		this.refenceTemplateId = refenceTemplateId;
	}

	public long getRefenceTemplateId() {
		return refenceTemplateId;
	}

	@Override
	public boolean isResubmittable() {
		return true;
	}

	@Override
	public TaskType getType() {
		return TaskType.TEMPLATE;
	}
	
}
