package org.gcube.portlets.user.joinvre.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoadingPanel extends Composite  {

	private static LoadingPanelUiBinder uiBinder = GWT.create(LoadingPanelUiBinder.class);

	interface LoadingPanelUiBinder extends UiBinder<Widget, LoadingPanel> {
	}

	public LoadingPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
