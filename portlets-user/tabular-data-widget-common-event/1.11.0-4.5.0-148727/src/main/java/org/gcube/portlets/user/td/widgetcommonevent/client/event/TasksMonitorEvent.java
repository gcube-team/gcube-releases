package org.gcube.portlets.user.td.widgetcommonevent.client.event;

import org.gcube.portlets.user.td.widgetcommonevent.client.type.TaskType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 9, 2013
 *
 */
public class TasksMonitorEvent extends GwtEvent<TasksMonitorEvent.TasksMonitorEventHandler> {

	public static Type<TasksMonitorEventHandler> TYPE = new Type<TasksMonitorEventHandler>();
	private TaskType taskType;
	private TRId trId;

	public interface TasksMonitorEventHandler extends EventHandler {
		void onTasksMonitorEvent(TasksMonitorEvent event);
	}
	
	public interface HasTasksMonitorEventHandler extends HasHandlers{
		public HandlerRegistration addTasksMonitorEventHandler(TasksMonitorEventHandler handler);
	}
	
	public static void fire(HasHandlers source, TaskType taskType, TRId trId) {
		source.fireEvent(new TasksMonitorEvent(taskType,trId));
	}

	public TasksMonitorEvent(TaskType closeType, TRId trId) {
		this.taskType = closeType;
		this.trId = trId;
	}

	public static Type<TasksMonitorEventHandler> getType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TasksMonitorEventHandler handler) {
		handler.onTasksMonitorEvent(this);
	}

	@Override
	public Type<TasksMonitorEventHandler> getAssociatedType() {
		return TYPE;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	@Override
	public String toString() {
		return "TasksMonitorEvent [taskType=" + taskType + ", trId=" + trId
				+ "]";
	}
	
	
	
}
