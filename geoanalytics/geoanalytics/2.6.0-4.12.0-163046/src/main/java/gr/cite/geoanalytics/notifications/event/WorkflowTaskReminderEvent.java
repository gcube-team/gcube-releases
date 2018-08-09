package gr.cite.geoanalytics.notifications.event;

import gr.cite.geoanalytics.notifications.Event;
import gr.cite.geoanalytics.notifications.EventType;

public class WorkflowTaskReminderEvent extends Event
{
	private String taskId = null;
	private long reminderDate;
	
	public WorkflowTaskReminderEvent()
	{
		type = EventType.WorkflowTaskReminder;
	}
	
	public WorkflowTaskReminderEvent(String taskId, long reminderDate)
	{
		this();
		this.taskId = taskId;
		this.reminderDate = reminderDate;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public long getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(long reminderDate) {
		this.reminderDate = reminderDate;
	}
}
