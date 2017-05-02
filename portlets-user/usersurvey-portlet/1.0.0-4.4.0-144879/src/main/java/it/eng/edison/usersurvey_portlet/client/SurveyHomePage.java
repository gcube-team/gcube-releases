package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;

/**
 * The Class SurveyHomePage.
 */
public class SurveyHomePage extends Composite {

	/** The ui binder. */
	private static SurveyHomePageUiBinder uiBinder = GWT.create(SurveyHomePageUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * The Interface SurveyHomePageUiBinder.
	 */
	interface SurveyHomePageUiBinder extends UiBinder<Widget, SurveyHomePage> {
	}

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;
	
	/** The survey home page. */
	@UiField WellForm surveyHomePage;
	
	/** The statistic survey button. */
	@UiField Button createNewSurveyButton, modifySurveyButton, deleteSurveyButton, statisticSurveyButton;

	/** The survey radio button value. */
	private String surveyRadioButtonValue = "";
	
	/** The manage survey view. */
	private ManageSurveyView manageSurveyView;
	
	/** The modify survey view. */
	private ModifySurveyView modifySurveyView;
	
	/** The survey model list. */
	private List<SurveyModel> surveyModelList;
	
	/** The survey question model list modify selected survey. */
	private List<SurveyQuestionModel> surveyQuestionModelListModifySelectedSurvey;
	
	/** The id survey selected. */
	private int idSurveySelected;
	
	/** The title survay. */
	private String titleSurvay;
	
	/** The is anonymous. */
	private boolean isAnonymous;
	
	/** The survey user answer model. */
	private SurveyUserAnswerModel surveyUserAnswerModel;
	
	/** The survey user answer model list. */
	private List<SurveyUserAnswerModel> surveyUserAnswerModelList;
	
	/** The statistics survey view. */
	private StatisticsSurveyView statisticsSurveyView;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The flex table. */
	private FlexTable flexTable;
	
	/** The simple panel. */
	private SimplePanel simplePanel;
	
	/** The expired survey date. */
	private Date expiredSurveyDate;
	
	/** The users answered survey. */
	private List<Integer> usersAnsweredSurvey;
	
	/** The filled surveys. */
	private Map<Integer,Integer> filledSurveys;
	
	/** The number of members filled survey. */
	private int numberOfMembersFilledSurvey;

	/**
	 * Instantiates a new survey home page.
	 */
	public SurveyHomePage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		modifySurveyButton.addStyleName("surveyButtonRight");
		deleteSurveyButton.addStyleName("surveyButtonRight");
		
		surveyQuestionModelListModifySelectedSurvey = new ArrayList<SurveyQuestionModel>();
		surveyModelList = new ArrayList<SurveyModel>();
		filledSurveys = new HashMap<Integer,Integer>();
		getUserFromSession();
	}
	
	/**
	 * Gets the user from session.
	 *
	 * @return the user from session
	 */
	private void getUserFromSession() {
		greetingService.getUser(new AsyncCallback<UserDTO>() {

			@Override
			public void onSuccess(UserDTO result) {
				setUserDTO(result);
				getIdSurveyAnswered();
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();				
			}
		});
		
	}
	
	/**
	 * Gets the id survey answered.
	 *
	 * @return the id survey answered
	 */
	private void getIdSurveyAnswered() {
		greetingService.usersAnsweredSurvey(new AsyncCallback<List<Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();	
			}

			@Override
			public void onSuccess(List<Integer> result) {
				setUsersAnsweredSurvey(result);
				getSurveys(getUserDTO());
				
			}

		});
		
	}

	/**
	 * On click create new survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("createNewSurveyButton")
	void onClickCreateNewSurveyButton(ClickEvent event) {
		RootPanel.get("survey-div").clear();
		manageSurveyView = new ManageSurveyView();
		RootPanel.get("survey-div").add(manageSurveyView);
	}

	/**
	 * On click modify survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("modifySurveyButton")
	void onClickModifySurveyButton(ClickEvent event) {
		if (getSurveyRadioButtonValue().isEmpty()) {
			Window.alert("Please select a survey");
			return;
		}
		getSurveyQuestionModelListModifySelectedSurvey().clear();
		surveySelected(getSurveyRadioButtonValue());
		getQuestionsSurvey();
	}

	/**
	 * On click delete survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("deleteSurveyButton")
	void onClickDeleteSurveyButton(ClickEvent event) {
		if (getSurveyRadioButtonValue().isEmpty()) {
			Window.alert("Please, select a survey");
			return;
		}
		if (Window.confirm("Are you sure you want to delete this survey? \nThis action will delete VRE's members answers too.")) {
			for (int i = 0; i < getSurveyModelList().size(); i++) {
				if(surveyRadioButtonValue.contains(" (expired)")){
					setSurveyRadioButtonValue(surveyRadioButtonValue.replace(" (expired)", ""));
				}
				if (getSurveyRadioButtonValue().equals(getSurveyModelList().get(i).getTitlesurvey())) {
					greetingService.deleteSurvey(getSurveyModelList().get(i), new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(Void result) {
							Window.Location.reload();
						}
					});
				}
			}
		} else {
			return;
		}
	}
	
	/**
	 * On click statistic survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("statisticSurveyButton")
	void onClickStatisticSurveyButton(ClickEvent event){
		if (getSurveyRadioButtonValue().isEmpty()) {
			Window.alert("Please select a survey");
			return;
		}
		int idSurveySelected = 0;
		idSurveySelected = surveySelected(getSurveyRadioButtonValue());
		if(getFilledSurveys().get(idSurveySelected) == 0){
			Window.alert("No VRE's member have partecipated to this survey");
			return;
		}
		statView(getSurveyRadioButtonValue(), getFilledSurveys());
	}

	/**
	 * Gets the surveys.
	 *
	 * @param userDTO the user DTO
	 * @return the surveys
	 */
	private void getSurveys(UserDTO userDTO) {
		greetingService.getSurveyList(userDTO, new AsyncCallback<List<SurveyModel>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<SurveyModel> result) {
				if(result != null && result.size() != 0){
					setSurveyModelList(result);
					flexTableCreate();
					setFilledSurveys(countFilledSurveys(getUsersAnsweredSurvey()));
					
					Date today = new Date();
					for (int i = 0; i < getSurveyModelList().size(); i++) {
						String radioButtonText = null;
						if(getSurveyModelList().get(i).getExpiredDateSurvay().before(today)){
							radioButtonText = getSurveyModelList().get(i).getTitlesurvey() + " (expired)";
						} else {
							radioButtonText = getSurveyModelList().get(i).getTitlesurvey();
						}
						final RadioButton surveyRadioButton = new RadioButton("radioGroup",
								 radioButtonText);
						surveyRadioButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								setSurveyRadioButtonValue(surveyRadioButton.getText());
							}
						});
						Date surveyDate = getSurveyModelList().get(i).getDateSurvay();
						Date surveyExpiredDate = getSurveyModelList().get(i).getExpiredDateSurvay();
						
						String surveyCreationDate = modifyDatePositionYMDtoDMY(surveyDate);
						String surveyExpirationDate = modifyDatePositionYMDtoDMY(surveyExpiredDate);
						String fullnameSurveyCreator = getSurveyModelList().get(i).getCreatorFullname();
						
						simplePanel = new SimplePanel();
						flexTable.setWidget(i+1, 0, surveyRadioButton);
						flexTable.setText(i+1, 1, fullnameSurveyCreator);
						flexTable.setText(i+1, 2, surveyCreationDate);
						flexTable.setText(i+1, 3, surveyExpirationDate);
						
						if(getFilledSurveys().get(getSurveyModelList().get(i).getIdsurvey()) == null){
							getFilledSurveys().put(getSurveyModelList().get(i).getIdsurvey(), 0);
						}
						flexTable.setText(i+1, 4, getFilledSurveys().get(getSurveyModelList().get(i).getIdsurvey()).toString());

					}
					simplePanel.add(flexTable);
					verticalPanel.add(simplePanel);
				}
			}

		});
	}
	
	/**
	 * Modify date position YM dto DMY.
	 *
	 * @param surveyDate the survey date
	 * @return the string
	 */
	private String modifyDatePositionYMDtoDMY(Date surveyDate) {
		int minus = surveyDate.toString().indexOf("-");
		
		String day =  surveyDate.toString().substring(minus+4, minus+6);
		String month =  surveyDate.toString().substring(minus+1, minus+3);
		String year =  surveyDate.toString().substring(0, minus);
		String surveyCreationDate = day + "-" + month + "-" + year;
		
		return surveyCreationDate;
}


	/**
	 * Count filled surveys.
	 *
	 * @param usersAnsweredSurveyList the users answered survey list
	 * @return the map
	 */
	private Map<Integer, Integer> countFilledSurveys(List<Integer> usersAnsweredSurveyList) {
		Map<Integer, Integer> countFilledSurveysTemp = new HashMap<Integer, Integer> ();

		
		if(usersAnsweredSurveyList != null && !usersAnsweredSurveyList.isEmpty()){
			for(int i=0; i<usersAnsweredSurveyList.size(); i++){
				if(!countFilledSurveysTemp.containsKey(usersAnsweredSurveyList.get(i))){
					countFilledSurveysTemp.put(usersAnsweredSurveyList.get(i), 1);
				} else {
					countFilledSurveysTemp.put(usersAnsweredSurveyList.get(i), countFilledSurveysTemp.get(usersAnsweredSurveyList.get(i))+1);
				}
			}
		}
		return countFilledSurveysTemp;
	}
	
	/**
	 * Flex table create.
	 */
	private void flexTableCreate() {
		HTML surveyTextHtml = new HTML ("Survey List", true);
		surveyTextHtml.addStyleName("surveyTextHtmlHomePage");
		HTML dateCreatedTextHtml = new HTML ("Created", true);
		dateCreatedTextHtml.addStyleName("dateCreatedTextHtmlHomePage");
		HTML dateExpiredTextHtml = new HTML ("Expiry Date", true);
		dateExpiredTextHtml.addStyleName("dateExpiredTextHtmlHomePage");
		HTML membersFilledSurveyTextHtml = new HTML ("Members Filled Survey", true);
		membersFilledSurveyTextHtml.addStyleName("dateExpiredTextHtmlHomePage");
		HTML creatorSurveyTextHtml = new HTML ("Creator", true);
		creatorSurveyTextHtml.addStyleName("dateExpiredTextHtmlHomePage");
		flexTable = new FlexTable();
		flexTable.addStyleName("flexTableHomePage");
		SimplePanel simplePanelSurveyText = new SimplePanel();
		simplePanelSurveyText.add(surveyTextHtml);
		SimplePanel simplePanelCreatorSurvey = new SimplePanel();
		simplePanelCreatorSurvey.add(creatorSurveyTextHtml);
		SimplePanel simplePanelDateCreated = new SimplePanel();
		simplePanelDateCreated.add(dateCreatedTextHtml);
		SimplePanel simplePanelDateExpired = new SimplePanel();
		simplePanelDateExpired.add(dateExpiredTextHtml);
		SimplePanel simplePanelMembersFilledSurvey = new SimplePanel();
		simplePanelMembersFilledSurvey.add(membersFilledSurveyTextHtml);
		flexTable.setWidget(0, 0, simplePanelSurveyText);
		flexTable.setWidget(0, 1, simplePanelCreatorSurvey);
		flexTable.setWidget(0, 2, simplePanelDateCreated);
		flexTable.setWidget(0, 3, simplePanelDateExpired);
		flexTable.setWidget(0, 4, simplePanelMembersFilledSurvey);
		verticalPanel.add(flexTable);				
	}
	
	/**
	 * Survey selected.
	 *
	 * @param surveyRadioButtonValue the survey radio button value
	 * @return the int
	 */
	private int surveySelected(String surveyRadioButtonValue) {
	for (int i = 0; i < getSurveyModelList().size(); i++) {
		if(surveyRadioButtonValue.contains(" (expired)")){
			surveyRadioButtonValue = surveyRadioButtonValue.replace(" (expired)", "");
		}
		if (surveyRadioButtonValue.equals(getSurveyModelList().get(i).getTitlesurvey())) {
			this.idSurveySelected = getSurveyModelList().get(i).getIdsurvey();
			this.titleSurvay = getSurveyModelList().get(i).getTitlesurvey();
			this.isAnonymous = getSurveyModelList().get(i).getIsAnonymous();
			this.expiredSurveyDate = getSurveyModelList().get(i).getExpiredDateSurvay();
			this.numberOfMembersFilledSurvey = filledSurveys.get(getSurveyModelList().get(i).getIdsurvey());
		}
	}
	return getIdSurveySelected();
}
	
	/**
	 * Gets the questions survey.
	 *
	 * @return the questions survey
	 */
	private void getQuestionsSurvey(){
		greetingService.getQuestionsSurvey(idSurveySelected, new AsyncCallback<List<SurveyQuestionModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();
			}

			@Override
			public void onSuccess(List<SurveyQuestionModel> result) {
				setSurveyQuestionModelListModifySelectedSurvey(result);
				RootPanel.get("survey-div").clear();
				modifySurveyView = new ModifySurveyView(idSurveySelected, titleSurvay, isAnonymous(), getExpiredSurveyDate(), getNumberOfMembersFilledSurvey(),
						surveyQuestionModelListModifySelectedSurvey, getUserDTO());
				RootPanel.get("survey-div").add(modifySurveyView);
			}
		});
	}
	
	/**
	 * Stat view.
	 *
	 * @param surveyRadioButtonValue the survey radio button value
	 * @param filledSurveys the filled surveys
	 */
	private void statView(String surveyRadioButtonValue, Map<Integer, Integer> filledSurveys){
		surveyUserAnswerModelList = new ArrayList<>();
		if(surveyRadioButtonValue.contains(" (expired)")){
			surveyRadioButtonValue = surveyRadioButtonValue.replace(" (expired)", "");
		}
		for (int i = 0; i < getSurveyModelList().size(); i++) {
			if (surveyRadioButtonValue.equals(getSurveyModelList().get(i).getTitlesurvey())) {
				this.idSurveySelected = getSurveyModelList().get(i).getIdsurvey();
				this.titleSurvay = getSurveyModelList().get(i).getTitlesurvey();
				this.isAnonymous = getSurveyModelList().get(i).getIsAnonymous();
				this.expiredSurveyDate = getSurveyModelList().get(i).getExpiredDateSurvay();
				this.numberOfMembersFilledSurvey = filledSurveys.get(getSurveyModelList().get(i).getIdsurvey());
			}
		}
		greetingService.getAnswersSurvey(idSurveySelected, new AsyncCallback<List<SurveyUserAnswerModel>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<SurveyUserAnswerModel> result) {
				surveyUserAnswerModelList = result;

				RootPanel.get("survey-div").clear();
				StatisticsSurveyView statisticsSurveyView = new StatisticsSurveyView(idSurveySelected, titleSurvay, isAnonymous, numberOfMembersFilledSurvey, surveyUserAnswerModelList);
				RootPanel.get("survey-div").add(statisticsSurveyView);
				
			}
		});
		
	}

	/**
	 * Gets the survey model list.
	 *
	 * @return the survey model list
	 */
	public List<SurveyModel> getSurveyModelList() {
		return surveyModelList;
	}

	/**
	 * Sets the survey model list.
	 *
	 * @param surveyModelList the new survey model list
	 */
	public void setSurveyModelList(List<SurveyModel> surveyModelList) {
		this.surveyModelList = surveyModelList;
	}

	/**
	 * Gets the survey radio button value.
	 *
	 * @return the survey radio button value
	 */
	public String getSurveyRadioButtonValue() {
		return surveyRadioButtonValue;
	}

	/**
	 * Sets the survey radio button value.
	 *
	 * @param surveyRadioButtonValue the new survey radio button value
	 */
	public void setSurveyRadioButtonValue(String surveyRadioButtonValue) {
		this.surveyRadioButtonValue = surveyRadioButtonValue;
	}

	/**
	 * Gets the survey question model list modify selected survey.
	 *
	 * @return the survey question model list modify selected survey
	 */
	public List<SurveyQuestionModel> getSurveyQuestionModelListModifySelectedSurvey() {
		return surveyQuestionModelListModifySelectedSurvey;
	}

	/**
	 * Sets the survey question model list modify selected survey.
	 *
	 * @param surveyQuestionModelListModifySelectedSurvey the new survey question model list modify selected survey
	 */
	public void setSurveyQuestionModelListModifySelectedSurvey(
			List<SurveyQuestionModel> surveyQuestionModelListModifySelectedSurvey) {
		this.surveyQuestionModelListModifySelectedSurvey = surveyQuestionModelListModifySelectedSurvey;
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
	 * Gets the title survay.
	 *
	 * @return the title survay
	 */
	public String getTitleSurvay() {
		return titleSurvay;
	}

	/**
	 * Sets the title survay.
	 *
	 * @param titleSurvay the new title survay
	 */
	public void setTitleSurvay(String titleSurvay) {
		this.titleSurvay = titleSurvay;
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
	 * Gets the expired survey date.
	 *
	 * @return the expired survey date
	 */
	public Date getExpiredSurveyDate() {
		return expiredSurveyDate;
	}

	/**
	 * Sets the expired survey date.
	 *
	 * @param expiredSurveyDate the new expired survey date
	 */
	public void setExpiredSurveyDate(Date expiredSurveyDate) {
		this.expiredSurveyDate = expiredSurveyDate;
	}

	/**
	 * Gets the users answered survey.
	 *
	 * @return the users answered survey
	 */
	public List<Integer> getUsersAnsweredSurvey() {
		return usersAnsweredSurvey;
	}

	/**
	 * Sets the users answered survey.
	 *
	 * @param usersAnsweredSurvey the new users answered survey
	 */
	public void setUsersAnsweredSurvey(List<Integer> usersAnsweredSurvey) {
		this.usersAnsweredSurvey = usersAnsweredSurvey;
	}

	/**
	 * Gets the filled surveys.
	 *
	 * @return the filled surveys
	 */
	public Map<Integer, Integer> getFilledSurveys() {
		return filledSurveys;
	}

	/**
	 * Sets the filled surveys.
	 *
	 * @param filledSurveys the filled surveys
	 */
	public void setFilledSurveys(Map<Integer, Integer> filledSurveys) {
		this.filledSurveys = filledSurveys;
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

}
