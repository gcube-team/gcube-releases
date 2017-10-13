package org.gcube.portlets.user.td.taskswidget.shared.job;

import java.io.Serializable;

public enum TdTaskStatusType implements Serializable {
	PENDING,
	
	INITIALIZING,
	RUNNING, //USED
	ABORTED,
//	FALLBACK,
	RUNNING_WITH_FAILURES, 
	FAILED, 
	COMPLETED, 
	COMPLETED_WITH_FAILURES, 
	SAVING,
	SAVED,
	STOPPED,
	STATUS_UNKNOWN, 
	VALIDATING_RULES, 
	GENERATING_VIEW
}