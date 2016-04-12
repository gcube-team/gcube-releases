package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class MyHyperlink extends Widget implements EventListener {

	private ClickListener listener;
	
	public MyHyperlink(Widget parent, String text, ClickListener clickListener) {
		Element myElement = DOM.createAnchor();
		setElement(myElement);
		
		// style the link
		setStyleName("collection-moreLink");
		
		// prevent doubleclick text selection
		DOM.setElementAttribute(myElement, "href", "#");
		
		
		// set the link text
		DOM.setInnerHTML(myElement, text);
		
		// append the hyperlink to the parent widget
		DOM.appendChild(parent.getElement(), myElement);
		
		// listen for click events
		DOM.sinkEvents(myElement, Event.ONCLICK);
		DOM.setEventListener(myElement, this);
		listener = clickListener;
	}

	public void onBrowserEvent(Event event) {
		if (DOM.eventGetType(event) == Event.ONCLICK) {
				listener.onClick(this);
				DOM.eventPreventDefault(event);
		}
		else
			super.onBrowserEvent(event);
	}
}
