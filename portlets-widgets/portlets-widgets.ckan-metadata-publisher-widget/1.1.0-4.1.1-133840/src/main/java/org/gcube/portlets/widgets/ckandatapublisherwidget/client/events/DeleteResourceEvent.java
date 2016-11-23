package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Deleted resource event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DeleteResourceEvent extends GwtEvent<DeleteResourceEventHandler> {
	public static Type<DeleteResourceEventHandler> TYPE = new Type<DeleteResourceEventHandler>();

	private ResourceBeanWrapper resource;
	
	public DeleteResourceEvent(ResourceBeanWrapper resource) {
		this.resource = resource;
	}

	public ResourceBeanWrapper getResource() {
		return resource;
	}
	
	@Override
	public Type<DeleteResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteResourceEventHandler handler) {
		handler.onDeletedResource(this);
	}

}
