package it.eng.edison.usersurvey_portlet.client;


import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import it.eng.edison.usersurvey_portlet.client.model.SurveyModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyUserAnswerModel;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	 String getSurveyInvitationLink(Integer surveyId, boolean isAnonymous);
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
	 * Check D bis created.
	 */
	void checkDBisCreated();
	
	/**
	 * Users answered survey.
	 *
	 * @return the list
	 */
	List<Integer> usersAnsweredSurvey();
	
	/**
	 * Save all survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param idTempFolder the id temp folder
	 * @param surveyQuestionModelList the survey question model list
	 */
	void saveAllSurvey(long idUser, SurveyModel surveyModel, long idTempFolder, List<SurveyQuestionModel> surveyQuestionModelList);
	
	/**
	 * Update survey.
	 *
	 * @param idUser the id user
	 * @param surveyModel the survey model
	 * @param surveyQuestionModelList the survey question model list
	 */
	void updateSurvey(long idUser, SurveyModel surveyModel, List<SurveyQuestionModel> surveyQuestionModelList);

	/**
	 * Creates the folder.
	 *
	 * @return the long
	 */
	Long createFolder();
	
	/**
	 * Delete old image.
	 *
	 * @param idSurvey the id survey
	 * @param idTmpFolder the id tmp folder
	 * @param image2Delete the image 2 delete
	 */
	void deleteOldImage(int idSurvey, long idTmpFolder, String image2Delete);	

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
	 * Gets the answers survey.
	 *
	 * @param idSurvey the id survey
	 * @return the answers survey
	 */
	/* Stats */
	List<SurveyUserAnswerModel> getAnswersSurvey(int idSurvey);
	
	/**
	 * Gets the user answered survey.
	 *
	 * @param idSurveySelected the id survey selected
	 * @return the user answered survey
	 */
	List<UserDTO> getUserAnsweredSurvey(int idSurveySelected);
	
	/**
	 * Gets the survey.
	 *
	 * @param idSurvey the id survey
	 * @return the survey
	 */
	SurveyModel getSurvey(int idSurvey);
	
	/**
	 * Export to CSV file.
	 *
	 * @param titleSurvey the title survey
	 * @param isAnonymous the is anonymous
	 * @param userDTOList the user DTO list
	 * @param surveyUserAnswerModelList the survey user answer model list
	 * @param surveyQuestionModelList the survey question model list
	 */
	void exportToCSVFile(String titleSurvey, boolean isAnonymous, List<UserDTO> userDTOList, List<SurveyUserAnswerModel> surveyUserAnswerModelList, List<SurveyQuestionModel> surveyQuestionModelList);

}
