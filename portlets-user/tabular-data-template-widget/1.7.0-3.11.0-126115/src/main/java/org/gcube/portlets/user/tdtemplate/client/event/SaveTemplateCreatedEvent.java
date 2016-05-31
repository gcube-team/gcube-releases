/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class TemplateCreatedEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 */
public class SaveTemplateCreatedEvent extends GwtEvent<SaveTemplateCreatedEventHandler> {

	public static final GwtEvent.Type<SaveTemplateCreatedEventHandler> TYPE = new Type<SaveTemplateCreatedEventHandler>();
	private boolean isUpdate = false;

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SaveTemplateCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Instantiates a new template created event.
	 *
	 * @param validate the validate
	 * @param save the save
	 */
	public SaveTemplateCreatedEvent(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	/**
	 * @return the isUpdate
	 */
	public boolean isUpdate() {
		return isUpdate;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SaveTemplateCreatedEventHandler handler) {
		handler.onSaveTemplate(this);

	}

}
