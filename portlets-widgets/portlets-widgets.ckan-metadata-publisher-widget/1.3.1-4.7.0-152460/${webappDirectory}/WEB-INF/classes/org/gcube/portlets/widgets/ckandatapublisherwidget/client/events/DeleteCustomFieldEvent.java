package org.gcube.portlets.widgets.ckandatapublisherwidget.client.events;
import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata.CustomFieldEntry;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Delete custom field event.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DeleteCustomFieldEvent  extends GwtEvent<DeleteCustomFieldEventHandler> {
	public static Type<DeleteCustomFieldEventHandler> TYPE = new Type<DeleteCustomFieldEventHandler>();

	private CustomFieldEntry removedEntry;

	public DeleteCustomFieldEvent(CustomFieldEntry removedEntry) {
		this.removedEntry = removedEntry;
	}
	
	public CustomFieldEntry getRemovedEntry() {
		return removedEntry;
	}

	@Override
	public Type<DeleteCustomFieldEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DeleteCustomFieldEventHandler handler) {
		handler.onRemoveEntry(this);
	}
}
