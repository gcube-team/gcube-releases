package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.WorkAreaEventType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.project.Project;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Project Status Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WorkAreaEvent extends GwtEvent<WorkAreaEvent.WorkAreaEventHandler> {

	public static Type<WorkAreaEventHandler> TYPE = new Type<WorkAreaEventHandler>();
	private WorkAreaEventType workAreaEventType;
	private Project project;

	public interface WorkAreaEventHandler extends EventHandler {
		void onWorkArea(WorkAreaEvent event);
	}

	public interface HasWorkAreaEventHandler extends HasHandlers {
		public HandlerRegistration addWorkAreaEventHandler(WorkAreaEventHandler handler);
	}

	public WorkAreaEvent() {
		this.workAreaEventType = WorkAreaEventType.WORK_AREA_SETUP;
		this.project = null;
	}

	public WorkAreaEvent(WorkAreaEventType workAreaEventType, Project project) {
		this.workAreaEventType = workAreaEventType;
		this.project = project;
	}

	@Override
	protected void dispatch(WorkAreaEventHandler handler) {
		handler.onWorkArea(this);
	}

	@Override
	public Type<WorkAreaEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<WorkAreaEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, WorkAreaEvent workAreaEvent) {
		source.fireEvent(workAreaEvent);
	}

	public WorkAreaEventType getWorkAreaEventType() {
		return workAreaEventType;
	}

	public Project getProject() {
		return project;
	}

	@Override
	public String toString() {
		return "WorkAreaEvent [workAreaEventType=" + workAreaEventType + ", project=" + project + "]";
	}

}
