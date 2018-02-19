package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Called on close form.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CloseCreationFormEvent extends GwtEvent<CloseCreationFormEventHandler>{
	
	public static Type<CloseCreationFormEventHandler> TYPE = new Type<CloseCreationFormEventHandler>();
	
	public CloseCreationFormEvent() {
		super();
	}

	@Override
	public Type<CloseCreationFormEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CloseCreationFormEventHandler handler) {
		handler.onClose(this);
	}

}
