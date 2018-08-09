package org.gcube.portlets.widgets.wsthreddssync.client.event;

import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class PerformDoUnSyncEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public class PerformDoUnSyncEvent extends GwtEvent<PerformDoUnSyncEventHandler> {

	/** The type. */
	public static Type<PerformDoUnSyncEventHandler> TYPE = new Type<PerformDoUnSyncEventHandler>();
	private WsFolder folder;


	/**
	 * Instantiates a new perform do sync event.
	 *
	 * @param folder the folder
	 */
	public PerformDoUnSyncEvent(WsFolder folder) {
		this.folder = folder;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<PerformDoUnSyncEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(PerformDoUnSyncEventHandler handler) {
		handler.onPerformDoUnSync(this);
	}


	/**
	 * Gets the folder.
	 *
	 * @return the folder
	 */
	public WsFolder getFolder() {
		return folder;
	}

}
