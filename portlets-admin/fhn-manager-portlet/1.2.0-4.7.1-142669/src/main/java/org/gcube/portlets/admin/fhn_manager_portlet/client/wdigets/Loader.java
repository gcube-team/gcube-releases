package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Loader extends Composite {

	private static LoaderUiBinder uiBinder = GWT.create(LoaderUiBinder.class);

	interface LoaderUiBinder extends UiBinder<Widget, Loader> {
	}

	@UiField 
	Modal m;
	@UiField
	Label content;
	
	public Loader(String title,String message) {
		initWidget(uiBinder.createAndBindUi(this));
		m.setTitle(title);
		content.setText(message);
	}

	public void show(){
		m.show();
	}
	
	public void hide(){
		m.hide();
	}
	
	
}
