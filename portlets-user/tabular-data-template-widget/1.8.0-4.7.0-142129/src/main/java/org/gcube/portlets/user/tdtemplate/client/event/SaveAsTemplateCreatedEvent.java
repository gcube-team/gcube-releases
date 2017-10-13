/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class SaveAsTemplateCreatedEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 16, 2015
 */
public class SaveAsTemplateCreatedEvent extends GwtEvent<SaveAsTemplateCreatedEventHandler> {

	public static final GwtEvent.Type<SaveAsTemplateCreatedEventHandler> TYPE = new Type<SaveAsTemplateCreatedEventHandler>();
	private String newTemplateName;

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SaveAsTemplateCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Instantiates a new save as template created event.
	 *
	 * @param newTemplateName the new template name
	 */
	public SaveAsTemplateCreatedEvent(String newTemplateName) {
		this.newTemplateName = newTemplateName;
	}

	/**
	 * Gets the new template name.
	 *
	 * @return the newTemplateName
	 */
	public String getNewTemplateName() {
		return newTemplateName;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SaveAsTemplateCreatedEventHandler handler) {
		handler.onSaveAsTemplate(this);
	}

}
