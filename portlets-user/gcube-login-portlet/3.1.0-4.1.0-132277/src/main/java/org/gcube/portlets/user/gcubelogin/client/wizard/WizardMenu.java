package org.gcube.portlets.user.gcubelogin.client.wizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WizardMenu extends Composite {

	private static WizardMenuUiBinder uiBinder = GWT
			.create(WizardMenuUiBinder.class);

	interface WizardMenuUiBinder extends UiBinder<Widget, WizardMenu> {
	}

	public WizardMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
