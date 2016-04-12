package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.save;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;

public class SaveCompoundMapRequest extends SaveRequest {

	private CompoundMapItem toSave;
	
	public SaveCompoundMapRequest() {
		// TODO Auto-generated constructor stub
	}
	public SaveCompoundMapRequest(SaveOperationType type, CompoundMapItem toSave,
			String destinationBasketId, String toSaveName) {
		super();
		setType(type);		
		setDestinationBasketId(destinationBasketId);
		setToSaveName(toSaveName);
		setToSave(toSave);
	}
	public CompoundMapItem getToSave() {
		return toSave;
	}
	public void setToSave(CompoundMapItem toSave) {
		this.toSave = toSave;
	}
}
