package org.gcube.portlets.user.accountingdashboard.client.application.customwidget;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.accountingdashboard.client.application.customwidget.TagWidgetHideEvent.HasTagWidgetHideEventHandlers;
import org.gcube.portlets.user.accountingdashboard.client.application.customwidget.TagWidgetHideEvent.TagWidgetHideEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class TagWidget extends Composite implements HasText, HasTagWidgetHideEventHandlers {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	private static TagWidgetUiBinder uiBinder = GWT.create(TagWidgetUiBinder.class);

	interface TagWidgetUiBinder extends UiBinder<Widget, TagWidget> {
	}

	public TagWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		int typeInt = Event.getTypeInt(ClickEvent.getType().getName());
		sinkEvents(typeInt);
	}

	@UiField
	Button button;

	@UiField
	SpanElement tagLabel;

	public TagWidget(String label) {
		initWidget(uiBinder.createAndBindUi(this));
		tagLabel.setInnerText(label);
		button.setText("x");
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		logger.log(Level.FINE,"Button Click");
		TagWidgetHideEvent event = new TagWidgetHideEvent(this);
		fireEvent(event);

	}

	public void setText(String text) {
		tagLabel.setInnerText(text);
	}

	public String getText() {
		return tagLabel.getInnerText();
	}

	@Override
	public HandlerRegistration addTagWidgetHideEventHandler(TagWidgetHideEventHandler handler) {
		return addHandler(handler, TagWidgetHideEvent.TYPE);
	}

}
