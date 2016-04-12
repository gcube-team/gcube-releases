/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util;

import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import org.gcube.application.framework.core.session.ASLSession;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class EntityExecutionData {

	/** The session object */
	private ASLSession session;
	
	private WorkflowLogEntry wfLogger;
	
	/**
	 * Class constructor
	 */
	public EntityExecutionData() { }
	
	/**
	 * Sets the session object
	 * @param session
	 */
	public void setSession(ASLSession session) {
		this.session = session;
	}
	
	public void setWorkflowLogger(WorkflowLogEntry wfLogger) {
		this.wfLogger = wfLogger;
	}
	
	public WorkflowLogEntry getWorkflowLogLogger() {
		return this.wfLogger;
	}
	
	/**
	 * Returns the session object
	 * @return
	 */
	public ASLSession getSession() {
		return this.session;
	}

}
