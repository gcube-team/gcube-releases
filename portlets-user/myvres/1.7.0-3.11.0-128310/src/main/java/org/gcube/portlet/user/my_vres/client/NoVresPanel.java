package org.gcube.portlet.user.my_vres.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NoVresPanel extends Composite {

	private static NoVresPanelUiBinder uiBinder = GWT
			.create(NoVresPanelUiBinder.class);

	interface NoVresPanelUiBinder extends UiBinder<Widget, NoVresPanel> {
	}

	public NoVresPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public NoVresPanel(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
