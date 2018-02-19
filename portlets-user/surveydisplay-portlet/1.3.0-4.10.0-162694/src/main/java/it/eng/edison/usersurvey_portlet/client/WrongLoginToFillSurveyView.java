package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class WrongLoginToFillSurveyView.
 */
public class WrongLoginToFillSurveyView extends Composite {

	/** The ui binder. */
	private static WrongLoginToFillSurveyViewUiBinder uiBinder = GWT
			.create(WrongLoginToFillSurveyViewUiBinder.class);

	/**
	 * The Interface WrongLoginToFillSurveyViewUiBinder.
	 */
	interface WrongLoginToFillSurveyViewUiBinder extends UiBinder<Widget, WrongLoginToFillSurveyView> {
	}

	/** The wrong login to fill. */
	@UiField Heading wrongLoginToFill;
	
	/**
	 * Instantiates a new wrong login to fill survey view.
	 */
	public WrongLoginToFillSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("displaysurvey-div").add(wrongLoginToFill);
	}

}
