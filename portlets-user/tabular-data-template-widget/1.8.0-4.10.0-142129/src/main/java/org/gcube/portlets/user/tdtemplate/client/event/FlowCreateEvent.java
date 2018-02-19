/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class FlowCreateEvent extends GwtEvent<FlowCreateEventHandler>  {
	
	public static final GwtEvent.Type<FlowCreateEventHandler> TYPE = new Type<FlowCreateEventHandler>();

	@Override
	public Type<FlowCreateEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	
	public FlowCreateEvent(){
	}


	@Override
	protected void dispatch(FlowCreateEventHandler handler) {
		handler.onFlowCreateEvent(this);
		
	}


}
