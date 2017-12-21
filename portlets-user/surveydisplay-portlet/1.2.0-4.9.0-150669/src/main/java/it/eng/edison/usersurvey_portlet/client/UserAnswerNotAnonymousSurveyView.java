package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;

/**
 * The Class UserAnswerNotAnonymousSurveyView.
 */
public class UserAnswerNotAnonymousSurveyView extends Composite {

	/** The ui binder. */
	private static UserAnswerNotAnonymousSurveyViewUiBinder uiBinder = GWT
			.create(UserAnswerNotAnonymousSurveyViewUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);


	/**
	 * The Interface UserAnswerNotAnonymousSurveyViewUiBinder.
	 */
	interface UserAnswerNotAnonymousSurveyViewUiBinder extends UiBinder<Widget, UserAnswerNotAnonymousSurveyView> {
	}

	/** The Constant BURNED. */
	private static final String BURNED = "BURNED";
	
	/** The vertical panel. */
	@UiField VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField HorizontalPanel horizontalPanel;
	
	/** The answer selects survey well form. */
	@UiField WellForm answerSelectsSurveyWellForm;
	
	/** The answer survey button. */
	@UiField Button answerSurveyButton;

	/** The user answers retrieve survey view. */
	private UserAnswersRetrieveSurveyView userAnswersRetrieveSurveyView;
	
	/** The answer survey question model list. */
	private List<SurveyQuestionModel> answerSurveyQuestionModelList;
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The user id. */
	private int userId;
	
	/** The token model list. */
	private List<TokenModel> tokenModelList;
	
	/** The token model. */
	private TokenModel tokenModel;
	
	/** The survey radio button value. */
	private String surveyRadioButtonValue = "";
	
	/** The survey model list. */
	private List<SurveyModel> surveyModelList;
	
	/** The survey model. */
	private SurveyModel surveyModel;
	
	/** The id survey selected. */
	private int idSurveySelected;
	
	/** The title survey. */
	private String titleSurvey;
	
	/** The back to homepage. */
	private boolean backToHomepage;
	
	/** The survey radio button. */
	private RadioButton surveyRadioButton;
	
	/** The line HTML. */
	private HTML lineHTML;
	
	/** The paragraph partecipate now paragraph. */
	private Paragraph paragraphPartecipateNowParagraph;

	/**
	 * Instantiates a new user answer not anonymous survey view.
	 */
	public UserAnswerNotAnonymousSurveyView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	/**
	 * Instantiates a new user answer not anonymous survey view.
	 *
	 * @param userDTO the user DTO
	 */
	public UserAnswerNotAnonymousSurveyView(UserDTO userDTO) {
		initWidget(uiBinder.createAndBindUi(this));
		
		answerSurveyButton.setVisible(Boolean.FALSE);
		answerSelectsSurveyWellForm.setVisible(Boolean.FALSE);
		answerSelectsSurveyWellForm.addStyleName("SurveyHomePage");
		
		lineHTML = new HTML("<hr>");
		paragraphPartecipateNowParagraph = new Paragraph("You have been invited to partecipate to the following Survey(s)");
		paragraphPartecipateNowParagraph.addStyleName("paragraphInviteUsers");
		paragraphPartecipateNowParagraph.setVisible(false);
		
		lineHTML.setVisible(Boolean.FALSE);
		RootPanel.get("displaysurvey-div").add(lineHTML);
		RootPanel.get("displaysurvey-div").add(paragraphPartecipateNowParagraph);
		RootPanel.get("displaysurvey-div").add(verticalPanel);

		userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView();
		answerSurveyQuestionModelList = new ArrayList<>();

		this.userDTO = userDTO;
		this.userId = (int) userDTO.getUserId();

		tokenModelList = null;
		tokenModel = new TokenModel();

		setBackToHomepage(Boolean.TRUE);
		this.backToHomepage = Boolean.TRUE;
		getSurveysByIdUser(userId);

	}

	/**
	 * Gets the surveys by id user.
	 *
	 * @param userId the user id
	 * @return the surveys by id user
	 */
	private void getSurveysByIdUser(int userId) {
		greetingService.getSurveyListByUserAnswerId(userId, new AsyncCallback<List<TokenModel>>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(List<TokenModel> result) {
				setTokenModelList(result);
				tokenModel.setIdSurvey(tokenModelList.get(0).getIdSurvey());
				getSurveys();
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
	 * Gets the surveys.
	 *
	 * @return the surveys
	 */
	private void getSurveys() {
		greetingService.getAllSurveysFromDB(new AsyncCallback<List<SurveyModel>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<SurveyModel> result) {
				setSurveyModelList(result);

				Date today = new Date();
				for (int i = 0; i < getSurveyModelList().size(); i++) {
					for(int j = 0; j < getTokenModelList().size(); j++){
						String radioButtonText;
						
						if(getSurveyModelList().get(i).getIdsurvey().equals(getTokenModelList().get(j).getIdSurvey()) && 
								!getTokenModelList().get(j).getToken().equalsIgnoreCase(BURNED)){
							
							answerSurveyButton.setVisible(Boolean.TRUE);
							answerSelectsSurveyWellForm.setVisible(Boolean.TRUE);
							lineHTML.setVisible(Boolean.TRUE);
							paragraphPartecipateNowParagraph.setVisible(Boolean.TRUE);
							
							if(getSurveyModelList().get(i).getExpiredDateSurvay().before(today)){
								answerSurveyButton.setVisible(Boolean.TRUE);
								answerSelectsSurveyWellForm.setVisible(Boolean.TRUE);
								lineHTML.setVisible(Boolean.TRUE);
								
								radioButtonText = getSurveyModelList().get(i).getTitlesurvey() + " (expired)";
								final RadioButton surveyRadioButton = new RadioButton("radioGroup",
										radioButtonText);
								surveyRadioButton.setEnabled(Boolean.FALSE);
								surveyRadioButton.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										setSurveyRadioButtonValue(surveyRadioButton.getText());
									}
								});
								verticalPanel.add(surveyRadioButton);
							} else {
								answerSurveyButton.setVisible(Boolean.TRUE);
								answerSelectsSurveyWellForm.setVisible(Boolean.TRUE);
								lineHTML.setVisible(Boolean.TRUE);
								
								radioButtonText = getSurveyModelList().get(i).getTitlesurvey();
								final RadioButton surveyRadioButton = new RadioButton("radioGroup",
										radioButtonText);
								surveyRadioButton.setEnabled(Boolean.TRUE);
								surveyRadioButton.addClickHandler(new ClickHandler() {
									@Override
									public void onClick(ClickEvent event) {
										setSurveyRadioButtonValue(surveyRadioButton.getText());
									}
								});
								verticalPanel.add(surveyRadioButton);
							}
							
						}
					}
				}
				answerSelectsSurveyWellForm.add(answerSurveyButton);
				verticalPanel.add(answerSelectsSurveyWellForm);
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
				tokenModel.setUUID(tokenModelList.get(i).getUUID());
			} 
		}
		
		if((userDTO.getUserId() == tokenModel.getIdUserAnswer())){
			greetingService.getQuestionsSurvey(idSurveySelected, new AsyncCallback<List<SurveyQuestionModel>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.getMessage();
				}

				@Override
				public void onSuccess(List<SurveyQuestionModel> result) {
					setAnswerSurveyQuestionModelList(result);
					RootPanel.get("displaysurvey-div").clear();
					userAnswersRetrieveSurveyView = new UserAnswersRetrieveSurveyView(idSurveySelected, titleSurvey, answerSurveyQuestionModelList, tokenModel, backToHomepage, getUserDTO());
					RootPanel.get("displaysurvey-div").add(userAnswersRetrieveSurveyView);
				}
			});
		} else if(tokenModel.getToken().isEmpty() || tokenModel.getToken() == null || tokenModel.getToken().equalsIgnoreCase(BURNED)){
			Window.alert("You have already completed this survey");
			return;
		} else {
			Window.alert("You cannot complete this survey now. \nplease click on link into invite email if you want to access to this survey");
			return;
		}
	}
	
	/**
	 * Gets the answer selects survey well form.
	 *
	 * @return the answer selects survey well form
	 */
	public WellForm getAnswerSelectsSurveyWellForm() {
		return answerSelectsSurveyWellForm;
	}

	/**
	 * Sets the answer selects survey well form.
	 *
	 * @param answerSelectsSurveyWellForm the new answer selects survey well form
	 */
	public void setAnswerSelectsSurveyWellForm(WellForm answerSelectsSurveyWellForm) {
		this.answerSelectsSurveyWellForm = answerSelectsSurveyWellForm;
	}

	/**
	 * Gets the answer survey button.
	 *
	 * @return the answer survey button
	 */
	public Button getAnswerSurveyButton() {
		return answerSurveyButton;
	}

	/**
	 * Sets the answer survey button.
	 *
	 * @param answerSurveyButton the new answer survey button
	 */
	public void setAnswerSurveyButton(Button answerSurveyButton) {
		this.answerSurveyButton = answerSurveyButton;
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
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(int userId) {
		this.userId = userId;
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
	 * Gets the answer survey question model list.
	 *
	 * @return the answer survey question model list
	 */
	public List<SurveyQuestionModel> getAnswerSurveyQuestionModelList() {
		return answerSurveyQuestionModelList;
	}

	/**
	 * Sets the answer survey question model list.
	 *
	 * @param answerSurveyQuestionModelList the new answer survey question model list
	 */
	public void setAnswerSurveyQuestionModelList(List<SurveyQuestionModel> answerSurveyQuestionModelList) {
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
	 * Gets the survey radio button.
	 *
	 * @return the survey radio button
	 */
	public RadioButton getSurveyRadioButton() {
		return surveyRadioButton;
	}

	/**
	 * Sets the survey radio button.
	 *
	 * @param surveyRadioButton the new survey radio button
	 */
	public void setSurveyRadioButton(RadioButton surveyRadioButton) {
		this.surveyRadioButton = surveyRadioButton;
	}

}
