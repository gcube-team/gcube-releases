package org.gcube.portlets.user.trainingcourse.client.rpc;

import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TrainingCourseAppServiceAsync {

	void createNewCourse(TrainingCourseObj project, AsyncCallback<TrainingCourseObj> callback);

	void getOwnedTrainingCoursesForCurrentVRE(AsyncCallback<List<TrainingCourseObj>> callback);

	void loadTrainingCourse(long trainingCourseIdt, AsyncCallback<TrainingCourseObj> callback);

	void createUnitFolder(TrainingCourseObj project, TrainingUnitDTO unit, AsyncCallback<TrainingUnitDTO> callback);

	void getWorkspaceItemInfo(String itemId, AsyncCallback<WorkspaceItemInfo> callback);

	void deleteWorkspaceItem(String itemId, AsyncCallback<Void> callback);

	void shareWithUsers(TrainingCourseObj project, List<String> listLogins, AsyncCallback<TrainingCourseObj> callback);

	void deleteTrainingProject(TrainingCourseObj project, AsyncCallback<Boolean> callback);

	void addQuestionnaireToUnit(TrainingUnitDTO unit, TrainingUnitQuestionnaireDTO questionnaire,
			AsyncCallback<TrainingUnitQuestionnaireDTO> callback);

	void getListOfQuestionnaireForUnit(long internalId, AsyncCallback<List<TrainingUnitQuestionnaireDTO>> callback);

	void changeStatus(TrainingCourseObj project, boolean isActive, AsyncCallback<TrainingCourseObj> callback);

	void addVideoToUnit(TrainingUnitDTO unit, TrainingVideoDTO videoDTO, AsyncCallback<TrainingVideoDTO> callback);
	
	void getListOfVideoForUnit(long internalId, AsyncCallback<List<TrainingVideoDTO>> callback);

	void countVideosForTrainingUnit(long trainingUnitId, AsyncCallback<Integer> callback);

	void countQuestionnairesForTrainingUnit(long trainingUnitId, AsyncCallback<Integer> callback);

	void updateCourse(TrainingCourseObj course, AsyncCallback<TrainingCourseObj> asyncCallback);

	void getQueryStringToShowUserProgress(TrainingCourseObj course, String userNameToShowProgress,
			AsyncCallback<String> callback);

	void shareWithCurrentScope(TrainingCourseObj project, AsyncCallback<TrainingCourseObj> callback);

	void deleteVideoForId(long videoId, AsyncCallback<Integer> callback);

	void deleteQuestionnaireForId(long questionnaireId, AsyncCallback<Integer> callback);


}
