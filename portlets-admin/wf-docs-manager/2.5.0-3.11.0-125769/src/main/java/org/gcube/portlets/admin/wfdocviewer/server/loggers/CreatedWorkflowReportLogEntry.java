package org.gcube.portlets.admin.wfdocviewer.server.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

/** 
 * Represents an access log entry for creating a new Template
 */
public class CreatedWorkflowReportLogEntry extends AccessLogEntry{
	
	private String reportname;
	
	private String workflowid;
	
	private String stepsNumber;

	public CreatedWorkflowReportLogEntry(String reportname, String workflowid, int stepsNumber) {
		super("Created_WorkflowReport_Output");
		this.reportname = replaceReservedChars(reportname);
		this.workflowid = replaceReservedChars(workflowid);
		this.stepsNumber = replaceReservedChars(stepsNumber+"");
	}
	
	@Override
	public String getLogMessage() {
		String message = "WorkflowDocuementName = " + reportname + "|WORKFLOWID = " + workflowid +"|STEPS_NO = " + stepsNumber;	
		return message;
	}

}