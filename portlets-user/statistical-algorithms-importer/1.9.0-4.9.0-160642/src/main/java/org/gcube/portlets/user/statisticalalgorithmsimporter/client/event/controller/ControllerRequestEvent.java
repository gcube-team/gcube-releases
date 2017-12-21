package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.controller;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.ControllerRequestEventType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ControllerRequestEvent extends
		GwtEvent<ControllerRequestEvent.ControllerRequestEventHandler> {

	public static Type<ControllerRequestEventHandler> TYPE = new Type<ControllerRequestEventHandler>();
	private ControllerRequestEventType controllerRequestEventType;
	private String projectFolderId;
	
	public interface ControllerRequestEventHandler extends EventHandler {
		void onControllerRequest(ControllerRequestEvent event);
	}

	public interface HasControllerRequestEventHandler extends HasHandlers {
		public HandlerRegistration addControllerRequestEventHandler(
				ControllerRequestEventHandler handler);
	}

	public ControllerRequestEvent(ControllerRequestEventType projectStatusEventType) {
		this.controllerRequestEventType = projectStatusEventType;
	}
	
	public ControllerRequestEvent(ControllerRequestEventType projectStatusEventType, String projectFolderId) {
		this.controllerRequestEventType = projectStatusEventType;
		this.projectFolderId = projectFolderId;
	}
	

	@Override
	protected void dispatch(ControllerRequestEventHandler handler) {
		handler.onControllerRequest(this);
	}

	@Override
	public Type<ControllerRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ControllerRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ControllerRequestEvent projectStatusEvent) {
		source.fireEvent(projectStatusEvent);
	}

	public ControllerRequestEventType getControllerRequestEventType() {
		return controllerRequestEventType;
	}

	public String getProjectFolderId() {
		return projectFolderId;
	}

	@Override
	public String toString() {
		return "ControllerRequestEvent [controllerRequestEventType="
				+ controllerRequestEventType + ", projectFolderId="
				+ projectFolderId + "]";
	}

	
	
	
}
