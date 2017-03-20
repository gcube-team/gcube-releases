package org.gcube.portlets.user.gcubelogin.client.wizard.errors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class WizardError extends Composite{

	private static WizardErrorUiBinder uiBinder = GWT
			.create(WizardErrorUiBinder.class);

	interface WizardErrorUiBinder extends UiBinder<Widget, WizardError> {
	}

	public WizardError() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	

}
