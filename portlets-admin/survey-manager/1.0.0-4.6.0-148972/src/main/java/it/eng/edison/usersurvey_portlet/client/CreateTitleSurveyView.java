package it.eng.edison.usersurvey_portlet.client;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class CreateTitleSurveyView.
 */
public class CreateTitleSurveyView extends Composite {

	private static CreateTitleSurveyViewUiBinder uiBinder = GWT.create(CreateTitleSurveyViewUiBinder.class);
	
	/**
	 * The Interface CreateTitleSurveyViewUiBinder.
	 */
	interface CreateTitleSurveyViewUiBinder extends UiBinder<Widget, CreateTitleSurveyView> {
	}
	
	/** The title survey text box. */
	@UiField TextBox titleSurveyTextBox;
	
	/** The title survey. */
	private static String titleSurvey;

	/**
	 * Instantiates a new creates the title survey view.
	 */
	public CreateTitleSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		titleSurveyTextBox.setMaxLength(30);
	}
	
	/**
	 * Gets the title survey text box.
	 *
	 * @return the title survey text box
	 */
	public TextBox getTitleSurveyTextBox() {
		return titleSurveyTextBox;
	}

	/**
	 * Sets the title survey text box.
	 *
	 * @param titleSurveyTextBox the new title survey text box
	 */
	public void setTitleSurveyTextBox(TextBox titleSurveyTextBox) {
		this.titleSurveyTextBox = titleSurveyTextBox;
	}
	
	/**
	 * On blur title survey text box.
	 *
	 * @param event the event
	 */
	@UiHandler("titleSurveyTextBox")
	void onBlurTitleSurveyTextBox(BlurEvent event){
		titleSurvey = titleSurveyTextBox.getValue();
	}

}
