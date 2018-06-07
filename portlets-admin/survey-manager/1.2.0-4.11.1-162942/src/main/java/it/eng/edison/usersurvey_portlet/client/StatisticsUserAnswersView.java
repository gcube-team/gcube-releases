package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;

/**
 * The Class StatisticsUserAnswersView.
 */
public class StatisticsUserAnswersView extends Composite {

	/** The ui binder. */
	private static StatisticsUserAnswersViewUiBinder uiBinder = GWT.create(StatisticsUserAnswersViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface StatisticsUserAnswersViewUiBinder.
	 */
	interface StatisticsUserAnswersViewUiBinder extends UiBinder<Widget, StatisticsUserAnswersView> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The export to CSV button. */
	@UiField Button backToStatisticsButton, backToStatisticsButtonBottom, exportToCSVButton;
	
	/** The well form answer bottom. */
	@UiField WellForm wellFormAnswer, wellFormAnswerBottom;
	
	/** The title survey heading. */
	@UiField Heading titleSurveyHeading;
	
	/** The mandatory field paragraph bottom. */
	@UiField Paragraph mandatoryFieldParagraph, usernameParagraph, mandatoryFieldParagraphBottom;
	
	/** The survey question model list. */
	private List<SurveyQuestionModel> surveyQuestionModelList = null;
	
	/** The number of members filled survey. */
	private int idSurveySelected, numberOfMembersFilledSurvey;
	
	/** The title survey. */
	private String titleSurvey;
	
	/** The create survey view. */
	private CreateQuestionView createSurveyView = null;
	
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
	
	/** The id user answer. */
	private long idUserAnswer;
	
	/** The survey model. */
	private SurveyModel surveyModel;
	
	/** The is mandatory field alert. */
	private boolean isMandatoryFieldAlert;
	
	/** The is anonymous. */
	private boolean isAnonymous;
	
	/** The statistics survey view. */
	private StatisticsSurveyView statisticsSurveyView = null;
	
	/** The survey home page. */
	private SurveyHomePage surveyHomePage = null;
	
	/** The survey user answer model list. */
	private List<SurveyUserAnswerModel> surveyUserAnswerModelList;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The survey user answer model list CSV. */
	private List<SurveyUserAnswerModel> surveyUserAnswerModelListCSV;

	
	/**
	 * Instantiates a new statistics user answers view.
	 */
	public StatisticsUserAnswersView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	/**
	 * Instantiates a new statistics user answers view.
	 *
	 * @param userFullName the user full name
	 * @param idUserAnswer the id user answer
	 * @param idSurveySelected the id survey selected
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param numberOfMembersFilledSurvey the number of members filled survey
	 * @param surveyQuestionModelList the survey question model list
	 * @param surveyUserAnswerModelList the survey user answer model list
	 * @param userDTO the user DTO
	 */
	public StatisticsUserAnswersView(String userFullName, long idUserAnswer, int idSurveySelected, String titleSurvey, boolean isAnonymous, int numberOfMembersFilledSurvey, List<SurveyQuestionModel> surveyQuestionModelList, List<SurveyUserAnswerModel> surveyUserAnswerModelList, UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		
		surveyHomePage = new SurveyHomePage();
		surveyUserAnswerModelListCSV = new ArrayList<>();

		this.idSurveySelected = idSurveySelected;
		this.titleSurvey = titleSurvey;
		this.surveyQuestionModelList = surveyQuestionModelList;
		this.surveyUserAnswerModelList = surveyUserAnswerModelList;
		this.idUserAnswer = idUserAnswer;
		this.setRow(0);
		this.isAnonymous = isAnonymous;
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
		this.userDTO = userDTO;
		
		setMandatoryFieldAlert(Boolean.FALSE);
		
		usernameParagraph.setVisible(true);
		usernameParagraph.addStyleName("usernameParagraphStatistics");
		
		if(!isAnonymous()){
			usernameParagraph.setText(userFullName + "'s answers");
		} else {
			usernameParagraph.setText("Guest's answers");
		}

		titleSurveyHeading.setVisible(true);
		titleSurveyHeading.setText("\"" + titleSurvey + "\"");
		flexTable = new FlexTable();
		listSurveyQuestion = new ArrayList<RetriveQuestionsSurveyView>();
		surveyAnswerModelList = new ArrayList<>();

		FlexTable questions = addQuestionAndAnswerSurveyToPanel(surveyQuestionModelList, surveyUserAnswerModelList);
		
		this.surveyModel = populateSurveyModel();
		
		verticalPanel.add(wellFormAnswer);
		verticalPanel.add(titleSurveyHeading);
		verticalPanel.add(usernameParagraph);
		verticalPanel.add(questions);
		verticalPanel.add(wellFormAnswerBottom);
		RootPanel.get("survey-div").add(verticalPanel);
	}
	
	/**
	 * On click back to statistics button.
	 *
	 * @param event the event
	 */
	@UiHandler("backToStatisticsButton")
	void onClickBackToStatisticsButton(ClickEvent event){
		goBackToStatisticsView();
	}
	
	/**
	 * On click back to statistics button bottom.
	 *
	 * @param event the event
	 */
	@UiHandler("backToStatisticsButtonBottom")
	void onClickBackToStatisticsButtonBottom(ClickEvent event){
		goBackToStatisticsView();
	}
	
	/**
	 * Go back to statistics view.
	 */
	private void goBackToStatisticsView(){
		 RootPanel.get("survey-div").clear();
		 statisticsSurveyView = new StatisticsSurveyView(getIdSurveySelected(), getTitleSurvey(), isAnonymous(), getNumberOfMembersFilledSurvey(), getSurveyUserAnswerModelList());
		 RootPanel.get("survey-div").add(statisticsSurveyView);
	}
	
	/**
	 * On click export to CSV button button.
	 *
	 * @param event the event
	 */
	@UiHandler("exportToCSVButton")
	void onClickExportToCSVButtonButton(ClickEvent event){
		List<UserDTO> userDTOList = new ArrayList<>();
		userDTOList.add(getUserDTO());
		
		greetingService.exportToCSVFile(titleSurvey, isAnonymous(), userDTOList, surveyUserAnswerModelListCSV, surveyQuestionModelList, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("The file cannot be opened created!");
			}

			@Override
			public void onSuccess(Void result) {
//				Window.alert("onSuccess1 "+GWT.getModuleBaseURL()+"exportCSV");
				Window.Location.replace(GWT.getModuleBaseURL()+"exportCSV?titleSurvey="+titleSurvey);
				 
//				final FormPanel form = new FormPanel();
//				form.setAction(GWT.getModuleBaseURL()+"exportCSV");
//				form.submit();
//				form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
//					@Override
//					public void onSubmitComplete(SubmitCompleteEvent event) {
//						Window.alert("File exported ");
//					}
//				}); 			
			}
		});
	}
	
 
	/**
	 * Populate survey model.
	 *
	 * @return the survey model
	 */
	private SurveyModel populateSurveyModel(){
		surveyModel = new SurveyModel();
		surveyModel.setTitlesurvey(getTitleSurvey());
		greetingService.getSurvey(getIdSurveySelected(), new AsyncCallback<SurveyModel>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SurveyModel result) {
				surveyModel.setIdsurvey(result.getIdsurvey());
				surveyModel.setIdUserCreator(result.getIdUserCreator());	
				surveyModel.setIsAnonymous(result.getIsAnonymous());
				surveyModel.setDateSurvay(result.getDateSurvay());
				
				surveyModel.setCreatorFullname(result.getCreatorFullname());
				surveyModel.setExpiredDateSurvay(result.getExpiredDateSurvay());
			}
		});
		return surveyModel;
	}
		
	/**
	 * Add question and answers to the view panel .
	 *
	 * @param listModifySurveyQuestionModel list of question
	 * @param surveyUserAnswerModelList list of answers
	 * @return a FlexTable including question and user's answer
	 */
	private FlexTable addQuestionAndAnswerSurveyToPanel(List<SurveyQuestionModel> listModifySurveyQuestionModel, List<SurveyUserAnswerModel> surveyUserAnswerModelList){
		int idUserAns = (int) idUserAnswer;
		
		if(listModifySurveyQuestionModel != null){
			for(int i=0; i<listModifySurveyQuestionModel.size(); i++){
				for(int j=0; j<surveyUserAnswerModelList.size(); j++ ){
					if((listModifySurveyQuestionModel.get(i).getNumberquestion() == surveyUserAnswerModelList.get(j).getNumberquestion()) &&
							(idUserAns == surveyUserAnswerModelList.get(j).getIduseranswer())){
						retriveQuestionsSurveyView = new RetriveQuestionsSurveyView(listModifySurveyQuestionModel.get(i), surveyUserAnswerModelList.get(j), getUserDTO());
						flexTable.setWidget(row, 0, retriveQuestionsSurveyView);
						this.setRow(row+1);
						this.getListSurveyQuestion().add(retriveQuestionsSurveyView);
						
						surveyUserAnswerModelListCSV.add(surveyUserAnswerModelList.get(j));
					}
				}
			}
		}
		return flexTable;
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
	 * Gets the survey user answer model list.
	 *
	 * @return the survey user answer model list
	 */
	public List<SurveyUserAnswerModel> getSurveyUserAnswerModelList() {
		return surveyUserAnswerModelList;
	}


	/**
	 * Sets the survey user answer model list.
	 *
	 * @param surveyUserAnswerModelList the new survey user answer model list
	 */
	public void setSurveyUserAnswerModelList(List<SurveyUserAnswerModel> surveyUserAnswerModelList) {
		this.surveyUserAnswerModelList = surveyUserAnswerModelList;
	}


	/**
	 * Checks if is anonymous.
	 *
	 * @return true, if is anonymous
	 */
	public boolean isAnonymous() {
		return isAnonymous;
	}


	/**
	 * Sets the anonymous.
	 *
	 * @param isAnonymous the new anonymous
	 */
	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}


	/**
	 * Gets the number of members filled survey.
	 *
	 * @return the number of members filled survey
	 */
	public int getNumberOfMembersFilledSurvey() {
		return numberOfMembersFilledSurvey;
	}


	/**
	 * Sets the number of members filled survey.
	 *
	 * @param numberOfMembersFilledSurvey the new number of members filled survey
	 */
	public void setNumberOfMembersFilledSurvey(int numberOfMembersFilledSurvey) {
		this.numberOfMembersFilledSurvey = numberOfMembersFilledSurvey;
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
	 * Gets the survey user answer model list CSV.
	 *
	 * @return the survey user answer model list CSV
	 */
	public List<SurveyUserAnswerModel> getSurveyUserAnswerModelListCSV() {
		return surveyUserAnswerModelListCSV;
	}


	/**
	 * Sets the survey user answer model list CSV.
	 *
	 * @param surveyUserAnswerModelListCSV the new survey user answer model list CSV
	 */
	public void setSurveyUserAnswerModelListCSV(List<SurveyUserAnswerModel> surveyUserAnswerModelListCSV) {
		this.surveyUserAnswerModelListCSV = surveyUserAnswerModelListCSV;
	}

}
