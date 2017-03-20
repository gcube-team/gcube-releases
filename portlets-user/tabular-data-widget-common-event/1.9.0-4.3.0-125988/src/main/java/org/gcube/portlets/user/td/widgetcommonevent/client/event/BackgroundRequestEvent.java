package org.gcube.portlets.user.td.widgetcommonevent.client.event;


import org.gcube.portlets.user.td.widgetcommonevent.client.type.BackgroundRequestType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class BackgroundRequestEvent extends GwtEvent<BackgroundRequestEvent.BackgroundRequestEventHandler> {

	public static Type<BackgroundRequestEventHandler> TYPE = new Type<BackgroundRequestEventHandler>();
	private BackgroundRequestType backgroundRequestType;

	public interface BackgroundRequestEventHandler extends EventHandler {
		void onBackgroundRequest(BackgroundRequestEvent event);
	}

	public interface HasBackgroundRequestEventHandler extends HasHandlers{
		public HandlerRegistration addBackgroundRequestEventHandler(BackgroundRequestEventHandler handler);
	}
	
	public BackgroundRequestEvent(BackgroundRequestType backgroundRequestType) {
		this.backgroundRequestType = backgroundRequestType;
	}

	public BackgroundRequestType getBackgroundRequestType() {
		return backgroundRequestType;
	}

	@Override
	protected void dispatch(BackgroundRequestEventHandler handler) {
		handler.onBackgroundRequest(this);
	}

	@Override
	public Type<BackgroundRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<BackgroundRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, BackgroundRequestType backgroundRequestType) {
		source.fireEvent(new BackgroundRequestEvent(backgroundRequestType));
	}

	@Override
	public String toString() {
		return "BackgroundRequestEvent [backgroundRequestType=" + backgroundRequestType + "]";
	}
	
	
	
}
