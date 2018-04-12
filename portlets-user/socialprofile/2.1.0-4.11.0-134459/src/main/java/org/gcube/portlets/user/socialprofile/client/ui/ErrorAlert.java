package org.gcube.portlets.user.socialprofile.client.ui;

import org.gcube.portlets.user.gcubewidgets.client.elements.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ErrorAlert extends Composite  {

	private static ErrorAlertUiBinder uiBinder = GWT
			.create(ErrorAlertUiBinder.class);

	interface ErrorAlertUiBinder extends UiBinder<Widget, ErrorAlert> {
	}
	
	@UiField Element errorMessage;
	@UiField Span handler;
	
	public ErrorAlert(String message, boolean removable) {
		initWidget(uiBinder.createAndBindUi(this));
		errorMessage.setInnerText(message);
		if (removable)
			handler.setHTML(" Close");
	}
	

	@UiHandler("handler") 
	void onCloseClick(ClickEvent e) {
		this.removeFromParent();
	}
}
