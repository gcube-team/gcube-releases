package org.gcube.portal.trainingmodule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.portal.trainingmodule.dao.ProgressPerUnit;
import org.gcube.portal.trainingmodule.dao.TrainingProject;
import org.gcube.portal.trainingmodule.dao.TrainingUnit;
import org.gcube.portal.trainingmodule.dao.TrainingUnitQuestionnaire;
import org.gcube.portal.trainingmodule.dao.TrainingVideo;
import org.gcube.portal.trainingmodule.shared.DTOConverter;
import org.gcube.portal.trainingmodule.shared.ItemType;
import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitProgressDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingModuleManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2018
 */
public class TrainingModuleManager {
	
	/** The hl group separator. */
	public static String HL_GROUP_SEPARATOR = "-";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(TrainingModuleManager.class);

	/** The instance. */
	private static TrainingModuleManager instance = null;
	
	/** The dto manger. */
	private TrainingDTOManager dtoManger;

	/**
	 * Gets the single instance of TrainingModuleManager.
	 *
	 * @return single instance of TrainingModuleManager
	 */
	public static TrainingModuleManager getInstance() {
		if (instance == null) {
			instance = new TrainingModuleManager();
		}
		return instance;
	}
	
	/**
	 * Instantiates a new training module manager singleton.
	 */
	private TrainingModuleManager() {
		
		if(dtoManger==null)
			dtoManger = TrainingDTOManager.getInstance();
		
	}

	/**
	 * Gets the user courses.
	 *
	 * @param username the username
	 * @param context the context
	 * @return the user courses shared with 'username' and 'group' as context passed in the scope 'context'
	 * @throws Exception the exception
	 */
	public List<TrainingCourseDTO> getUserCourses(String username, String context) throws Exception {
		logger.debug("Get user courses for: "+username+", in the context: "+context);
		
		if(username==null)
			throw new Exception("Invalid parameter: 'username' is null");
		
		if(context==null)
			throw new Exception("Invalid parameter: 'context' is null");
		
		List<TrainingProject> list = dtoManger.getCoursesFor(context);
		logger.info("VRE courses in the context: "+context + " are: "+list.size());
		
		if(logger.isDebugEnabled()) {
			logger.debug("Project are: ");
			for (TrainingProject trainingProject : list) {
				logger.debug(trainingProject.getTitle() + " shared with: "+trainingProject.getSharedWith());
			}
		}
		
		String contextAsGroup = context.replaceAll("/", HL_GROUP_SEPARATOR);
		contextAsGroup = contextAsGroup.substring(1,contextAsGroup.length());
		logger.debug("Group is: "+contextAsGroup);
		
		List<TrainingCourseDTO> listDTO = new ArrayList<>(list.size());
		for (TrainingProject trainingProject : list) {
			logger.debug("Course id: "+trainingProject.getInternalId() + ", title: "+trainingProject.getTitle());
			List<String> listUsername = DTOConverter.fromSharedWith(trainingProject.getSharedWith());
			logger.debug("It has list username: "+listUsername);
			if(listUsername!=null) {
				for (String login : listUsername) {
					logger.trace("Comparing login: "+login+" with username: "+username + " or group: "+contextAsGroup);
					if(login.compareTo(username)==0 || login.compareToIgnoreCase(contextAsGroup)==0) {
						logger.trace("Adding login: "+username);
						listDTO.add(DTOConverter.fromTrainingProject(trainingProject));
						break;
					}
				}
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Returining for user "+username + ", "+listDTO.size()+" shared course/s:");
			for (TrainingCourseDTO trainingCourseDTO : listDTO) {
				logger.debug("Course id: "+trainingCourseDTO.getInternalId()+", Title: "+trainingCourseDTO.getTitle());
			}
		}else
			logger.debug("Returining for user "+username+", "+listDTO.size()+" shared course/s");
		
		return listDTO;
		
	}

	/**
	 * Gets the courses shared with.
	 *
	 * @param context the context
	 * @param sharedWith the shared with
	 * @return the courses shared with: 'username' or 'group'
	 * @throws Exception the exception
	 */
	public List<TrainingCourseDTO> getCoursesSharedWith(String context, String... sharedWith) throws Exception {
		logger.debug("Get user courses for shared with: "+sharedWith+", in the context: "+context);
		
		if(context==null)
			throw new Exception("Invalid parameter: 'context' is null");
		
		if(sharedWith==null) 
			throw new Exception("Invalid parameter: 'sharedWith' is null");
		
		List<TrainingProject> list = dtoManger.getCoursesFor(context);
		logger.info("VRE courses in the context: "+context + " are: "+list.size());
		
		if(logger.isDebugEnabled()) {
			logger.debug("Project are: ");
			for (TrainingProject trainingProject : list) {
				logger.debug(trainingProject.getTitle() + " shared with: "+trainingProject.getSharedWith());
			}
		}
		
		List<String> listLogins = Arrays.asList(sharedWith);
		
		List<TrainingCourseDTO> listDTO = new ArrayList<>(list.size());
		for (TrainingProject trainingProject : list) {
			logger.debug("Course id: "+trainingProject.getInternalId() + ", title: "+trainingProject.getTitle());
			List<String> listUsername = DTOConverter.fromSharedWith(trainingProject.getSharedWith());
			logger.debug("It has list username: "+listUsername);
			if(listUsername!=null) {
				for (String login : listUsername) {
					for (String searchShared : listLogins) {
						logger.trace("Comparing login: "+login+" with login shared: "+searchShared);
						if(login.compareTo(searchShared)==0) {
							logger.trace("Adding login: "+searchShared);
							listDTO.add(DTOConverter.fromTrainingProject(trainingProject));
							break;
						}
					}
					
				}
			}
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("Returining for sharedWith: "+sharedWith + ", "+listDTO.size()+" shared course/s:");
			for (TrainingCourseDTO trainingCourseDTO : listDTO) {
				logger.debug("Course id: "+trainingCourseDTO.getInternalId()+", Title: "+trainingCourseDTO.getTitle());
			}
		}else
			logger.debug("Returining for sharedWith "+sharedWith+", "+listDTO.size()+" shared course/s");
		
		return listDTO;
		
	}
	
	
	/**
	 * Gets the owned courses.
	 *
	 * @param owner the owner
	 * @param context the context
	 * @return the owned courses by 'owner' in the scope 'context'
	 * @throws Exception the exception
	 */
	public List<TrainingCourseDTO> getOwnedCourses(String owner, String context) throws Exception {
		
		if(owner==null)
			throw new Exception("Invalid parameter 'username' is null");
		
		List<TrainingProject> listCourses = dtoManger.getOwnedCourses(owner, context);
		return DTOConverter.fromListOfProject(listCourses);
		
	}


	/**
	 * Store new course.
	 *
	 * @param projectDTO the project DTO
	 * @param context the context
	 * @param owner the username
	 * @return the training course DTO
	 * @throws Exception the exception
	 */
	public TrainingCourseDTO storeNewCourse(TrainingCourseDTO projectDTO, String context, String owner) throws Exception {
		// STORING PROJECT INTO DB
		projectDTO.setOwnerLogin(owner);
		projectDTO.setScope(context);
		TrainingProject project = DTOConverter.toTrainingProject(projectDTO);
		project = dtoManger.storeNewCourse(project, context, owner);
		return DTOConverter.fromTrainingProject(project);
	}


	/**
	 * Store new unit.
	 *
	 * @param trainingProjectId the training project id
	 * @param unitDTO the unit DTO
	 * @param context the context
	 * @param owner the owner username
	 * @return the training unit DTO
	 * @throws Exception the exception
	 */
	public TrainingUnitDTO storeNewUnit(long trainingProjectId, TrainingUnitDTO unitDTO, String context, String owner)
			throws Exception {
		// STORING UNIT INTO DB
		unitDTO.setOwnerLogin(owner);
		unitDTO.setScope(context);
		TrainingUnit unit  = DTOConverter.toTrainingUnit(unitDTO);
		TrainingUnit newUnit = dtoManger.storeNewUnit(trainingProjectId, unit, context, owner);
		return DTOConverter.fromTrainingUnit(newUnit);
	}
	

	/**
	 * Gets the training unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @return the training unit
	 * @throws Exception the exception
	 */
	public TrainingUnitDTO getTrainingUnit(long unitId, String context) throws Exception {

		TrainingUnit newUnit = dtoManger.getTrainingUnit(unitId, context, null);
		return DTOConverter.fromTrainingUnit(newUnit);
	}


	/**
	 * Delete training course.
	 *
	 * @param trainingCourseId the training course id
	 * @param context the context
	 * @param owner the owner username
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingCourse(long trainingCourseId, String context, String owner) throws Exception {

		return dtoManger.deleteTrainingProject(trainingCourseId, context, owner);

	}

	
	/**
	 * Change status.
	 *
	 * @param trainingProjectId the training project id
	 * @param isActive the is active
	 * @param context the context
	 * @param owner the owner username
	 * @return the training course DTO
	 * @throws Exception the exception
	 */
	public TrainingCourseDTO changeStatus(long trainingProjectId, boolean isActive, String context, String owner)
			throws Exception {
		TrainingProject project = dtoManger.changeStatus(trainingProjectId, isActive, context, owner);
		return DTOConverter.fromTrainingProject(project);
	}



	/**
	 * Gets the training course.
	 *
	 * @param courseId the course id
	 * @param context the context
	 * @return the training course
	 * @throws Exception the exception
	 */
	public TrainingCourseDTO getTrainingCourse(long courseId, String context) throws Exception {

		TrainingProject project = dtoManger.getTrainingProject(courseId, context, null);
		return DTOConverter.fromTrainingProject(project);
	}
	
	
	/**
	 * Update training course.
	 *
	 * @param project the project
	 * @param context the context
	 * @return the training course DTO
	 * @throws Exception the exception
	 */
	public TrainingCourseDTO updateTrainingCourse(TrainingCourseDTO project, String context) throws Exception {
		TrainingProject proj = DTOConverter.toTrainingProject(project);
		TrainingProject dto = dtoManger.updateTrainingProject(proj, context);
		return DTOConverter.fromTrainingProject(dto);
	}
	
	
	/**
	 * Gets the training unit for.
	 *
	 * @param ownerLogin the owner login
	 * @param workspaceFolderId the workspace folder id
	 * @param context the context
	 * @param username the username
	 * @return the training unit for
	 * @throws Exception the exception
	 */
	public TrainingUnitDTO getTrainingUnitFor(String ownerLogin, String workspaceFolderId, String context, String username)
			throws Exception {
		
		 TrainingUnit tu = dtoManger.getTrainingUnitFor(ownerLogin, workspaceFolderId, context);
		 return DTOConverter.fromTrainingUnit(tu);

	}

	
	/**
	 * Gets the list of training unit for.
	 *
	 * @param courseId the course id
	 * @param context the context
	 * @return the list of training unit for
	 * @throws Exception the exception
	 */
	public List<TrainingUnitDTO> getListOfTrainingUnitFor(long courseId, String context)
			throws Exception {
		
		 List<TrainingUnit> list = dtoManger.getListUnitForTrainingProject(courseId, context);
		 return DTOConverter.fromListOfUnit(list);

	}
	
	
	/**
	 * Gets the list of questionnaire for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the list of questionnaire for training unit
	 * @throws Exception the exception
	 */
	public List<TrainingUnitQuestionnaireDTO> getListOfQuestionnaireForTrainingUnit(long trainingUnitId, String context) throws Exception {
		
		List<TrainingUnitQuestionnaire> listQ = dtoManger.getListOfQuestionnaireForTrainingUnit(trainingUnitId, context);
		
		if(listQ==null)
			return null;
		
		List<TrainingUnitQuestionnaireDTO> listQST = new ArrayList<TrainingUnitQuestionnaireDTO>(listQ.size());
		for (TrainingUnitQuestionnaire trainingUnitQuestionnaire : listQ) {
			listQST.add(DTOConverter.fromTrainingUnitQuestionnaire(trainingUnitQuestionnaire));
		}
		
		return listQST;
	}
	
	
	/**
	 * Count questionnaires for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int countQuestionnairesForTrainingUnit(long trainingUnitId, String context) throws Exception {
		List<TrainingUnitQuestionnaire> listQ = dtoManger.getListOfQuestionnaireForTrainingUnit(trainingUnitId, context);
		
		if(listQ!=null)
			return listQ.size();
		
		return -1;
	}
	
	
	
	
	/**
	 * Adds the questionnaire to training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param questionnaireDTO the questionnaire DTO
	 * @param context the context
	 * @param owner the owner
	 * @return the training unit questionnaire DTO
	 * @throws Exception the exception
	 */
	public TrainingUnitQuestionnaireDTO addQuestionnaireToTrainingUnit(long trainingUnitId, TrainingUnitQuestionnaireDTO questionnaireDTO, String context, String owner) throws Exception{
		
		TrainingUnitQuestionnaire questionnaire = DTOConverter.toTrainingUnitQuestionnaire(questionnaireDTO);
		if(questionnaire!=null) {
			TrainingUnitQuestionnaire quest = dtoManger.addQuestionnaireToTrainingUnit(trainingUnitId, questionnaire, context, owner);
			if(quest==null) {
				throw new Exception("Sorry, an error occurred adding Questionnaire to Unit with id: "+trainingUnitId+", try again later or contact the support");
			}
			return DTOConverter.fromTrainingUnitQuestionnaire(quest);
		}
		return null;
	}
	

	/**
	 * Adds the video to training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param videoDTO the video DTO
	 * @param context the context
	 * @param owner the owner
	 * @return the training video DTO
	 * @throws Exception the exception
	 */
	public TrainingVideoDTO addVideoToTrainingUnit(long trainingUnitId, TrainingVideoDTO videoDTO, String context, String owner) throws Exception{
		
		TrainingVideo video = DTOConverter.toTrainingVideo(videoDTO);
		if(video!=null) {
			TrainingVideo tv = dtoManger.addVideoToTrainingUnit(trainingUnitId, video, context, owner);
			if(tv==null) {
				throw new Exception("Sorry, an error occurred adding Training Video to Unit with id: "+trainingUnitId+", try again later or contact the support");
			}
			return DTOConverter.fromTrainingVideo(tv);
		}
		return null;
	}
	

	/**
	 * Gets the list of video for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the list of video for training unit
	 * @throws Exception the exception
	 */
	public List<TrainingVideoDTO> getListOfVideoForTrainingUnit(long trainingUnitId, String context) throws Exception {
		
		/*
		TrainingUnit unit = dtoManger.getTrainingUnit(trainingUnitId, context, null);
		List<TrainingVideo> listV = unit.getListVideo();
		*/
		
		//PATCH TO TAKE COURSE
		List<TrainingVideo> listVideo = dtoManger.getListOfVideoForTrainingUnit(trainingUnitId, context);
		
		if(listVideo==null)
			return null;
			
		
		List<TrainingVideoDTO> listVST = new ArrayList<TrainingVideoDTO>(listVideo.size());
		for (TrainingVideo traiV : listVideo) {
			listVST.add(DTOConverter.fromTrainingVideo(traiV));
		}
		
		return listVST;
	}
	

	/**
	 * Count videos for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int countVideosForTrainingUnit(long trainingUnitId, String context) throws Exception {
		List<TrainingVideo> listVideo = dtoManger.getListOfVideoForTrainingUnit(trainingUnitId, context);
		if(listVideo!=null)
			return listVideo.size();
		
		return -1;
	}

	

	/**
	 * Delete training unit.
	 *
	 * @param workspaceFolderId the workspace folder id
	 * @param context the context
	 * @param owner the owner
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingUnit(String workspaceFolderId, String context, String owner) throws Exception {

		return dtoManger.deleteTrainingUnitFor(owner, workspaceFolderId, context);

	}
	

	/**
	 * Delete training unit for id.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteTrainingUnitForId(Long unitId, String context) throws Exception {

		return dtoManger.deleteTrainingUnitForId(unitId, context);

	}
	

	/**
	 * Delete video for id.
	 *
	 * @param videoId the video id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteVideoForId(long videoId, String context) throws Exception {

		return dtoManger.deleteVideoForId(videoId, context);

	}
	


	/**
	 * Delete questionnaire for id.
	 *
	 * @param questionnaireId the questionnaire id
	 * @param context the context
	 * @return the int
	 * @throws Exception the exception
	 */
	public int deleteQuestionnaireForId(long questionnaireId, String context) throws Exception {

		return dtoManger.deleteQuestionnaireForId(questionnaireId, context);

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
	 * @return the training unit progress DTO
	 * @throws Exception the exception
	 */
	public TrainingUnitProgressDTO setProgressForUnit(long unitId, String context, String username, String itemId, ItemType itemType, boolean read) throws Exception {
		
		if(unitId<0)
			throw new Exception("UnitId parameter fault, it is less then 0");
		
		if(username==null || username.isEmpty())
			throw new Exception("The username parameter is required");
		
		if(itemId==null || itemId.isEmpty())
			throw new Exception("The itemId parameter is required");
		
		ProgressPerUnit dao = dtoManger.setProgressForUnit(unitId, context, username, itemId, itemType, read);
		
		if(dao!=null)
			return DTOConverter.fromProgressUnit(dao);
		
		return null;
		
	}
	


	/**
	 * Gets the progresses for unit.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @return the progresses for unit
	 * @throws Exception the exception
	 */
	public List<TrainingUnitProgressDTO> getProgressesForUnit(long unitId, String context, String username, String itemId, ItemType itemType) throws Exception {
		
		
		List<ProgressPerUnit> daoList = dtoManger.getProgressForUnit(unitId, context, username, itemId, itemType);
		List<TrainingUnitProgressDTO> dtoList = null;
		
		if(daoList!=null) {
			dtoList = new ArrayList<>(daoList.size());
			for (ProgressPerUnit progressPerUnit : daoList) {
				dtoList.add(DTOConverter.fromProgressUnit(progressPerUnit));
			}
		}
		
		return dtoList;
		

	}

}
