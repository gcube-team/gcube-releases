package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Raised when the user wants to see his datasets.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowUserDatasetsEvent extends GwtEvent<ShowUserDatasetsEventHandler>{
	
	public static Type<ShowUserDatasetsEventHandler> TYPE = new Type<ShowUserDatasetsEventHandler>();
	
	/**
	 * Instantiates a new show user datasets event.
	 */
	public ShowUserDatasetsEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShowUserDatasetsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShowUserDatasetsEventHandler handler) {
		handler.onShowDatasets(this);
	}

}
