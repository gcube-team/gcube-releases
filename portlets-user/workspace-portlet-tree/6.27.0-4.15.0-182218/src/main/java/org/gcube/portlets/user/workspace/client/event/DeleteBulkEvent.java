package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class DeleteBulkEvent extends GwtEvent<DeleteBulkEventHandler> {
	public static Type<DeleteBulkEventHandler> TYPE = new Type<DeleteBulkEventHandler>();
	
	private String bulkId = null;
	
	public DeleteBulkEvent(String bulkId) {
		this.bulkId = bulkId;
	}

	@Override
	public Type<DeleteBulkEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteBulkEventHandler handler) {
		handler.onDeleteBulk(this);
		
	}

	public String getBulkId() {
		return bulkId;
	}

}
