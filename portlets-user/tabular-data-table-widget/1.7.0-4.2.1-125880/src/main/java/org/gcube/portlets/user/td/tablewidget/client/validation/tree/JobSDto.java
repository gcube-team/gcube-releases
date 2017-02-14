package org.gcube.portlets.user.td.tablewidget.client.validation.tree;



import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JobSDto  extends FolderDto {

	private static final long serialVersionUID = 4644048540524701598L;
	private JobSClassifier jobClassifier;
	
	public JobSDto(){
		super();
	}
	
	public JobSDto(String type,String id,JobSClassifier jobClassifier,String description,  ArrayList<BaseDto> childrens){
		super(type,id,description,childrens);
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
