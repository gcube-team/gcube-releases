package org.gcube.portlets.user.shareupdates.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class LinkLoader extends Composite {

	private static LinkLoaderUiBinder uiBinder = GWT
			.create(LinkLoaderUiBinder.class);

	interface LinkLoaderUiBinder extends UiBinder<Widget, LinkLoader> {
	}

	public LinkLoader() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
