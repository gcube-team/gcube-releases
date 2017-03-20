package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PopupMessage extends Composite {

	private static PopupMessageUiBinder uiBinder = GWT
			.create(PopupMessageUiBinder.class);

	interface PopupMessageUiBinder extends UiBinder<Widget, PopupMessage> {
	}

	@UiField 
	Modal m;
	@UiField
	Label content;
	
	
	public PopupMessage(String title,String message) {
		initWidget(uiBinder.createAndBindUi(this));
		m.setTitle(title);
		content.setText(message);
	}

	
	public void show(){
		m.show();
	}
}
