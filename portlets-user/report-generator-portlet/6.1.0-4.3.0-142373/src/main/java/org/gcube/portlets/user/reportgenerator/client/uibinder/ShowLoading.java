package org.gcube.portlets.user.reportgenerator.client.uibinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ShowLoading extends Composite {
	public static final String LOADING = GWT.getModuleBaseURL() + "../images/reports-loader.gif";
	
	private static ShowoadingUiBinder uiBinder = GWT
			.create(ShowoadingUiBinder.class);

	interface ShowoadingUiBinder extends UiBinder<Widget, ShowLoading> {
	}
	
	@UiField Image loadingReport;
	
	public ShowLoading() {
		initWidget(uiBinder.createAndBindUi(this));
		loadingReport.setUrl(LOADING);
	}

	
}
