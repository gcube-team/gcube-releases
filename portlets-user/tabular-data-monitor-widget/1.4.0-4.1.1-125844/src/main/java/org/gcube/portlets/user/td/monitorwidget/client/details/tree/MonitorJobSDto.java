package org.gcube.portlets.user.td.monitorwidget.client.details.tree;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MonitorJobSDto extends MonitorFolderDto {

	private static final long serialVersionUID = 4644048540524701598L;
	private JobSClassifier jobClassifier;
	
	public MonitorJobSDto(){
		super();
	}
	
	public MonitorJobSDto(String type,String id, JobSClassifier jobClassifier,String description, String state, 
			String humanReadableStatus, float progress,  ArrayList<MonitorBaseDto> childrens){
		super(type, id, description, state,humanReadableStatus,progress, childrens);
		this.jobClassifier=jobClassifier;
		
	}

	public JobSClassifier getJobClassfier() {
		return jobClassifier;
	}

	public void setJobClassfier(JobSClassifier jobClassfier) {
		this.jobClassifier = jobClassfier;
	}

	@Override
	public String toString() {
		return description;
	}

}
