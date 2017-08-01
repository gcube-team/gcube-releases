package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Raised when the user wants to see his datasets.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowDatasetsEvent extends GwtEvent<ShowDatasetsEventHandler>{
	
	public static Type<ShowDatasetsEventHandler> TYPE = new Type<ShowDatasetsEventHandler>();
	
	private boolean ownOnly;
	
	/**
	 * Instantiates a new show user datasets event.
	 */
	public ShowDatasetsEvent(boolean ownOnly) {
		
		this.ownOnly = ownOnly;
		
	}
	
	public boolean isOwnOnly() {
		return ownOnly;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowDatasetsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowDatasetsEventHandler handler) {
		handler.onShowDatasets(this);
	}

}
