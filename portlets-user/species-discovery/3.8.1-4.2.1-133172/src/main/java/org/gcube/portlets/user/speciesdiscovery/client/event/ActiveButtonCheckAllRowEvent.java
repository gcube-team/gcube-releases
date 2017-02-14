/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ActiveButtonCheckAllRowEvent extends GwtEvent<ActiveButtonCheckAllRowEventHandler> {
	
	public static final GwtEvent.Type<ActiveButtonCheckAllRowEventHandler> TYPE = new Type<ActiveButtonCheckAllRowEventHandler>();
	private boolean activeFilter;

	@Override
	public Type<ActiveButtonCheckAllRowEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ActiveButtonCheckAllRowEventHandler handler) {
		handler.onActiveCkeckAllRow(this);	
	}
	
	public ActiveButtonCheckAllRowEvent(boolean activeFilter) {
		this.activeFilter = activeFilter;
	}

	public boolean isActiveFilter() {
		return activeFilter;
	}
}
