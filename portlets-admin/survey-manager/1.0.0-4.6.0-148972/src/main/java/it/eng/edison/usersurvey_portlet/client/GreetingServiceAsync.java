package it.eng.edison.usersurvey_portlet.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;

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
	 * Check D bis created.
	 *
	 * @param asyncCallback the async callback
	 */
	void checkDBisCreated(AsyncCallback<Void> asyncCallback);
	
	/**
	 * Users answered survey.
	 *
	 * @param asyncCallback the async callback
	 */
	void usersAnsweredSurvey(AsyncCallback<List<Integer>> asyncCallback);
	
	/**
	 * Save all survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param idTempFolder the id temp folder
	 * @param surveyQuestionModelList the survey question model list
	 * @param asyncCallback the async callback
	 */
	void saveAllSurvey(long idUser, SurveyModel surveyModel, long idTempFolder, List<SurveyQuestionModel> surveyQuestionModelList, AsyncCallback<Void> asyncCallback);
	
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
	 * Creates the folder.
	 *
	 * @param asyncCallback the async callback
	 */
	void createFolder(AsyncCallback<Long> asyncCallback);
	
	/**
	 * Delete old image.
	 *
	 * @param idSurvey the id survey
	 * @param idTmpFolder the id tmp folder
	 * @param image2Delete the image 2 delete
	 * @param asyncCallback the async callback
	 */
	void deleteOldImage(int idSurvey, long idTmpFolder, String image2Delete, AsyncCallback<Void> asyncCallback);
	
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
 * Gets the answers survey.
 *
 * @param idSurvey the id survey
 * @param asyncCallback the async callback
 * @return the answers survey
 */
/* Stats */
	void getAnswersSurvey(int idSurvey, AsyncCallback<List<SurveyUserAnswerModel>> asyncCallback);
	
	/**
	 * Gets the user answered survey.
	 *
	 * @param idSurveySelected the id survey selected
	 * @param asyncCallback the async callback
	 * @return the user answered survey
	 */
	void getUserAnsweredSurvey(int idSurveySelected, AsyncCallback<List<UserDTO>> asyncCallback);
	
	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @param asyncCallback the async callback
	 * @return the survey
	 */
	void getSurvey(int idSurvey, AsyncCallback<SurveyModel> asyncCallback);
	
	/**
	 * Export to CSV file.
	 *
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param userDTOList the user DTO list
	 * @param surveyUserAnswerModelList the survey user answer model list
	 * @param surveyQuestionModelList the survey question model list
	 * @param asyncCallback the async callback
	 */
	void exportToCSVFile(String titleSurvey, boolean isAnonymous, List<UserDTO> userDTOList, List<SurveyUserAnswerModel> surveyUserAnswerModelList, List<SurveyQuestionModel> surveyQuestionModelList, AsyncCallback<Void> asyncCallback);


}
