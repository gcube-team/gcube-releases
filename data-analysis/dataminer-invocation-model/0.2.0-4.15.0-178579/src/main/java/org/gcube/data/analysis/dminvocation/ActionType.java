/**
 *
 */
package org.gcube.data.analysis.dminvocation;

/**
 * The Enum ActionType.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Mar 14, 2019
 */
public enum ActionType {

	EDIT("edit"),
	
	SHOW("show"),
	
	RUN("run");

	private String action;

	/**
	 * Instantiates a new action type.
	 *
	 * @param action the action
	 */
	ActionType(String action){
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
