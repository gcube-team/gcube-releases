package org.gcube.portlets.widgets.wsthreddssync.client.event;

import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class PerformDoSyncEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 15, 2018
 */
public class ShowMonitorSyncStatusEvent extends GwtEvent<ShowMonitorSyncStatusEventHandler> {
	
	/** The type. */
	public static Type<ShowMonitorSyncStatusEventHandler> TYPE = new Type<ShowMonitorSyncStatusEventHandler>();
	private WsFolder folder;

	
	/**
	 * Instantiates a new perform do sync event.
	 *
	 * @param folder the folder
	 * @param conf the conf
	 */
	public ShowMonitorSyncStatusEvent(WsFolder folder) {
		this.folder = folder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowMonitorSyncStatusEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowMonitorSyncStatusEventHandler handler) {
		handler.onShowMonitorSyncStatus(this);
	}
	
	public WsFolder getFolder() {
		return folder;
	}



}
