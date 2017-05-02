package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;

/**
 * The Class UserAnswersSelectSurveyView.
 */
public class UserAnswersSelectSurveyView extends Composite {

	/** The ui binder. */
	private static UserAnswersSelectSurveyViewUiBinder uiBinder = GWT.create(UserAnswersSelectSurveyViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	
	/**
	 * The Interface UserAnswersSelectSurveyViewUiBinder.
	 */
	interface UserAnswersSelectSurveyViewUiBinder extends UiBinder<Widget, UserAnswersSelectSurveyView> {
	}
	
	/** The Constant BURNED. */
	private static final String BURNED = "BURNED";
	
	/** The Constant WRONG_UUID. */
	private static final String WRONG_UUID = "WRONG_UUID";

	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;
	
	/** The answer selects survey well form. */
	@UiField WellForm answerSelectsSurveyWellForm;
	
	/** The answer survey button. */
	@UiField Button answerSurveyButton;
	
	/** The survey model. */
	private SurveyModel surveyModel;
	
	/** The survey model list. */
	private List<SurveyModel> surveyModelList;
	
	/** The survey radio button value. */
	private String surveyRadioButtonValue = "";
	
	/** The answer survey question model list. */
	private List<SurveyQuestionModel> answerSurveyQuestionModelList;
	
	/** The id survey selected. */
	private int idSurveySelected;
	
	/** The id survey selected from UUID. */
	private int idSurveySelectedFromUUID;
	
	/** The user answers retrieve survey view. */
	private UserAnswersRetrieveSurveyView userAnswersRetrieveSurveyView;
	
	/** The title survey. */
	private String titleSurvey;
	
	/** The token model list. */
	private List<TokenModel> tokenModelList;
	
	/** The token model. */
	private TokenModel tokenModel;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The UUID extract from url. */
	private String UUIDExtractFromUrl;
	
	/** The back to homepage. */
	private boolean backToHomepage;

	
	/**
	 * Instantiates a new user answers select survey view.
	 */
	public UserAnswersSelectSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
		userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView();
		answerSurveyQuestionModelList = new ArrayList<>();
		this.backToHomepage = Boolean.TRUE;
	}
	
	/**
	 * Instantiates a new user answers select survey view.
	 *
	 * @param idUserAnswer the id user answer
	 */
	public UserAnswersSelectSurveyView(int idUserAnswer) {
		initWidget(uiBinder.createAndBindUi(this));
		userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView();
		answerSurveyQuestionModelList = new ArrayList<>();
		tokenModelList = null;
		tokenModel = new TokenModel();
		this.backToHomepage = Boolean.TRUE;
		
		getSurveysByIdUserAnswer(idUserAnswer);
	}
	
	/**
	 * Instantiates a new user answers select survey view.
	 *
	 * @param UUIDExtractFromUrl the UUID extract from url
	 */
	/* Token - Portlet on public page */
	public UserAnswersSelectSurveyView(String UUIDExtractFromUrl) {
		initWidget(uiBinder.createAndBindUi(this));
		
		answerSelectsSurveyWellForm.setVisible(false);
		
		userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView();
		answerSurveyQuestionModelList = new ArrayList<>();
		tokenModelList = null;
		tokenModel = new TokenModel();
		this.UUIDExtractFromUrl = UUIDExtractFromUrl;
		this.backToHomepage = Boolean.FALSE;
		
		getSurveyModelFromDBWithToken(UUIDExtractFromUrl);
	}

	/**
	 * Instantiates a new user answers select survey view.
	 *
	 * @param userDTO the user DTO
	 * @param UUIDExtractFromUrl the UUID extract from url
	 */
	public UserAnswersSelectSurveyView(UserDTO userDTO, String UUIDExtractFromUrl) {
		initWidget(uiBinder.createAndBindUi(this));
		
		answerSelectsSurveyWellForm.setVisible(false);
		
		userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView();
		answerSurveyQuestionModelList = new ArrayList<>();
		tokenModelList = null;
		tokenModel = new TokenModel();
		this.userDTO = userDTO;
		this.UUIDExtractFromUrl = UUIDExtractFromUrl;
		this.backToHomepage = Boolean.FALSE;
		
		getSurveyModelFromDB(UUIDExtractFromUrl);
	}
	
	
	/**
	 * Gets the survey model from DB with token.
	 *
	 * @param UUIDExtractFromUrl the UUID extract from url
	 * @return the survey model from DB with token
	 */
	private void getSurveyModelFromDBWithToken(String UUIDExtractFromUrl) {
		final String UUIDExtractFromUrlTemp = UUIDExtractFromUrl;
		greetingService.getSurveyByUUID(UUIDExtractFromUrl, new AsyncCallback<SurveyModel>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(SurveyModel result) {
				setSurveyModel(result);
				
				if(getSurveyModel().getIdsurvey() == -1){
					RootPanel.get("displaysurvey-div").clear();
					WrongLoginToFillSurveyView wrongLoginToFillSurveyView = new WrongLoginToFillSurveyView();
					RootPanel.get("displaysurvey-div").add(wrongLoginToFillSurveyView);
					return;
				}
				
				if(getSurveyModel().getIdsurvey() == -2){
					RootPanel.get("displaysurvey-div").clear();
					NotAuthorizeToFillSurveyView notAuthorizeToFillSurveyView = new NotAuthorizeToFillSurveyView();
					RootPanel.get("displaysurvey-div").add(notAuthorizeToFillSurveyView);
					return;
				}
				
				Date today = new Date();
				if(getSurveyModel() != null && getSurveyModel().getExpiredDateSurvay().after(today)){
					getTokenModelByUUID(UUIDExtractFromUrlTemp, getSurveyModel());	
				} else if(getSurveyModel() != null && getSurveyModel().getExpiredDateSurvay().before(today)){
					RootPanel.get("displaysurvey-div").clear();
					ExpiredSurveyView expiredSurveyView = new ExpiredSurveyView();
					RootPanel.get("displaysurvey-div").add(expiredSurveyView);
					return;
				} else {
					getTokenModelByUUID(UUIDExtractFromUrlTemp, getSurveyModel());
					return;
				}
			}
		});
	}
	
	/**
	 * Gets the survey model from DB.
	 *
	 * @param UUIDExtractFromUrl the UUID extract from url
	 * @return the survey model from DB
	 */
	private void getSurveyModelFromDB(String UUIDExtractFromUrl) {
		final String UUIDExtractFromUrlTemp = UUIDExtractFromUrl;
		greetingService.getSurveyByUUIDAndUserId(UUIDExtractFromUrl, (int) userDTO.getUserId(), new AsyncCallback<SurveyModel>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(SurveyModel result) {
				setSurveyModel(result);
				
				if(getSurveyModel().getIdsurvey() == -1){
					RootPanel.get("displaysurvey-div").clear();
					WrongLoginToFillSurveyView wrongLoginToFillSurveyView = new WrongLoginToFillSurveyView();
					RootPanel.get("displaysurvey-div").add(wrongLoginToFillSurveyView);
					return;
				}
				
				if(getSurveyModel().getIdsurvey() == -2){
					RootPanel.get("displaysurvey-div").clear();
					NotAuthorizeToFillSurveyView notAuthorizeToFillSurveyView = new NotAuthorizeToFillSurveyView();
					RootPanel.get("displaysurvey-div").add(notAuthorizeToFillSurveyView);
					return;
				}
				
				
				Date today = new Date();
				if(getSurveyModel() != null && getSurveyModel().getExpiredDateSurvay().after(today)){
					getTokenModelByUUID(UUIDExtractFromUrlTemp, getSurveyModel());	
				} else if(getSurveyModel() != null && getSurveyModel().getExpiredDateSurvay().before(today)){
					RootPanel.get("displaysurvey-div").clear();
					ExpiredSurveyView expiredSurveyView = new ExpiredSurveyView();
					RootPanel.get("displaysurvey-div").add(expiredSurveyView);
					return;
				} else {
					getTokenModelByUUID(UUIDExtractFromUrlTemp, getSurveyModel());
					return;
				}
			}
		});
	}

	/**
	 * Open survey answer page by UUID.
	 *
	 * @param UUIDExtractFromUrl the UUID extract from url
	 * @param surveyModel the survey model
	 */
	private void openSurveyAnswerPageByUUID(String UUIDExtractFromUrl, SurveyModel surveyModel) {
		final SurveyModel surveyModelTemp = surveyModel;
		greetingService.getIdSurveyByUUID(UUIDExtractFromUrl, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Integer result) {
				idSurveySelectedFromUUID = result;	
				openSurveyPage(idSurveySelectedFromUUID, surveyModelTemp);
			}
		});
	
	}

	/**
	 * Open survey page.
	 *
	 * @param idSurveySelectedFromUUID the id survey selected from UUID
	 * @param surveyModel the survey model
	 */
	private void openSurveyPage(int idSurveySelectedFromUUID, SurveyModel surveyModel) {
		idSurveySelected = idSurveySelectedFromUUID;
		final SurveyModel surveyModelTemp = surveyModel;
		greetingService.getQuestionsSurvey(idSurveySelectedFromUUID, new AsyncCallback<List<SurveyQuestionModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();
			}

			@Override
			public void onSuccess(List<SurveyQuestionModel> result) {
				setAnswerSurveyQuestionModelListSurvey(result);
				RootPanel.get("displaysurvey-div").clear();
				userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView(idSurveySelected, surveyModelTemp.getTitlesurvey(), answerSurveyQuestionModelList, getTokenModel(), backToHomepage, getUserDTO());
				RootPanel.get("displaysurvey-div").add(userAnswersRetrieveSurveyView);
			}
		});
		
	}

	/**
	 * On click answer survey button.
	 *
	 * @param event the event
	 */
	@UiHandler("answerSurveyButton")
	void onClickAnswerSurveyButton(ClickEvent event) {
		if (getSurveyRadioButtonValue().isEmpty()) {
			Window.alert("Please select a survey");
			return;
		}
		
		getAnswerSurveyQuestionModelList().clear();
		surveySelected(getSurveyRadioButtonValue(), tokenModelList);
	}
	
	/**
	 * Gets the token model by UUID.
	 *
	 * @param UUID the uuid
	 * @param surveyModel the survey model
	 * @return the token model by UUID
	 */
	private void getTokenModelByUUID(String UUID, SurveyModel surveyModel) {
		final SurveyModel surveyModelTemp = surveyModel;
		greetingService.getTokenModelByUUID(UUID, new AsyncCallback<TokenModel>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(TokenModel result) {
				setTokenModel(result);
				
				// Token Not Burned
				if(!getTokenModel().getToken().equalsIgnoreCase(BURNED)
						&& !getTokenModel().getToken().equalsIgnoreCase(WRONG_UUID)){
					openSurveyAnswerPageByUUID(UUIDExtractFromUrl, surveyModelTemp);
				} else if (getTokenModel().getToken().equalsIgnoreCase(BURNED)){
					RootPanel.get("displaysurvey-div").clear();
					SurveyJustFilledView surveyJustFilledView = new SurveyJustFilledView();
					RootPanel.get("displaysurvey-div").add(surveyJustFilledView);
					return;
				} else if(getTokenModel().getToken().equalsIgnoreCase(WRONG_UUID)){
					RootPanel.get("displaysurvey-div").clear();
					NotAuthorizeToFillSurveyView notAuthorizeToFillSurveyView = new NotAuthorizeToFillSurveyView();
					RootPanel.get("displaysurvey-div").add(notAuthorizeToFillSurveyView);
					return;
				} 
				else if(getSurveyModel().getIdsurvey() == -1){
				RootPanel.get("displaysurvey-div").clear();
				WrongLoginToFillSurveyView wrongLoginToFillSurveyView = new WrongLoginToFillSurveyView();
				RootPanel.get("displaysurvey-div").add(wrongLoginToFillSurveyView);
				return;
			}
				
			}
		});
	
	}
	
	/**
	 * Gets the surveys by id user answer.
	 *
	 * @param idUserAnswer the id user answer
	 * @return the surveys by id user answer
	 */
	private void getSurveysByIdUserAnswer(int idUserAnswer) {
		greetingService.getSurveyListByUserAnswerId(idUserAnswer, new AsyncCallback<List<TokenModel>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<TokenModel> result) {
				setTokenModelList(result);
				tokenModel.setIdSurvey(tokenModelList.get(0).getIdSurvey());

			}
		});
	
	}

	/**
	 * Survey selected.
	 *
	 * @param surveyRadioButtonValue the survey radio button value
	 * @param tokenModelList the token model list
	 */
	private void surveySelected(String surveyRadioButtonValue, List<TokenModel> tokenModelList) {
		for (int i = 0; i < getSurveyModelList().size(); i++) {
			if (surveyRadioButtonValue.equals(getSurveyModelList().get(i).getTitlesurvey())) {
				this.idSurveySelected = getSurveyModelList().get(i).getIdsurvey();
				this.titleSurvey = getSurveyModelList().get(i).getTitlesurvey();
			}
		}
		
		for(int i=0; i<tokenModelList.size(); i++){
			if(idSurveySelected == tokenModelList.get(i).getIdSurvey()){
				tokenModel.setIdSurvey(tokenModelList.get(i).getIdSurvey());
				tokenModel.setIdUserAnswer(tokenModelList.get(i).getIdUserAnswer());
				tokenModel.setToken(tokenModelList.get(i).getToken());
				tokenModel.setEmail(tokenModelList.get(i).getEmail());
			} 
		}
		
		if((userDTO.getUserId() == tokenModel.getIdUserAnswer()) && (tokenModel.getToken() == UUIDExtractFromUrl)){
			greetingService.getQuestionsSurvey(idSurveySelected, new AsyncCallback<List<SurveyQuestionModel>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.getMessage();
				}

				@Override
				public void onSuccess(List<SurveyQuestionModel> result) {
					setAnswerSurveyQuestionModelListSurvey(result);
					RootPanel.get("displaysurvey-div").clear();
					userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView(idSurveySelected, titleSurvey, answerSurveyQuestionModelList, tokenModel, backToHomepage, getUserDTO());
					RootPanel.get("displaysurvey-div").add(userAnswersRetrieveSurveyView);
				}
			});
		}	else if(tokenModel.getToken().isEmpty() || tokenModel.getToken() == null){
			Window.alert("You have already completed this survey");
			return;
		} else {
			Window.alert("You cannot complete this survey now. \nPlease click on an email link to compile this survey");
			return;
		}
	}

	/**
	 * Gets the answer survey question model list.
	 *
	 * @return the answer survey question model list
	 */
	public List<SurveyQuestionModel> getAnswerSurveyQuestionModelList() {
		return answerSurveyQuestionModelList;
	}

	/**
	 * Sets the answer survey question model list survey.
	 *
	 * @param answerSurveyQuestionModelList the new answer survey question model list survey
	 */
	public void setAnswerSurveyQuestionModelListSurvey(
			List<SurveyQuestionModel> answerSurveyQuestionModelList) {
		this.answerSurveyQuestionModelList = answerSurveyQuestionModelList;
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
	 * Gets the token model list.
	 *
	 * @return the token model list
	 */
	public List<TokenModel> getTokenModelList() {
		return tokenModelList;
	}

	/**
	 * Sets the token model list.
	 *
	 * @param tokenModelList the new token model list
	 */
	public void setTokenModelList(List<TokenModel> tokenModelList) {
		this.tokenModelList = tokenModelList;
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
	 * Gets the UUID extract from url.
	 *
	 * @return the UUID extract from url
	 */
	public String getUUIDExtractFromUrl() {
		return UUIDExtractFromUrl;
	}

	/**
	 * Sets the UUID extract from url.
	 *
	 * @param uUIDExtractFromUrl the new UUID extract from url
	 */
	public void setUUIDExtractFromUrl(String uUIDExtractFromUrl) {
		UUIDExtractFromUrl = uUIDExtractFromUrl;
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

}
