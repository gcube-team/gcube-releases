/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SubmittedJobInfoUIElement implements IsSerializable {
	
private String uid;
	
	private String submitter;
	
	private String name;
	
	private String type;
	
	private String description;
	
	private String status;
	
	private Date startDate;
	
	private Date endDate;
	
	private String scope;
	
	private List<TaskInfoUIElement> entries;
	
	/**
	 * Class constructor
	 */
	public SubmittedJobInfoUIElement() {
		
	}
	
	public SubmittedJobInfoUIElement(String uid, String name, String type, String submitter, String status, Date startDate, Date endDate, String description, String scope,  List<TaskInfoUIElement> entries) {
		this.uid = uid;
		this.name = name;
		this.type = type;
		this.status = status;
		this.submitter = submitter;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.scope = scope;
		this.entries = entries;
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return submitter;
	}

	public Date getStartDate() {
		return startDate;
	}


	public String getSubmitter() {
		return submitter;
	}

	public String getStatus() {
		return status;
	}

	public Date getEndDate() {
		return endDate;
	}


	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getScope() {
		return scope;
	}

	public List<TaskInfoUIElement> getEntries() {
		return entries;
	}
	
}
