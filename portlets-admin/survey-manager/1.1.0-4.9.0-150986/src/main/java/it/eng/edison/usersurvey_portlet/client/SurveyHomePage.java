package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;
import it.eng.edison.usersurvey_portlet.client.resources.Resources;

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

	private Resources resources = GWT.create(Resources.class);
	
	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;

	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;

	/** The survey home page. */
	@UiField WellForm surveyHomePage;

	/** The statistic survey button. */
	@UiField Button createNewSurveyButton;

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
	private String titleSurvey;

	/** The is anonymous. */
	private boolean isAnonymous;

	/** The survey user answer model. */
	private SurveyUserAnswerModel surveyUserAnswerModel;

	/** The survey user answer model list. */
	private List<SurveyUserAnswerModel> surveyUserAnswerModelList;

	/** The user DTO. */
	private UserDTO userDTO;

	/** The expired survey date. */
	private Date expiredSurveyDate;

	/** The users answered survey. */
	private List<Integer> usersAnsweredSurvey;

	/** The filled surveys. */
	private Map<Integer,Integer> filledSurveys;

	/** The number of members filled survey. */
	private int numberOfMembersFilledSurvey;

	private SurveyHomePage instance;
	/**
	 * Instantiates a new survey home page.
	 */
	public SurveyHomePage() {
		initWidget(uiBinder.createAndBindUi(this));
		instance = this;

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
	 * Gets the surveys.
	 *
	 * @param userDTO the user DTO
	 * @return the surveys
	 */
	private void getSurveys(UserDTO userDTO) {
		final Image loadingImage = new Image(resources.loadingImage());
		verticalPanel.add(loadingImage);
	
		greetingService.getSurveyList(userDTO, new AsyncCallback<List<SurveyModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				verticalPanel.remove(loadingImage);
			}

			@Override
			public void onSuccess(List<SurveyModel> result) {
				verticalPanel.remove(loadingImage);
				if(result != null && result.size() != 0){
					setSurveyModelList(result);
					setFilledSurveys(countFilledSurveys(getUsersAnsweredSurvey()));
					SurveyTable table = new SurveyTable(instance, getSurveyModelList());
					verticalPanel.add(table);
				}
				else
					verticalPanel.add(new Paragraph("No surveys have been created so far in this VRE, to create one Click on the Create new Survey button above!"));
			}

		});
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
	 * Survey selected.
	 *
	 * @param surveyRadioButtonValue the survey radio button value
	 * @return the int
	 */
	protected int surveySelected(SurveyModel selectedSurvey) {
		this.idSurveySelected = selectedSurvey.getIdsurvey();
		this.titleSurvey =selectedSurvey.getTitlesurvey();
		this.isAnonymous = selectedSurvey.getIsAnonymous();
		this.expiredSurveyDate = selectedSurvey.getExpiredDateSurvay();
		return getIdSurveySelected();
	}

	/**
	 * Gets the questions survey.
	 *
	 * @return the questions survey
	 */
	protected void getQuestionsSurvey(final int idSurveySelected){
		greetingService.getQuestionsSurvey(idSurveySelected, new AsyncCallback<List<SurveyQuestionModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();
			}

			@Override
			public void onSuccess(List<SurveyQuestionModel> result) {
				setSurveyQuestionModelListModifySelectedSurvey(result);
				RootPanel.get("survey-div").clear();
				modifySurveyView = new ModifySurveyView(idSurveySelected, titleSurvey, isAnonymous(), getExpiredSurveyDate(), getNumberOfMembersFilledSurvey(),
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
	protected void statView(final SurveyModel selectedSurvey, Map<Integer, Integer> filledSurveys){
		surveySelected(selectedSurvey);
		numberOfMembersFilledSurvey = getFilledSurveys().get(selectedSurvey.getIdsurvey());
		greetingService.getAnswersSurvey(selectedSurvey.getIdsurvey(), new AsyncCallback<List<SurveyUserAnswerModel>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<SurveyUserAnswerModel> result) {
				surveyUserAnswerModelList = result;
				RootPanel.get("survey-div").clear();
				StatisticsSurveyView statisticsSurveyView = new StatisticsSurveyView(
						selectedSurvey.getIdsurvey(), 
						selectedSurvey.getTitlesurvey(), 
						selectedSurvey.getIsAnonymous(), numberOfMembersFilledSurvey, surveyUserAnswerModelList);
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
		return titleSurvey;
	}

	/**
	 * Sets the title survay.
	 *
	 * @param titleSurvay the new title survay
	 */
	public void setTitleSurvay(String titleSurvay) {
		this.titleSurvey = titleSurvay;
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
