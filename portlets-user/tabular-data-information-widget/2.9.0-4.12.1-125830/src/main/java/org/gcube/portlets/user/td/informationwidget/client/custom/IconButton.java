package org.gcube.portlets.user.td.informationwidget.client.custom;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.widget.core.client.Component;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class IconButton extends Component implements HasClickHandlers {

	private IconButtonAppearance appearance;

	public IconButton() {
		this((IconButtonAppearance) GWT.create(DefaultAppearance.class));
	}

	public IconButton(IconButtonAppearance appearance) {
		this.appearance = appearance;

		SafeHtmlBuilder sb = new SafeHtmlBuilder();
		this.appearance.render(sb);
		
		XElement element=XDOM.create(sb.toSafeHtml());
		setElement((Element)element);
		sinkEvents(Event.ONCLICK);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	
	
	public void setIcon(ImageResource icon) {
		appearance.onUpdateIcon(getElement(), icon);
	}
}
