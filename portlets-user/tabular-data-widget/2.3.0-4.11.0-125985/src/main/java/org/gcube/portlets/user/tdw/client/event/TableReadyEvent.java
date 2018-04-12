/**
 * 
 */
package org.gcube.portlets.user.tdw.client.event;

import org.gcube.portlets.user.tdw.shared.model.TableDefinition;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TableReadyEvent extends GwtEvent<TableReadyEventHandler> {
	
	public static GwtEvent.Type<TableReadyEventHandler> TYPE = new Type<TableReadyEventHandler>();

	@Override
	public Type<TableReadyEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TableReadyEventHandler handler) {
		handler.onTableReady(this);	
	}
	
	protected TableDefinition tableDefinition;

	/**
	 * @param tableDefinition
	 */
	public TableReadyEvent(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
	}

	/**
	 * @return the tableDefinition
	 */
	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableReadyEvent [tableDefinition=");
		builder.append(tableDefinition);
		builder.append("]");
		return builder.toString();
	}
}
