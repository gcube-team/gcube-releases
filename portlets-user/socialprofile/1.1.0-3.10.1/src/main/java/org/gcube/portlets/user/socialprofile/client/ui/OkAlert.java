package org.gcube.portlets.user.socialprofile.client.ui;

import org.gcube.portlets.user.gcubewidgets.client.elements.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class OkAlert extends Composite {

	private static OkAlertUiBinder uiBinder = GWT.create(OkAlertUiBinder.class);

	interface OkAlertUiBinder extends UiBinder<Widget, OkAlert> {
	}
	@UiField Element message;
	@UiField Span handler;
	
	public OkAlert(String message2Show, boolean removable) {
		initWidget(uiBinder.createAndBindUi(this));
		message.setInnerHTML(message2Show);
		if (removable)
			handler.setHTML(" Close");
	}

	@UiHandler("handler") 
	void onCloseClick(ClickEvent e) {
		this.removeFromParent();
	}
}
