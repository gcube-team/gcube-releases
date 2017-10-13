package org.gcube.portlets.user.reportgenerator.client.uibinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShowSaving extends Composite {
	public static final String LOADING = GWT.getModuleBaseURL() + "../images/reports-saving.gif";
	
	private static ShowoadingUiBinder uiBinder = GWT
			.create(ShowoadingUiBinder.class);

	interface ShowoadingUiBinder extends UiBinder<Widget, ShowSaving> {
	}
	
	@UiField Image loadingReport;
	
	public ShowSaving() {
		initWidget(uiBinder.createAndBindUi(this));
		loadingReport.setUrl(LOADING);
	}

	
}
