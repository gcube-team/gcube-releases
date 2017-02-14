package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class HeaderLayout extends Composite  {

	private static HeaderLayoutUiBinder uiBinder = GWT
			.create(HeaderLayoutUiBinder.class);

	interface HeaderLayoutUiBinder extends UiBinder<Widget, HeaderLayout> {
	}

	public HeaderLayout() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	

}
