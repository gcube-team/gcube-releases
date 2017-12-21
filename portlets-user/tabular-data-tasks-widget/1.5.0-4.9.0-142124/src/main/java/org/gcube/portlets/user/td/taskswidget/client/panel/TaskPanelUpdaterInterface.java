/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel;

import org.gcube.portlets.user.td.taskswidget.client.panel.result.JobInfoPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultCollateralTablePanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.ResultTabularDataPanel;
import org.gcube.portlets.user.td.taskswidget.client.panel.result.TaskInfoPanel;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 19, 2013
 *
 */
public interface TaskPanelUpdaterInterface {
	

	/**
	 * 
	 * @param lc
	 * @param setAsVisible
	 */
	void addTaskInfoContainer(TaskInfoPanel lc, boolean setAsVisible);

	/**
	 * 
	 * @param lc
	 * @param setAsVisible
	 */
	void addJobInfoContainer(JobInfoPanel lc, boolean setAsVisible);

	/**
	 * 
	 * @param lc
	 * @param setAsVisible
	 */
	void addResultTabularContainer(ResultTabularDataPanel lc);
	
	/**
	 * 
	 * @param lc
	 * @param setAsVisible
	 */
	void addResultCollateralContainer(ResultCollateralTablePanel lc);

}
