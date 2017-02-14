/**
 * 
 */
package org.gcube.portlets.user.tdw.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CloseTableEvent extends GwtEvent<CloseTableEventHandler> {
	
	public static GwtEvent.Type<CloseTableEventHandler> TYPE = new Type<CloseTableEventHandler>();

	@Override
	public Type<CloseTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CloseTableEventHandler handler) {
		handler.onCloseTable(this);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpenTableEvent []");
		return builder.toString();
	}
}
