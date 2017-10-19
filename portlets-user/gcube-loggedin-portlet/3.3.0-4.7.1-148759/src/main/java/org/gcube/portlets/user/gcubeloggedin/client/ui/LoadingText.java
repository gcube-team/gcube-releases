package org.gcube.portlets.user.gcubeloggedin.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LoadingText extends Composite {

	private static LoadingTextUiBinder uiBinder = GWT
			.create(LoadingTextUiBinder.class);

	interface LoadingTextUiBinder extends UiBinder<Widget, LoadingText> {
	}

	public LoadingText() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
