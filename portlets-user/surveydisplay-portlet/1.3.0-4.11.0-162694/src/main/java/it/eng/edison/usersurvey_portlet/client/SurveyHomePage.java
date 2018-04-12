package it.eng.edison.usersurvey_portlet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.WellForm;
import com.github.gwtbootstrap.client.ui.base.Style;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	@UiField
	VerticalPanel verticalPanel;
	
	/** The horizontal panel. */
	@UiField
	HorizontalPanel horizontalPanel;
	
	/** The survey home page. */
	@UiField
	WellForm surveyHomePage;
	
	/** The invite survey button. */
	@UiField
	Button inviteSurveyButton;
	
	/** The paragraph invite users. */
	@UiField Paragraph paragraphInviteUsers;

	/** The survey radio button value. */
	private String surveyRadioButtonValue = "";
	
	/** The send survey to users view. */
	private SendSurveyToUsersView sendSurveyToUsersView;
	
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
	
	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The survey radio button. */
	private RadioButton surveyRadioButton;

	/**
	 * Instantiates a new survey home page.
	 */
	public SurveyHomePage() {
		initWidget(uiBinder.createAndBindUi(this));
		
		surveyHomePage.addStyleName("SurveyHomePage");
		paragraphInviteUsers.addStyleName("paragraphInviteUsers");
		
		surveyQuestionModelListModifySelectedSurvey = new ArrayList<SurveyQuestionModel>();
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
				getSurveys(getUserDTO());
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();				
			}
		});		
	}

	/**
	 * On click send to survey button.
	 *
	 * @param clickEvent the click event
	 */
	@UiHandler("inviteSurveyButton")
	void onClickSendToSurveyButton(ClickEvent clickEvent) {
		if (getSurveyRadioButtonValue().isEmpty()) {
			Window.alert("Please select a survey");
			return;
		}
		getSurveyQuestionModelListModifySelectedSurvey().clear();
		surveySelected(getSurveyRadioButtonValue());
		
		RootPanel.get("displaysurvey-div").clear();
		sendSurveyToUsersView = new SendSurveyToUsersView(idSurveySelected, titleSurvay, isAnonymous, getUserDTO().getFullName());
		RootPanel.get("displaysurvey-div").add(sendSurveyToUsersView);
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
				caught.getMessage();
			}

			@Override
			public void onSuccess(List<SurveyModel> result) {
				setSurveyModelList(result);
				
				Date today = new Date();
				for (int i = 0; i < getSurveyModelList().size(); i++) {
					String radioButtonText;
					boolean radioButtonEnabled = Boolean.TRUE;
					if(getSurveyModelList().get(i).getExpiredDateSurvay().before(today)){
						radioButtonText = getSurveyModelList().get(i).getTitlesurvey() + " (expired)";
						radioButtonEnabled = Boolean.FALSE;
					} else {
						radioButtonText = getSurveyModelList().get(i).getTitlesurvey();
						radioButtonEnabled = Boolean.TRUE;
					}
					
					final RadioButton surveyRadioButton = new RadioButton("radioGroup",
							radioButtonText);
					surveyRadioButton.setEnabled(radioButtonEnabled);
					surveyRadioButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							setSurveyRadioButtonValue(surveyRadioButton.getText());
						}
					});
					verticalPanel.add(surveyRadioButton);
				}
				surveyHomePage.setVisible(Boolean.TRUE);
				inviteSurveyButton.setVisible(Boolean.TRUE);
				verticalPanel.add(surveyHomePage);
			}
		});
	}

	/**
	 * Survey selected.
	 *
	 * @param surveyRadioButtonValue the survey radio button value
	 */
	private void surveySelected(String surveyRadioButtonValue) {
		for (int i = 0; i < getSurveyModelList().size(); i++) {
			if (surveyRadioButtonValue.equals(getSurveyModelList().get(i).getTitlesurvey())) {
				this.idSurveySelected = getSurveyModelList().get(i).getIdsurvey();
				this.titleSurvay = getSurveyModelList().get(i).getTitlesurvey();
				this.isAnonymous = getSurveyModelList().get(i).getIsAnonymous();
			}
		}
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
