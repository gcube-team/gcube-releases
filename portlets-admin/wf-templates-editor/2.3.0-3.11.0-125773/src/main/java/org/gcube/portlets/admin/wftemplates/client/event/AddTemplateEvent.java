package org.gcube.portlets.admin.wftemplates.client.event;

import com.google.gwt.event.shared.GwtEvent;
/**
 * <code> AddTemplateEvent </code>  is the event fired in case of new template button clicked
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AddTemplateEvent extends GwtEvent<AddTemplateEventHandler> {
	public static Type<AddTemplateEventHandler> TYPE = new Type<AddTemplateEventHandler>();
	private String templateName;
	public AddTemplateEvent(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public Type<AddTemplateEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(AddTemplateEventHandler handler) {
		handler.onAddTemplates(this);
		
	}

	public String getTemplateName() {
		return templateName;
	}
}
