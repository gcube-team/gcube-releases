/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class UndoLastOperationEvent extends GwtEvent<UndoLastOperationEventHandler>  {
	
	public static final GwtEvent.Type<UndoLastOperationEventHandler> TYPE = new Type<UndoLastOperationEventHandler>();

	@Override
	public Type<UndoLastOperationEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(UndoLastOperationEventHandler handler) {
		handler.onUndoLastOperation(this);
	}
}
