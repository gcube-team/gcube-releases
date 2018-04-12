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
public class BackToTemplateCreatedEvent extends GwtEvent<BackToTemplateCreatedEventHandler> {

	public static final GwtEvent.Type<BackToTemplateCreatedEventHandler> TYPE = new Type<BackToTemplateCreatedEventHandler>();

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<BackToTemplateCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Instantiates a new template created event.
	 *
	 * @param validate the validate
	 * @param save the save
	 */
	public BackToTemplateCreatedEvent() {
		
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(BackToTemplateCreatedEventHandler handler) {
		handler.onBack(this);

	}

}
