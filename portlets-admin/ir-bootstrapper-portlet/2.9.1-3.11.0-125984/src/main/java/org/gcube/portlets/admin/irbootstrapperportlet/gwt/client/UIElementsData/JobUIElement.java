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
public class JobUIElement extends ExecutionEntityUIElement implements IsSerializable {
	
	/** The task three to execute */
	private ExecutionEntityUIElement taskTree;
	
	/** The list of initialization assignments */
	private List<AssignUIElement> initAssignments;
	
	/** The name of the job's type */
	private String jobTypeName;
	
	/** The name of the base job that this job extends */
	private String jobExtends;
	
	private Boolean requiresUserInput;
	
	/**
	 * Class constructor
	 */
	public JobUIElement() {
		initAssignments = new LinkedList<AssignUIElement>();
	}
	
	/**
	 * Sets the task tree to execute
	 * @param taskTree the task tree to execute
	 */
	public void setTaskTree(ExecutionEntityUIElement taskTree) {
		this.taskTree = taskTree;
	}
	
	/**
	 * Returns the task tree to execute
	 * @return the task tree to execute
	 */
	public ExecutionEntityUIElement getTaskTree() {
		return this.taskTree;
	}
	
	public void setInitAssignments(List<AssignUIElement> initAssignments) {
		this.initAssignments = initAssignments;
	}
	
	public List<AssignUIElement> getInitAssignments() {
		return this.initAssignments;
	}
	
	public void setJobTypeName(String jobTypeName) {
		this.jobTypeName = jobTypeName;
	}
	
	public String getJobTypeName() {
		return this.jobTypeName;
	}
	
	public void setJobExtends(String jobExtends) {
		this.jobExtends = jobExtends;
	}
	
	public String getJobExtends() {
		return this.jobExtends;
	}
	
	public Boolean requiresUserInput() {
		return requiresUserInput;
	}

	public void setRequiresUserInput(Boolean requiresUserInput) {
		this.requiresUserInput = requiresUserInput;
	}

}
