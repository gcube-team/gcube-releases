package gr.uoa.di.madgik.taskexecutionlogger.model;

import java.util.Date;

public class TaskLogEntry {
	
	private Date date;
	
	private String message;
	
	private  LogEntryLevel level;
	
	public TaskLogEntry(LogEntryLevel level, String message) {
		this.message = message;
		this.level = level;
		this.date = new Date();
	}

	public String getMessage() {
		return message;
	}

	public LogEntryLevel getLevel() {
		return level;
	}

	public Date getDate() {
		return date;
	}
}
