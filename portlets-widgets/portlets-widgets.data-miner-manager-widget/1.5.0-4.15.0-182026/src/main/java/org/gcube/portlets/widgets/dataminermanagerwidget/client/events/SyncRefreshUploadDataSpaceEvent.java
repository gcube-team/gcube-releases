package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * SyncRefreshUpload
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SyncRefreshUploadDataSpaceEvent
		extends GwtEvent<SyncRefreshUploadDataSpaceEvent.SyncRefreshUploadDataSpaceEventHandler> {

	public static Type<SyncRefreshUploadDataSpaceEventHandler> TYPE = new Type<SyncRefreshUploadDataSpaceEventHandler>();

	public interface SyncRefreshUploadDataSpaceEventHandler extends EventHandler {
		void onRefresh(SyncRefreshUploadDataSpaceEvent event);
	}

	public interface HasSyncRefreshUploadDataSpaceEventHandler extends HasHandlers {
		public HandlerRegistration addSyncRefreshUploadDataSpaceEventHandler(
				SyncRefreshUploadDataSpaceEventHandler handler);
	}

	public SyncRefreshUploadDataSpaceEvent() {

	}

	@Override
	protected void dispatch(SyncRefreshUploadDataSpaceEventHandler handler) {
		handler.onRefresh(this);
	}

	@Override
	public Type<SyncRefreshUploadDataSpaceEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SyncRefreshUploadDataSpaceEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, SyncRefreshUploadDataSpaceEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	@Override
	public String toString() {
		return "SyncRefreshUploadDataSpaceEvent []";
	}

}
