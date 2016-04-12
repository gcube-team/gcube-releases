package org.gcube.portlets.user.socialprofile.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class DisplaySummary extends Composite {

	private static DisplaySummaryUiBinder uiBinder = GWT
			.create(DisplaySummaryUiBinder.class);

	interface DisplaySummaryUiBinder extends UiBinder<Widget, DisplaySummary> {
	}

	@UiField HTML summary;
	
	public DisplaySummary(String summaryText) {
		initWidget(uiBinder.createAndBindUi(this));
		summary.setHTML(summaryText);
	}
}
