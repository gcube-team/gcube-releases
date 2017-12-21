package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class NotAuthorizeToFillSurveyView.
 */
public class NotAuthorizeToFillSurveyView extends Composite {

	/** The ui binder. */
	private static NotAuthorizeToFillSurveyViewUiBinder uiBinder = GWT
			.create(NotAuthorizeToFillSurveyViewUiBinder.class);

	/**
	 * The Interface NotAuthorizeToFillSurveyViewUiBinder.
	 */
	interface NotAuthorizeToFillSurveyViewUiBinder extends UiBinder<Widget, NotAuthorizeToFillSurveyView> {
	}

	/** The not auth to fill. */
	@UiField Heading notAuthToFill;
	
	/**
	 * Instantiates a new not authorize to fill survey view.
	 */
	public NotAuthorizeToFillSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("displaysurvey-div").add(notAuthToFill);
	}

}
