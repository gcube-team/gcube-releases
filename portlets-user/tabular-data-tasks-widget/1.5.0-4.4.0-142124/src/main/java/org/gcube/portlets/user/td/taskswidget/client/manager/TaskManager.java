/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.manager;

import org.gcube.portlets.user.td.taskswidget.client.panel.result.TaskInfoPanel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 *
 */
public class TaskManager{

	
	//New task info panel
	TaskInfoPanel taskInfo;
	/**
	 * 
	 */
	public TaskManager(TdTaskModel taskModel) {
		taskInfo = new TaskInfoPanel(taskModel);
		
	}
	
	public void updateField(TdTaskModel taskModel){
		taskInfo.updateFormFields(taskModel);
	}

	public TaskInfoPanel getTaskInfo() {
		return taskInfo;
	}

}
