package org.gcube.portal.trainingmodule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.gcube.portal.trainingmodule.dao.ProgressPerUnit;
import org.gcube.portal.trainingmodule.dao.TrainingProject;
import org.gcube.portal.trainingmodule.dao.TrainingUnit;
import org.gcube.portal.trainingmodule.dao.TrainingUnitQuestionnaire;
import org.gcube.portal.trainingmodule.dao.TrainingVideo;
import org.gcube.portal.trainingmodule.database.EntityManagerFactoryCreator;
import org.gcube.portal.trainingmodule.persistence.TrainingProgressUnitPersistence;
import org.gcube.portal.trainingmodule.persistence.TrainingProjectPersistence;
import org.gcube.portal.trainingmodule.persistence.TrainingUnitPersistence;
import org.gcube.portal.trainingmodule.shared.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc

/**
 * The Class TrainingDTOManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 24, 2018
 */
public class TrainingDTOManager {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(TrainingDTOManager.class);

	/** The instance. */
	private static TrainingDTOManager instance = null;
	
	/** The jdbc manager. */
	private TrainingProjectJDBCManager jdbcManager;

	/**
	 * Gets the single instance of TrainingModuleManager.
	 *
	 * @return single instance of TrainingModuleManager
	 */
	protected static TrainingDTOManager getInstance() {
		if (instance == null) {
			instance = new TrainingDTOManager();
		}
		return instance;
	}
	
	/**
	 * Instantiates a new training DTO manager singleton.
	 */
	private TrainingDTOManager() {
		jdbcManager = new TrainingProjectJDBCManager();
	}
	
	/**
	 * Gets the user courses.
	 *
	 * @param context the context
	 * @return the user courses
	 * @throws Exception the exception
	 */
	public List<TrainingProject> getCoursesFor(String context) throws Exception {
		try {
//			TrainingProjectPersistence dbm = getTrainingProjectDBManager(context);
//			Map<String, String> andFilterMap = new HashMap<String, String>();
//			andFilterMap.put("scope", context);
//			return dbm.getRowsFiltered(andFilterMap);
			
			//PATCH TO TAKE COURSE PORTLET
			return jdbcManager.selectTrainingProject(context);
			
		} catch (Exception e) {
			logger.error("An error occurred contacting the Training Courses DB", e);
			throw new Exception(
					"Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}
	
	/**
	 * Gets the list of video for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the list of video for training unit
	 * @throws Exception the exception
	 */
	//PATCH TO TAKE COURSE PORTLET
	public List<TrainingVideo> getListOfVideoForTrainingUnit(long trainingUnitId, String context) throws Exception {
		
		return jdbcManager.selectTrainingVideo(trainingUnitId, null, context);
	}
	

	/**
	 * Gets the list of questionnaire for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the list of questionnaire for training unit
	 * @throws Exception the exception
	 */
	//PATCH TO TAKE COURSE PORTLET
	public List<TrainingUnitQuestionnaire> getListOfQuestionnaireForTrainingUnit(long trainingUnitId, String context) throws Exception {
		
		return jdbcManager.selectTrainingQuestionnaire(trainingUnitId, context);
	}

	

	/**
	 * Gets the owned courses.
	 *
	 * @param owner the username
	 * @param context the context
	 * @return the owned courses
	 * @throws Exception the exception
	 */
	public List<TrainingProject> getOwnedCourses(String owner, String context) throws Exception {
		List<TrainingProject> list = null;
		try {
			TrainingProjectPersistence dbm = getTrainingProjectDBManager(context);

			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put("scope", context);
			andFilterMap.put("ownerLogin", owner);
			list = dbm.getRowsFiltered(andFilterMap);

			if (list != null && list.size() > 0) {
				logger.info(
						"Returning " + list.size() + " course/s for user: " + owner + " in the scope: " + context);
			} else {
				logger.info("No course created by the user: " + owner + " in the scope: " + owner);
			}

			return list;

		} catch (Exception e) {
			logger.error("An error occurred contacting the Training Courses DB", e);
			throw new Exception(
					"Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}
	

	/**
	 * Store new course.
	 *
	 * @param project the project
	 * @param context the context
	 * @param owner the owner
	 * @return the training project
	 * @throws Exception the exception
	 */
	public TrainingProject storeNewCourse(TrainingProject project, String context,String owner) throws Exception {
		TrainingProjectPersistence dbManager = getTrainingProjectDBManager(context);
		project.setScope(context);
		project.setOwnerLogin(owner);
		boolean isInsertOk = dbManager.insert(project);

		if (!isInsertOk)
			throw new Exception(
					"Sorry, an error occurred saving the Project in the DB, refresh and try again or contact the support");

		return project;
	}
	

	/**
	 * Store new unit.
	 *
	 * @param trainingProjectId the training project id
	 * @param unit the unit
	 * @param context the context
	 * @param owner the owner
	 * @return the training unit
	 * @throws Exception the exception
	 */
	public TrainingUnit storeNewUnit(long trainingProjectId, TrainingUnit unit, String context, String owner)
			throws Exception {

		TrainingProject prj = getTrainingProject(trainingProjectId, context, owner);

		// STORING UNIT INTO DB
		unit.setTrainingProjectRef(prj);
		unit.setOwnerLogin(owner);
		unit.setScope(context);
		TrainingUnitPersistence dbManager = getTrainingUnitDBManager(context);
		boolean isInsertOk = dbManager.insert(unit);

		if (!isInsertOk)
			throw new Exception(
					"Sorry, an error occurred saving Unit in the DB, refresh and try again or contact the support");

		return unit;
	}
	

	/**
	 * Gets the training unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param owner the owner
	 * @return the training unit
	 * @throws Exception the exception
	 */
	public TrainingUnit getTrainingUnit(long unitId, String context, String owner) throws Exception {

		if (unitId <= 0)
			throw new Exception("Invalid unit "+unitId+" to chanage the status");
		
		List<TrainingUnit> list = null;
		try {
			TrainingUnitPersistence dbm = getTrainingUnitDBManager(context);

			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put("internalId", unitId + "");
			if(context!=null)
				andFilterMap.put("scope", context);
			if(owner!=null)
				andFilterMap.put("ownerLogin", owner);
			
			list = dbm.getRowsFiltered(andFilterMap);

			if (list != null && list.size() > 0) {
				logger.info("Returning the first unit having id: " + unitId + ", scope: " + context +", owner: "+owner);
				return list.get(0);
				
			} else {
				logger.info("No unit created with id: " + unitId + ", scope: " + context +", owner: "+owner);
				return null;
			}

		} catch (Exception e) {
			logger.error("An error occurred loading the Training Unit for [id: " + unitId + ", context: " + context+", owner: "+owner+ "]", e);
			throw new Exception(
					"Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}
	
	/**
	 * Gets the training project.
	 *
	 * @param projectId the project id
	 * @param context the context
	 * @param owner the owner
	 * @return the training project
	 * @throws Exception the exception
	 */
	public TrainingProject getTrainingProject(long projectId, String context, String owner) throws Exception {

		List<TrainingProject> list = null;
		
		if(projectId<0)
			throw new Exception("Invalid project id less then 0 to perform the query");
		
		try {
			TrainingProjectPersistence dbm = getTrainingProjectDBManager(context);

			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put("internalId", projectId + "");
			if(context!=null)
				andFilterMap.put("scope", context);
			if(owner!=null)
				andFilterMap.put("ownerLogin", owner);
			
			list = dbm.getRowsFiltered(andFilterMap);

			if (list != null && list.size() > 0) {
				logger.info("Returning " + list.size() + " course/s for id: " + projectId + ", scope: " + context +", owner: "+owner);
				return list.get(0);
			} else {
				logger.info("No course created for id: " + projectId + ", scope: " + context +", owner: "+owner);
				return null;
			}

		} catch (Exception e) {
			logger.error("An error occurred loading the Training Course for [id: " + projectId + ", context: " + context+", owner: "+owner+ "]", e);
			throw new Exception(
					"Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}

	
	/**
	 * Delete training project.
	 *
	 * @param trainingProjectId the training project id
	 * @param context the context
	 * @param owner the owner
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingProject(long trainingProjectId, String context, String owner) throws Exception {

		if (trainingProjectId <= 0)
			throw new Exception("Invalid project id: "+trainingProjectId+" to delete it");
		
		TrainingProject project = null;
		try {
			project = getTrainingProject(trainingProjectId, context, owner);
			if(project==null)
				throw new Exception("No project with id: "+trainingProjectId);
			
			try {
				// DELETING THE UNIT/S BELONING TO THE COURSE
				TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
				int deleted = tU.deleteUnitsForTrainingProject(project);
				logger.info("Deleted " + deleted + " unit/s for project:  " + project);
			} catch (Exception e) {
				logger.warn("Error: ", e);
			}

			// DELETING THE PROJECT
			TrainingProjectPersistence tpers = getTrainingProjectDBManager(context);
			return tpers.deleteItemByInternalId(project.getInternalId());

		} catch (Exception e) {
			logger.error("Error on deleting project: " + project, e);
			throw new Exception(
					"Sorry, an error occurred during deleting the course, try again later or contact the support");
		}

	}

	/**
	 * Change status.
	 *
	 * @param trainingProjectId the training project id
	 * @param isActive the is active
	 * @param context the context
	 * @param owner the owner username
	 * @return the training project DTO
	 * @throws Exception the exception
	 */
	public TrainingProject changeStatus(long trainingProjectId, boolean isActive, String context, String owner)
			throws Exception {
		if (trainingProjectId <= 0)
			throw new Exception("Invalid project to chanage the status");

		try {

			TrainingProjectPersistence tpers = getTrainingProjectDBManager(context);
			TrainingProject theProject = getTrainingProject(trainingProjectId, context, owner);
			//TODO CHECK ON OWNER LOGIN?
			theProject.setCourseActive(isActive);
			return tpers.update(theProject);

		} catch (Exception e) {
			logger.error("Error on changing status of project id: " + trainingProjectId, e);
			throw new Exception(
					"Sorry, an error occurred changing status of the course, try again later or contact the support");
		}
	}
	
	
	/**
	 * Gets the training project DB manager.
	 *
	 * @param context
	 *            the context
	 * @return the training project DB manager
	 * @throws Exception
	 *             the exception
	 */
	private synchronized TrainingProjectPersistence getTrainingProjectDBManager(String context) throws Exception {

		try {

			EntityManagerFactory emf = EntityManagerFactoryCreator.instanceFactoryCreator(context);
			return new TrainingProjectPersistence(emf);

		} catch (Exception e) {
			logger.error("An error occurred when creating Entity Factory", e);
			throw new Exception("Sorry, an error occurred on contacting the Training App DB");
		}

	}

	/**
	 * Gets the training unit DB manager.
	 *
	 * @param context
	 *            the context
	 * @return the training unit DB manager
	 * @throws Exception
	 *             the exception
	 */
	private synchronized TrainingUnitPersistence getTrainingUnitDBManager(String context) throws Exception {

		try {

			EntityManagerFactory emf = EntityManagerFactoryCreator.instanceFactoryCreator(context);
			return new TrainingUnitPersistence(emf);

		} catch (Exception e) {
			logger.error("An error occurred when creating Entity Factory", e);
			throw new Exception("Sorry, an error occurred on contacting the Training App DB");
		}

	}
	
	
	/**
	 * Gets the training unit progress DB manager.
	 *
	 * @param context the context
	 * @return the training unit progress DB manager
	 * @throws Exception the exception
	 */
	private synchronized TrainingProgressUnitPersistence getTrainingUnitProgressDBManager(String context) throws Exception {

		try {

			EntityManagerFactory emf = EntityManagerFactoryCreator.instanceFactoryCreator(context);
			return new TrainingProgressUnitPersistence(emf);

		} catch (Exception e) {
			logger.error("An error occurred when creating Entity Factory", e);
			throw new Exception("Sorry, an error occurred on contacting the Training App DB");
		}

	}
	

	/**
	 * Gets the training unit for.
	 *
	 * @param ownerLogin the owner login
	 * @param workspaceFolderId the workspace folder id
	 * @param context the context
	 * @return the training unit for
	 * @throws Exception the exception
	 */
	public TrainingUnit getTrainingUnitFor(String ownerLogin, String workspaceFolderId, String context)
			throws Exception {

		if (workspaceFolderId == null || ownerLogin == null)
			throw new Exception("Parameters errors, It is not possible to perform the query");

		try {

			TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
			Map<String, String> andFilterMap = new HashMap<String, String>();
			andFilterMap.put("ownerLogin", ownerLogin);
			andFilterMap.put("workspaceFolderId", workspaceFolderId);
			if(context!=null)
				andFilterMap.put("scope", context);
			
			List<TrainingUnit> theFolder = tU.getRowsFiltered(andFilterMap);
			if (theFolder != null && theFolder.size() > 0) {
				return theFolder.get(0);
			}
			
			return null;

		} catch (Exception e) {
			logger.error("Error on training unit for ownerLogin: " + ownerLogin, ", workspaceFolderId: "+workspaceFolderId, e);
			throw new Exception(
					"Sorry, an error occurred getting training unit, try again later or contact the support");
		}
	}
	


	/**
	 * Gets the list unit for training project.
	 *
	 * @param trainingProjectId the training project id
	 * @param context the context
	 * @return the list unit for training project
	 * @throws Exception the exception
	 */
	public List<TrainingUnit> getListUnitForTrainingProject(long trainingProjectId, String context)
			throws Exception {

		if (trainingProjectId <=0)
			throw new Exception("Parameters errors, It is not possible to perform the query");

		try {
			
			TrainingUnitPersistence tum = getTrainingUnitDBManager(context);
			TrainingProject tp = getTrainingProject(trainingProjectId, context, null);
			return tum.getListUnitForTrainingProject(tp);

		} catch (Exception e) {
			logger.error("Error on getting list of training unit for context: "+context+ ", projectId: "+trainingProjectId, e);
			throw new Exception(
					"Sorry, an error occurred getting list of training unit, try again later or contact the support");
		}
	}

	
	/**
	 * Delete training unit for.
	 *
	 * @param ownerLogin the owner login
	 * @param workspaceFolderId the workspace folder id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingUnitFor(String ownerLogin, String workspaceFolderId, String context)
			throws Exception {

		if (workspaceFolderId == null || ownerLogin == null)
			throw new Exception("Parameters errors: workspaceFolderId and ownerLogin are mandatory parameters");

		try {

			TrainingUnit tu = getTrainingUnitFor(ownerLogin, workspaceFolderId, context);
			TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
			//deleteAllQuestionnaireForUnit(tu, context);
			//deleteAllVideoForUnit(tu, context);
			return tU.deleteItemByInternalId(tu.getInternalId());
			
		} catch (Exception e) {
			logger.error("Error on delete training unit for ownerLogin: " + ownerLogin, ", workspaceFolderId: "+workspaceFolderId, e);
			throw new Exception(
					"Sorry, an error occurred deleting training unit, try again later or contact the support");
		}
	}
	


	/**
	 * Delete training unit for id.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingUnitForId(Long unitId, String context)
			throws Exception {

		if (unitId == null || unitId <= 0)
			throw new Exception("Parameter error: unit id is null or less then 0");

		try {

			TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
			return tU.deleteItemByInternalId(unitId);
			
		} catch (Exception e) {
			logger.error("Error on deleting training unit for id: " + unitId, e);
			throw new Exception(
					"Sorry, an error occurred deleting training unit, try again later or contact the support");
		}
	}
	
	

	/**
	 * Delete all questionnaire for unit.
	 *
	 * @param tu the tu
	 * @param context the context
	 * @return number of questionnaire deleted
	 * @throws Exception the exception
	 */
	private int deleteAllQuestionnaireForUnit(TrainingUnit tu, String context) throws Exception {
		
		if(tu==null || tu.getInternalId()<0)
			throw new Exception("Invalid unit to perform delete Questionnaire for it");
		
		int deleted = 0;
		if(tu.getListQuestionnaire()!=null && tu.getListQuestionnaire().size()>0) {
			for (TrainingUnitQuestionnaire quest : tu.getListQuestionnaire()) {
				deleteQuestionnaireForId(quest.getInternalId(), context);
				deleted++;
			}
			
		}
		logger.info("Deleted "+deleted+" questionnaire/s for Unit id: "+tu.getInternalId());
		return deleted;
	}
	
	
	/**
	 * Delete all video for unit.
	 *
	 * @param tu the tu
	 * @param context the context
	 * @return number of video deleted
	 * @throws Exception the exception
	 */
	private int deleteAllVideoForUnit(TrainingUnit tu, String context) throws Exception {
		
		if(tu==null || tu.getInternalId()<0)
			throw new Exception("Invalid unit to perform delete Video for it");
		
		int deleted = 0;
		if(tu.getListVideo()!=null && tu.getListVideo().size()>0) {
			for (TrainingVideo video : tu.getListVideo()) {
				deleteVideoForId(video.getInternalId(), context);
				deleted++;
			}
			
		}
		
		logger.info("Deleted "+deleted+" video/s for Unit id: "+tu.getInternalId());
		return deleted;
	}
	
	
	/**
	 * Delete video for id.
	 *
	 * @param videoId the video id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteVideoForId(long videoId, String context)
			throws Exception {

		if (videoId <0)
			throw new Exception("Parameter error: value of videoId "+videoId+" is less then 0");

		try {
			return jdbcManager.deleteTrainingVideo(videoId, context);
			
		} catch (Exception e) {
			logger.error("Error deleteting for videoId: " + videoId, e);
			throw new Exception(
					"Sorry, an error occurred deleting video, try again later or contact the support");
		}
	}
	
	

	/**
	 * Delete questionnaire for id.
	 *
	 * @param questionnaireId the questionnaire id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteQuestionnaireForId(long questionnaireId, String context)
			throws Exception {

		if (questionnaireId <0)
			throw new Exception("Parameter error: value of questionnaireId "+questionnaireId+" is less then 0");

		try {
			return jdbcManager.deleteQuestionnaireForId(questionnaireId, context);
			
		} catch (Exception e) {
			logger.error("Error deleteting for videoId: " + questionnaireId, e);
			throw new Exception(
					"Sorry, an error occurred deleting video, try again later or contact the support");
		}
	}



	

	/**
	 * Update training project.
	 *
	 * @param project the project
	 * @param context the context
	 * @return the training project
	 * @throws Exception the exception
	 */
	public TrainingProject updateTrainingProject(TrainingProject project, String context) throws Exception {
		
		if (project == null)
			throw new Exception("Training Project is null");
		
		try {
			TrainingProjectPersistence tp = getTrainingProjectDBManager(context);
			//TrainingProject traningPrj = tp.getItemByKey(project.getInternalId(), TrainingProject.class);
			return tp.update(project);
		
		} catch (Exception e) {
			logger.error("Error on updatint training porject for context: " + context, ", project: "+project, e);
			throw new Exception(
					"Sorry, an error occurred deleting training unit, try again later or contact the support");
		}
	}

	

	/**
	 * Adds the questionnaire to training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param questionnaire the questionnaire
	 * @param context the context
	 * @param owner the owner
	 * @return the training unit questionnaire inserted
	 * @throws Exception the exception
	 */
	public TrainingUnitQuestionnaire addQuestionnaireToTrainingUnit(long trainingUnitId, TrainingUnitQuestionnaire questionnaire, String context, String owner) throws Exception{
		
		if (trainingUnitId <=0)
			throw new Exception("Parameter error. Invalid Training Unit id: "+trainingUnitId);
		
		if(questionnaire==null || questionnaire.getQuestionnaireId()==null)
			throw new Exception("Parameter error. Invalid Questionnaire or Questionnaire id null");
		
		TrainingUnit unit = getTrainingUnit(trainingUnitId, context, owner);
		
		if(unit==null)
			throw new Exception("Parameter error. No Training Unit found with id: "+trainingUnitId);
		
		TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
		unit.addQuestionnaire(questionnaire);
		unit = tU.update(unit);
		return unit.getQuestionnaireFor(questionnaire.getQuestionnaireId());
	}
	
	

	/**
	 * Adds the video to training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param video the video
	 * @param context the context
	 * @param owner the owner
	 * @return the training video
	 * @throws Exception the exception
	 */
	public TrainingVideo addVideoToTrainingUnit(long trainingUnitId, TrainingVideo video, String context, String owner) throws Exception{
		
		if (trainingUnitId <=0)
			throw new Exception("Parameter error. Invalid Training Unit id: "+trainingUnitId);
		
		
		TrainingUnit unit = getTrainingUnit(trainingUnitId, context, owner);
		
		if(unit==null)
			throw new Exception("Parameter error. No Training Unit found with id: "+trainingUnitId);
		
		TrainingUnitPersistence tU = getTrainingUnitDBManager(context);
		unit.addVideo(video);
		unit = tU.update(unit);
		return unit.getVideoForURL(video.getUrl());
	}

	

	/**
	 * Sets the progress for unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @param read the read
	 * @return the progress per unit
	 * @throws Exception the exception
	 */
	public ProgressPerUnit setProgressForUnit(long unitId, String context, String username, String itemId, ItemType itemType, boolean read) throws Exception {
		
		TrainingUnit theUnit = getTrainingUnit(unitId, context, null);
		
		if(theUnit==null)
			throw new Exception("No Unit with id: "+unitId);
		
		TrainingProgressUnitPersistence tPU = getTrainingUnitProgressDBManager(context);
		
		List<ProgressPerUnit> progresses = jdbcManager.selectTrainingUnitProgress(context, unitId, username, itemId, null);
		
		if(progresses.size()>0) {
			logger.info("Found progress unit for unitid: "+unitId +", context: "+context+ ", username: "+username +", itemId: "+itemId);
			logger.info("Updating it");
			//IS UPDATE
			ProgressPerUnit dao = progresses.get(0);
			dao.setUnitId(unitId);
			dao.setUsername(username);
			dao.setItemId(itemId);
			dao.setType(itemType);
			dao.setRead(read);
//			logger.trace("Updating: "+dto);
//			ProgressPerUnit dao = DTOConverter.toProgressUnit(dto);
			return tPU.update(dao);
//			return DTOConverter.fromProgressUnit(dao);
			
		}else {
			logger.info("No progress unit for unitid: "+unitId +", context: "+context+ ", username: "+username +", itemId: "+itemId+ ", itemType: "+itemId);
			logger.info("Creating new one");
			//IS CREATE
			ProgressPerUnit dao = new ProgressPerUnit(unitId, username, itemType, itemId, read);
			boolean created = tPU.create(dao);
			if(created) {
				return dao;
			}
			else {
				throw new Exception("Error on creating the progress entry for Unit id: "+unitId +" and Username: "+username +" on itemId: "+itemId);
			}
				
		}
		
		
	}
	
	
	/**
	 * Gets the progress for unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @return the progress for unit
	 * @throws Exception the exception
	 */
	public List<ProgressPerUnit> getProgressForUnit(long unitId, String context, String username, String itemId, ItemType itemType) throws Exception {
		
		TrainingUnit theUnit = getTrainingUnit(unitId, context, null);
		
		if(theUnit==null)
			throw new Exception("No Unit with id: "+unitId);
		
		
		return jdbcManager.selectTrainingUnitProgress(context, unitId, username, itemId, itemType);

	}
	

}
