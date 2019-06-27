package org.gcube.portlets.user.dataminerexecutor.shared.process;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public enum InvocationAction {
	
	SHOW("show"),
	EDIT("edit"),
	RUN("run");

	private String action;

	/**
	 * Instantiates a new action type.
	 *
	 * @param action the action
	 */
	InvocationAction(String action){
		this.action = action;
	}


	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {

		return action;
	}
}
