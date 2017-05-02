package it.eng.edison.usersurvey_portlet.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Class SurveyStart.
 */
public class SurveyStart extends Composite {

	
	/** The ui binder. */
	private static SurveyStartUiBinder uiBinder = GWT.create(SurveyStartUiBinder.class);
	
	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/** The Constant EMPTY_TOKEN. */
	private static final String EMPTY_TOKEN = "EMPTY TOKEN";

	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The admin survey. */
	private boolean adminSurvey;
	
	/** The is survey creator. */
	private boolean isSurveyCreator = Boolean.FALSE;
	
	/** The current URL. */
	private String currentURL;
	
	/** The UUID extract from url. */
	private String UUIDExtractFromUrl;
	
	/** The survey just filled view. */
	private SurveyJustFilledView surveyJustFilledView;
	
	/** The is survey manager. */
	private boolean isSurveyManager = Boolean.FALSE;
	
	
	/**
	 * The Interface SurveyStartUiBinder.
	 */
	interface SurveyStartUiBinder extends UiBinder<Widget, SurveyStart> {
	}

	/**
	 * Instantiates a new survey start.
	 */
	public SurveyStart() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.userDTO = new UserDTO();
		this.currentURL = Window.Location.getHref();	
		this.UUIDExtractFromUrl = extractUUIDfromURL(currentURL);
		this.adminSurvey = Boolean.FALSE;
		
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
				
				if((userDTO != null) && (userDTO.isAdminUser() 
						|| userDTO.isManageSurveyUser() 
						|| userDTO.isVreManager())){
					setSurveyManager(Boolean.TRUE);
				}
				
				if((userDTO != null) && 
						(isSurveyManager()) && 
						(UUIDExtractFromUrl == EMPTY_TOKEN)){
					SurveyHomePage surveyHomePage = new SurveyHomePage();
					RootPanel.get("displaysurvey-div").add(surveyHomePage);
					UserAnswerNotAnonymousSurveyView userAnswerNotAnonymousSurveyView = new UserAnswerNotAnonymousSurveyView(userDTO);
					RootPanel.get("displaysurvey-div").add(userAnswerNotAnonymousSurveyView);
				} else if((userDTO != null) && 
						UUIDExtractFromUrl.equalsIgnoreCase(EMPTY_TOKEN)) {
					UserAnswerNotAnonymousSurveyView userAnswerNotAnonymousSurveyView = new UserAnswerNotAnonymousSurveyView(userDTO);
					RootPanel.get("displaysurvey-div").add(userAnswerNotAnonymousSurveyView);
				} else if(!UUIDExtractFromUrl.equalsIgnoreCase(EMPTY_TOKEN) && 
						userDTO.getUserId() == 0){
					UserAnswersSelectSurveyView userAnswersSelectSurveyView = new UserAnswersSelectSurveyView(UUIDExtractFromUrl);
					RootPanel.get("displaysurvey-div").add(userAnswersSelectSurveyView);
				} else {
					UserAnswersSelectSurveyView userAnswersSelectSurveyView = new UserAnswersSelectSurveyView(userDTO, UUIDExtractFromUrl);
					RootPanel.get("displaysurvey-div").add(userAnswersSelectSurveyView);
				}
				
				setSurveyManager(Boolean.FALSE);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});		
	}

	/**
	 * Extract UUI dfrom URL.
	 *
	 * @param url the url
	 * @return the string
	 */
	private String extractUUIDfromURL(String url){
		String uuidExtracted;
		int subStr1 = url.indexOf("UUID=");
		int subStr2 = url.indexOf("&");

		if(subStr1 == -1){
			uuidExtracted = EMPTY_TOKEN;
		}else if(subStr2 == -1){
			uuidExtracted = url.substring(subStr1+5);
		} else {
			uuidExtracted = url.substring(subStr1+5,subStr2);
		}
		
		return uuidExtracted;
	}
	
	
	/**
	 * Checks if is admin survey.
	 *
	 * @return true, if is admin survey
	 */
	public boolean isAdminSurvey() {
		return adminSurvey;
	}
	
	/**
	 * Sets the admin survey.
	 *
	 * @param isAdminSurvey the new admin survey
	 */
	public void setAdminSurvey(boolean isAdminSurvey) {
		this.adminSurvey = isAdminSurvey;
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
	 * Checks if is survey creator.
	 *
	 * @return true, if is survey creator
	 */
	public boolean isSurveyCreator() {
		return isSurveyCreator;
	}

	/**
	 * Sets the survey creator.
	 *
	 * @param isSurveyCreator the new survey creator
	 */
	public void setSurveyCreator(boolean isSurveyCreator) {
		this.isSurveyCreator = isSurveyCreator;
	}

	/**
	 * Gets the survey just filled view.
	 *
	 * @return the survey just filled view
	 */
	public SurveyJustFilledView getSurveyJustFilledView() {
		return surveyJustFilledView;
	}

	/**
	 * Sets the survey just filled view.
	 *
	 * @param surveyJustFilledView the new survey just filled view
	 */
	public void setSurveyJustFilledView(SurveyJustFilledView surveyJustFilledView) {
		this.surveyJustFilledView = surveyJustFilledView;
	}


	/**
	 * Checks if is survey manager.
	 *
	 * @return true, if is survey manager
	 */
	public boolean isSurveyManager() {
		return isSurveyManager;
	}


	/**
	 * Sets the survey manager.
	 *
	 * @param isSurveyManager the new survey manager
	 */
	public void setSurveyManager(boolean isSurveyManager) {
		this.isSurveyManager = isSurveyManager;
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
