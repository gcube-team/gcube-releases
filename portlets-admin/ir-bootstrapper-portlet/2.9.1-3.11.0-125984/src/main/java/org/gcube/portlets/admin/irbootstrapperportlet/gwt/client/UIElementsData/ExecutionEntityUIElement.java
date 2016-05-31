/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ExecutionEntityUIElement extends UIElement implements IsSerializable {
	
	public enum UIExecutionState implements IsSerializable {
		NOT_STARTED, RUNNING, COMPLETED_SUCCESS, COMPLETED_FAILURE, COMPLETED_WARNINGS, CANCELLED; 
		
		private UIExecutionState() {}
	}
	
	/** Specifies whether this entity's goal has been fulfilled or not */
	protected boolean bIsGoalFulfilled;
	
	/** The parent execution entity UID */
	protected String parentUID;
	
	/** The state of execution of this {@link ExecutionEntityUIElement} */
	protected UIExecutionState execState;
	
	/** The execution log of this element */
	protected LinkedList<UILogEntry> execLog;
	
	/**
	 * Class constructor
	 */
	public ExecutionEntityUIElement() {
		this.parentUID = null;
		this.bIsGoalFulfilled = false;
		this.execState = UIExecutionState.NOT_STARTED;
		this.execLog = new LinkedList<UILogEntry>();
	}
	
	/**
	 * Sets whether this entity's goal has been fulfilled or not
	 * @param bFulfilled
	 */
	public void setIsFulfilled(boolean bFulfilled) {
		this.bIsGoalFulfilled = bFulfilled;
	}
	
	/**
	 * Returns true if this entity's goal has been fulfilled, else false
	 * @return
	 */
	public boolean isFulfilled() {
		return this.bIsGoalFulfilled;
	}
	
	public void setParentUID(String parentUID) {
		this.parentUID = parentUID;
	}
	
	public String getParentUID() {
		return this.parentUID;
	}
	
	public void setExecutionState(UIExecutionState execState) {
		this.execState = execState;
	}
	
	public UIExecutionState getExecutionState() {
		return this.execState;
	}
	
	public LinkedList<UILogEntry> getExecutionLog() {
		return this.execLog;
	}
}
