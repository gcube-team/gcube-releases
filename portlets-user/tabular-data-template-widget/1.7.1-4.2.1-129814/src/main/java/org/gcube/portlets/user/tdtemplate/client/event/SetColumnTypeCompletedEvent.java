/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 *
 */
public class SetColumnTypeCompletedEvent extends GwtEvent<SetColumnTypeCompletedEventHandler> {

	public static final GwtEvent.Type<SetColumnTypeCompletedEventHandler> TYPE = new Type<SetColumnTypeCompletedEventHandler>();


	@Override
	public Type<SetColumnTypeCompletedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public SetColumnTypeCompletedEvent() {
	}

	@Override
	protected void dispatch(SetColumnTypeCompletedEventHandler handler) {
		handler.onSetTypeCompleted(this);

	}

}
