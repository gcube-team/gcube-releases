/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ActiveFilterOnResultEvent extends GwtEvent<ActiveFilterOnResultEventHandler> {
	
	public static final GwtEvent.Type<ActiveFilterOnResultEventHandler> TYPE = new Type<ActiveFilterOnResultEventHandler>();
	private ResultFilter activeFilterObject;

	@Override
	public Type<ActiveFilterOnResultEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ActiveFilterOnResultEventHandler handler) {
		handler.onActiveFilter(this);	
	}
	
	public ActiveFilterOnResultEvent(ResultFilter activeFilter) {
		this.activeFilterObject = activeFilter;
	}

	public ResultFilter getActiveFilterObject() {
		return activeFilterObject;
	}
}
