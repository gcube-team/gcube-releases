package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.model.BulkCreatorModel;

import com.google.gwt.event.shared.GwtEvent;

public class BulkCreatorEvent extends GwtEvent<BulkCreatorEventHandler> {
	public static Type<BulkCreatorEventHandler> TYPE = new Type<BulkCreatorEventHandler>();
	private List<BulkCreatorModel> listBulks;
	
	public BulkCreatorEvent(List<BulkCreatorModel> listBulks) {
		this.listBulks = listBulks;
	}

	@Override
	public Type<BulkCreatorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(BulkCreatorEventHandler handler) {
		handler.onBulkCreator(this);
		
	}

	public List<BulkCreatorModel> getListBulks() {
		return listBulks;
	}
}
