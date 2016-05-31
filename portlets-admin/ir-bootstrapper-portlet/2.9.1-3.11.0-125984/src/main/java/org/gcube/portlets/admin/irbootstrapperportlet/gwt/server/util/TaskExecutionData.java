/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util;

import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class TaskExecutionData extends EntityExecutionData {

	/** The execution log object */
	private TaskExecutionLogger logger;
	
	/**
	 * Constructs a new {@link TaskExecutionData} object by copying the
	 * given {@link EntityExecutionData} object
	 */
	public TaskExecutionData(EntityExecutionData eed) {
		this.setSession(eed.getSession());
	}
	
	/**
	 * Sets the execution logger
	 * @param logger
	 */
	public void setExecutionLogger(TaskExecutionLogger logger) {
		this.logger = logger;
	}
	
	/**
	 * Returns the execution logger
	 * @return
	 */
	public TaskExecutionLogger getExecutionLogger() {
		return this.logger;
	}
	
//	/**
//	 * Sets the execution logger
//	 * @param logger
//	 */
//	public void setWorkflowLogger(WorkflowLogEntry logger) {
//		this.wfLogger = logger;
//	}
//	
//	/**
//	 * Returns the execution logger
//	 * @return
//	 */
//	public WorkflowLogEntry getWorkflowLogger() {
//		return this.wfLogger;
//	}
}
