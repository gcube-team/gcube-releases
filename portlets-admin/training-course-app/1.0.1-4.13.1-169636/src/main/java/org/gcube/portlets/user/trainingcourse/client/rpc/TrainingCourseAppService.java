package org.gcube.portlets.user.trainingcourse.client.rpc;

import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("trainingCourseAppService")
public interface TrainingCourseAppService extends RemoteService {

	TrainingCourseObj createNewCourse(TrainingCourseObj project) throws Exception;

	List<TrainingCourseObj> getOwnedTrainingCoursesForCurrentVRE() throws Exception;

	TrainingCourseObj loadTrainingCourse(long trainingCourseIdt) throws Exception;

	TrainingUnitDTO createUnitFolder(TrainingCourseObj project, TrainingUnitDTO unit) throws Exception;

	WorkspaceItemInfo getWorkspaceItemInfo(String itemId) throws Exception;

	void deleteWorkspaceItem(String itemId) throws Exception;

	TrainingCourseObj shareWithUsers(TrainingCourseObj project, List<String> listLogins) throws Exception;

	boolean deleteTrainingProject(TrainingCourseObj project) throws Exception;

	TrainingUnitQuestionnaireDTO addQuestionnaireToUnit(TrainingUnitDTO unit,
			TrainingUnitQuestionnaireDTO questionnaire) throws Exception;

	List<TrainingUnitQuestionnaireDTO> getListOfQuestionnaireForUnit(long internalId) throws Exception;

	TrainingCourseObj changeStatus(TrainingCourseObj project, boolean isActive) throws Exception;

	TrainingVideoDTO addVideoToUnit(TrainingUnitDTO unit, TrainingVideoDTO videoDTO) throws Exception;

	List<TrainingVideoDTO> getListOfVideoForUnit(long internalId) throws Exception;

	int countVideosForTrainingUnit(long trainingUnitId) throws Exception;

	int countQuestionnairesForTrainingUnit(long trainingUnitId) throws Exception;

	TrainingCourseObj updateCourse(TrainingCourseObj course) throws Exception;

	String getQueryStringToShowUserProgress(TrainingCourseObj course, String userNameToShowProgress) throws Exception;

	TrainingCourseObj shareWithCurrentScope(TrainingCourseObj project) throws Exception;

	int deleteVideoForId(long videoId) throws Exception;

	int deleteQuestionnaireForId(long questionnaireId) throws Exception;
}
