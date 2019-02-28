/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class UpdateRowSelectionEvent extends GwtEvent<UpdateRowSelectionEventHandler> {
	
	public static final GwtEvent.Type<UpdateRowSelectionEventHandler> TYPE = new Type<UpdateRowSelectionEventHandler>();
	private int rowId;
	private boolean selectionValue;


	@Override
	public Type<UpdateRowSelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateRowSelectionEventHandler handler) {
		handler.onUpdateRowSelection(this);	
	}
	
	public UpdateRowSelectionEvent(int rowId, boolean selectionValue){
		this.rowId = rowId;
		this.selectionValue = selectionValue;
		
	}

	public int getRowId() {
		return rowId;
	}

	public boolean getSelectionValue() {
		return selectionValue;
	}
	
}
