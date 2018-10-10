package it.eng.edison.usersurvey_portlet.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.TokenModel;

/**
 * The Interface GreetingServiceAsync.
 */
public interface GreetingServiceAsync {
	
	/**
	 * Insert survey.
	 *
	 * @param surveyModel the survey model
	 * @param asyncCallback the async callback
	 */
	/* Survey */
	void insertSurvey(SurveyModel surveyModel, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Gets the user.
	 *
	 * @param asyncCallback the async callback
	 * @return the user
	 */
	void getUser(AsyncCallback<UserDTO> asyncCallback);
	
	/**
	 * Delete survey.
	 *
	 * @param surveyModel the survey model
	 * @param asyncCallback the async callback
	 */
	void deleteSurvey(SurveyModel surveyModel, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Gets the survey list.
	 *
	 * @param userDTO the user DTO
	 * @param asyncCallback the async callback
	 * @return the survey list
	 */
	void getSurveyList(UserDTO userDTO, AsyncCallback<List<SurveyModel>> asyncCallback);
	
	/**
	 * Gets the survey by UUID.
	 *
	 * @param UUID the uuid
	 * @param asyncCallback the async callback
	 * @return the survey by UUID
	 */
	void getSurveyByUUID(String UUID, AsyncCallback<SurveyModel> asyncCallback);
	
	/**
	 * Gets the id survey by UUID.
	 *
	 * @param UUID the uuid
	 * @param asyncCallback the async callback
	 * @return the id survey by UUID
	 */
	void getIdSurveyByUUID(String UUID, AsyncCallback<Integer> asyncCallback);
	
	/**
	 * Gets the survey by UUID and user id.
	 *
	 * @param UUID the uuid
	 * @param userId the user id
	 * @param asyncCallback the async callback
	 * @return the survey by UUID and user id
	 */
	void getSurveyByUUIDAndUserId(String UUID, int userId, AsyncCallback<SurveyModel> asyncCallback);
	
	/**
	 * Gets the all surveys from DB.
	 *
	 * @param asyncCallback the async callback
	 * @return the all surveys from DB
	 */
	void getAllSurveysFromDB(AsyncCallback<List<SurveyModel>> asyncCallback);
	
	/**
	 * Save all survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 * @param asyncCallback the async callback
	 */
	void saveAllSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Update survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 * @param asyncCallback the async callback
	 */
	void updateSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList,  AsyncCallback<Void> asyncCallback);

	/**
	 * Insert survey question.
	 *
	 * @param surveyModel the survey model
	 * @param idSurvay the id survay
	 * @param asyncCallback the async callback
	 */
	/* Survey Question */
	void insertSurveyQuestion(SurveyQuestionModel surveyModel, int idSurvay, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Gets the questions survey.
	 *
	 * @param idSurvey the id survey
	 * @param asyncCallback the async callback
	 * @return the questions survey
	 */
	void getQuestionsSurvey(int idSurvey, AsyncCallback<List<SurveyQuestionModel>> asyncCallback);
	
	/**
	 * Gets the user list current company.
	 *
	 * @param idSurvey the id survey
	 * @param asyncCallback the async callback
	 * @return the user list current company
	 */
	/* User Answer */
	void getUserListCurrentCompany (int idSurvey, AsyncCallback<UserDTO> asyncCallback);
	
	/**
	 * Send survey to users.
	 *
	 * @param currentURL the current URL
	 * @param idSurveySelected the id survey selected
	 * @param isAnonymous the is anonymous
	 * @param surveySender the survey sender
	 * @param usersInviteSurveyList the users invite survey list
	 * @param asyncCallback the async callback
	 */
	void sendSurveyToUsers(String currentURL, int idSurveySelected, boolean isAnonymous, String surveySender, List<String> usersInviteSurveyList, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @param asyncCallback the async callback
	 * @return the survey
	 */
	void getSurvey(int idSurvey, AsyncCallback<SurveyModel> asyncCallback);
	
	/**
	 * Save all answer.
	 *
	 * @param surveyModel the survey model
	 * @param surveyAnswerModelList the survey answer model list
	 * @param tokenModel the token model
	 * @param currentURL the current URL
	 * @param fullNameUser the full name user
	 * @param asyncCallback the async callback
	 */
	void saveAllAnswer(SurveyModel surveyModel, List<SurveyAnswerModel> surveyAnswerModelList, TokenModel tokenModel, String currentURL, String fullNameUser, AsyncCallback<Void> asyncCallback);
	
	/**
	 * Gets the survey list by user answer id.
	 *
	 * @param idUserAnswer the id user answer
	 * @param asyncCallback the async callback
	 * @return the survey list by user answer id
	 */
	void getSurveyListByUserAnswerId(int idUserAnswer, AsyncCallback<List<TokenModel>> asyncCallback);
	
	/**
	 * Gets the token model by UUID.
	 *
	 * @param UUID the uuid
	 * @param asyncCallback the async callback
	 * @return the token model by UUID
	 */
	void getTokenModelByUUID(String UUID, AsyncCallback<TokenModel> asyncCallback);
	
	/**
	 * Gets the users invited to fill survey.
	 *
	 * @param idSurvey the id survey
	 * @param asyncCallback the async callback
	 * @return the users invited to fill survey
	 */
	void getUsersInvitedToFillSurvey(int idSurvey, AsyncCallback<Map<String,String>> asyncCallback);
}
