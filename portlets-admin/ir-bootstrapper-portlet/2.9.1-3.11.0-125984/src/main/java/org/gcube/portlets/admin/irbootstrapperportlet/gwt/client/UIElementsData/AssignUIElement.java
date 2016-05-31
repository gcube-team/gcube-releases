/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class AssignUIElement extends ExecutionEntityUIElement implements IsSerializable {

	private String assignFrom;
	
	private String assignTo;
	
	private Boolean requiresUserInput;
	
	private String userInputLabel;

	public AssignUIElement() { }
	
	public AssignUIElement(String from, String to, Boolean requiresUserInput, String userInputLabel) {
		this.assignFrom = from;
		this.assignTo = to;
		this.requiresUserInput = requiresUserInput;
		this.userInputLabel = userInputLabel;
	}
	
	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}
	
	public String getAssignTo() {
		return this.assignTo;
	}
	
	public void setAssignFrom(String assignFrom) {
		this.assignFrom = assignFrom;
	}
	
	public String getAssignFrom() {
		return this.assignFrom;
	}
	
	public Boolean requiresUserInput() {
		return this.requiresUserInput;
	}
	
	public void setRequiresUserInput(Boolean value) {
		this.requiresUserInput = value;
	}
	
	public String getUserInputLabel() {
		return this.userInputLabel;
	}
	
	public void setUserInputLabel(String label) {
		this.userInputLabel = label;
	}
}
