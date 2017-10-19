/**
 * 
 */
package org.gcube.portlets.user.tdw.client.event;

import org.gcube.portlets.user.tdw.shared.model.TableId;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OpenTableEvent extends GwtEvent<OpenTableEventHandler> {
	
	public static GwtEvent.Type<OpenTableEventHandler> TYPE = new Type<OpenTableEventHandler>();

	@Override
	public Type<OpenTableEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OpenTableEventHandler handler) {
		handler.onOpenTable(this);	
	}
	
	protected TableId tableId;

	/**
	 * @param tableId
	 */
	public OpenTableEvent(TableId tableId) {
		this.tableId = tableId;
	}

	/**
	 * @return the tableId
	 */
	public TableId getTableId() {
		return tableId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OpenTableEvent [tableId=");
		builder.append(tableId);
		builder.append("]");
		return builder.toString();
	}
}
