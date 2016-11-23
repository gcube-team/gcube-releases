/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class DeleteColumnOperationEvent extends GwtEvent<DeleteColumnOperationEventHandler>  {
	
	public static final GwtEvent.Type<DeleteColumnOperationEventHandler> TYPE = new Type<DeleteColumnOperationEventHandler>();
	private TdColumnDefinition column;

	/**
	 * @return the column
	 */
	public TdColumnDefinition getColumn() {
		return column;
	}

	/**
	 * @param col
	 */
	public DeleteColumnOperationEvent(TdColumnDefinition col) {
		this.column = col;
	}

	@Override
	public Type<DeleteColumnOperationEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(DeleteColumnOperationEventHandler handler) {
		handler.onDeleteColumnOperation(this);
	}
}
