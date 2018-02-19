package org.gcube.portlets.widgets.wsmail.client.multisuggests;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 *
 */
public class ListItem extends ComplexPanel {
	HandlerRegistration clickHandler;
	
	public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
		return addDomHandler(handler, KeyDownEvent.getType());
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	public String getHTML() {
		return DOM.getInnerHTML(getElement());
	}

	public void setHTML(String html) {
		DOM.setInnerHTML(getElement(), (html == null) ? "" : html);
	}
	
	public ListItem() {
		setElement(DOM.createElement("li"));
	}
	
	public void setId(String id) {
		DOM.setElementAttribute(getElement(), "id", id);
	}
	
	public void add(Widget w) {
		super.add(w, getElement());
	}

	public void insert(Widget w, int beforeIndex) {
		super.insert(w, getElement(), beforeIndex, true);
	}

	public String getText() {
		return DOM.getInnerText(getElement());
	}

	public void setText(String text) {
		DOM.setInnerText(getElement(), (text == null) ? "" : text);
	}

	
}