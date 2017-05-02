package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.List;


import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;


/**
 * The Class MultipleChoiceView.
 */
public class MultipleChoiceView extends Composite {

	/** The ui binder. */
	private static MultipleChoiceViewUiBinder uiBinder = GWT.create(MultipleChoiceViewUiBinder.class);

	/**
	 * The Interface MultipleChoiceViewUiBinder.
	 */
	interface MultipleChoiceViewUiBinder extends UiBinder<Widget, MultipleChoiceView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;
	
	/** The answer X text box. */
	@UiField TextBox answerXTextBox;
	
	/** The html panel. */
	@UiField HTMLPanel htmlPanel;

	/** The survey answer multiple choice list. */
	private List<String> surveyAnswerMultipleChoiceList = new ArrayList<>(); 

	/** The answer text box current. */
	private String answerTextBoxCurrent="";

	/**
	 * Instantiates a new multiple choice view.
	 */
	public MultipleChoiceView() {
		initWidget(uiBinder.createAndBindUi(this));

		verticalPanel.add(horizontalPanel);
		addQuestionPanel();

		answerXTextBox.addValueChangeHandler(new ValueChangeHandler<String>(){
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				int indexCurrentListElementTextBox = surveyAnswerMultipleChoiceList.indexOf(answerTextBoxCurrent);
				surveyAnswerMultipleChoiceList.set(indexCurrentListElementTextBox, answerXTextBox.getValue());
				answerTextBoxCurrent = answerXTextBox.getValue();
				}
		});
	}

	/**
	 * Adds the question panel.
	 */
	private void addQuestionPanel(){
		horizontalPanel.add(answerXTextBox);
		answerXTextBox.setVisible(true);
	}

	/**
	 * Gets the answer X text box.
	 *
	 * @return the answer X text box
	 */
	public TextBox getAnswerXTextBox() {
		return answerXTextBox;
	}

	/**
	 * Sets the answer X text box.
	 *
	 * @param answerXTextBox the new answer X text box
	 */
	public void setAnswerXTextBox(TextBox answerXTextBox) {
		this.answerXTextBox = answerXTextBox;
	}

	/**
	 * Gets the survey answer multiple choice list.
	 *
	 * @return the survey answer multiple choice list
	 */
	public List<String> getSurveyAnswerMultipleChoiceList() {
		return surveyAnswerMultipleChoiceList;
	}

	/**
	 * Sets the survey answer multiple choice list.
	 *
	 * @param surveyAnswerMultipleChoiceList the new survey answer multiple choice list
	 */
	public void setSurveyAnswerMultipleChoiceList(List<String> surveyAnswerMultipleChoiceList) {
		this.surveyAnswerMultipleChoiceList = surveyAnswerMultipleChoiceList;
	}

}
