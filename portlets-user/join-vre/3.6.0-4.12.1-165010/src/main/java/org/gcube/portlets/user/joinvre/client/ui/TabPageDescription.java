package org.gcube.portlets.user.joinvre.client.ui;

import org.gcube.portlets.user.joinvre.shared.TabbedPage;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TabPageDescription extends Composite {

	private static TabPageDescriptionUiBinder uiBinder = GWT.create(TabPageDescriptionUiBinder.class);

	interface TabPageDescriptionUiBinder extends UiBinder<Widget, TabPageDescription> {
	}

	public TabPageDescription() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Paragraph desc;

	public TabPageDescription(TabbedPage theTab) {
		initWidget(uiBinder.createAndBindUi(this));
		desc.setText(theTab.getDescription());
	}



}
