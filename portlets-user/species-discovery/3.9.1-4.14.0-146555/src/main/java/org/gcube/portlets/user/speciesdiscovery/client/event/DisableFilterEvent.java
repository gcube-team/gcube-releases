/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DisableFilterEvent extends GwtEvent<DisableFilterEventHandler> {
	
	public static final GwtEvent.Type<DisableFilterEventHandler> TYPE = new Type<DisableFilterEventHandler>();

	@Override
	public Type<DisableFilterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DisableFilterEventHandler handler) {
		handler.onDisableFilter(this);	
	}


}
