package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;

/**
 * The Class CreateQuestionView.
 */
public class CreateQuestionView extends Composite {

	/** The ui binder. */
	private static CreateSurveyViewUiBinder uiBinder = GWT.create(CreateSurveyViewUiBinder.class);

	/**
	 * The Interface CreateSurveyViewUiBinder.
	 */
	interface CreateSurveyViewUiBinder extends UiBinder<Widget, CreateQuestionView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel; 
	
	/** The question survey. */
	@UiField WellForm questionSurvey;
	
	/** The type survey. */
	@UiField ListBox typeSurvey;
	
	/** The scale to survey list box. */
	@UiField ListBox scaleFromSurveyListBox, scaleToSurveyListBox;
	
	/** The multiple choice survey answer controls. */
	@UiField Controls textBoxSurveyAnswerControls, textAreaSurveyAnswerControls, multipleChoiceSurveyAnswerControls;
	
	/** The scale answer survey controls. */
	@UiField Controls dateAnswerSurveyControls, timeAnswerSurveyControls, scaleAnswerSurveyControls;
	
	/** The control radio controls. */
	@UiField Controls checkBoxMandatoryControls, controlRadioControls;
	
	/** The question survey text box. */
	@UiField TextBox questionSurveyTextBox;
	
	/** The answer text box. */
	@UiField TextBox answerTextBox;
	
	/** The scale to survey text box. */
	@UiField TextBox scaleFromSurveyTextBox, scaleToSurveyTextBox;
	
	/** The time minute answer survey. */
	@UiField TextBox timeHourAnswerSurvey, timeMinuteAnswerSurvey;
	
	/** The answer text area. */
	@UiField TextArea answerTextArea;
	
	/** The add answer X survey button. */
	@UiField Button addAnswerXSurveyButton;
	
	/** The date answer survey. */
	@UiField DateBoxAppended dateAnswerSurvey;
	
	/** The scale answer survey control label to. */
	@UiField ControlLabel scaleAnswerSurveyControlLabelFrom, scaleAnswerSurveyControlLabelTo;
	
	/** The time answer survey controls label from. */
	@UiField Label timeAnswerSurveyControlsLabelFrom;
	
	/** The radio button scale survey. */
	@UiField RadioButton radioButtonScaleSurvey;
	
	/** The check box mandatory. */
	@UiField CheckBox checkBoxMandatory;


	/** The Constant MAX_VALUE_HOUR. */
	private static final int MAX_VALUE_HOUR = 23;
	
	/** The Constant MAX_VALUE_MIN. */
	private static final int MAX_VALUE_MIN = 59;
	
	/** The Constant GETANSWER_ELEMENTS. */
	private static final int GETANSWER_ELEMENTS = 10;
	
	
	/** The row. */
	private int row = 0;
	
	/** The id question. */
	private Integer idQuestion = 0;
	
	/** The survey question list. */
	private List<SurveyQuestionModel> surveyQuestionList = new ArrayList<>(); 
	
	/** The multiple choice list. */
	private List<MultipleChoiceView> multipleChoiceList = null; 
	
	/** The multiple choice view. */
	private MultipleChoiceView multipleChoiceView;
	
	/** The flex table. */
	private FlexTable flexTable; 

	/**
	 * Instantiates a new creates the question view.
	 */
	public CreateQuestionView() {
		initWidget(uiBinder.createAndBindUi(this));
		verticalPanel.add(horizontalPanel);
		questionSurvey.setVisible(false);
		typeSurvey.setVisible(false);
		dateAnswerSurvey.setVisible(false);

		typeSurveyAddItem();
		scaleSurveyAddItem();

		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		flexTable = new FlexTable();
		addSurveyPanel();
	} 
	
	/**
	 * Instantiates a new creates the question view.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	public CreateQuestionView(SurveyQuestionModel surveyQuestionModel) {
		this();
		visibleTypeSurveyBoxes();
		populateSurveyQuestionFromDB(surveyQuestionModel);
	}

	/**
	 * On change type survey.
	 *
	 * @param event the event
	 */
	@UiHandler("typeSurvey")
	void onChangeTypeSurvey(ChangeEvent event){
		visibleTypeSurveyBoxes();
		
		if(typeSurvey.getValue().equals("Text")){
			typeSurveyEqualsText();
		}
		
		else if(typeSurvey.getValue().equals("Paragraph Text")){
			typeSurveyEqualsParagraphText();
		}
		
		else if(typeSurvey.getValue().equals("Multiple Choice") 
				|| typeSurvey.getValue().equals("CheckBoxes") 
				|| typeSurvey.getValue().equals("Drop-Down")){	
			typeSurveyEqualsMultipleCheckDrop();
		}
		
		else if(typeSurvey.getValue().equals("Date")){
			typeSurveyEqualsDate();
		}

		else if(typeSurvey.getValue().equals("Time")){
			typeSurveyEqualsTime();
		}

		else if(typeSurvey.getValue().equals("Scale")){
			typeSurveyEqualsScale();
		}
	}

	/**
	 * On click add answer X survey button.
	 *
	 * @param event the event
	 */
	/* Multiple Choice */
	@UiHandler("addAnswerXSurveyButton")
	void onClickAddAnswerXSurveyButton(ClickEvent event){
		questionSurvey.remove(checkBoxMandatory);
		
		multipleChoiceView = new MultipleChoiceView();
		Button removeButton = new Button("x");
		removeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int rowIndex = flexTable.getCellForEvent(event).getRowIndex();

				getMultipleChoiceList().remove(rowIndex);
				flexTable.removeRow(rowIndex);
				if ( getRow()>0){
					setRow(row-1);
				}
			}
		});
		multipleChoiceView.getAnswerXTextBox().setValue("Option " + (row+1));
		flexTable.setWidget(row, 0, multipleChoiceView);
		flexTable.setWidget(row, 1, removeButton);
		this.setRow(row+1);
		
		questionSurvey.add(flexTable);
		this.getMultipleChoiceList().add(multipleChoiceView);
		questionSurvey.add(checkBoxMandatory);
	}

	/**
	 * On change time hour answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeHourAnswerSurvey")
	void onChangeTimeHourAnswerSurvey(ChangeEvent event){
		int timeHourAnswerSurveyTemp = Integer.parseInt(timeHourAnswerSurvey.getValue());
		if(timeHourAnswerSurveyTemp > MAX_VALUE_HOUR || !timeHourAnswerSurvey.getValue().matches("[0-9]*")){
			timeHourAnswerSurvey.setValue("");
			return;
		}
	}

	/**
	 * On key press event time hour answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeHourAnswerSurvey")
	void onKeyPressEventTimeHourAnswerSurvey(KeyPressEvent event){
		if (!Character.isDigit(event.getCharCode())) {
			((TextBox) event.getSource()).cancelKey();
			return;
		}
	}

	/**
	 * On change time minute answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeMinuteAnswerSurvey")
	void onChangeTimeMinuteAnswerSurvey(ChangeEvent event){
		int timeMinuteAnswerSurveyTemp = Integer.parseInt(timeMinuteAnswerSurvey.getValue());
		if(timeMinuteAnswerSurveyTemp > MAX_VALUE_MIN || !timeMinuteAnswerSurvey.getValue().matches("[0-9]*")){
			timeMinuteAnswerSurvey.setValue("");
			return;
		}
	}

	/**
	 * On key press event time minute answer survey.
	 *
	 * @param event the event
	 */
	@UiHandler("timeMinuteAnswerSurvey")
	void onKeyPressEventTimeMinuteAnswerSurvey(KeyPressEvent event){
		if (!Character.isDigit(event.getCharCode())) {
			((TextBox) event.getSource()).cancelKey();
			return;
		}
	}

	/**
	 * On change scale from survey.
	 *
	 * @param event the event
	 */
	@UiHandler("scaleFromSurveyListBox")
	void onChangeScaleFromSurvey(ChangeEvent event){
		generateRadioButton(scaleFromSurveyListBox.getValue(), scaleToSurveyListBox.getValue());
	}

	/**
	 * On change scale to survey.
	 *
	 * @param event the event
	 */
	@UiHandler("scaleToSurveyListBox")
	void onChangeScaleToSurvey(ChangeEvent event){
		generateRadioButton(scaleFromSurveyListBox.getValue(), scaleToSurveyListBox.getValue());
	}
	
	/**
	 * Adds the survey panel.
	 */
	private void addSurveyPanel() {
		visibleSurveyPanels();
		answerTextBox.setEnabled(false);

		timeHourAnswerSurvey.setSize(2);
		timeMinuteAnswerSurvey.setSize(2);
		timeHourAnswerSurvey.setMaxLength(2);
		timeMinuteAnswerSurvey.setMaxLength(2);

		timeAnswerSurveyControlsLabelFrom.setText(":");
	}

	/**
	 * Generate radio button.
	 *
	 * @param start the start
	 * @param end the end
	 */
	private void generateRadioButton(String start, String end){
		int startTemp = Integer.parseInt(start);
		int endTemp = Integer.parseInt(end);
		radioButtonScaleSurvey = null;
		horizontalPanel.clear();
		for(int i=startTemp; i<=endTemp; i++){
			radioButtonScaleSurvey = new RadioButton("radioGroup","" + String.valueOf(i) + "  ");
			horizontalPanel.add(radioButtonScaleSurvey);
			questionSurvey.add(horizontalPanel);
			radioButtonScaleSurvey.setEnabled(false);
		}
		questionSurvey.add(checkBoxMandatory);
	}

	/**
	 * Scale survey add item.
	 */
	private void scaleSurveyAddItem(){
		scaleFromSurveyListBox.addItem("0");
		scaleFromSurveyListBox.addItem("1");

		scaleToSurveyListBox.addItem("2");
		scaleToSurveyListBox.addItem("3");
		scaleToSurveyListBox.addItem("4");
		scaleToSurveyListBox.addItem("5");
		scaleToSurveyListBox.addItem("6");
		scaleToSurveyListBox.addItem("7");
		scaleToSurveyListBox.addItem("8");
		scaleToSurveyListBox.addItem("9");
		scaleToSurveyListBox.addItem("10");
	}

	/**
	 * Type survey add item.
	 */
	private void typeSurveyAddItem(){
		typeSurvey.addItem("Text");
		typeSurvey.addItem("Paragraph Text");
		typeSurvey.addItem("Multiple Choice");
		typeSurvey.addItem("CheckBoxes");
		typeSurvey.addItem("Drop-Down");
		typeSurvey.addItem("Scale");
		typeSurvey.addItem("Date");
		typeSurvey.addItem("Time");
	}

	/**
	 * Visible survey panels.
	 */
	private void visibleSurveyPanels() {
		verticalPanel.add(questionSurvey);
		answerTextArea.setVisible(false);
		addAnswerXSurveyButton.setVisible(false);
		scaleFromSurveyListBox.setVisible(false);
		scaleToSurveyListBox.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(false);
		scaleFromSurveyTextBox.setVisible(false);
		scaleAnswerSurveyControlLabelTo.setVisible(false);
		scaleToSurveyTextBox.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		controlRadioControls.setVisible(false);
		checkBoxMandatory.setVisible(false);

		questionSurvey.setVisible(true);
		typeSurvey.setVisible(true);
		answerTextBox.setVisible(true);

		checkBoxMandatory.setVisible(true);
	}
	
	/**
	 * Visible type survey boxes.
	 */
	private void visibleTypeSurveyBoxes(){
		answerTextBox.setVisible(false);
		answerTextArea.setVisible(false);
		addAnswerXSurveyButton.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(false);
		scaleFromSurveyListBox.setVisible(false);
		scaleAnswerSurveyControlLabelTo.setVisible(false);
		scaleToSurveyListBox.setVisible(false);
		scaleFromSurveyTextBox.setVisible(false);
		scaleToSurveyTextBox.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		answerTextBox.setEnabled(false);
	}
	
	/**
	 * Type survey equals text.
	 */
	private void typeSurveyEqualsText(){
		questionSurvey.clear();
		answerTextBox.setVisible(true);

		questionSurvey.add(questionSurveyTextBox);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(textBoxSurveyAnswerControls);
		
		answerTextBox.setEnabled(false);

		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);
	}
	
	/**
	 * Type survey equals paragraph text.
	 */
	private void typeSurveyEqualsParagraphText(){
		questionSurvey.clear();
		answerTextArea.setVisible(true);

		questionSurvey.add(questionSurveyTextBox);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(textBoxSurveyAnswerControls);
		questionSurvey.add(answerTextArea);
		answerTextArea.setEnabled(false);

		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);
	}
	
	/**
	 * Type survey equals multiple check drop.
	 */
	private void typeSurveyEqualsMultipleCheckDrop(){
		
		questionSurvey.remove(checkBoxMandatoryControls);
		questionSurvey.remove(checkBoxMandatory);
		
		addAnswerXSurveyButton.setVisible(true);

		questionSurvey.insert(questionSurveyTextBox,0);
		questionSurvey.insert(typeSurvey,1);
		questionSurvey.insert(addAnswerXSurveyButton,2);
		
		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);
	}
	
	/**
	 * Type survey equals date.
	 */
	private void typeSurveyEqualsDate() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		dateAnswerSurvey.setVisible(true);

		questionSurvey.add(questionSurveyTextBox);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(dateAnswerSurveyControls);
		questionSurvey.add(dateAnswerSurvey);
		
		dateAnswerSurvey.setReadOnly(true);

		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);		
	}
	
	/**
	 * Type survey equals time.
	 */
	private void typeSurveyEqualsTime() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(true);
		timeHourAnswerSurvey.setVisible(true);
		timeMinuteAnswerSurvey.setVisible(true);

		questionSurvey.add(questionSurveyTextBox);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(timeAnswerSurveyControls);
		questionSurvey.add(timeHourAnswerSurvey);
		questionSurvey.add(timeAnswerSurveyControlsLabelFrom);
		questionSurvey.add(timeMinuteAnswerSurvey);
		
		timeHourAnswerSurvey.setEnabled(false);
		timeMinuteAnswerSurvey.setEnabled(false);

		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);
	}
	
	/**
	 * Type survey equals scale.
	 */
	private void typeSurveyEqualsScale() {
		questionSurvey.clear();
		answerTextBox.setVisible(false);
		scaleAnswerSurveyControlLabelFrom.setVisible(true);
		scaleFromSurveyListBox.setVisible(true);
		scaleAnswerSurveyControlLabelTo.setVisible(true);
		scaleToSurveyListBox.setVisible(true);
		scaleFromSurveyTextBox.setVisible(true);
		scaleToSurveyTextBox.setVisible(true);

		questionSurvey.add(questionSurveyTextBox);
		questionSurvey.add(typeSurvey);
		questionSurvey.add(dateAnswerSurveyControls);
		questionSurvey.add(scaleAnswerSurveyControlLabelFrom);
		questionSurvey.add(scaleFromSurveyListBox);
		questionSurvey.add(scaleAnswerSurveyControlLabelTo);
		questionSurvey.add(scaleToSurveyListBox);
		questionSurvey.add(scaleAnswerSurveyControls);
		questionSurvey.add(scaleFromSurveyTextBox);
		questionSurvey.add(scaleToSurveyTextBox);

		questionSurvey.add(checkBoxMandatoryControls);
		questionSurvey.add(checkBoxMandatory);		
	}
	
	/**
	 * Populate survey question from DB.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void populateSurveyQuestionFromDB(SurveyQuestionModel surveyQuestionModel) {
		if(surveyQuestionModel.getQuestiontype().equals("Text")){
			typeSurveyEqualsText();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Paragraph Text")){
			typeSurveyEqualsParagraphText();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Multiple Choice")
				|| surveyQuestionModel.getQuestiontype().equals("CheckBoxes") 
				|| surveyQuestionModel.getQuestiontype().equals("Drop-Down")){
			typeSurveyEqualsMultipleCheckDrop();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			addAnswerMultipleCheckDrop(surveyQuestionModel);
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Date")){
			typeSurveyEqualsDate();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			dateAnswerSurvey.setValue(surveyQuestionModel.getDateAnswer());
		
		}else if(surveyQuestionModel.getQuestiontype().equals("Time")){
			typeSurveyEqualsTime();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			timeHourAnswerSurvey.setValue(surveyQuestionModel.getAnswer1());
			timeMinuteAnswerSurvey.setValue(surveyQuestionModel.getAnswer2());
			
		}else if(surveyQuestionModel.getQuestiontype().equals("Scale")){
			typeSurveyEqualsScale();
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			scaleFromSurveyListBox.setSelectedValue(surveyQuestionModel.getAnswer1());
			scaleToSurveyListBox.setSelectedValue(surveyQuestionModel.getAnswer2());
			scaleFromSurveyTextBox.setValue(surveyQuestionModel.getAnswer3());
			scaleToSurveyTextBox.setValue(surveyQuestionModel.getAnswer4());
		}		
	}

	/**
	 * Adds the answer multiple check drop.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void addAnswerMultipleCheckDrop(SurveyQuestionModel surveyQuestionModel) {
		questionSurvey.remove(checkBoxMandatory);
		List<String> surveyQuestionModelMultipleAnswerList = surveyQuestionModelMultipleAnswerList(surveyQuestionModel);

		for(int i=0; i<surveyQuestionModelMultipleAnswerList.size(); i++){
			if(surveyQuestionModelMultipleAnswerList.get(i)!=null){
				multipleChoiceView = new MultipleChoiceView();
				Button removeButton = new Button("x");
				removeButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int rowIndex = flexTable.getCellForEvent(event).getRowIndex();
						getMultipleChoiceList().remove(rowIndex);
						flexTable.removeRow(rowIndex);
						if ( getRow()>0){
							setRow(row-1);
						}
					}
				});
				
				multipleChoiceView.getAnswerXTextBox().setValue(surveyQuestionModelMultipleAnswerList.get(i));
				flexTable.setWidget(row, 0, multipleChoiceView);
				flexTable.setWidget(row, 1, removeButton);
				this.setRow(row+1);

				questionSurvey.add(flexTable);
				this.getMultipleChoiceList().add(multipleChoiceView);
			}
		}
		questionSurvey.add(checkBoxMandatory);
	}

	/**
	 * Survey question model multiple answer list.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @return the list
	 */
	private List<String> surveyQuestionModelMultipleAnswerList(SurveyQuestionModel surveyQuestionModel) {
		List<String> surveyQuestionModelMultipleAnswerList = new ArrayList<>();
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer1());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer2());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer3());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer4());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer5());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer6());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer7());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer8());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer9());
		surveyQuestionModelMultipleAnswerList.add(surveyQuestionModel.getAnswer10());
		return surveyQuestionModelMultipleAnswerList;
	}

	/**
	 * Sets the text box and check box values.
	 *
	 * @param surveyQuestionModel the new text box and check box values
	 */
	private void setTextBoxAndCheckBoxValues(SurveyQuestionModel surveyQuestionModel){
		typeSurvey.setSelectedValue(surveyQuestionModel.getQuestiontype());
		questionSurveyTextBox.setText(surveyQuestionModel.getQuestion());
		checkBoxMandatory.setValue(surveyQuestionModel.getIsmandatory());
	}
	
	/**
	 * Gets the id question.
	 *
	 * @return the id question
	 */
	public Integer getIdQuestion() {
		return idQuestion;
	}

	/**
	 * Sets the id question.
	 *
	 * @param idQuestion the new id question
	 */
	public void setIdQuestion(Integer idQuestion) {
		this.idQuestion = idQuestion;
	}

	/**
	 * Gets the type survey.
	 *
	 * @return the type survey
	 */
	public ListBox getTypeSurvey() {
		return typeSurvey;
	}

	/**
	 * Sets the type survey.
	 *
	 * @param typeSurvey the new type survey
	 */
	public void setTypeSurvey(ListBox typeSurvey) {
		this.typeSurvey = typeSurvey;
	}

	/**
	 * Gets the question survey text box.
	 *
	 * @return the question survey text box
	 */
	public TextBox getQuestionSurveyTextBox() {
		return questionSurveyTextBox;
	}

	/**
	 * Sets the question survey text box.
	 *
	 * @param questionSurveyTextBox the new question survey text box
	 */
	public void setQuestionSurveyTextBox(TextBox questionSurveyTextBox) {
		this.questionSurveyTextBox = questionSurveyTextBox;
	}

	/**
	 * Gets the answer text box.
	 *
	 * @return the answer text box
	 */
	public TextBox getAnswerTextBox() {
		return answerTextBox;
	}

	/**
	 * Sets the answer text box.
	 *
	 * @param answerTextBox the new answer text box
	 */
	public void setAnswerTextBox(TextBox answerTextBox) {
		this.answerTextBox = answerTextBox;
	}

	/**
	 * Gets the answer text area.
	 *
	 * @return the answer text area
	 */
	public TextArea getAnswerTextArea() {
		return answerTextArea;
	}

	/**
	 * Sets the answer text area.
	 *
	 * @param answerTextArea the new answer text area
	 */
	public void setAnswerTextArea(TextArea answerTextArea) {
		this.answerTextArea = answerTextArea;
	}

	/**
	 * Gets the multiple choice list.
	 *
	 * @return the multiple choice list
	 */
	public List<MultipleChoiceView> getMultipleChoiceList() {
		return multipleChoiceList;
	}

	/**
	 * Sets the multiple choice list.
	 *
	 * @param multipleChoiceList the new multiple choice list
	 */
	public void setMultipleChoiceList(List<MultipleChoiceView> multipleChoiceList) {
		this.multipleChoiceList = multipleChoiceList;
	}

	/**
	 * Gets the date answer survey.
	 *
	 * @return the date answer survey
	 */
	public DateBoxAppended getDateAnswerSurvey() {
		return dateAnswerSurvey;
	}

	/**
	 * Sets the date answer survey.
	 *
	 * @param dateAnswerSurvey the new date answer survey
	 */
	public void setDateAnswerSurvey(DateBoxAppended dateAnswerSurvey) {
		this.dateAnswerSurvey = dateAnswerSurvey;
	}

	/**
	 * Gets the time hour answer survey.
	 *
	 * @return the time hour answer survey
	 */
	public TextBox getTimeHourAnswerSurvey() {
		return timeHourAnswerSurvey;
	}

	/**
	 * Sets the time hour answer survey.
	 *
	 * @param timeHourAnswerSurvey the new time hour answer survey
	 */
	public void setTimeHourAnswerSurvey(TextBox timeHourAnswerSurvey) {
		this.timeHourAnswerSurvey = timeHourAnswerSurvey;
	}

	/**
	 * Gets the time minute answer survey.
	 *
	 * @return the time minute answer survey
	 */
	public TextBox getTimeMinuteAnswerSurvey() {
		return timeMinuteAnswerSurvey;
	}

	/**
	 * Sets the time minute answer survey.
	 *
	 * @param timeMinuteAnswerSurvey the new time minute answer survey
	 */
	public void setTimeMinuteAnswerSurvey(TextBox timeMinuteAnswerSurvey) {
		this.timeMinuteAnswerSurvey = timeMinuteAnswerSurvey;
	}

	/**
	 * Sets the scale answer survey controls.
	 *
	 * @param scaleAnswerSurveyControls the new scale answer survey controls
	 */
	public void setScaleAnswerSurveyControls(Controls scaleAnswerSurveyControls) {
		this.scaleAnswerSurveyControls = scaleAnswerSurveyControls;
	}

	/**
	 * Gets the scale from survey text box.
	 *
	 * @return the scale from survey text box
	 */
	public TextBox getScaleFromSurveyTextBox() {
		return scaleFromSurveyTextBox;
	}

	/**
	 * Sets the scale from survey text box.
	 *
	 * @param scaleFromSurveyTextBox the new scale from survey text box
	 */
	public void setScaleFromSurveyTextBox(TextBox scaleFromSurveyTextBox) {
		this.scaleFromSurveyTextBox = scaleFromSurveyTextBox;
	}

	/**
	 * Gets the scale to survey text box.
	 *
	 * @return the scale to survey text box
	 */
	public TextBox getScaleToSurveyTextBox() {
		return scaleToSurveyTextBox;
	}

	/**
	 * Sets the scale to survey text box.
	 *
	 * @param scaleToSurveyTextBox the new scale to survey text box
	 */
	public void setScaleToSurveyTextBox(TextBox scaleToSurveyTextBox) {
		this.scaleToSurveyTextBox = scaleToSurveyTextBox;
	}

	/**
	 * Gets the check box mandatory.
	 *
	 * @return the check box mandatory
	 */
	public CheckBox getCheckBoxMandatory() {
		return checkBoxMandatory;
	}

	/**
	 * Sets the check box mandatory.
	 *
	 * @param checkBoxMandatory the new check box mandatory
	 */
	public void setCheckBoxMandatory(CheckBox checkBoxMandatory) {
		this.checkBoxMandatory = checkBoxMandatory;
	}

	/**
	 * Gets the row.
	 *
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Sets the row.
	 *
	 * @param row the new row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Gets the scale from survey list box.
	 *
	 * @return the scale from survey list box
	 */
	public ListBox getScaleFromSurveyListBox() {
		return scaleFromSurveyListBox;
	}

	/**
	 * Sets the scale from survey list box.
	 *
	 * @param scaleFromSurveyListBox the new scale from survey list box
	 */
	public void setScaleFromSurveyListBox(ListBox scaleFromSurveyListBox) {
		this.scaleFromSurveyListBox = scaleFromSurveyListBox;
	}

	/**
	 * Gets the scale to survey list box.
	 *
	 * @return the scale to survey list box
	 */
	public ListBox getScaleToSurveyListBox() {
		return scaleToSurveyListBox;
	}

	/**
	 * Sets the scale to survey list box.
	 *
	 * @param scaleToSurveyListBox the new scale to survey list box
	 */
	public void setScaleToSurveyListBox(ListBox scaleToSurveyListBox) {
		this.scaleToSurveyListBox = scaleToSurveyListBox;
	}

}
