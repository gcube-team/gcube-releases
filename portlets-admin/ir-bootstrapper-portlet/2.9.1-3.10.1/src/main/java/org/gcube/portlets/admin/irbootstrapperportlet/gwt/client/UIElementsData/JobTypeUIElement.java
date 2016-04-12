/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class JobTypeUIElement implements IsSerializable {

	private String jobTypeName;
	private JobUIElement jobTypeObj;
	private List<JobUIElement> jobs;
	
	public JobTypeUIElement() {
		this.jobTypeName = null;
		this.jobs = new LinkedList<JobUIElement>();
	}
	
	public void setJobTypeName(String typeName) {
		this.jobTypeName = typeName;
	}
	
	public String getJobTypeName() {
		return this.jobTypeName;
	}
	
	public void setJobTypeObject(JobUIElement jobTypeObj) {
		this.jobTypeObj = jobTypeObj;
	}
	
	public JobUIElement getJobTypeObject() {
		return this.jobTypeObj;
	}
	
	public void addJob(JobUIElement job) {
		this.jobs.add(job);
	}
	
	public List<JobUIElement> getJobs() {
		return this.jobs;
	}
}
