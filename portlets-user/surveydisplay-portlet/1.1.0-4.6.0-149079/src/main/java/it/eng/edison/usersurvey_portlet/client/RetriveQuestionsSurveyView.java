package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portal.clientcontext.client.GCubeClientContext;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;

/**
 * The Class RetriveQuestionsSurveyView.
 */
public class RetriveQuestionsSurveyView extends Composite {

	/** The ui binder. */
	private static RetriveQuestionsSurveyViewUiBinder uiBinder = GWT.create(RetriveQuestionsSurveyViewUiBinder.class);

	/**
	 * The Interface RetriveQuestionsSurveyViewUiBinder.
	 */
	interface RetriveQuestionsSurveyViewUiBinder extends UiBinder<Widget, RetriveQuestionsSurveyView> {
	}
	
	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel scale survey. */
	@UiField HorizontalPanel horizontalPanel, horizontalPanelScaleSurvey; 
	
	/** The question survey. */
	@UiField Form questionSurvey;
	
	/** The date answer survey. */
	@UiField DateBoxAppended dateAnswerSurvey;
	
	/** The answer text box. */
	@UiField TextBox answerTextBox;
	
	/** The time minute answer survey. */
	@UiField TextBox timeHourAnswerSurvey, timeMinuteAnswerSurvey;
	
	/** The time answer survey controls label from. */
	@UiField Label timeAnswerSurveyControlsLabelFrom;
	
	/** The answer text area. */
	@UiField TextArea answerTextArea;
	
	/** The control radio controls. */
	@UiField Controls controlRadioControls;
	
	/** The time answer survey controls. */
	@UiField Controls textBoxSurveyAnswerControls, dateAnswerSurveyControls, timeAnswerSurveyControls;
	
	/** The radio button scale survey. */
	@UiField RadioButton radioButtonScaleSurvey;
	
	/** The scale to survey paragraph. */
	@UiField Paragraph scaleFromSurveyParagraph, scaleToSurveyParagraph;
	
	/** The multiple choice list. */
	private List<MultipleChoiceView> multipleChoiceList = null; 
	
	/** The multiple choice view. */
	private MultipleChoiceView multipleChoiceView;
	
	/** The type survey. */
	private String typeSurvey;
	
	/** The id survey. */
	private Integer idSurvey;
    
    /** The numberquestion. */
    private int numberquestion;
    
    /** The is mandatory. */
    private boolean isMandatory;
    
    /** The radio button. */
    private RadioButton radioButton;
    
    /** The list box. */
    private ListBox listBox;
    
    /** The check box. */
    private CheckBox checkBox;
    
    /** The check box list. */
    private List<CheckBox> checkBoxList;
    
    /** The radio button list. */
    private List<RadioButton> radioButtonList;
    
    /** The grid radio button list. */
    private List<RadioButton> gridRadioButtonList;
    
    /** The grid radio button answer map. */
    private Map<Integer,String> gridRadioButtonAnswerMap;
	
	/** The radio button scale survey list. */
	private List<RadioButton> radioButtonScaleSurveyList;
	
	/** The question survey paragraph. */
	private Paragraph mandatoryFieldParagraph, questionSurveyParagraph;
	
	/** The other text box. */
	private TextBox otherTextBox;
	
	/** The img. */
	private Image img;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The section title stripe HTML. */
	private HTML sectionTitleStripeHTML = null;  
	
	/** The section description stripe HTML. */
	private HTML sectionDescriptionStripeHTML = null;  
	
	/** The Constant MAX_VALUE_HOUR. */
	private static final int MAX_VALUE_HOUR = 23;
	
	/** The Constant MAX_VALUE_MIN. */
	private static final int MAX_VALUE_MIN = 59;

	/**
	 * Instantiates a new retrive questions survey view.
	 */
	public RetriveQuestionsSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	/**
	 * Instantiates a new retrive questions survey view.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param userDTO the user DTO
	 */
	public RetriveQuestionsSurveyView(SurveyQuestionModel surveyQuestionModel, UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		answerTextBox.setMaxLength(512);
		
		verticalPanel.add(horizontalPanel);
		questionSurvey.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		
		mandatoryFieldParagraph = new Paragraph("*");
		questionSurveyParagraph = new Paragraph();
		
		this.typeSurvey = surveyQuestionModel.getQuestiontype();
		this.isMandatory = surveyQuestionModel.getIsmandatory();
		this.numberquestion = surveyQuestionModel.getNumberquestion();
		this.userDTO = userDTO;

		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		gridRadioButtonAnswerMap = new HashMap<>();
		addSurveyPanel();
		visibleTypeSurveyBoxes();
		populateSurveyQuestionFromDB(surveyQuestionModel);
	}
	
	/**
	 * Adds the survey panel.
	 */
	private void addSurveyPanel() {
		visibleSurveyPanels();
		answerTextBox.setEnabled(false);

		timeHourAnswerSurvey.addStyleName("timeStyleRetriveQuestions");
		timeMinuteAnswerSurvey.addStyleName("timeStyleRetriveQuestions");
		timeHourAnswerSurvey.setSize(2);
		timeMinuteAnswerSurvey.setSize(2);
		timeHourAnswerSurvey.setMaxLength(2);
		timeMinuteAnswerSurvey.setMaxLength(2);
		timeAnswerSurveyControlsLabelFrom.setText(":");
	}
	
	/**
	 * Visible type survey boxes.
	 */
	private void visibleTypeSurveyBoxes(){
		answerTextBox.setVisible(false);
		answerTextArea.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		answerTextBox.setEnabled(false);
	}
	
	/**
	 * Visible survey panels.
	 */
	private void visibleSurveyPanels() {
		verticalPanel.add(questionSurvey);
		answerTextArea.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		controlRadioControls.setVisible(false);

		questionSurvey.setVisible(true);
		answerTextBox.setVisible(true);

	}
	
	/**
	 * Populate survey question from DB.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void populateSurveyQuestionFromDB(SurveyQuestionModel surveyQuestionModel) {
		if(surveyQuestionModel.getQuestiontype().equals("Text")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsText();
		}
		else if(surveyQuestionModel.getQuestiontype().equals("Paragraph Text")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsParagraphText();
		}
		
		else if(surveyQuestionModel.getQuestiontype().equals("Multiple Choice")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsMultipleChoice(surveyQuestionModel);
		}
		
		else if(surveyQuestionModel.getQuestiontype().equals("CheckBoxes")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsCheckBoxes(surveyQuestionModel);
		}
		
		else if(surveyQuestionModel.getQuestiontype().equals("Drop-Down")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsDropDown(surveyQuestionModel);
		}
		
		else if(surveyQuestionModel.getQuestiontype().equals("Date")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsDate();
		}
			else if(surveyQuestionModel.getQuestiontype().equals("Time")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsTime();
			timeHourAnswerSurvey.setValue(surveyQuestionModel.getAnswer1());
			timeMinuteAnswerSurvey.setValue(surveyQuestionModel.getAnswer2());
			
		}
		else if(surveyQuestionModel.getQuestiontype().equals("Scale")){
			setTextBoxAndCheckBoxValuesScale(surveyQuestionModel);
			typeSurveyEqualsScaleAndGrid();
			scaleToSurveyParagraph.setText(surveyQuestionModel.getAnswer4());
			scaleFromSurveyParagraph.addStyleName("scaleFromSurveyParagraphRetriveQuestion");
			generateRadioButtonScaleQuestion(surveyQuestionModel.getAnswer1(), surveyQuestionModel.getAnswer2());
		
		}else if(surveyQuestionModel.getQuestiontype().equals("Grid")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsScaleAndGrid();
			generateGridQuestions(surveyQuestionModel);
		}		
		
	}
	
	/**
	 * Generate grid questions.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void generateGridQuestions(SurveyQuestionModel surveyQuestionModel) {
		FlexTable gridFlexTable = new FlexTable();
		gridRadioButtonList = new ArrayList<>();

		gridFlexTable.setHTML(0, 0, " ");

		for(int i = 0; i < surveyQuestionModel.getRowGridList().size() ; i++){
			if((i % 2) == 0){
				gridFlexTable.getRowFormatter().addStyleName(i+1,"GridFlexTableEvenRows"); 
			} else {
				gridFlexTable.getRowFormatter().addStyleName(i+1, "GridFlexTableOddRows");
			}
			
			final int indexRowGrid = i;
			gridFlexTable.setHTML(i+1, 0, surveyQuestionModel.getRowGridList().get(i));

			for(int j = 0; j < surveyQuestionModel.getColumnGridList().size() ; j++){
				final String columnGridValue = surveyQuestionModel.getColumnGridList().get(j);
				final RadioButton radioButtonRowGrid = new RadioButton("rowGridRadioGroup" + i);

				radioButtonRowGrid.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						gridRadioButtonAnswerMap.put(indexRowGrid, columnGridValue);
						
					}
				});
				
				gridRadioButtonList.add(radioButtonRowGrid);
				if(i == 0){
					gridFlexTable.setHTML(i, j+1, surveyQuestionModel.getColumnGridList().get(j));
				} 
				gridFlexTable.setWidget(i+1, j+1, radioButtonRowGrid);
			}

		}
		questionSurvey.add(gridFlexTable);
	}

	/**
	 * Sets the text box and check box values.
	 *
	 * @param surveyQuestionModel the new text box and check box values
	 */
	private void setTextBoxAndCheckBoxValues(SurveyQuestionModel surveyQuestionModel){
		
		if(surveyQuestionModel.getSectionTitle() != null
				&& !surveyQuestionModel.getSectionTitle().isEmpty()
				&& !surveyQuestionModel.getSectionTitle().equalsIgnoreCase("")
				&& !surveyQuestionModel.getSectionTitle().equalsIgnoreCase("Untitled Section")){
			sectionTitleStripeHTML = new HTML(surveyQuestionModel.getSectionTitle());
			sectionDescriptionStripeHTML = new HTML(surveyQuestionModel.getSectionDescription());
			sectionTitleStripeHTML.addStyleName("sectionTitleStripe");
			sectionDescriptionStripeHTML.addStyleName("sectionDescriptionStripe");
			verticalPanel.insert(sectionTitleStripeHTML, 0);
			verticalPanel.insert(sectionDescriptionStripeHTML, 1);
		}
		
		questionSurveyParagraph = new Paragraph(surveyQuestionModel.getQuestion());
		questionSurveyParagraph.getElement().setAttribute("style", "font-weight: bold");
		if(surveyQuestionModel.getIsmandatory()){
			questionSurveyParagraph.setText("*" + surveyQuestionModel.getQuestion());
		}
		
		if(surveyQuestionModel.getImageFileName() != null &&
				!surveyQuestionModel.getImageFileName().isEmpty()){
			
			img = new Image(GWT.getModuleBaseURL()+"imageDownloadFromServer?imgName="+surveyQuestionModel.getImageFileName()+"&groupID="+getUserDTO().getGroupId()+"&folderID="+surveyQuestionModel.getFolderIdImage());
			img.addStyleName("imgRetriveQuestionsSurveyView");
		} 
	}
	
	/**
	 * Sets the text box and check box values scale.
	 *
	 * @param surveyQuestionModel the new text box and check box values scale
	 */
	private void setTextBoxAndCheckBoxValuesScale(SurveyQuestionModel surveyQuestionModel){
		questionSurveyParagraph = new Paragraph(surveyQuestionModel.getQuestion());
		questionSurveyParagraph.getElement().setAttribute("style", "font-weight: bold");
		scaleFromSurveyParagraph.setText(surveyQuestionModel.getAnswer3());
		if(surveyQuestionModel.getIsmandatory() != null && surveyQuestionModel.getIsmandatory()){
			scaleFromSurveyParagraph.setText("*" + surveyQuestionModel.getAnswer3());
			if(surveyQuestionModel.getQuestion() != null && !surveyQuestionModel.getQuestion().isEmpty()){
				questionSurveyParagraph.setText("*" + surveyQuestionModel.getQuestion());
			}
		}
		GWT.log("setTextBoxAndCheckBoxValuesScale");
		if(surveyQuestionModel.getImageFileName() != null &&
				!surveyQuestionModel.getImageFileName().isEmpty()){
			String groupId = "-1";
			if (getUserDTO() != null && getUserDTO().getGroupId() > 0)
				groupId = ""+getUserDTO().getGroupId();
			else
				groupId = GCubeClientContext.getCurrentContextId() ;
			
			img = new Image(GWT.getModuleBaseURL()+"imageDownloadFromServer?imgName="+surveyQuestionModel.getImageFileName()+"&groupID="+groupId+"&folderID="+surveyQuestionModel.getFolderIdImage());

		} 

	}
	
	/**
	 * Type survey equals text.
	 */
	private void typeSurveyEqualsText(){
		questionSurvey.clear();
		horizontalPanel.clear();
		answerTextBox.setVisible(true);

		questionSurvey.add(questionSurveyParagraph);
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(textBoxSurveyAnswerControls);

		answerTextBox.setEnabled(true);
	}
	
	/**
	 * Type survey equals paragraph text.
	 */
	private void typeSurveyEqualsParagraphText(){
		questionSurvey.clear();
		horizontalPanel.clear();
		answerTextArea.setVisible(true);

		questionSurvey.add(questionSurveyParagraph);
		if(img != null){
			questionSurvey.add(img);
			questionSurvey.add(textBoxSurveyAnswerControls);
		}
		questionSurvey.add(answerTextArea);
		answerTextArea.setEnabled(true);

	}
	
	/**
	 * Type survey equals date.
	 */
	private void typeSurveyEqualsDate() {
		questionSurvey.clear();
		horizontalPanel.clear();
		dateAnswerSurvey.setVisible(true);

		questionSurvey.add(questionSurveyParagraph);
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(dateAnswerSurveyControls);
		questionSurvey.add(dateAnswerSurvey);
		
		dateAnswerSurvey.setReadOnly(false);
	}
	
	/**
	 * Type survey equals time.
	 */
	private void typeSurveyEqualsTime() {
		questionSurvey.clear();
		horizontalPanel.clear();
		
		answerTextBox.setEnabled(false);
		answerTextBox.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(true);
		timeHourAnswerSurvey.setVisible(true);
		timeMinuteAnswerSurvey.setVisible(true);

		questionSurvey.add(questionSurveyParagraph);
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(timeAnswerSurveyControls);
		questionSurvey.add(timeHourAnswerSurvey);
		questionSurvey.add(timeAnswerSurveyControlsLabelFrom);
		questionSurvey.add(timeMinuteAnswerSurvey);
		
		timeHourAnswerSurvey.setEnabled(true);
		timeMinuteAnswerSurvey.setEnabled(true);
	}
	
	/**
	 * Type survey equals scale and grid.
	 */
	private void typeSurveyEqualsScaleAndGrid() {
		questionSurvey.clear();
		horizontalPanel.clear();
		answerTextBox.setVisible(false);
		answerTextBox.setEnabled(false);
		questionSurvey.add(questionSurveyParagraph);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		questionSurvey.add(new HTML("<br>"));
	}
	

	/**
	 * Type survey equals check boxes.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void typeSurveyEqualsCheckBoxes(SurveyQuestionModel surveyQuestionModel) {
		
		otherTextBox = new TextBox();
		otherTextBox.setPlaceholder("Please specify");
		otherTextBox.setVisible(Boolean.FALSE);
		questionSurvey.add(questionSurveyParagraph);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		checkBoxList = new ArrayList<>();
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if(surveyQuestionModel.getMultipleChoiceList().get(i)!=null){
				final String surveyQuestionModelMultipleChoice = surveyQuestionModel.getMultipleChoiceList().get(i);
				checkBox = new CheckBox(surveyQuestionModel.getMultipleChoiceList().get(i));
				checkBoxList.add(checkBox);
				questionSurvey.add(checkBox);
				
				checkBox.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						if(surveyQuestionModelMultipleChoice.contains("Other...") && checkBox.getValue()){
							otherTextBox.setVisible(Boolean.TRUE);
						} 
						if(surveyQuestionModelMultipleChoice.contains("Other...") && !checkBox.getValue()){
							otherTextBox.setVisible(Boolean.FALSE);
						} 
					}
				});
				
				
				if(surveyQuestionModel.getMultipleChoiceList().get(i).contains("Other...")){
					questionSurvey.add(otherTextBox);
				}
			}
		}
	}
		
	/**
	 * Type survey equals multiple choice.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void typeSurveyEqualsMultipleChoice(SurveyQuestionModel surveyQuestionModel) {
		
		otherTextBox = new TextBox();
		otherTextBox.setPlaceholder("Please specify");
		otherTextBox.setVisible(Boolean.FALSE);
		questionSurvey.add(questionSurveyParagraph);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		radioButtonList = new ArrayList<>();
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if(surveyQuestionModel.getMultipleChoiceList().get(i)!=null){
				final String surveyQuestionModelMultipleChoice = surveyQuestionModel.getMultipleChoiceList().get(i);
				radioButton = new RadioButton("radioMultipleChoiceGroup", surveyQuestionModel.getMultipleChoiceList().get(i));

				radioButtonList.add(radioButton);
				questionSurvey.add(radioButton);


				radioButton.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						if(surveyQuestionModelMultipleChoice.contains("Other...")){
							questionSurvey.add(otherTextBox);
							otherTextBox.setVisible(Boolean.TRUE);
						} else {
							otherTextBox.setVisible(Boolean.FALSE);
						}
					}
				});
			}
		}
	}
	
	/**
	 * Type survey equals drop down.
	 *
	 * @param surveyQuestionModel the survey question model
	 */
	private void typeSurveyEqualsDropDown(SurveyQuestionModel surveyQuestionModel) {
		
		questionSurvey.add(questionSurveyParagraph);
		
		if(img != null){
			questionSurvey.add(img);
		}
		
		listBox = new ListBox();
		listBox.addStyleName("listBoxRetriveQuestionSurvey");
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if(surveyQuestionModel.getMultipleChoiceList().get(i)!=null){
				listBox.addItem(surveyQuestionModel.getMultipleChoiceList().get(i));
			}
		}	
		questionSurvey.add(listBox);
	}
	
	/**
	 * Generate radio button scale question.
	 *
	 * @param start the start
	 * @param end the end
	 */
	private void generateRadioButtonScaleQuestion(String start, String end){
		int startTemp = Integer.parseInt(start);
		int endTemp = Integer.parseInt(end);
		radioButtonScaleSurvey = null;
		radioButtonScaleSurveyList = new ArrayList<>();
		
		horizontalPanelScaleSurvey.clear();
		horizontalPanelScaleSurvey.add(scaleFromSurveyParagraph);
		horizontalPanelScaleSurvey.setCellHorizontalAlignment(scaleFromSurveyParagraph, horizontalPanelScaleSurvey.ALIGN_LEFT);
		for(int i=startTemp; i<endTemp+1; i++){
			radioButtonScaleSurvey = new RadioButton("radioGroup","\n" + String.valueOf(i) + "  ");
			horizontalPanelScaleSurvey.add(radioButtonScaleSurvey);
			horizontalPanelScaleSurvey.setCellHorizontalAlignment(radioButtonScaleSurvey, horizontalPanelScaleSurvey.ALIGN_LEFT);
			radioButtonScaleSurvey.setEnabled(true);
			radioButtonScaleSurveyList.add(radioButtonScaleSurvey);
		}
		horizontalPanelScaleSurvey.add(scaleToSurveyParagraph);
		horizontalPanelScaleSurvey.setCellHorizontalAlignment(scaleToSurveyParagraph, horizontalPanelScaleSurvey.ALIGN_RIGHT);
		questionSurvey.add(horizontalPanelScaleSurvey);
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
	 * Gets the type survey.
	 *
	 * @return the type survey
	 */
	public String getTypeSurvey() {
		return typeSurvey;
	}

	/**
	 * Sets the type survey.
	 *
	 * @param typeSurvey the new type survey
	 */
	public void setTypeSurvey(String typeSurvey) {
		this.typeSurvey = typeSurvey;
	}

	/**
	 * Gets the id survey.
	 *
	 * @return the id survey
	 */
	public Integer getIdSurvey() {
		return idSurvey;
	}

	/**
	 * Sets the id survey.
	 *
	 * @param idSurvey the new id survey
	 */
	public void setIdSurvey(Integer idSurvey) {
		this.idSurvey = idSurvey;
	}

	/**
	 * Gets the numberquestion.
	 *
	 * @return the numberquestion
	 */
	public int getNumberquestion() {
		return numberquestion;
	}

	/**
	 * Sets the numberquestion.
	 *
	 * @param numberquestion the new numberquestion
	 */
	public void setNumberquestion(int numberquestion) {
		this.numberquestion = numberquestion;
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
	 * Gets the radio button.
	 *
	 * @return the radio button
	 */
	public RadioButton getRadioButton() {
		return radioButton;
	}

	/**
	 * Sets the radio button.
	 *
	 * @param radioButton the new radio button
	 */
	public void setRadioButton(RadioButton radioButton) {
		this.radioButton = radioButton;
	}

	/**
	 * Gets the list box.
	 *
	 * @return the list box
	 */
	public ListBox getListBox() {
		return listBox;
	}

	/**
	 * Sets the list box.
	 *
	 * @param listBox the new list box
	 */
	public void setListBox(ListBox listBox) {
		this.listBox = listBox;
	}

	/**
	 * Gets the check box.
	 *
	 * @return the check box
	 */
	public CheckBox getCheckBox() {
		return checkBox;
	}

	/**
	 * Sets the check box.
	 *
	 * @param checkBox the new check box
	 */
	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}

	/**
	 * Gets the check box list.
	 *
	 * @return the check box list
	 */
	public List<CheckBox> getCheckBoxList() {
		return checkBoxList;
	}

	/**
	 * Sets the check box list.
	 *
	 * @param checkBoxList the new check box list
	 */
	public void setCheckBoxList(List<CheckBox> checkBoxList) {
		this.checkBoxList = checkBoxList;
	}

	/**
	 * Gets the radio button scale survey.
	 *
	 * @return the radio button scale survey
	 */
	public RadioButton getRadioButtonScaleSurvey() {
		return radioButtonScaleSurvey;
	}

	/**
	 * Sets the radio button scale survey.
	 *
	 * @param radioButtonScaleSurvey the new radio button scale survey
	 */
	public void setRadioButtonScaleSurvey(RadioButton radioButtonScaleSurvey) {
		this.radioButtonScaleSurvey = radioButtonScaleSurvey;
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
	 * Gets the radio button list.
	 *
	 * @return the radio button list
	 */
	public List<RadioButton> getRadioButtonList() {
		return radioButtonList;
	}

	/**
	 * Sets the radio button list.
	 *
	 * @param radioButtonList the new radio button list
	 */
	public void setRadioButtonList(List<RadioButton> radioButtonList) {
		this.radioButtonList = radioButtonList;
	}

	/**
	 * Gets the radio button scale survey list.
	 *
	 * @return the radio button scale survey list
	 */
	public List<RadioButton> getRadioButtonScaleSurveyList() {
		return radioButtonScaleSurveyList;
	}

	/**
	 * Sets the radio button scale survey list.
	 *
	 * @param radioButtonScaleSurveyList the new radio button scale survey list
	 */
	public void setRadioButtonScaleSurveyList(List<RadioButton> radioButtonScaleSurveyList) {
		this.radioButtonScaleSurveyList = radioButtonScaleSurveyList;
	}

	/**
	 * Checks if is mandatory.
	 *
	 * @return true, if is mandatory
	 */
	public boolean isMandatory() {
		return isMandatory;
	}

	/**
	 * Sets the mandatory.
	 *
	 * @param isMandatory the new mandatory
	 */
	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	/**
	 * Gets the other text box.
	 *
	 * @return the other text box
	 */
	public TextBox getOtherTextBox() {
		return otherTextBox;
	}

	/**
	 * Sets the other text box.
	 *
	 * @param otherTextBox the new other text box
	 */
	public void setOtherTextBox(TextBox otherTextBox) {
		this.otherTextBox = otherTextBox;
	}

	/**
	 * Gets the img.
	 *
	 * @return the img
	 */
	public Image getImg() {
		return img;
	}

	/**
	 * Sets the img.
	 *
	 * @param img the new img
	 */
	public void setImg(Image img) {
		this.img = img;
	}

	/**
	 * Gets the user DTO.
	 *
	 * @return the user DTO
	 */
	public UserDTO getUserDTO() {
		//fix needed when the user is not logged in and the survey is anonymous
		if (this.userDTO == null)
			this.userDTO = new UserDTO();
		//fix needed when the user is not logged in and the survey is anonymous
		if (this.userDTO != null && this.userDTO.getGroupId() < 1) {	
			String groupId = GCubeClientContext.getCurrentContextId();
			if (groupId == null || groupId.compareTo("") == 0)
				groupId = "1";
			this.userDTO.setGroupId(Long.parseLong(groupId));		
		}
		return userDTO;
	}

	/**
	 * Sets the user DTO.
	 *
	 * @param userDTO the new user DTO
	 */
	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	/**
	 * Gets the grid radio button list.
	 *
	 * @return the grid radio button list
	 */
	public List<RadioButton> getGridRadioButtonList() {
		return gridRadioButtonList;
	}

	/**
	 * Sets the grid radio button list.
	 *
	 * @param gridRadioButtonList the new grid radio button list
	 */
	public void setGridRadioButtonList(List<RadioButton> gridRadioButtonList) {
		this.gridRadioButtonList = gridRadioButtonList;
	}

	/**
	 * Gets the grid radio button answer map.
	 *
	 * @return the grid radio button answer map
	 */
	public Map<Integer, String> getGridRadioButtonAnswerMap() {
		return gridRadioButtonAnswerMap;
	}

	/**
	 * Sets the grid radio button answer map.
	 *
	 * @param gridRadioButtonAnswerMap the grid radio button answer map
	 */
	public void setGridRadioButtonAnswerMap(Map<Integer, String> gridRadioButtonAnswerMap) {
		this.gridRadioButtonAnswerMap = gridRadioButtonAnswerMap;
	}

}
