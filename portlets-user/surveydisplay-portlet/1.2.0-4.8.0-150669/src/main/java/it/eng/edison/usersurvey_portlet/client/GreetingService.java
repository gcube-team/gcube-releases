package it.eng.edison.usersurvey_portlet.client;


import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.liferay.portal.model.User;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;
import it.eng.edison.usersurvey_portlet.server.entity.Invitationtoken;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	
	/**
	 * Insert survey.
	 *
	 * @param surveyModel the survey model
	 */
	/* Survey */
	void insertSurvey(SurveyModel surveyModel);
	
	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	UserDTO getUser();
	
	/**
	 * Delete survey.
	 *
	 * @param surveyModel the survey model
	 */
	void deleteSurvey(SurveyModel surveyModel);
	
	/**
	 * Gets the survey list.
	 *
	 * @param userDTO the user DTO
	 * @return the survey list
	 */
	List<SurveyModel> getSurveyList(UserDTO userDTO);
	
	/**
	 * Gets the survey by UUID.
	 *
	 * @param UUID the uuid
	 * @return the survey by UUID
	 */
	SurveyModel getSurveyByUUID(String UUID);
	
	/**
	 * Gets the id survey by UUID.
	 *
	 * @param UUID the uuid
	 * @return the id survey by UUID
	 */
	int getIdSurveyByUUID(String UUID);
	
	/**
	 * Gets the survey by UUID and user id.
	 *
	 * @param UUID the uuid
	 * @param userId the user id
	 * @return the survey by UUID and user id
	 */
	SurveyModel getSurveyByUUIDAndUserId(String UUID, int userId);
	
	/**
	 * Gets the all surveys from DB.
	 *
	 * @return the all surveys from DB
	 */
	List<SurveyModel> getAllSurveysFromDB();
	
	/**
	 * Save all survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 */
	void saveAllSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList);
	
	/**
	 * Update survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 */
	void updateSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList);

	/**
	 * Insert survey question.
	 *
	 * @param surveyModel the survey model
	 * @param idSurvay the id survay
	 */
	/* Survey Question */
	void insertSurveyQuestion(SurveyQuestionModel surveyModel, int idSurvay);
	
	/**
	 * Gets the questions survey.
	 *
	 * @param idSurvey the id survey
	 * @return the questions survey
	 */
	List<SurveyQuestionModel> getQuestionsSurvey(int idSurvey);

	/**
	 * Gets the user list current company.
	 *
	 * @param idSurvey the id survey
	 * @return the user list current company
	 */
	/* User Answer */
	UserDTO getUserListCurrentCompany(int idSurvey);
	
	/**
	 * Send survey to users.
	 *
	 * @param currentURL the current URL
	 * @param idSurveySelected the id survey selected
	 * @param isAnonymous the is anonymous
	 * @param surveySender the survey sender
	 * @param usersInviteSurveyList the users invite survey list
	 */
	void sendSurveyToUsers(String currentURL, int idSurveySelected, boolean isAnonymous, String surveySender, List<String> usersInviteSurveyList);
	
	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @return the survey
	 */
	SurveyModel getSurvey(int idSurvey);
	
	/**
	 * Save all answer.
	 *
	 * @param surveyModel the survey model
	 * @param surveyAnswerModelList the survey answer model list
	 * @param tokenModel the token model
	 * @param currentURL the current URL
	 * @param fullNameUser the full name user
	 */
	void saveAllAnswer(SurveyModel surveyModel, List<SurveyAnswerModel> surveyAnswerModelList, TokenModel tokenModel, String currentURL, String fullNameUser);
	
	/**
	 * Gets the survey list by user answer id.
	 *
	 * @param idUserAnswer the id user answer
	 * @return the survey list by user answer id
	 */
	List<TokenModel> getSurveyListByUserAnswerId(int idUserAnswer);
	
	/**
	 * Gets the token model by UUID.
	 *
	 * @param UUID the uuid
	 * @return the token model by UUID
	 */
	TokenModel getTokenModelByUUID(String UUID);
	
	/**
	 * Gets the users invited to fill survey.
	 *
	 * @param idSurvey the id survey
	 * @return the users invited to fill survey
	 */
	Map<String,String> getUsersInvitedToFillSurvey(int idSurvey);
}
