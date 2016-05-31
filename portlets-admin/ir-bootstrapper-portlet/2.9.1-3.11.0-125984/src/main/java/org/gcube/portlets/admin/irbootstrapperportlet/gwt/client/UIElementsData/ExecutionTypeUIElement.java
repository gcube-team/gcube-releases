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
public class ExecutionTypeUIElement extends ExecutionEntityUIElement implements IsSerializable {

	/** The list of tasks to be executed */
	private List<ExecutionEntityUIElement> taskList;
	
	/**
	 * Class constructor
	 */
	public ExecutionTypeUIElement() {
		taskList = new LinkedList<ExecutionEntityUIElement>();
	}
	
	/**
	 * Adds a task to this {@link ExecutionTypeUIElement}
	 * @param task the task to add
	 */
	public void addTask(ExecutionEntityUIElement task) {
		taskList.add(task);
	}
	
	/**
	 * Returns the list of {@link ExecutionEntityUIElement}s contained in this {@link ExecutionTypeUIElement}
	 * @return the list of execution entities contained in this object
	 */
	public List<ExecutionEntityUIElement> getTasks() {
		return taskList;
	}
}
