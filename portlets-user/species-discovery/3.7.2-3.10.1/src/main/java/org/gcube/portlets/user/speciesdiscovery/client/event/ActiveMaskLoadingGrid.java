/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ActiveMaskLoadingGrid extends GwtEvent<ActiveMaskLoadingGridHandler> {
	
	public static final GwtEvent.Type<ActiveMaskLoadingGridHandler> TYPE = new Type<ActiveMaskLoadingGridHandler>();
	private boolean active;

	@Override
	public Type<ActiveMaskLoadingGridHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ActiveMaskLoadingGridHandler handler) {
		handler.onActiveMaskLoadingGrid(this);	
	}
	
	public ActiveMaskLoadingGrid(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
	
	
