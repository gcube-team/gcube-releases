/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class JobAutoCompleteEntryDesc implements IsSerializable {

	private String jobTypeName;
	private String rootObjectDescID;
	
	public JobAutoCompleteEntryDesc() { }
	
	public JobAutoCompleteEntryDesc(String jobTypeName, String rootObjectDescID) {
		this.jobTypeName = jobTypeName;
		this.rootObjectDescID = rootObjectDescID;
	}
	
	public String getJobTypeName() {
		return this.jobTypeName;
	}
	
	public String getRootObjectDescID() {
		return this.rootObjectDescID;
	}
}
