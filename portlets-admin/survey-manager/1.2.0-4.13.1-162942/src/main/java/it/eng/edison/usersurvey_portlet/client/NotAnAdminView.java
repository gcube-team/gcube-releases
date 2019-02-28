package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.Heading;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class NotAnAdminView.
 */
public class NotAnAdminView extends Composite {

	/** The ui binder. */
	private static NotAnAdminViewUiBinder uiBinder = GWT.create(NotAnAdminViewUiBinder.class);

	/**
	 * The Interface NotAnAdminViewUiBinder.
	 */
	interface NotAnAdminViewUiBinder extends UiBinder<Widget, NotAnAdminView> {
	}

	/** The not an admin. */
	@UiField Heading notAnAdmin;
	
	/**
	 * Instantiates a new not an admin view.
	 */
	public NotAnAdminView() {
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get("survey-div").add(notAnAdmin);
	}

}
