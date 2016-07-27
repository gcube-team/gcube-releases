package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceBeanWrapper;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Added resource event
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddResourceEvent extends GwtEvent<AddResourceEventHandler> {
	public static Type<AddResourceEventHandler> TYPE = new Type<AddResourceEventHandler>();

	private ResourceBeanWrapper resource;

	public AddResourceEvent(ResourceBeanWrapper resource) {
		this.resource = resource;
	}

	public ResourceBeanWrapper getResource() {
		return resource;
	}

	@Override
	public Type<AddResourceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddResourceEventHandler handler) {
		handler.onAddedResource(this);
	}
}
