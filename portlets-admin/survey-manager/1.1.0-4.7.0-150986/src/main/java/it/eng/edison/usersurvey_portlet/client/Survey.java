package it.eng.edison.usersurvey_portlet.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Class Survey.
 */
public class Survey implements EntryPoint {

	/** The greeting service. */
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/** The user DTO. */
	private UserDTO userDTO;
	
	/** The is survey manager. */
	private boolean isSurveyManager = Boolean.FALSE;
	
	/** The is survey creator. */
	private boolean isSurveyCreator = Boolean.FALSE;
	
	public void onModuleLoad() {
		userDTO = new UserDTO();
		checkDataBaseIsCreated();
	}


	/**
	 * Check data base is created.
	 */
	private void checkDataBaseIsCreated() {
		greetingService.checkDBisCreated(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Void result) {
				getUserFromSession();

			}
		});
		
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
				userDTO.setUserId(result.getUserId());
				userDTO.setContactId(result.getContactId());
				userDTO.setScreenName(result.getScreenName());
				userDTO.setFullName(result.getFullName());
				userDTO.setEmailAddress(result.getEmailAddress());
				userDTO.setRolesId(result.getRolesId());
				userDTO.setAdminUser(result.isAdminUser());
				userDTO.setVreManager(result.isVreManager());
				userDTO.setGroupId(result.getGroupId());

				if(userDTO.isAdminUser() 
						|| userDTO.isManageSurveyUser() 
						|| userDTO.isVreManager()){
					setSurveyManager(Boolean.TRUE);
				}

				SurveyHomePage surveyHomePage = new SurveyHomePage();
				RootPanel.get("survey-div").add(surveyHomePage);

				setSurveyManager(Boolean.FALSE);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.getMessage();
			}
		});
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
}
