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
public class TemplateCreatedEvent extends GwtEvent<TemplateCreatedEventHandler> {

	public static final GwtEvent.Type<TemplateCreatedEventHandler> TYPE = new Type<TemplateCreatedEventHandler>();
	private boolean validate;
	private boolean save;

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TemplateCreatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Instantiates a new template created event.
	 *
	 * @param validate the validate
	 * @param save the save
	 */
	public TemplateCreatedEvent(boolean validate, boolean save) {
		this.validate = validate;
		this.save = save;
	}

	/**
	 * Checks if is validate.
	 *
	 * @return the validate
	 */
	public boolean isValidate() {
		return validate;
	}

	/**
	 * Sets the validate.
	 *
	 * @param validate the validate to set
	 */
	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * Checks if is save.
	 *
	 * @return the save
	 */
	public boolean isSave() {
		return save;
	}

	/**
	 * Sets the save.
	 *
	 * @param save the save to set
	 */
	public void setSave(boolean save) {
		this.save = save;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(TemplateCreatedEventHandler handler) {
		handler.onTemplateCreated(this);

	}

}
