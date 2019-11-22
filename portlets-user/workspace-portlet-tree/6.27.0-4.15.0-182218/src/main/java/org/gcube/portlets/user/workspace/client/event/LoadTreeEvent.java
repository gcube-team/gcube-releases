package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class LoadTreeEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 31, 2019
 */
public class LoadTreeEvent extends GwtEvent<LoadTreeEventHandler> {
	public static Type<LoadTreeEventHandler> TYPE = new Type<LoadTreeEventHandler>();
	private boolean selectRoot;

	/**
	 * Instantiates a new load tree event.
	 *
	 * @param selectRoot the select root
	 */
	public LoadTreeEvent(boolean selectRoot) {
		this.selectRoot = selectRoot;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadTreeEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadTreeEventHandler handler) {
		handler.doLoadTree(this);

	}
	
	/**
	 * Checks if is select root.
	 *
	 * @return true, if is select root
	 */
	public boolean isSelectRoot() {
		return selectRoot;
	}
}
