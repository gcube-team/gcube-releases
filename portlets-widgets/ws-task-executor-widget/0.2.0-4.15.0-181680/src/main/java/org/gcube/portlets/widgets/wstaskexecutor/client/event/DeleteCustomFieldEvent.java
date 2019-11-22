package org.gcube.portlets.widgets.wstaskexecutor.client.event;
import org.gcube.portlets.widgets.wstaskexecutor.client.view.binder.CustomFieldEntry;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class DeleteCustomFieldEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 8, 2018
 */
public class DeleteCustomFieldEvent  extends GwtEvent<DeleteCustomFieldEventHandler> {
	public static Type<DeleteCustomFieldEventHandler> TYPE = new Type<DeleteCustomFieldEventHandler>();

	private CustomFieldEntry removedEntry;

	/**
	 * Instantiates a new delete custom field event.
	 *
	 * @param removedEntry the removed entry
	 */
	public DeleteCustomFieldEvent(CustomFieldEntry removedEntry) {
		this.removedEntry = removedEntry;
	}

	/**
	 * Gets the removed entry.
	 *
	 * @return the removed entry
	 */
	public CustomFieldEntry getRemovedEntry() {
		return removedEntry;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DeleteCustomFieldEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteCustomFieldEventHandler handler) {
		handler.onRemoveEntry(this);
	}
}
