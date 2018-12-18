package org.gcube.portlets.user.takecourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.trainingmodule.TrainingModuleManager;
import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitProgressDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portlets.user.takecourse.dto.StudentProgressDTO;
import org.gcube.portlets.user.takecourse.dto.UnitProgress;
import org.gcube.portlets.user.takecourse.questionnaire.QuestionnaireDTO;
import org.gcube.portlets.user.takecourse.questionnaire.QuestionnaireDatabaseManager;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

public class ProgressManager {
	private static Log _log = LogFactoryUtil.getLog(ProgressManager.class);
	
	public static List<StudentProgressDTO> getAllStudentProgress(long courseId, long groupId) {
		List<StudentProgressDTO> toReturn = new ArrayList<StudentProgressDTO>();
		try {
			TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
			String context = Utils.getCurrentContext(groupId);
			List<TrainingUnitDTO> units = trainingManager.getListOfTrainingUnitFor(courseId, context);	
			QuestionnaireDatabaseManager qMan = QuestionnaireDatabaseManager.getInstance(context);
			List<User> allUsers = UserLocalServiceUtil.getGroupUsers(groupId);
			boolean doOnce = true;
			int totalFiles = 0;
			int totalQuiz = 0;
			Map<Long, List<UnitProgress>> userProgress = new HashMap<>();
			List<TrainingUnitQuestionnaireDTO> questionnaires = null;
			for (TrainingUnitDTO unit : units) {
				questionnaires = trainingManager.getListOfQuestionnaireForTrainingUnit(unit.getInternalId(), context);
				for (User user : allUsers) {
					if (doOnce) { //only once per unit
						totalFiles = getTotalUnitFileCount(unit.getWorkspaceFolderId(), user.getScreenName(), context); 
						totalQuiz = questionnaires.size();					
						doOnce = false;
					}
					List<TrainingUnitProgressDTO> filesReadList = trainingManager.getProgressesForUnit(unit.getInternalId(), context, user.getScreenName(), null, null);
					int quizAnswered = 0;
					for (TrainingUnitQuestionnaireDTO item :questionnaires) {
						QuestionnaireDTO q = qMan.getQuestionnaireURLForUser(user.getUserId(), user.getEmailAddress(), context, item.getQuestionnaireId());
						if (q.isAnswered())
							quizAnswered++;
					}
					int currentProgress = filesReadList.size() + quizAnswered;
					int grandTotal = totalFiles + totalQuiz;
					int progressPercentage = percentage(currentProgress, grandTotal);
					UnitProgress toAdd = new UnitProgress(unit.getInternalId(), unit.getTitle(), progressPercentage);
					if (userProgress.get(user.getUserId()) == null) {
						List<UnitProgress> unitProgresses = new ArrayList<>();
						unitProgresses.add(toAdd);
						userProgress.put(user.getUserId(), unitProgresses);
					}
					else {
						userProgress.get(user.getUserId()).add(toAdd);
					}
				}
				doOnce = true;
			}
			for (long userId : userProgress.keySet()) {
				User theUser = UserLocalServiceUtil.getUser(userId);
				List<UnitProgress> list = userProgress.get(userId);
				toReturn.add(new StudentProgressDTO(theUser.getUserId(), theUser.getScreenName(), theUser.getFullName(), list));
			}
			for (StudentProgressDTO sp : toReturn) {
				_log.debug(sp.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private static int percentage(double num, double total){
		return (int) Math.floor((num * 100 / total));
	}
	
	private static int getTotalUnitFileCount(String folderId, String username, String context) {
		ScopeProvider.instance.set(context);
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);
		Workspace ws = null;
		try {
			ws = HomeLibrary.getUserWorkspace(username);
			WorkspaceFolder sharedFolder = (WorkspaceFolder) ws.getItem(folderId);
			int count =  sharedFolder.getChildrenCount(false);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}