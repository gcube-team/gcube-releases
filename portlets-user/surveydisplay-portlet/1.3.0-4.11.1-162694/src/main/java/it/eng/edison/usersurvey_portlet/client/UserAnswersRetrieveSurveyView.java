package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;

/**
 * The Class UserAnswersRetrieveSurveyView.
 */
public class UserAnswersRetrieveSurveyView extends Composite {

	/** The ui binder. */
	private static UserAnswersRetrieveSurveyViewUiBinder uiBinder = GWT.create(UserAnswersRetrieveSurveyViewUiBinder.class);

	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface UserAnswersRetrieveSurveyViewUiBinder.
	 */
	interface UserAnswersRetrieveSurveyViewUiBinder extends UiBinder<Widget, UserAnswersRetrieveSurveyView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;

	/** The back to home survey button bottom. */
	@UiField Button backToHomeSurveyButton, sendAnswersSurveyButtonBottom, backToHomeSurveyButtonBottom;

	/** The well form answer bottom. */
	@UiField WellForm wellFormAnswer, wellFormAnswerBottom;

	/** The title survey heading. */
	@UiField Heading titleSurveyHeading;

	/** The mandatory field paragraph bottom. */
	@UiField Paragraph mandatoryFieldParagraph, mandatoryFieldParagraphBottom;

	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList = null;

	/** The id survey selected. */
	private int idSurveySelected;

	/** The title survey. */
	private String titleSurvey;

	/** The retrive questions survey view. */
	private RetriveQuestionsSurveyView retriveQuestionsSurveyView = null;

	/** The flex table. */
	private FlexTable flexTable; 

	/** The list survey question. */
	private List<RetriveQuestionsSurveyView> listSurveyQuestion = null;

	/** The row. */
	private int row;

	/** The survey answer model. */
	private SurveyAnswerModel surveyAnswerModel = null;

	/** The survey answer model list. */
	private List<SurveyAnswerModel> surveyAnswerModelList = null;

	/** The user DTO. */
	private UserDTO userDTO;

	/** The id user answer. */
	private long idUserAnswer;

	/** The survey model. */
	private SurveyModel surveyModel;

	/** The is mandatory field alert. */
	private boolean isMandatoryFieldAlert;

	/** The token model. */
	private TokenModel tokenModel;

	/** The send answer success view. */
	private SendAnswerSuccessView sendAnswerSuccessView;

	/** The user answer not anonymous survey view. */
	private UserAnswerNotAnonymousSurveyView userAnswerNotAnonymousSurveyView;

	/** The multiple choice list temp. */
	private List<String> multipleChoiceListTemp;
	
	/** The grid answer list temp. */
	private List<String> gridAnswerListTemp;

	/** The question and answer wellform. */
	private WellForm questionAndAnswerWellform;

	/** The current URL. */
	private String currentURL;

	/** The back to homepage. */
	private boolean backToHomepage;

	/** The Constant MIN_ID_USER_RANDOM. */
	public final static Integer MIN_ID_USER_RANDOM = 50000000;

	/** The Constant MAX_ID_USER_RANDOM. */
	public final static Integer MAX_ID_USER_RANDOM = 60000000;	

	/** The Constant EMPTY_TOKEN. */
	private static final String EMPTY_TOKEN = "EMPTY TOKEN";

	/**
	 * Instantiates a new user answers retrieve survey view.
	 */
	public UserAnswersRetrieveSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Instantiates a new user answers retrieve survey view.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvey the title survey
	 * @param surveyQuestionModelList the survey question model list
	 * @param tokenModel the token model
	 * @param backToHomepage the back to homepage
	 * @param userDTO the user DTO
	 */
	public UserAnswersRetrieveSurveyView(int idSurveySelected, String titleSurvey, List<SurveyQuestionModel> surveyQuestionModelList, TokenModel tokenModel, boolean backToHomepage, UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));

		this.idSurveySelected = idSurveySelected;
		this.surveyQuestionModelList = surveyQuestionModelList;
		this.titleSurvey = titleSurvey;
		this.setRow(0);
		this.tokenModel = tokenModel;
		this.currentURL = Window.Location.getHref();
		this.backToHomepage = backToHomepage;
		this.userDTO = userDTO;
		if (this.userDTO != null)
			setIdUserAnswer(userDTO.getUserId());

		setMandatoryFieldAlert(Boolean.FALSE);
		backToHomeSurveyButtonBottom.setVisible(Boolean.FALSE);

		if(isBackToHomepage()){
			wellFormAnswer.setVisible(Boolean.TRUE);
			backToHomeSurveyButton.setVisible(Boolean.TRUE);
			mandatoryFieldParagraph.setVisible(Boolean.TRUE);
		} else {
			wellFormAnswer.setVisible(Boolean.FALSE);
			backToHomeSurveyButton.setVisible(Boolean.FALSE);
			mandatoryFieldParagraph.setVisible(Boolean.FALSE);
		}

		questionAndAnswerWellform = new WellForm();
		questionAndAnswerWellform.setVisible(true);
		getUserFromSession();

		flexTable = new FlexTable();
		listSurveyQuestion = new ArrayList<RetriveQuestionsSurveyView>();
		surveyAnswerModelList = new ArrayList<>();

		FlexTable questions = addQuestionSurveyToPanel(surveyQuestionModelList);
		questionAndAnswerWellform.add(questions);

		this.surveyModel = populateSurveyModel();

		titleSurveyHeading.setVisible(true);
		titleSurveyHeading.setText("\"" + titleSurvey + "\"");

		verticalPanel.add(wellFormAnswer);
		verticalPanel.add(titleSurveyHeading);
		verticalPanel.add(questionAndAnswerWellform);
		verticalPanel.add(wellFormAnswerBottom);
		RootPanel.get("displaysurvey-div").add(verticalPanel);
	}

	/**
	 * On click back to home survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("backToHomeSurveyButton")
	void onClickBackToHomeSurveyButton(ClickEvent event){
		backToHomepage();
	}

	/**
	 * On click back to home survey button bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("backToHomeSurveyButtonBottom")
	void onClickBackToHomeSurveyButtonBottom(ClickEvent event){
		backToHomepage();
	}

	/**
	 * Back to homepage.
	 */
	private void backToHomepage(){
		RootPanel.get("displaysurvey-div").clear();
		SurveyStart surveyStart = new SurveyStart();
		RootPanel.get("displaysurvey-div").add(surveyStart);
	}


	/**
	 * On click send answers survey button bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("sendAnswersSurveyButtonBottom")
	void onClickSendAnswersSurveyButtonBottom(ClickEvent event){

		saveAllAnswer();
		if(isMandatoryFieldAlert()){
			setMandatoryFieldAlert(Boolean.FALSE);
			return;
		}

		greetingService.saveAllAnswer(surveyModel, surveyAnswerModelList, tokenModel, getCurrentURL(), userDTO.getFullName(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Void result) {
				surveyAnswerModelList.clear();
				sendAnswerSuccess();
			}
		});
	}

	/**
	 * Send answer success.
	 */
	private void sendAnswerSuccess(){
		RootPanel.get("displaysurvey-div").clear();
		sendAnswerSuccessView = new SendAnswerSuccessView(userDTO);
		RootPanel.get("displaysurvey-div").add(sendAnswerSuccessView);
	}

	/**
	 * Save all answer.
	 */
	private void saveAllAnswer() {
		surveyAnswerModelList.clear();

		List<RetriveQuestionsSurveyView> questionSurveyList = null;
		questionSurveyList = this.getListSurveyQuestion();
		RetriveQuestionsSurveyView retriveCurrQuestion = null;

		for (int i=0;i<questionSurveyList.size();i++){
			retriveCurrQuestion = (RetriveQuestionsSurveyView) questionSurveyList.get(i);

			/* Text Question */
			if(retriveCurrQuestion.getTypeSurvey().equals("Text")){
				addQuestionToSurveyList(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getAnswerTextBox().getValue(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}

			/* Paragraph Text Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Paragraph Text")){
				addQuestionToSurveyList(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getAnswerTextArea().getValue(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}

			/* Multiple Choices */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Multiple Choice")){
				addQuestionToSurveyListMultipleChoice(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getRadioButtonList(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey(), retriveCurrQuestion.getOtherTextBox());
			}

			/* Drop-Down Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Drop-Down")){
				addQuestionToSurveyList(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getListBox().getValue(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}

			/* CheckBoxes Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("CheckBoxes")){
				addQuestionToSurveyListCheckBox(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getCheckBoxList(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey(), retriveCurrQuestion.getOtherTextBox());
			}

			/* Scale Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Scale")){
				addQuestionToSurveyListMultipleChoice(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getRadioButtonScaleSurveyList(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey(), retriveCurrQuestion.getOtherTextBox());
			}

			/* Time Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Time")){
				addQuestionToSurveyList(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getTimeHourAnswerSurvey().getText(), retriveCurrQuestion.getTimeMinuteAnswerSurvey().getText(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}

			/* Date Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Date")){
				addQuestionToSurveyList(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getDateAnswerSurvey().getValue(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}
			
			/* Grid Question */
			else if(retriveCurrQuestion.getTypeSurvey().equals("Grid")){
				addQuestionToSurveyListGrid(getIdUserAnswer(), getIdSurveySelected(), retriveCurrQuestion.getNumberquestion(), retriveCurrQuestion.getGridRadioButtonAnswerMap(), retriveCurrQuestion.isMandatory(), retriveCurrQuestion.getTypeSurvey());
			}

			if(isMandatoryFieldAlert()){
				surveyAnswerModelList.clear();
				return;
			}
		}		
	}

	/**
	 * Adds the question to survey list.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param answer1 the answer 1
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 */
	private void addQuestionToSurveyList(long idUserAnswer, int idSurvey, int numberquestion, String answer1, boolean isMandatory, String typeSurvey) {

		if(isMandatory && (answer1 == null || answer1.isEmpty())){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);
		surveyAnswerModel.setAnswer1(answer1);

		surveyAnswerModelList.add(surveyAnswerModel);
	}

	/**
	 * Adds the question to survey list.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param hours the hours
	 * @param minutes the minutes
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 */
	private void addQuestionToSurveyList(long idUserAnswer, int idSurvey, int numberquestion, String hours, String minutes, boolean isMandatory, String typeSurvey) {

		if(isMandatory && (hours == null || minutes == null
				|| hours.isEmpty() || minutes.isEmpty())){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);
		surveyAnswerModel.setAnswer1(hours);
		surveyAnswerModel.setAnswer2(minutes);

		surveyAnswerModelList.add(surveyAnswerModel);
	}

	/**
	 * Adds the question to survey list.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param date the date
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 */
	private void addQuestionToSurveyList(long idUserAnswer, int idSurvey, int numberquestion, Date date, boolean isMandatory, String typeSurvey) {

		if(isMandatory && (date == null)){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);
		surveyAnswerModel.setDate(date);

		surveyAnswerModelList.add(surveyAnswerModel);
	}

	/**
	 * Adds the question to survey list multiple choice.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param radioButtonList the radio button list
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 * @param otherTextBox the other text box
	 */
	private void addQuestionToSurveyListMultipleChoice(long idUserAnswer, int idSurvey, int numberquestion, List<RadioButton> radioButtonList, boolean isMandatory, String typeSurvey, TextBox otherTextBox) {

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);

		for(int i=0; i<radioButtonList.size(); i++){
			if(radioButtonList.get(i).getValue()){
				surveyAnswerModel.setAnswer1(radioButtonList.get(i).getText().trim());

				if(radioButtonList.get(i).getText().contains("Other...")){
					surveyAnswerModel.setAnswer2(otherTextBox.getValue().trim());
				}
			} 
		}

		if(isMandatory && (surveyAnswerModel.getAnswer1()==null || surveyAnswerModel.getAnswer1().isEmpty())){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		if(isMandatory && 
				(surveyAnswerModel.getAnswer1().contains("Other...") && 
						(surveyAnswerModel.getAnswer2() == null || surveyAnswerModel.getAnswer2().isEmpty()))){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModelList.add(surveyAnswerModel);
	}

	/**
	 * Adds the question to survey list check box.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param checkBoxList the check box list
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 * @param otherTextBox the other text box
	 */
	private void addQuestionToSurveyListCheckBox(long idUserAnswer, int idSurvey, int numberquestion, List<CheckBox> checkBoxList, boolean isMandatory, String typeSurvey, TextBox otherTextBox) {

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);

		multipleChoiceListTemp = new ArrayList<>();

		for(int i=0; i<checkBoxList.size(); i++){
			if(checkBoxList.get(i).getValue()){
				multipleChoiceListTemp.add(checkBoxList.get(i).getText());

				if(checkBoxList.get(i).getText().contains("Other...")){
					surveyAnswerModel.setAnswer1(otherTextBox.getValue());
				} 
			}

		}

		surveyAnswerModel.setMultipleChoiceList(multipleChoiceListTemp);

		if(isMandatory && ((surveyAnswerModel.getMultipleChoiceList().size() == 0) || (surveyAnswerModel.getMultipleChoiceList().isEmpty()))) {
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		if(isMandatory && 
				(surveyAnswerModel.getMultipleChoiceList().contains("Other...") && 
						(surveyAnswerModel.getAnswer1() == null || surveyAnswerModel.getAnswer1().isEmpty()))){
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModelList.add(surveyAnswerModel);
	}
	
	/**
	 * Adds the question to survey list grid.
	 *
	 * @param idUserAnswer the id user answer
	 * @param idSurvey the id survey
	 * @param numberquestion the numberquestion
	 * @param gridRadioButtonAnswerMap the grid radio button answer map
	 * @param isMandatory the is mandatory
	 * @param typeSurvey the type survey
	 */
	private void addQuestionToSurveyListGrid(long idUserAnswer, int idSurvey, int numberquestion, Map<Integer,String> gridRadioButtonAnswerMap, boolean isMandatory, String typeSurvey) {

		surveyAnswerModel = new SurveyAnswerModel();
		surveyAnswerModel.setIdUserAnswer((int) idUserAnswer);
		surveyAnswerModel.setIdSurvey(idSurvey);
		surveyAnswerModel.setQuestiontype(typeSurvey);
		surveyAnswerModel.setNumberquestion(numberquestion);
		
		List<String> gridRadioButtonAnswerValues = new ArrayList<String>(gridRadioButtonAnswerMap.values());
		
		surveyAnswerModel.setGridAnswerList(gridRadioButtonAnswerValues);


		if(isMandatory && ((surveyAnswerModel.getGridAnswerList().size() == 0) || (surveyAnswerModel.getGridAnswerList().isEmpty()))) {
			setMandatoryFieldAlert(Boolean.TRUE);
			Window.alert("Please, fill all required fields");
			return;
		}

		surveyAnswerModelList.add(surveyAnswerModel);
	}

	
	
	
	/**
	 * Populate survey model.
	 *
	 * @return the survey model
	 */
	private SurveyModel populateSurveyModel(){
		surveyModel = new SurveyModel();
		greetingService.getSurvey(getIdSurveySelected(), new AsyncCallback<SurveyModel>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SurveyModel result) {
				surveyModel.setIdsurvey(result.getIdsurvey());
				surveyModel.setTitlesurvey(result.getTitlesurvey());
				surveyModel.setIdUserCreator(result.getIdUserCreator());	
				surveyModel.setIsAnonymous(result.getIsAnonymous());
				surveyModel.setDateSurvay(result.getDateSurvay());
				surveyModel.setGroupId(result.getGroupId());

			}
		});
		return surveyModel;
	}

	/**
	 * Adds the question survey to panel.
	 *
	 * @param listModifySurveyQuestionModel the list modify survey question model
	 * @return the flex table
	 */
	private FlexTable addQuestionSurveyToPanel(List<SurveyQuestionModel> listModifySurveyQuestionModel){
		if(listModifySurveyQuestionModel != null){
			for(int i=1; i<=listModifySurveyQuestionModel.size(); i++){

				for(int j=0; j<listModifySurveyQuestionModel.size(); j++){
					if(listModifySurveyQuestionModel.get(j).getNumberquestion() == i){
						retriveQuestionsSurveyView = new RetriveQuestionsSurveyView(listModifySurveyQuestionModel.get(j), getUserDTO());
						flexTable.setWidget(row, 0, retriveQuestionsSurveyView);
						this.setRow(row+1);
						this.getListSurveyQuestion().add(retriveQuestionsSurveyView);
					}
				}
			}
		}
		return flexTable;
	}

	/**
	 * Gets the user from session.
	 *
	 * @return the user from session
	 */
	private void getUserFromSession(){
		greetingService.getUser(new AsyncCallback<UserDTO>(){
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(UserDTO result) {

				setUserDTO(result);
				setIdUserAnswer(result.getUserId());
			}
		});
	}


	/**
	 * Gets the id survey selected.
	 *
	 * @return the id survey selected
	 */
	public int getIdSurveySelected() {
		return idSurveySelected;
	}


	/**
	 * Sets the id survey selected.
	 *
	 * @param idSurveySelected the new id survey selected
	 */
	public void setIdSurveySelected(int idSurveySelected) {
		this.idSurveySelected = idSurveySelected;
	}

	/**
	 * Gets the survey question model list.
	 *
	 * @return the survey question model list
	 */
	public List<SurveyQuestionModel> getSurveyQuestionModelList() {
		return surveyQuestionModelList;
	}

	/**
	 * Sets the survey question model list.
	 *
	 * @param surveyQuestionModelList the new survey question model list
	 */
	public void setSurveyQuestionModelList(List<SurveyQuestionModel> surveyQuestionModelList) {
		this.surveyQuestionModelList = surveyQuestionModelList;
	}

	/**
	 * Gets the title survey.
	 *
	 * @return the title survey
	 */
	public String getTitleSurvey() {
		return titleSurvey;
	}

	/**
	 * Sets the title survey.
	 *
	 * @param titleSurvey the new title survey
	 */
	public void setTitleSurvey(String titleSurvey) {
		this.titleSurvey = titleSurvey;
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
	 * Gets the retrive questions survey view.
	 *
	 * @return the retrive questions survey view
	 */
	public RetriveQuestionsSurveyView getRetriveQuestionsSurveyView() {
		return retriveQuestionsSurveyView;
	}

	/**
	 * Sets the retrive questions survey view.
	 *
	 * @param retriveQuestionsSurveyView the new retrive questions survey view
	 */
	public void setRetriveQuestionsSurveyView(RetriveQuestionsSurveyView retriveQuestionsSurveyView) {
		this.retriveQuestionsSurveyView = retriveQuestionsSurveyView;
	}

	/**
	 * Gets the list survey question.
	 *
	 * @return the list survey question
	 */
	public List<RetriveQuestionsSurveyView> getListSurveyQuestion() {
		return listSurveyQuestion;
	}

	/**
	 * Sets the list survey question.
	 *
	 * @param listSurveyQuestion the new list survey question
	 */
	public void setListSurveyQuestion(List<RetriveQuestionsSurveyView> listSurveyQuestion) {
		this.listSurveyQuestion = listSurveyQuestion;
	}

	/**
	 * Gets the id user answer.
	 *
	 * @return the id user answer
	 */
	public long getIdUserAnswer() {
		return idUserAnswer;
	}

	/**
	 * Sets the id user answer.
	 *
	 * @param idUserAnswer the new id user answer
	 */
	public void setIdUserAnswer(long idUserAnswer) {
		this.idUserAnswer = idUserAnswer;
	}

	/**
	 * Gets the survey model.
	 *
	 * @return the survey model
	 */
	public SurveyModel getSurveyModel() {
		return surveyModel;
	}

	/**
	 * Sets the survey model.
	 *
	 * @param surveyModel the new survey model
	 */
	public void setSurveyModel(SurveyModel surveyModel) {
		this.surveyModel = surveyModel;
	}

	/**
	 * Gets the survey answer model list.
	 *
	 * @return the survey answer model list
	 */
	public List<SurveyAnswerModel> getSurveyAnswerModelList() {
		return surveyAnswerModelList;
	}

	/**
	 * Sets the survey answer model list.
	 *
	 * @param surveyAnswerModelList the new survey answer model list
	 */
	public void setSurveyAnswerModelList(List<SurveyAnswerModel> surveyAnswerModelList) {
		this.surveyAnswerModelList = surveyAnswerModelList;
	}

	/**
	 * Checks if is mandatory field alert.
	 *
	 * @return true, if is mandatory field alert
	 */
	public boolean isMandatoryFieldAlert() {
		return isMandatoryFieldAlert;
	}

	/**
	 * Sets the mandatory field alert.
	 *
	 * @param isMandatoryFieldAlert the new mandatory field alert
	 */
	public void setMandatoryFieldAlert(boolean isMandatoryFieldAlert) {
		this.isMandatoryFieldAlert = isMandatoryFieldAlert;
	}

	/**
	 * Gets the token model.
	 *
	 * @return the token model
	 */
	public TokenModel getTokenModel() {
		return tokenModel;
	}

	/**
	 * Sets the token model.
	 *
	 * @param tokenModel the new token model
	 */
	public void setTokenModel(TokenModel tokenModel) {
		this.tokenModel = tokenModel;
	}

	/**
	 * Gets the user answer not anonymous survey view.
	 *
	 * @return the user answer not anonymous survey view
	 */
	public UserAnswerNotAnonymousSurveyView getUserAnswerNotAnonymousSurveyView() {
		return userAnswerNotAnonymousSurveyView;
	}

	/**
	 * Sets the user answer not anonymous survey view.
	 *
	 * @param userAnswerNotAnonymousSurveyView the new user answer not anonymous survey view
	 */
	public void setUserAnswerNotAnonymousSurveyView(UserAnswerNotAnonymousSurveyView userAnswerNotAnonymousSurveyView) {
		this.userAnswerNotAnonymousSurveyView = userAnswerNotAnonymousSurveyView;
	}

	/**
	 * Gets the multiple choice list temp.
	 *
	 * @return the multiple choice list temp
	 */
	public List<String> getMultipleChoiceListTemp() {
		return multipleChoiceListTemp;
	}

	/**
	 * Sets the multiple choice list temp.
	 *
	 * @param multipleChoiceListTemp the new multiple choice list temp
	 */
	public void setMultipleChoiceListTemp(List<String> multipleChoiceListTemp) {
		this.multipleChoiceListTemp = multipleChoiceListTemp;
	}

	/**
	 * Gets the current URL.
	 *
	 * @return the current URL
	 */
	public String getCurrentURL() {
		return currentURL;
	}

	/**
	 * Sets the current URL.
	 *
	 * @param currentURL the new current URL
	 */
	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
	}

	/**
	 * Checks if is back to homepage.
	 *
	 * @return true, if is back to homepage
	 */
	public boolean isBackToHomepage() {
		return backToHomepage;
	}

	/**
	 * Sets the back to homepage.
	 *
	 * @param backToHomepage the new back to homepage
	 */
	public void setBackToHomepage(boolean backToHomepage) {
		this.backToHomepage = backToHomepage;
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

	/**
	 * Gets the grid answer list temp.
	 *
	 * @return the grid answer list temp
	 */
	public List<String> getGridAnswerListTemp() {
		return gridAnswerListTemp;
	}

	/**
	 * Sets the grid answer list temp.
	 *
	 * @param gridAnswerListTemp the new grid answer list temp
	 */
	public void setGridAnswerListTemp(List<String> gridAnswerListTemp) {
		this.gridAnswerListTemp = gridAnswerListTemp;
	}

}
