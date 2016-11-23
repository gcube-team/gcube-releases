/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.io.Serializable;
import java.util.Date;

import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.output.Resource;


/**
 * @author ceras
 *	
 */
public class ImportStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 190974315819110637L;
	Status status;
	String id, fileName, resourceId;
	Date date;
	Resource resource;
	
	/**
	 * 
	 */
	public ImportStatus() {
		super();
	}

	/**
	 * @param id
	 * @param fileName
	 * @param isCompleted
	 */
	public ImportStatus(String id, String fileName, Status status, Date date) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.status = status;
		this.date = date;
		this.resource=new Resource();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}
	
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	public void setResource(Resource resource)
	{
		this.resource=resource;
	}
	
	public Resource getResoruce()
	{
		return resource;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isFailed() {
		return this.status == Status.FAILED;
	}

	public boolean isComplete() {
		return this.status == Status.COMPLETE;
	}
	
	public boolean isTerminated() {
		return this.status==Status.COMPLETE || this.status==Status.FAILED;
	}
}
