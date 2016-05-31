package gr.uoa.di.madgik.taskexecutionlogger.model;

import gr.uoa.di.madgik.taskexecutionlogger.TaskExecutionLogger;
import gr.uoa.di.madgik.taskexecutionlogger.utils.JSONConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkflowLogEntry {

	private String uid;
	
	private String submitter;
	
	private String name;
	
	private String type;
	
	private String description;
	
	private String status;

	private Date startDate;
	
	private Date endDate;
	
	private String scope;
	
	private List<TaskLogEntry> entries;
	
	
	public WorkflowLogEntry(String uid, String name, String type, String submitter, String status, Date startDate, String description, String scope) {
		super(); 
		this.uid = uid;
		this.name = name;
		this.type = type;
		this.status = status;
		this.submitter = submitter;
		this.startDate = startDate;
		this.description = description;
		this.scope = scope;
		this.entries = new ArrayList<TaskLogEntry>();
	}

	public void addEntry(TaskLogEntry entry) {
		this.entries.add(entry);
	}
	
	public void addEntry(LogEntryLevel level, String message) {
		this.entries.add(new TaskLogEntry(level, message));
	}

	public void persistLog() {
		this.endDate = new Date();
		TaskExecutionLogger logger = TaskExecutionLogger.getLogger();
		logger.logTask(JSONConverter.convertToJSON(this));
	}

	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return submitter;
	}

	public Date getStartDate() {
		return startDate;
	}


	public String getSubmitter() {
		return submitter;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEndDate() {
		return endDate;
	}


	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	public String getScope() {
		return scope;
	}

	public List<TaskLogEntry> getEntries() {
		return entries;
	}
}
