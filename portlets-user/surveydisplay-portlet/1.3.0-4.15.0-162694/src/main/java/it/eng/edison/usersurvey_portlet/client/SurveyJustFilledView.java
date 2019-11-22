package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class SurveyJustFilledView.
 */
public class SurveyJustFilledView extends Composite {

	/** The ui binder. */
	private static SurveyJustFilledViewUiBinder uiBinder = GWT.create(SurveyJustFilledViewUiBinder.class);

	/**
	 * The Interface SurveyJustFilledViewUiBinder.
	 */
	interface SurveyJustFilledViewUiBinder extends UiBinder<Widget, SurveyJustFilledView> {
	}

	/** The survey just filled. */
	@UiField Heading surveyJustFilled;
	
	/**
	 * Instantiates a new survey just filled view.
	 */
	public SurveyJustFilledView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("displaysurvey-div").add(surveyJustFilled);
	}

}
