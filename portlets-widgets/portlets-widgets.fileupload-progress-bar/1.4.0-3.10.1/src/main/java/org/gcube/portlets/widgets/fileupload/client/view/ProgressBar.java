package org.gcube.portlets.widgets.fileupload.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ProgressBar extends Composite {

	private static final double COMPLETE_PERECENTAGE = 100d;
	private static final double START_PERECENTAGE = 0d;

	private static ProgressBarUiBinder uiBinder = GWT
			.create(ProgressBarUiBinder.class);

	interface ProgressBarUiBinder extends UiBinder<Widget, ProgressBar> { }

	@UiField HTML progressBarContainer;

	public ProgressBar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void update(int percentage) {		
		if (percentage > 100)
			percentage = 100;
		if (percentage < START_PERECENTAGE || percentage > COMPLETE_PERECENTAGE) {
			throw new IllegalArgumentException("invalid value for percentage " + percentage);
		} else { //cannot use DOM getElemById cus the second time you open the popup it fails		
			progressBarContainer.getElement().setAttribute("style", "width: "+percentage+"%");
			progressBarContainer.getElement().getFirstChildElement().getFirstChildElement().setInnerText(percentage+"%");
		}
	}
}
