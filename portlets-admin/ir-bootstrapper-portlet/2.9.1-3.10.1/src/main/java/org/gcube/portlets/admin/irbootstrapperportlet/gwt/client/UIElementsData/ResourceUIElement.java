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
public class ResourceUIElement extends UIElement implements IsSerializable {
	
	private List<JobUIElement> jobs;
	
	//TODO ADDED
	private String contentCollectionID;
	
	/**
	 * Class constructor
	 */
	public ResourceUIElement() {
		jobs = new LinkedList<JobUIElement>();
	}
	
	/**
	 * Returns the list of jobs of this {@link ResourceUIElement}
	 * @return
	 */
	public List<JobUIElement> getJobs() {
		return this.jobs;
	}
	
	public String getContentCollectionID() {
		return contentCollectionID;
	}

	public void setContentCollectionID(String contentCollectionID) {
		this.contentCollectionID = contentCollectionID;
	}

	/**
	 * Adds a job to this {@link ResourceUIElement}'s job list
	 * @param job the job to add
	 */
	public void addJob(JobUIElement job) {
		this.jobs.add(job);
	}	
}
