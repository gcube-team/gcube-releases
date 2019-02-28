package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Deleted resource event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DeleteResourceEvent extends GwtEvent<DeleteResourceEventHandler> {
	public static Type<DeleteResourceEventHandler> TYPE = new Type<DeleteResourceEventHandler>();

	private ResourceElementBean resource;
	
	public DeleteResourceEvent(ResourceElementBean resource) {
		this.resource = resource;
	}

	public ResourceElementBean getResource() {
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
