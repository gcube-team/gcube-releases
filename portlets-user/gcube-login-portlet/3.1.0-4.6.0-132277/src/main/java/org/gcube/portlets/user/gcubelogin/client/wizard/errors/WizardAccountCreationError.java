package org.gcube.portlets.user.gcubelogin.client.wizard.errors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WizardAccountCreationError extends Composite  {

	private static WizardAccountCreationErrorUiBinder uiBinder = GWT
			.create(WizardAccountCreationErrorUiBinder.class);

	interface WizardAccountCreationErrorUiBinder extends
			UiBinder<Widget, WizardAccountCreationError> {
	}

	public WizardAccountCreationError() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	

}
