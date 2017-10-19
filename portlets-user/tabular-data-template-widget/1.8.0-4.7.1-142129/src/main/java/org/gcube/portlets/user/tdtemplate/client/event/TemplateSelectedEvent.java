/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class TemplateSelectedEvent extends 	GwtEvent<TemplateSelectedEventHandler>  {
	
	public static final GwtEvent.Type<TemplateSelectedEventHandler> TYPE = new Type<TemplateSelectedEventHandler>();
	private TemplateSwitcherInteface switcher;

	
	@Override
	public Type<TemplateSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	
	public TemplateSelectedEvent(TemplateSwitcherInteface switcher){
		this.switcher = switcher;
	}


	@Override
	protected void dispatch(TemplateSelectedEventHandler handler) {
		handler.onTemplateSelectedEvent(this);
		
	}

	public TemplateSwitcherInteface getSwitcher() {
		return switcher;
	}

}
