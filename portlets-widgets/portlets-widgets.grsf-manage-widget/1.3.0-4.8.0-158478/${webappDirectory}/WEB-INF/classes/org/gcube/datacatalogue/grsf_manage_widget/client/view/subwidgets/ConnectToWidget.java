package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ConnectToWidget extends Composite{

	private static ConnectToWidgetUiBinder uiBinder = GWT
			.create(ConnectToWidgetUiBinder.class);

	interface ConnectToWidgetUiBinder extends UiBinder<Widget, ConnectToWidget> {
	}

	public ConnectToWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ConnectToWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
		
		
	}

}
