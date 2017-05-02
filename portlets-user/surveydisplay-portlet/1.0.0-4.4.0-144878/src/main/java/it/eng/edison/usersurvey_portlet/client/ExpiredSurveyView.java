package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class ExpiredSurveyView.
 */
public class ExpiredSurveyView extends Composite {

	/** The ui binder. */
	private static ExpiredSurveyViewUiBinder uiBinder = GWT.create(ExpiredSurveyViewUiBinder.class);

	/**
	 * The Interface ExpiredSurveyViewUiBinder.
	 */
	interface ExpiredSurveyViewUiBinder extends UiBinder<Widget, ExpiredSurveyView> {
	}
	
	/** The expired. */
	@UiField Heading expired;

	/**
	 * Instantiates a new expired survey view.
	 */
	public ExpiredSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("displaysurvey-div").add(expired);
	}

}
