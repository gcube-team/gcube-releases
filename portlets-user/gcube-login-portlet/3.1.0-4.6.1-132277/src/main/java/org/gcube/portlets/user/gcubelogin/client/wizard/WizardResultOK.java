package org.gcube.portlets.user.gcubelogin.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WizardResultOK extends Composite {

	private static WizardResultOKUiBinder uiBinder = GWT
			.create(WizardResultOKUiBinder.class);

	interface WizardResultOKUiBinder extends UiBinder<Widget, WizardResultOK> {
	}

	public WizardResultOK() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	

}
