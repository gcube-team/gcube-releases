package org.gcube.portlets.widgets.inviteswidget.client.ui;

import org.gcube.portlets.widgets.inviteswidget.client.validation.FormErrorsValidation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class InviteWidget extends Composite{
	


	private static InviteWidgetUiBinder uiBinder = GWT.create(InviteWidgetUiBinder.class);
	
	interface InviteWidgetUiBinder extends UiBinder<Widget, InviteWidget> {	}

	@UiField HTML header;
	@UiField SimplePanel validationErrorsFormPanel;

	public InviteWidget() {
		super();
		initWidget(uiBinder.createAndBindUi(this));
		new FormErrorsValidation().start(validationErrorsFormPanel, null);
	}
	/**
	 * if you want to add an header text use this constructor
	 * @param headerText
	 */
	public InviteWidget(String headerText) {
		this();
		header.setText(headerText);
	}
}
