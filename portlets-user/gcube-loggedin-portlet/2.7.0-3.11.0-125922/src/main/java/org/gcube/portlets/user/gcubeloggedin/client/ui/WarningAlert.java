package org.gcube.portlets.user.gcubeloggedin.client.ui;

import org.gcube.portlets.user.gcubewidgets.client.elements.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WarningAlert extends Composite  {

	private static WarningAlertUiBinder uiBinder = GWT
			.create(WarningAlertUiBinder.class);

	interface WarningAlertUiBinder extends UiBinder<Widget, WarningAlert> {
	}
	
	@UiField Element errorMessage;
	@UiField Span cancelHandler;
	@UiField Span confirmHandler;
	
	private AboutView owner;
	
	public WarningAlert(String message, AboutView owner) {
		initWidget(uiBinder.createAndBindUi(this));
		errorMessage.setInnerText(message);
		this.owner = owner;
		cancelHandler.setHTML(" Cancel");
		
		confirmHandler.setHTML(" Confirm Leave");
	}
	

	@UiHandler("cancelHandler") 
	void onCloseClick(ClickEvent e) {
		this.removeFromParent();
	}
	
	@UiHandler("confirmHandler") 
	void onConfirmClick(ClickEvent e) {
		owner.abandonGroup();
	}
}
