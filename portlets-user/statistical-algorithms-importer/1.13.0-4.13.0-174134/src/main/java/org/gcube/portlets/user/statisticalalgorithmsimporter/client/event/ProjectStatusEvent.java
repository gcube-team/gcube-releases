package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.ProjectStatusEventType;
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
public class ProjectStatusEvent extends
		GwtEvent<ProjectStatusEvent.ProjectStatusEventHandler> {

	public static Type<ProjectStatusEventHandler> TYPE = new Type<ProjectStatusEventHandler>();
	private ProjectStatusEventType projectStatusEventType;
	private Project project;
	
	public interface ProjectStatusEventHandler extends EventHandler {
		void onProjectStatus(ProjectStatusEvent event);
	}

	public interface HasProjectStatusEventHandler extends HasHandlers {
		public HandlerRegistration addProjectStatusEventHandler(
				ProjectStatusEventHandler handler);
	}

	public ProjectStatusEvent(){
		this.projectStatusEventType = ProjectStatusEventType.START;
		this.project = null;
	}
	
	public ProjectStatusEvent(ProjectStatusEventType projectStatusEventType,Project project) {
		this.projectStatusEventType = projectStatusEventType;
		this.project = project;
	}

	@Override
	protected void dispatch(ProjectStatusEventHandler handler) {
		handler.onProjectStatus(this);
	}

	@Override
	public Type<ProjectStatusEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ProjectStatusEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ProjectStatusEvent projectStatusEvent) {
		source.fireEvent(projectStatusEvent);
	}

	public ProjectStatusEventType getProjectStatusEventType() {
		return projectStatusEventType;
	}

	public Project getProject() {
		return project;
	}

	@Override
	public String toString() {
		return "ProjectStatusEvent [projectStatusEventType="
				+ projectStatusEventType + ", project=" + project + "]";
	}

	
	
	

	
}
