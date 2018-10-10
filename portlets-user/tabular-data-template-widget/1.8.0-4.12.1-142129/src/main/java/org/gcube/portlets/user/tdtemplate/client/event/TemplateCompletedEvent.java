/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 *
 */
public class TemplateCompletedEvent extends GwtEvent<TemplateComplitedEventHandler> {

	public static final GwtEvent.Type<TemplateComplitedEventHandler> TYPE = new Type<TemplateComplitedEventHandler>();
	private boolean isCompleted;

	@Override
	public Type<TemplateComplitedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public TemplateCompletedEvent(boolean bool) {
		this.isCompleted = bool;
	}

	@Override
	protected void dispatch(TemplateComplitedEventHandler handler) {
		handler.onTemplateComplitedEvent(this);

	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

}
