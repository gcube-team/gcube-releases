package org.gcube.portlets.user.workspace.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class UpdadeTreePanelLevelEvent extends GwtEvent<UpdadeTreePanelLevelEventHandler>{
	public static Type<UpdadeTreePanelLevelEventHandler> TYPE = new Type<UpdadeTreePanelLevelEventHandler>();
	
	private String parentIdentifier = null;
	
	public UpdadeTreePanelLevelEvent(String parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	@Override
	public Type<UpdadeTreePanelLevelEventHandler> getAssociatedType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	protected void dispatch(UpdadeTreePanelLevelEventHandler handler) {
		handler.onUpdateTreePanel(this);
		
	}

	public String getFolderIdentifier() {
		return parentIdentifier;
	}
}
