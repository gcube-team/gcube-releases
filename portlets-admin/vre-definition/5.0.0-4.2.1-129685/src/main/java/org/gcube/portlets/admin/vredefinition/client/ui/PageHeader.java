package org.gcube.portlets.admin.vredefinition.client.ui;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PageHeader extends Composite{

	private static PageHeaderUiBinder uiBinder = GWT
			.create(PageHeaderUiBinder.class);

	interface PageHeaderUiBinder extends UiBinder<Widget, PageHeader> {
	}

	public PageHeader() {
		initWidget(uiBinder.createAndBindUi(this));
		icon.addStyleName("icon-style");
		popoverInfo.setPlacement(Placement.BOTTOM);
		popoverInfo.setHtml(true);
	}

	@UiField HeadingElement mainText;
	@UiField Popover popoverInfo;
	@UiField Icon icon;

	/**
	 * Set the main text
	 */
	public void setText(String text, String subtext){
		mainText.setInnerHTML(text);
		popoverInfo.setHeading("<b>" + text + "</b>");
		popoverInfo.setText(subtext);
	}
}
