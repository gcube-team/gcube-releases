package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class HideMonitorEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Oct 14, 2015
 */
public class HideMonitorEvent extends GwtEvent<HideMonitorEventHandler> {
	public static Type<HideMonitorEventHandler> TYPE = new Type<HideMonitorEventHandler>();

	/**
	 * Instantiates a new cancel upload event.
	 */
	public HideMonitorEvent() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<HideMonitorEventHandler> getAssociatedType() {
		return TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared
	 * .EventHandler)
	 */
	@Override
	protected void dispatch(HideMonitorEventHandler handler) {
		handler.onHideMonitor(this);
	}
}
