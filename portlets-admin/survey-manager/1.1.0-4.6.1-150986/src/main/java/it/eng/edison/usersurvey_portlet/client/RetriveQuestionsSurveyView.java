package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Controls;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;


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
	@UiField WellForm questionSurvey;
	
	/** The date answer survey. */
	@UiField DateBoxAppended dateAnswerSurvey;
	
	/** The time minute answer survey. */
	@UiField TextBox timeHourAnswerSurvey, timeMinuteAnswerSurvey;
	
	/** The time answer survey controls label from. */
	@UiField Label timeAnswerSurveyControlsLabelFrom;
	
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
	
	/** The flex table. */
	private FlexTable flexTable; 
	
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
	
	/** The radio button scale survey list. */
	private List<RadioButton> radioButtonScaleSurveyList;
	
	/** The survey user answer model temp. */
	private SurveyUserAnswerModel surveyUserAnswerModelTemp;
	
	/** The survey user answer model. */
	private SurveyUserAnswerModel surveyUserAnswerModel;
	
	/** The paragraph text. */
	private Paragraph inputText, paragraphText;
	
	/** The type question heading. */
	private HTML typeQuestionHeading;
	
	/** The question survey heading. */
	private HTML questionSurveyHeading;
	
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
	 * @param surveyUserAnswerModel the survey user answer model
	 * @param userDTO the user DTO
	 */
	public RetriveQuestionsSurveyView(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel, UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		verticalPanel.add(horizontalPanel);
		questionSurvey.setVisible(false);
		dateAnswerSurvey.setVisible(false);
		
		typeQuestionHeading = new HTML();
		questionSurveyHeading = new HTML();
		
		this.typeSurvey = surveyQuestionModel.getQuestiontype();
		this.isMandatory = surveyQuestionModel.getIsmandatory();
		this.numberquestion = surveyQuestionModel.getNumberquestion();
		this.surveyUserAnswerModel = surveyUserAnswerModel;
		this.userDTO = userDTO;
		
		multipleChoiceList = new ArrayList<MultipleChoiceView>();
		flexTable = new FlexTable();
		addSurveyPanel();
		visibleTypeSurveyBoxes();
		populateSurveyQuestionAndAnswerFromDB(surveyQuestionModel, getSurveyUserAnswerModel());
	}
	

	/**
	 * Adds the survey panel.
	 */
	private void addSurveyPanel() {
		visibleSurveyPanels();

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
		dateAnswerSurvey.setVisible(false);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
	}
	
	/**
	 * Visible survey panels.
	 */
	private void visibleSurveyPanels() {
		verticalPanel.add(questionSurvey);
		timeHourAnswerSurvey.setVisible(false);
		timeMinuteAnswerSurvey.setVisible(false);
		timeAnswerSurveyControlsLabelFrom.setVisible(false);
		controlRadioControls.setVisible(false);

		questionSurvey.setVisible(true);
	}
	

	/**
	 * Populate survey question and answer from DB.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void populateSurveyQuestionAndAnswerFromDB(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel) {
		if(surveyQuestionModel.getQuestiontype().equals("Text")){
			inputText = new Paragraph(surveyUserAnswerModel.getAnswer1());
			inputText.addStyleName("decorate-answer");
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsText();

		}
		else if(surveyQuestionModel.getQuestiontype().equals("Paragraph Text")){
			paragraphText = new Paragraph(surveyUserAnswerModel.getAnswer1());
			paragraphText.addStyleName("decorate-answer");
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsParagraphText();

		}

		else if(surveyQuestionModel.getQuestiontype().equals("Multiple Choice")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsMultipleChoiceAnswer(surveyQuestionModel, surveyUserAnswerModel);
		}

		else if(surveyQuestionModel.getQuestiontype().equals("CheckBoxes")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsCheckBoxesAnswer(surveyQuestionModel, surveyUserAnswerModel);
		}

		else if(surveyQuestionModel.getQuestiontype().equals("Drop-Down")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsDropDownAnswer(surveyQuestionModel, surveyUserAnswerModel);
		}

		else if(surveyQuestionModel.getQuestiontype().equals("Date")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsDate();

			dateAnswerSurvey.setValue(surveyUserAnswerModel.getDateAnswer()); 
			dateAnswerSurvey.setReadOnly(true);
		}

		else if(surveyQuestionModel.getQuestiontype().equals("Time")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsTime();
			timeHourAnswerSurvey.setValue(surveyUserAnswerModel.getAnswer1());
			timeMinuteAnswerSurvey.setValue(surveyUserAnswerModel.getAnswer2());
			timeHourAnswerSurvey.setEnabled(false);
			timeMinuteAnswerSurvey.setEnabled(false);

		}

		else if(surveyQuestionModel.getQuestiontype().equals("Scale")){
			setTextBoxAndCheckBoxValuesScale(surveyQuestionModel);
			typeSurveyEqualsScaleAndGrid();
			scaleToSurveyParagraph.setText(surveyQuestionModel.getAnswer4());
			scaleFromSurveyParagraph.addStyleName("scaleFromSurveyParagraphRetriveQuestion");
			generateRadioButtonScaleQuestionAnswer(surveyQuestionModel.getAnswer1(), surveyQuestionModel.getAnswer2(), surveyUserAnswerModel);
		}
		else if(surveyQuestionModel.getQuestiontype().equals("Grid")){
			setTextBoxAndCheckBoxValues(surveyQuestionModel);
			typeSurveyEqualsScaleAndGrid();
			generateGridQuestions(surveyQuestionModel, surveyUserAnswerModel);
		}	
	}
	
	/**
	 * Generate grid questions.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void generateGridQuestions(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel) {
		FlexTable gridFlexTable = new FlexTable();

		gridFlexTable.setHTML(0, 0, " ");

		for(int i = 0; i < surveyQuestionModel.getRowGridList().size() ; i++){
			
			if((i % 2) == 0){
				gridFlexTable.getRowFormatter().addStyleName(i+1,"GridFlexTableEvenRows"); 
			} else {
				gridFlexTable.getRowFormatter().addStyleName(i+1, "GridFlexTableOddRows");
			}
			
			RadioButton radioButtonRowGrid;
			gridFlexTable.setHTML(i+1, 0, surveyQuestionModel.getRowGridList().get(i));

			for(int j = 0; j < surveyQuestionModel.getColumnGridList().size() ; j++){
				radioButtonRowGrid = new RadioButton("rowGridRadioGroup" + i);
				radioButtonRowGrid.setEnabled(false);
				if(i == 0){
					gridFlexTable.setHTML(i, j+1, surveyQuestionModel.getColumnGridList().get(j));
				} 

				if(surveyQuestionModel.getColumnGridList().get(j).equalsIgnoreCase(surveyUserAnswerModel.getGridAnswerList().get(i))){
					radioButtonRowGrid.setValue(true);
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
		
		typeQuestionHeading = new HTML();
		typeQuestionHeading.setHTML("<h4> <small>" + surveyQuestionModel.getQuestiontype() + " Question" + "</small> </h4>");
		
		questionSurveyHeading = new HTML();
		questionSurveyHeading.setHTML("<h5> " + surveyQuestionModel.getQuestion() + "</h5>");
		
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
		
		if(surveyQuestionModel.getIsmandatory()){
			questionSurveyHeading.setHTML("<h5>*" + surveyQuestionModel.getQuestion() + "</h5>");
		}
		
		if(surveyQuestionModel.getImageFileName() != null &&
				!surveyQuestionModel.getImageFileName().isEmpty()){
			img = new Image(Window.Location.getProtocol()+"//"+Window.Location.getHostName()+"/surveydisply-portlet/Survey/imageDownloadFromServer?imgName="+surveyQuestionModel.getImageFileName()+"&groupID="+getUserDTO().getGroupId()+"&folderID="+surveyQuestionModel.getFolderIdImage());
			img.addStyleName("imgRetriveQuestionsSurveyView");
		} 
		
	}
	
	
	/**
	 * Sets the text box and check box values scale.
	 *
	 * @param surveyQuestionModel the new text box and check box values scale
	 */
	private void setTextBoxAndCheckBoxValuesScale(SurveyQuestionModel surveyQuestionModel){
		if(surveyQuestionModel.getQuestion() != null && !surveyQuestionModel.getQuestion().equalsIgnoreCase("")){
			
			typeQuestionHeading = new HTML();
			typeQuestionHeading.setHTML("<h4> <small>" + surveyQuestionModel.getQuestiontype() + " Question" + "</small> </h4>");
			
			questionSurveyHeading = new HTML();
			questionSurveyHeading.setHTML("<h5> " + surveyQuestionModel.getQuestion() + "</h5>");
			
		}

		scaleFromSurveyParagraph.setText(surveyQuestionModel.getAnswer3());
		if(surveyQuestionModel.getIsmandatory() != null && surveyQuestionModel.getIsmandatory()){
			scaleFromSurveyParagraph.setText("*" + surveyQuestionModel.getAnswer3());
			if(surveyQuestionModel.getQuestion() != null && !surveyQuestionModel.getQuestion().isEmpty()){
				questionSurveyHeading.setHTML("<h5>*" + surveyQuestionModel.getQuestion() + "</h5>");
			}
		}
		
		if(surveyQuestionModel.getImageFileName() != null &&
				!surveyQuestionModel.getImageFileName().isEmpty()){
			
			img = new Image(Window.Location.getProtocol()+"//"+Window.Location.getHostName()+"/surveydisply-portlet/Survey/imageDownloadFromServer?imgName="+surveyQuestionModel.getImageFileName()+"&groupID="+getUserDTO().getGroupId()+"&folderID="+surveyQuestionModel.getFolderIdImage());
			img.addStyleName("imgRetriveQuestionsSurveyView");
		} 
		
	}
	
	/**
	 * Type survey equals text.
	 */
	private void typeSurveyEqualsText(){
		questionSurvey.clear();
		horizontalPanel.clear();
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(textBoxSurveyAnswerControls);
		questionSurvey.add(inputText);
	}
	
	/**
	 * Type survey equals paragraph text.
	 */
	private void typeSurveyEqualsParagraphText(){
		questionSurvey.clear();
		horizontalPanel.clear();
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(textBoxSurveyAnswerControls);
		questionSurvey.add(paragraphText);
	}
	
	/**
	 * Type survey equals date.
	 */
	private void typeSurveyEqualsDate() {
		questionSurvey.clear();
		horizontalPanel.clear();
		dateAnswerSurvey.setVisible(true);

		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
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
		
		timeAnswerSurveyControlsLabelFrom.setVisible(true);
		timeHourAnswerSurvey.setVisible(true);
		timeMinuteAnswerSurvey.setVisible(true);
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
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
	 * Type survey equals scale.
	 */
	private void typeSurveyEqualsScaleAndGrid() {
		questionSurvey.clear();
		horizontalPanel.clear();
		questionSurveyHeading.setVisible(true);
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		if(img != null){
			questionSurvey.add(img);
		}
		questionSurvey.add(dateAnswerSurveyControls);
	}
	
	/**
	 * Generate radio button scale question answer.
	 *
	 * @param start the start
	 * @param end the end
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void generateRadioButtonScaleQuestionAnswer(String start, String end, SurveyUserAnswerModel surveyUserAnswerModel){
		int startTemp = Integer.parseInt(start);
		int endTemp = Integer.parseInt(end);
		radioButtonScaleSurvey = null;
		radioButtonScaleSurveyList = new ArrayList<>();
		
		horizontalPanelScaleSurvey.clear();
		horizontalPanelScaleSurvey.add(scaleFromSurveyParagraph);
		horizontalPanelScaleSurvey.setCellHorizontalAlignment(scaleFromSurveyParagraph, horizontalPanelScaleSurvey.ALIGN_LEFT);
		for(int i=startTemp; i<endTemp+1; i++){
			radioButtonScaleSurvey = new RadioButton("radioGroup","\n" + String.valueOf(i) + "  ");

			if(surveyUserAnswerModel.getAnswer1() != null){
				int scaleValue = Integer.parseInt(surveyUserAnswerModel.getAnswer1().trim());
				if(i == scaleValue){
					radioButtonScaleSurvey.setValue(true);
				}
			}
			radioButtonScaleSurvey.setWidth("20px");
			horizontalPanelScaleSurvey.add(radioButtonScaleSurvey);
			horizontalPanelScaleSurvey.setCellHorizontalAlignment(radioButtonScaleSurvey, horizontalPanelScaleSurvey.ALIGN_CENTER);
			radioButtonScaleSurvey.setEnabled(false);
			radioButtonScaleSurveyList.add(radioButtonScaleSurvey);
		}
		horizontalPanelScaleSurvey.add(scaleToSurveyParagraph);
		horizontalPanelScaleSurvey.setCellHorizontalAlignment(scaleToSurveyParagraph, horizontalPanelScaleSurvey.ALIGN_RIGHT);
		questionSurvey.add(horizontalPanelScaleSurvey);
	}
	
	
	
	/**
	 * Type survey equals check boxes answer.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void typeSurveyEqualsCheckBoxesAnswer(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel) {
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		if(img != null){
			questionSurvey.add(img);
			questionSurvey.add(new HTML("<br>"));
		}
		
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if(surveyQuestionModel.getMultipleChoiceList().get(i) != null && (surveyQuestionModel.getMultipleChoiceList() != null)){
				if(surveyUserAnswerModel.getMultipleChoiceList() != null){
					checkBox = new CheckBox(surveyQuestionModel.getMultipleChoiceList().get(i));
					checkBox.setEnabled(false);
					checkBox.setValue(false);
					
					for(int j = 0; j < surveyUserAnswerModel.getMultipleChoiceList().size(); j++){
						if(surveyUserAnswerModel.getMultipleChoiceList().get(j).equals(surveyQuestionModel.getMultipleChoiceList().get(i))
								&& surveyUserAnswerModel.getQuestiontype().equals("CheckBoxes")
								&& surveyUserAnswerModel.getNumberquestion() == surveyQuestionModel.getNumberquestion()){
							checkBox.setValue(true);
							if((surveyUserAnswerModel.getMultipleChoiceList().get(j).contains("Other...")) 
									&& (surveyUserAnswerModel.getAnswer1() != null)){
								checkBox.setText("Other: " + surveyUserAnswerModel.getAnswer1());
							}
						}
					}
					questionSurvey.add(checkBox);
					questionSurvey.add(new HTML("<br>"));
				}

			}
		}

	}
	
	
	/**
	 * Type survey equals multiple choice answer.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void typeSurveyEqualsMultipleChoiceAnswer(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel) {
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		if(img != null){
			questionSurvey.add(img);
			questionSurvey.add(new HTML("<br>"));
		}
		
		radioButtonList = new ArrayList<>();
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if((surveyQuestionModel.getMultipleChoiceList().get(i) != null)){
				radioButton = new RadioButton("radioMultipleChoiceGroup", surveyQuestionModel.getMultipleChoiceList().get(i));
				radioButton.setEnabled(false);
				if(surveyQuestionModel.getMultipleChoiceList().get(i).equals(surveyUserAnswerModel.getAnswer1())
						&& surveyUserAnswerModel.getQuestiontype().equals("Multiple Choice") 
						&& surveyUserAnswerModel.getNumberquestion() == surveyQuestionModel.getNumberquestion()){
					radioButton.setValue(true);
			
					if(surveyUserAnswerModel.getAnswer1().contains("Other...")){
						inputText = new Paragraph(surveyUserAnswerModel.getAnswer2());
						radioButton.setText("Other: " + surveyUserAnswerModel.getAnswer2());
					}
				}
				radioButtonList.add(radioButton);
				questionSurvey.add(radioButton);
				questionSurvey.add(new HTML("<br>"));
			}
		}
	}
	
	
	
	/**
	 * Type survey equals drop down answer.
	 *
	 * @param surveyQuestionModel the survey question model
	 * @param surveyUserAnswerModel the survey user answer model
	 */
	private void typeSurveyEqualsDropDownAnswer(SurveyQuestionModel surveyQuestionModel, SurveyUserAnswerModel surveyUserAnswerModel ) {
		
		questionSurvey.add(typeQuestionHeading);
		questionSurvey.add(questionSurveyHeading);
		
		if(img != null){
			questionSurvey.add(img);
			questionSurvey.add(new HTML("<br>"));
		}
		
		listBox = new ListBox();
		listBox.setWidth("100%");
		for(int i=0; i<surveyQuestionModel.getMultipleChoiceList().size(); i++){
			if(surveyQuestionModel.getMultipleChoiceList().get(i)!=null){
				listBox.addItem(surveyQuestionModel.getMultipleChoiceList().get(i));
			}
		listBox.setSelectedValue(surveyUserAnswerModel.getAnswer1());
		listBox.setEnabled(false);
		questionSurvey.add(listBox);
		}
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
	 * Gets the survey user answer model temp.
	 *
	 * @return the survey user answer model temp
	 */
	public SurveyUserAnswerModel getSurveyUserAnswerModelTemp() {
		return surveyUserAnswerModelTemp;
	}

	/**
	 * Sets the survey user answer model temp.
	 *
	 * @param surveyUserAnswerModelTemp the new survey user answer model temp
	 */
	public void setSurveyUserAnswerModelTemp(SurveyUserAnswerModel surveyUserAnswerModelTemp) {
		this.surveyUserAnswerModelTemp = surveyUserAnswerModelTemp;
	}

	/**
	 * Gets the survey user answer model.
	 *
	 * @return the survey user answer model
	 */
	public SurveyUserAnswerModel getSurveyUserAnswerModel() {
		return surveyUserAnswerModel;
	}

	/**
	 * Sets the survey user answer model.
	 *
	 * @param surveyUserAnswerModel the new survey user answer model
	 */
	public void setSurveyUserAnswerModel(SurveyUserAnswerModel surveyUserAnswerModel) {
		this.surveyUserAnswerModel = surveyUserAnswerModel;
	}

	/**
	 * Gets the input text.
	 *
	 * @return the input text
	 */
	public Paragraph getInputText() {
		return inputText;
	}

	/**
	 * Sets the input text.
	 *
	 * @param inputText the new input text
	 */
	public void setInputText(Paragraph inputText) {
		this.inputText = inputText;
	}

	/**
	 * Gets the paragraph text.
	 *
	 * @return the paragraph text
	 */
	public Paragraph getParagraphText() {
		return paragraphText;
	}

	/**
	 * Sets the paragraph text.
	 *
	 * @param paragraphText the new paragraph text
	 */
	public void setParagraphText(Paragraph paragraphText) {
		this.paragraphText = paragraphText;
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

}
