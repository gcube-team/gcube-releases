package org.gcube.portlets.admin.wfdocviewer.server.loggers;

import org.gcube.application.framework.accesslogger.model.AccessLogEntry;

public class DeletedWorkflowLogEntry  extends AccessLogEntry{
	private String reportname;
	
	private String workflowid;
	
	private String status;


	public DeletedWorkflowLogEntry(String reportname, String workflowid, String status) {
		super("Deleted_WorkflowReport_Output");
		this.reportname = replaceReservedChars(reportname);
		this.workflowid = replaceReservedChars(workflowid);
		this.status = replaceReservedChars(status);	
	}
	
	@Override
	public String getLogMessage() {
		String message = "WorkflowDocuementName = " + reportname + "|WORKFLOWID = " + workflowid+ "|STATUS = " + status;
		return message;
	}

}
