package org.gcube.portlets.user.trainingcourse.server;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.trainingmodule.TrainingModuleManager;
import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService;
import org.gcube.portlets.user.trainingcourse.server.hl.TrainingCourseHLPathManager;
import org.gcube.portlets.user.trainingcourse.server.hl.WsUtil;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;
import org.gcube.portlets.user.trainingcourse.shared.bean.PortalContextInfo;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Portlet;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.PortletLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;

// TODO: Auto-generated Javadoc
/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 10, 2018
 */
@SuppressWarnings("serial")
public class TrainingCourseAppServiceImpl extends RemoteServiceServlet implements TrainingCourseAppService {

	/** The Constant UTF_8. */
	public static final String UTF_8 = "UTF-8";

	/** The Constant USER_PARAM. */
	public static final String USER_PARAM = "u";

	/** The Constant COURSEID_PARAM. */
	public static final String COURSEID_PARAM = "c";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(TrainingCourseAppServiceImpl.class);
	
	/** The training module. */
	private TrainingModuleManager trainingModule;
	
	//TO MASSI
	public final static String TakeSurveyPortlet_PLUGIN_ID_PREFIX = "TakeSurveyPortlet_WAR_";
	
	
	/**
	 * Gets the training library.
	 *
	 * @return the training library
	 */
	private TrainingModuleManager getTrainingLibrary(){
		
		if(trainingModule==null)
			trainingModule = TrainingModuleManager.getInstance();
		
		return trainingModule;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#
	 * greetServer(java.lang.String)
	 */
	@Override
	public TrainingCourseObj createNewCourse(TrainingCourseObj project) throws Exception {
		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		
		//GET ROOT FOLDER
		WorkspaceFolder rootFolder = TrainingCourseHLPathManager.getRootFolderForTrainingCourses(getThreadLocalRequest());

		//CREATE NEW COURSE FOLDER ON WORKSPACE
		WorkspaceFolder theFolder = rootFolder.createFolder(project.getWorkspaceFolderName(), project.getDescription());
		
		//UPDATING PROJECT INFO
		project.setWorkspaceFolderId(theFolder.getId());
		project.setWorkspaceFolderName(theFolder.getName());
		project.setOwnerLogin(pc.getUsername());
		project.setScope(pc.getCurrentScope());
		TrainingCourseDTO tcdto = getTrainingLibrary().storeNewCourse((TrainingCourseDTO) project, pc.getCurrentScope(), pc.getUsername());
		return ServerUtil.toTrainingCourse(tcdto, pc.getCurrentScope());
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#getOwnedTrainingCoursesForCurrentVRE()
	 */
	@Override
	public List<TrainingCourseObj> getOwnedTrainingCoursesForCurrentVRE() throws Exception {

		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		List<TrainingCourseObj> listC;
		try {
			logger.info("Getting Trainining Courses for owner: "+pc.getUsername(), " scope: "+pc.getCurrentScope());
			List<TrainingCourseDTO> listDTO = getTrainingLibrary().getOwnedCourses(pc.getUsername(), pc.getCurrentScope());
			
			if(listDTO==null) {
				logger.info("No Trainining Courses for owner: "+pc.getUsername(), " scope: "+pc.getCurrentScope()+" returning empty list");
				return new ArrayList<TrainingCourseObj>();
			}
			
			logger.info(listDTO.size() +" list of Trainining Courses for owner: "+pc.getUsername(), " scope: "+pc.getCurrentScope());
			
			
//			//TODO REMOVE JUST FOR TEST
//			System.out.println("THIS IS A TEST ***************************** \n\n\n");
//			getTrainingLibrary().getUserCourses(pc.getUsername(), pc.getCurrentScope());
//			System.out.println("END ***************************** \n\n\n");
//			
			listC = new ArrayList<>(listDTO.size());
			
			for (TrainingCourseDTO dto : listDTO) {
				listC.add(ServerUtil.toTrainingCourse(dto, pc.getCurrentScope()));
			}
			
			return listC;
		} catch (Exception e) {
			logger.error("An error occurred contacting the Training Courses DB", e);
			throw new Exception("Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#loadTrainingCourse(org.gcube.portlets.user.trainingcourse.shared.TrainingProject)
	 */
	@Override
	public TrainingCourseObj loadTrainingCourse(long trainingCourseId) throws Exception {

		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		try {
			
			TrainingCourseDTO dto = getTrainingLibrary().getTrainingCourse(trainingCourseId, pc.getCurrentScope());
			WorkspaceItem wsFolderRootProject = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(), dto.getWorkspaceFolderId());
			//UPDATING DB INFO UPDATED WS-SIDE
			if(wsFolderRootProject.isFolder() && (wsFolderRootProject instanceof WorkspaceSharedFolder)) {
				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) wsFolderRootProject;
				List<String> logins = wsFolder.getMembers();
				
				if(logger.isDebugEnabled()) {
					for (String member : logins) {
						logger.debug(member + " is a member of Training Id: "+trainingCourseId);
					}
				}
				
				String folderName = wsFolder.getName();
				dto.setSharedWith(logins);
				dto.setWorkspaceFolderName(folderName);
				dto = getTrainingLibrary().updateTrainingCourse(dto, pc.getCurrentScope());
			}
			
			return ServerUtil.toTrainingCourse(dto, pc.getCurrentScope());
			
		} catch (Exception e) {
			logger.error("An error occurred contacting the Training Courses DB", e);
			throw new Exception("Sorry, an error occurred contacting the Training Courses DB, try again later or contact the support");
		}
	}
	

	/**
	 * Creates the unit folder.
	 *
	 * @param project
	 *            the project
	 * @param unit
	 *            the unit
	 * @return the training unit DTO
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public TrainingUnitDTO createUnitFolder(TrainingCourseObj project, TrainingUnitDTO unit) throws Exception {

		// GET FOLDER
		WorkspaceItem wi = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(),project.getWorkspaceFolderId());

		if (!wi.isFolder()) {
			logger.error("It is not possible to create the unit, the parent folder id: "
					+ project.getWorkspaceFolderId() + " passed is an item");
			throw new Exception("It is not possible to create the unit, the parent folder id: "
					+ project.getWorkspaceFolderId() + " is wrong");
		}

		WorkspaceFolder theParentFolder = (WorkspaceFolder) wi;

		// CREATE NEW COURSE FOLDER INTO PARENT FOLDER
		WorkspaceFolder theFolder = theParentFolder.createFolder(unit.getWorkspaceFolderName(), unit.getDescription());

		// UPDATING UNIT INFO
		unit.setWorkspaceFolderId(theFolder.getId());
		unit.setWorkspaceFolderName(theFolder.getName());
		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		unit.setOwnerLogin(pc.getUsername());
		unit.setScope(pc.getCurrentScope());

		TrainingCourseDTO prj = loadTrainingCourse(project.getInternalId());
		unit.setTrainingProjectRef(prj);

		return getTrainingLibrary().storeNewUnit(project.getInternalId(), unit, pc.getCurrentScope(), pc.getUsername());
	}

	/**
	 * Gets the workspace item info.
	 *
	 * @param itemId the item id
	 * @return the workspace item info
	 * @throws Exception the exception
	 */
	@Override
	public WorkspaceItemInfo getWorkspaceItemInfo(String itemId) throws Exception {
		try {
			//GET WORKSPACE ITEM
			WorkspaceItem item = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(), itemId);
			WorkspaceItemInfo wsItem = WsUtil.toWorkspaceItemInfo(item);
			try{
				if(wsItem.isFolder()) {
					PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
					TrainingUnitDTO tu = getTrainingLibrary().getTrainingUnitFor(pc.getUsername(), itemId, pc.getCurrentScope(), pc.getUsername());
					wsItem.setUnit(tu);

				}
			}catch (Exception e) {
				//silent
			}
			
			return wsItem;
			
		} catch (Exception e) {
			logger.error("Error on getting info on workspace item: "+itemId, e);
			throw new Exception("Sorry, an error occurred getting the item info, try again later or contact the support");
		}
	}
	
	
	/**
	 * Count videos for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @return the int
	 * @throws Exception the exception
	 */
	@Override
	public int countVideosForTrainingUnit(long trainingUnitId) throws Exception {
		logger.debug("Getting number of Videos for unit id: "+trainingUnitId);
		int count = getTrainingLibrary().countVideosForTrainingUnit(trainingUnitId, PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest()));
		
		logger.debug("Counted: "+count+" video/s for unit id: "+trainingUnitId);
		return count;
	}
	
	
	/**
	 * Count questionnaires for training unit.
	 *
	 * @param trainingUnitId the training unit id
	 * @return the int
	 * @throws Exception the exception
	 */
	@Override
	public int countQuestionnairesForTrainingUnit(long trainingUnitId) throws Exception {
		logger.debug("Getting number of Questionnaires for unit id: "+trainingUnitId);
		int count = getTrainingLibrary().countQuestionnairesForTrainingUnit(trainingUnitId, PortalContext.getConfiguration().getCurrentScope(getThreadLocalRequest()));
		
		logger.debug("Counted: "+count+" questionnaires/s for unit id: "+trainingUnitId);
		return count;
	}
	
	
	/**
	 * Delete workspace item.
	 *
	 * @param itemId the item id
	 * @throws Exception the exception
	 */
	@Override
	public void deleteWorkspaceItem(String itemId) throws Exception {
		try {
			WorkspaceItem item = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(), itemId);
			item.remove();
			try{
				if(item.isFolder()) {
					PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
					int deleted = getTrainingLibrary().deleteTrainingUnit(itemId,pc.getCurrentScope(),pc.getUsername());
					logger.info("Deleted "+deleted+" training unit");
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		} catch (Exception e) {
			logger.error("Error on removoving the workspace item: "+itemId, e);
			throw new Exception("Sorry, an error occurred deleting the item, try again later or contact the support. Details: +"+e.getMessage());
		}
	}
	
	
	/**
	 * Share with users.
	 *
	 * @param project the project
	 * @param listLogins the list logins
	 * @return the training project
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#shareWithUsers(org.gcube.portlets.user.trainingcourse.shared.TrainingProject, java.util.List)
	 */
	@Override
	public TrainingCourseObj shareWithUsers(TrainingCourseObj project, List<String> listLogins) throws Exception {
		if(listLogins==null || listLogins.size()==0 || project==null || project.getInternalId()<=0)
			return null;
		
		try {
			WorkspaceItem wsFolderRootProject = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(), project.getWorkspaceFolderId());
			List<String> newSharedLogins;
			//IT IS A FOLDER
			if(wsFolderRootProject.isFolder()) {
				WorkspaceFolder theBaseFolder = (WorkspaceFolder) wsFolderRootProject;
				WorkspaceSharedFolder sharedFolder = null;
				//IT IS NOT SHARED
				if(!theBaseFolder.isShared()) {
					logger.info("The folder: "+theBaseFolder.getName()+ " with id: "+theBaseFolder.getId()+" is not shared, sharing it adding logins: "+listLogins);
					Workspace workspace = WsUtil.getWorkspace(getThreadLocalRequest());
					//SHARING IT
					sharedFolder = workspace.shareFolder(listLogins, theBaseFolder.getId());
					sharedFolder.setACL(listLogins, ACLType.READ_ONLY);
					newSharedLogins = new ArrayList<>(listLogins);
				}else {
					//IT IS ALREADY SHARED
					sharedFolder = (WorkspaceSharedFolder) theBaseFolder;
					logger.info("The folder: "+theBaseFolder.getName()+ " with id: "+theBaseFolder.getId()+" is already shared, adding logins: "+listLogins);
					
					List<String> alreadySharedWith = sharedFolder.getMembers();
					
					newSharedLogins = new ArrayList<>(alreadySharedWith);
					//MERGING TWO LISTS WITHOUT DUPLICATED
					if(listLogins!=null) {
						newSharedLogins.removeAll(listLogins);
						newSharedLogins.addAll(listLogins);
					}
					
					if(logger.isDebugEnabled()) {
						for (String newLogin : newSharedLogins) {
							logger.debug("Adding to share the username: "+newLogin);
						}
					}
						
					sharedFolder.share(newSharedLogins);
					sharedFolder.setACL(newSharedLogins, ACLType.READ_ONLY);
				}
				
				PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
				TrainingCourseDTO traningPrj = getTrainingLibrary().getTrainingCourse(project.getInternalId(), pc.getCurrentScope());
				logger.debug("Shared folder: "+sharedFolder.getId() + " name: "+sharedFolder.getName() + " members: "+sharedFolder.getMembers() + " ACL: "+sharedFolder.getACLUser());
				

				//ADDING LIST LOGINS TO BEAN AND UPDATING DB
				//String sharedWith = PersistenceUtil.toSharedWith(listLogins);
				traningPrj.setSharedWith(newSharedLogins);
				traningPrj.setWorkspaceFolderId(sharedFolder.getId());
				traningPrj.setWorkspaceFolderName(sharedFolder.getName());
				TrainingCourseDTO fp = getTrainingLibrary().updateTrainingCourse(traningPrj,pc.getCurrentScope());
				logger.info("Added sharedWith to: "+traningPrj);
//				return (TrainingProject) fp;
				return ServerUtil.toTrainingCourse(traningPrj, pc.getCurrentScope());
				
			}
			
			logger.warn("Project passed is not a folder, returning null");
			return null;
			
		}catch (Exception e) {
			logger.error("Error on sharing project to list logins: "+listLogins, e);
			throw new Exception("Sorry, an error occurred during sharing project to users, try again later or contact the support");
		}
		
		
	}
	
	
	/**
	 * Share with current scope.
	 *
	 * @param project the project
	 * @return the training course obj
	 * @throws Exception the exception
	 */
	@Override
	public TrainingCourseObj shareWithCurrentScope(TrainingCourseObj project) throws Exception {
		if(project==null || project.getInternalId()<=0)
			return null;
		
		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		
		List<String> listLogins = new ArrayList<>(1);
		String group = ServerUtil.scopeToHLGroup(pc.getCurrentScope());
		listLogins.add(group);
		return shareWithUsers(project, listLogins);
		
	}
	
	
	
	/**
	 * Delete training project.
	 *
	 * @param project the project
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#deleteTrainingProject(org.gcube.portlets.user.trainingcourse.shared.TrainingProject)
	 */
	@Override
	public boolean deleteTrainingProject(TrainingCourseObj project) throws Exception{
		
		if(project==null || project.getInternalId()<=0)
			throw new Exception("Invalid project to delete it");
		
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			int deleted = getTrainingLibrary().deleteTrainingCourse(project.getInternalId(), pc.getCurrentScope(), pc.getUsername());
	
			if(deleted==1) {
				logger.info("Deleted Training Project with id: "+project.getInternalId());
				
				try {
					//DELETEING THE FOLDER COURSE IN THE WORKSPACE
					WorkspaceItem wsFolderRootProject = TrainingCourseHLPathManager.getWorkspaceItemForId(getThreadLocalRequest(), project.getWorkspaceFolderId());
					wsFolderRootProject.remove();
				}catch (Exception e) {
					logger.warn("Error on removing workspace folder: "+ project.getWorkspaceFolderId(), e);
				}
				
				return true;
			}
			
			return false;
			
			
		}catch (Exception e) {
			logger.error("Error on deleting project: "+project, e);
			throw new Exception("Sorry, an error occurred during deleting the course, try again later or contact the support");
		}
		
	}
	
	
	/**
	 * Change status.
	 *
	 * @param project the project
	 * @param isActive the is active
	 * @return the training project
	 * @throws Exception the exception
	 */
	@Override
	public TrainingCourseObj changeStatus(TrainingCourseObj project, boolean isActive) throws Exception {
		if(project==null || project.getInternalId()<=0)
			throw new Exception("Invalid project to chanage the status");
		
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			TrainingCourseDTO dto = getTrainingLibrary().changeStatus(project.getInternalId(), isActive, pc.getCurrentScope(), pc.getUsername());
			return ServerUtil.toTrainingCourse(dto, pc.getCurrentScope());
			
		}catch (Exception e) {
			logger.error("Error on changing status of project: "+project, e);
			throw new Exception("Sorry, an error occurred changing status of the course, try again later or contact the support");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#addQuestionnaireToUnit(org.gcube.portal.trainingmodule.shared.TrainingUnitDTO, org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO)
	 */
	@Override
	public TrainingUnitQuestionnaireDTO addQuestionnaireToUnit(TrainingUnitDTO unit,
			TrainingUnitQuestionnaireDTO questionnaire) throws Exception {
		if(unit==null || questionnaire==null)
			throw new Exception("Invalid add to Questionnaire");
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			TrainingUnitQuestionnaireDTO que = getTrainingLibrary().addQuestionnaireToTrainingUnit(unit.getInternalId(), questionnaire, pc.getCurrentScope(), pc.getUsername());
			try {
				getSurveyInvitationLink(false);
			}catch (Exception e) {
				logger.warn("Survey invitation link ex: ",e);
			}
			return que;
		}catch (Exception e) {
			logger.error("Error on adding Questionnaire to Unit: "+unit.getTitle(), e);
			throw new Exception("Sorry, an error occurred adding Questionnaire to Unit: "+unit.getTitle()+", try again later or contact the support");
		}
	}
	

	
	//NEEDED TO MASSI
	private String getSurveyInvitationLink(boolean isAnonymous) {
		List<Portlet> allPortlets = PortletLocalServiceUtil.getPortlets();
		String takeSurveyPluginID = null;
		for (Portlet portlet : allPortlets) {
			if (portlet.getPluginId().startsWith(TakeSurveyPortlet_PLUGIN_ID_PREFIX)) 
				takeSurveyPluginID = portlet.getPluginId();					
		}
		if (takeSurveyPluginID == null)
			return null;
		
		PortalContext pContext = PortalContext.getConfiguration();
		GCubeUser user = pContext.getCurrentUser(getThreadLocalRequest());
		long groupId = pContext.getCurrentGroupId(getThreadLocalRequest());
		boolean hiddenPage = true;
		String pageName = "Take the survey";
		String layoutName = pageName;
		String friendlyURL= "/"+pageName.replaceAll("\\s", "-").toLowerCase();
		ServiceContext ctx = new ServiceContext();
		
		try {
			Layout created = LayoutLocalServiceUtil.addLayout(user.getUserId(), groupId, !isAnonymous, 0,  pageName, layoutName, 
					"none", "portlet", hiddenPage, friendlyURL, ctx);
			
			String typeSettings = "layout-template-id=1_column\n";
			typeSettings += "column-"+1+"="+ takeSurveyPluginID + ",\n";
			LayoutLocalServiceUtil.updateLayout(created.getGroupId(), created.isPrivateLayout(), created.getLayoutId(), typeSettings);
			
		} catch (PortalException | SystemException e) {
			logger.warn("Exception during layout creation, it could be a non problem if the page exist already:" + e.getMessage());
		}
		logger.debug("getSurveyInvitationLink OK for " + takeSurveyPluginID);
		String prefix = isAnonymous ? "/web" : "/group";
		String groupName = PortalContext.getConfiguration().getCurrentGroupName(getThreadLocalRequest()).toLowerCase();
		return new StringBuilder(prefix).append("/").append(groupName).append(friendlyURL).toString();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#getListOfQuestionnaireForUnit(long)
	 */
	@Override
	public List<TrainingUnitQuestionnaireDTO> getListOfQuestionnaireForUnit(long unitInternalId) throws Exception {
		if(unitInternalId<=0)
			throw new Exception("Invalid Unit id to search the list of Questionnaire");
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			return getTrainingLibrary().getListOfQuestionnaireForTrainingUnit(unitInternalId, pc.getCurrentScope());
		}catch (Exception e) {
			logger.error("Error on getting Questionnaire for Unit: "+unitInternalId, e);
			throw new Exception("Sorry, an error occurred loading list of Questionnaire for Unit id: "+unitInternalId+", try again later or contact the support");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#addVideoToUnit(org.gcube.portal.trainingmodule.shared.TrainingUnitDTO, org.gcube.portal.trainingmodule.shared.TrainingVideoDTO)
	 */
	@Override
	public TrainingVideoDTO addVideoToUnit(TrainingUnitDTO unit, TrainingVideoDTO videoDTO) throws Exception {
		if(unit==null || videoDTO==null)
			throw new Exception("Error on adding to Video: invalid parameters");
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			return getTrainingLibrary().addVideoToTrainingUnit(unit.getInternalId(), videoDTO, pc.getCurrentScope(), pc.getUsername());
		}catch (Exception e) {
			logger.error("Error on adding Video to Unit: "+unit.getTitle(), e);
			throw new Exception("Sorry, an error occurred adding Video to Unit: "+unit.getTitle()+", try again later or contact the support");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#getListOfVideoForUnit(long)
	 */
	@Override
	public List<TrainingVideoDTO> getListOfVideoForUnit(long unitInternalId) throws Exception {
		if(unitInternalId<=0)
			throw new Exception("Invalid Unit id to search the list of Video");
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			return getTrainingLibrary().getListOfVideoForTrainingUnit(unitInternalId, pc.getCurrentScope());
		}catch (Exception e) {
			logger.error("Error on getting Video for Unit: "+unitInternalId, e);
			throw new Exception("Sorry, an error occurred loading list of Video for Unit id: "+unitInternalId+", try again later or contact the support");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService#updateCourse(org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj)
	 */
	@Override
	public TrainingCourseObj updateCourse(TrainingCourseObj course) throws Exception {
		if(course==null || course.getInternalId()<-1)
			throw new Exception("Invalid id to updated the Course");
		try {
			PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
			TrainingCourseDTO dto = getTrainingLibrary().updateTrainingCourse(course, pc.getCurrentScope());
			return ServerUtil.toTrainingCourse(dto, pc.getCurrentScope());
		}catch (Exception e) {
			logger.error("Error on updating the course with id: "+course.getInternalId(), e);
			throw new Exception("Sorry, an error occurred updating info for the Course: "+course.getTitle()+", try again later or contact the support");
		}
		
	}
	
	

	/**
	 * Gets the query string to show user progress.
	 *
	 * @param course the course
	 * @param userNameToShowProgress the user name to show progress
	 * @return the query string to show user progress
	 * @throws Exception the exception
	 */
	@Override
	public String getQueryStringToShowUserProgress(TrainingCourseObj course, String userNameToShowProgress) throws Exception{
		if(course==null || course.getInternalId()<-1)
			throw new Exception("Invalid course id to encrypt it");
		
		if(userNameToShowProgress==null)
			throw new Exception("Invalid username to encrypt it");
		try {
			String encriptedCourseId = StringEncrypter.getEncrypter().encrypt(course.getInternalId()+"");
			String encriptedUserName = StringEncrypter.getEncrypter().encrypt(userNameToShowProgress);
			logger.info("Encrypted course Id: "+encriptedCourseId +", Encrypted username: "+encriptedUserName);
			String encodedC = StringUtil.base64EncodeStringURLSafe(encriptedCourseId);
			String encodedU = StringUtil.base64EncodeStringURLSafe(encriptedUserName);
			logger.info("Encoded in Base 64 [Username: "+encodedU +", Course Id: "+encodedU+"]");
			return COURSEID_PARAM+"="+URLEncoder.encode(encodedC, UTF_8) +"&"+ USER_PARAM+"="+URLEncoder.encode(encodedU, UTF_8);
		}catch (Exception e) {
			logger.error("Error on encrypting/endoding: ", e);
			throw new Exception("Sorry, an error occurred encrypting parameters, try again or contact the support");
		}
		
	}
	

	/**
	 * Delete questionnaire for id.
	 *
	 * @param questionnaireId the questionnaire id
	 * @return the int
	 * @throws Exception the exception
	 */
	@Override
	public int deleteQuestionnaireForId(long questionnaireId) throws Exception {

		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());
		
		return getTrainingLibrary().deleteQuestionnaireForId(questionnaireId, pc.getCurrentScope());

	}
	

	/**
	 * Delete video for id.
	 *
	 * @param videoId the video id
	 * @return the int
	 * @throws Exception the exception
	 */
	@Override
	public int deleteVideoForId(long videoId) throws Exception {
		
		PortalContextInfo pc = WsUtil.getPortalContext(getThreadLocalRequest());

		return getTrainingLibrary().deleteVideoForId(videoId, pc.getCurrentScope());
	}
	
	
	


}
