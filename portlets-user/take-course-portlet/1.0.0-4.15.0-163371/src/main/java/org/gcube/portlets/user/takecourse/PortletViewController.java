/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package org.gcube.portlets.user.takecourse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.trainingmodule.TrainingModuleManager;
import org.gcube.portal.trainingmodule.shared.ItemType;
import org.gcube.portal.trainingmodule.shared.TrainingCourseDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.takecourse.dto.FileItemsWrapper;
import org.gcube.portlets.user.takecourse.dto.ImageType;
import org.gcube.portlets.user.takecourse.dto.TMFileItem;
import org.gcube.portlets.user.takecourse.dto.TrainerDTO;
import org.gcube.portlets.user.takecourse.dto.TrainingCourseWithUnits;
import org.gcube.portlets.user.takecourse.questionnaire.QuestionnaireDTO;
import org.gcube.portlets.user.takecourse.questionnaire.QuestionnaireDatabaseManager;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

/**
 * 
 * @author M. Assante, CNR-ISTI
 *
 */
@Controller
@RequestMapping("VIEW")
public class PortletViewController {
	private static Log _log = LogFactoryUtil.getLog(PortletViewController.class);

	private static String PAGE_VIEW = "take-course-portlet/view";	
	private static String PAGE_VIEW_SINGLE = "take-course-portlet/view-single-course";	
	//this pages is needed when the course is shared with a group and the instructor needs to see user by user
	private static String PAGE_VIEW_USER_TABLE = "take-course-portlet/view-user-table";	
	public static final String USER_PROFILE_OID = "userIdentificationParameter";

	private static String COURSE_ID_PARAM = "c";
	private static String USER_OR_GROUP_NAME_PARAM = "u";


	/** First method called from view.jsp
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RenderMapping
	public String handleRenderRequest(RenderRequest request,RenderResponse response, Model model) {
		HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request)); 
		String encryptedcourseId = httpReq.getParameter(COURSE_ID_PARAM);
		String encryptedusernameOrGroupname = httpReq.getParameter(USER_OR_GROUP_NAME_PARAM);
		String courseId = null;
		String usernameOrGroupname = null;
		String context = Utils.getCurrentContext(request);
		ScopeProvider.instance.set(context);
		if (ParamUtil.getString(request, "userId") != null &&  ParamUtil.getLong(request, "userId") > 0) { //here the instructor has clicked on the table		
			long theUserId = ParamUtil.getLong(request, "userId");
			String theCourseId = ParamUtil.getString(request, "courseId");
			request.getPortletSession().setAttribute(COURSE_ID_PARAM, null);
			request.getPortletSession().setAttribute(USER_OR_GROUP_NAME_PARAM, null);
			return goToViewSingleCoursePage(request, response, model, theUserId, theCourseId);			
		} 
		//the instructor is seeing the student progress
		else if ((encryptedcourseId != null && encryptedusernameOrGroupname != null)|| (
				request.getPortletSession().getAttribute(COURSE_ID_PARAM) != null &&
				request.getPortletSession().getAttribute(USER_OR_GROUP_NAME_PARAM) != null)) {	

			//put the courseId and User/groupId in session (needed for sorting and pagination of the table)
			if (encryptedcourseId != null)
				request.getPortletSession().setAttribute(COURSE_ID_PARAM, encryptedcourseId);
			if (encryptedusernameOrGroupname != null)
				request.getPortletSession().setAttribute(USER_OR_GROUP_NAME_PARAM, encryptedusernameOrGroupname);
			courseId = decodeAndDecryptParameter((String)request.getPortletSession().getAttribute(COURSE_ID_PARAM));
			usernameOrGroupname = decodeAndDecryptParameter((String)request.getPortletSession().getAttribute(USER_OR_GROUP_NAME_PARAM));
			if (isGroup(usernameOrGroupname)) { //if is a group we show the table with the users
				long groupId = -1;
				try {
					groupId = PortalUtil.getScopeGroupId(request);
				} catch (Exception e) {
					_log.error("Could not read groupId from request: ", e);
				}
				request.setAttribute("courseId", courseId);	
				request.setAttribute("groupId", groupId);
				return PAGE_VIEW_USER_TABLE;
			}
			else {  //otherwise we show the single user
				request.getPortletSession().setAttribute(COURSE_ID_PARAM, null);
				request.getPortletSession().setAttribute(USER_OR_GROUP_NAME_PARAM, null);
				return goToViewSingleCoursePage(request, response, model, Utils.getCurrentUserByUsername(usernameOrGroupname).getUserId(), courseId);	
			}
		} 
		else { //regular mode the student is seeing the course
			fetchCourses(request, model);
			return PAGE_VIEW;
		}
	}

	private String goToViewSingleCoursePage(RenderRequest request, RenderResponse response, Model model, long userId, String courseId) {
		request.setAttribute("userId", userId);
		request.setAttribute("courseId", courseId);		
		request.setAttribute("jspValue", PAGE_VIEW_SINGLE); 
		return afterCourseSelectionRenderMethod(request, response, model);
	}
	/**
	 * called onRender  when back to courses is selected
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RenderMapping(params = "action=renderjsp")
	public String renderjsp(RenderRequest request, RenderResponse response, Model model){
		fetchCourses(request, model);		
		return PAGE_VIEW;
	}

	/**
	 * called when a course is selected 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RenderMapping(params = "action=renderAfterAction")
	public String afterCourseSelectionRenderMethod(RenderRequest request,RenderResponse response, Model model) {
		String jspValue = (String) request.getAttribute("jspValue");
		if ( request.getAttribute("courseId") != null) {
			long courseId = Long.parseLong((String) request.getAttribute("courseId"));
			request.setAttribute("courseId", courseId);
			TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
			TrainingCourseWithUnits toShow = null;
			String context =  Utils.getCurrentContext(request); 
			String username = "";
			if ( request.getAttribute("userId") != null) {
				long userId = (long) request.getAttribute("userId");
				username = Utils.getCurrentUser(userId).getUsername();				
				request.setAttribute("userId", userId);
			}
			else
				username = Utils.getCurrentUser(request).getUsername();
			try {			
				toShow = getCourseWithUnits(request, trainingManager, courseId, context, username);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Returned " + toShow);
			model.addAttribute("course", toShow);		
		}
		return jspValue;
	}

	@ActionMapping(params = "action=courseSubmit")
	public void courseSubmit(ActionRequest request, ActionResponse response) {
		String courseId = ParamUtil.getString(request, "course-id", "");
		response.setRenderParameter("action", "renderAfterAction"); //This parameter decide which method is called after completion of this method
		request.setAttribute("jspValue", PAGE_VIEW_SINGLE); //Attribute use for which jsp is render
		request.setAttribute("courseId", courseId);
	}
	/**
	 * 
	 * @param request
	 * @param model
	 */
	private void fetchCourses(RenderRequest request, Model model) {
		GCubeUser currentUser = Utils.getCurrentUser(request);
		String context = Utils.getCurrentContext(request);
		TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
		List<TrainingCourseDTO> list = null;
		try {
			list = trainingManager.getUserCourses(currentUser.getUsername(), context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<TrainingCourseDTO>  listActivatedCourses = new ArrayList<>();
		for (TrainingCourseDTO tc : list) {
			_log.debug("found course: " + tc); 
			//the non active courses are shown to Insturctors only
			if (tc.isCourseActive() || tc.getOwnerLogin().compareTo(currentUser.getUsername()) == 0)
				listActivatedCourses.add(tc);
		}
		model.addAttribute("list", listActivatedCourses);
		model.addAttribute("membersPage", GCubePortalConstants.GROUP_MEMBERS_FRIENDLY_URL.substring(1));
	}
	/**
	 * 
	 * @param tm
	 * @param courseId
	 * @param context
	 * @param username
	 * @return
	 * @throws Exception
	 */
	private TrainingCourseWithUnits getCourseWithUnits(RenderRequest request, TrainingModuleManager tm, long courseId, String context, String username) throws Exception {
		TrainingCourseDTO tc = tm.getTrainingCourse(courseId, context);
		//this is need for users added to the VRE after the course was created
		checkCourseIsSharedWithUser(request, tc, context, username);

		TrainingCourseWithUnits toReturn = new TrainingCourseWithUnits();
		toReturn.setId(tc.getInternalId());
		toReturn.setCommitment(tc.getCommitment());
		toReturn.setCourseActive(tc.isCourseActive());
		toReturn.setCreatedBy(tc.getCreatedBy());
		toReturn.setDescription(tc.getDescription());
		toReturn.setLanguages(tc.getLanguages());
		toReturn.setTitle(tc.getTitle());
		toReturn.setWorkspaceFolderId(tc.getWorkspaceFolderId());
		toReturn.setWorkspaceFolderName(tc.getWorkspaceFolderName());
		GCubeUser trainer = null;
		TrainerDTO toSet = null;
		try {
			trainer = new LiferayUserManager().getUserByUsername(tc.getOwnerLogin());
			toSet = new TrainerDTO(
					trainer.getUsername(), 
					trainer.getFullname(), 
					trainer.getJobTitle(),
					trainer.getUserAvatarURL(), 
					trainer.getEmail(), 
					getUserProfileLink(trainer.getUsername()));
		} catch (UserRetrievalFault ex) {
			_log.warn("User could not be found in the LR Database: " + tc.getOwnerLogin());
			toSet = new TrainerDTO(
					tc.getOwnerLogin(), 
					tc.getOwnerLogin(), 
					"",
					"", 
					"", 
					"");
		}

		toReturn.setTrainer(toSet);
		List<TrainingUnitDTO> units = tm.getListOfTrainingUnitFor(courseId, context);
		toReturn.setUnits(units);
		return toReturn;
	}

	private boolean checkCourseIsSharedWithUser(RenderRequest request, TrainingCourseDTO tc, String context, String studentUsername) {
		_log.debug("checkCourseIsSharedWithUser " + studentUsername);
		String groupName = Utils.scopeToHLGroup(context);
		if (tc.getSharedWith() != null && tc.getSharedWith().contains(groupName)) { //then this course is shared with the VRE Group
			try {	
				_log.debug("*** this course is shared with the VRE Group " + groupName + " checking if user belongs to it");
				String instructorUsername = tc.getOwnerLogin();
				String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, instructorUsername);
				SecurityTokenProvider.instance.set(authorizationToken);
				ScopeProvider.instance.set(context);				
				Workspace ws = null;
				ws = HomeLibrary.getUserWorkspace(instructorUsername);
				WorkspaceSharedFolder toShare = (WorkspaceSharedFolder) ws.getItem(tc.getWorkspaceFolderId());
				List<String> groupMembersHL = toShare.getUsers();
				if (!groupMembersHL.contains(studentUsername)) {
					_log.warn("*** this user DOES NOT BELONG to the group trying share ... " + studentUsername);
					toShare.share(Arrays.asList(new String[]{studentUsername}));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}	
		}
		return true;
	}

	@ResourceMapping(value="getUnitQuestionnaires")
	public void getUnitQuestionnaires(ResourceRequest request, ResourceResponse response) throws Exception  {
		long unitId = ParamUtil.getLong(request, "unitId");
		long userId = ParamUtil.getLong(request, "userId");
		long groupId = ParamUtil.getLong(request, "groupId");

		String context =  Utils.getCurrentContext(groupId); 
		GCubeUser user =  Utils.getCurrentUser(userId);

		JSONArray fileArray = JSONFactoryUtil.createJSONArray();
		TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
		List<TrainingUnitQuestionnaireDTO> questionnaires = trainingManager.getListOfQuestionnaireForTrainingUnit(unitId, context);
		QuestionnaireDatabaseManager qMan = QuestionnaireDatabaseManager.getInstance(context);

		for (TrainingUnitQuestionnaireDTO item :questionnaires) {
			System.out.println("Questonaire id = " + item.getQuestionnaireId());
			QuestionnaireDTO q = qMan.getQuestionnaireURLForUser(user.getUserId(), user.getEmail(), context, item.getQuestionnaireId());
			System.out.println("QuestionnaireDTO = " + q);
			JSONObject fileObject = JSONFactoryUtil.createJSONObject();
			fileObject.put("id", q.getId());
			fileObject.put("name", q.getName());
			fileObject.put("url", q.getUrl());
			fileObject.put("answered", q.isAnswered());
			fileArray.put(fileObject);
		}
		response.getWriter().println(fileArray);	
	}

	@ResourceMapping(value="getUnitVideos")
	public void getUnitVideos(ResourceRequest request, ResourceResponse response) throws Exception  {
		long unitId = ParamUtil.getLong(request, "unitId");
		long groupId = ParamUtil.getLong(request, "groupId");

		String context =  Utils.getCurrentContext(groupId); 
		//GCubeUser user =  Utils.getCurrentUser(userId);

		TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
		List<TrainingVideoDTO> videos = trainingManager. getListOfVideoForTrainingUnit(unitId, context);
		JSONArray fileArray = JSONFactoryUtil.createJSONArray();
		for (TrainingVideoDTO item :videos) {
			JSONObject fileObject = JSONFactoryUtil.createJSONObject();
			fileObject.put("id", item.getInternalId());
			fileObject.put("name", item.getTitle());
			fileObject.put("description", item.getDescription());
			fileObject.put("url", item.getUrl());
			fileObject.put("seen", false); //TODO: francesco to implement it.
			fileArray.put(fileObject);
		}
		response.getWriter().println(fileArray);	
	}


	@ResourceMapping(value="getUnitContent")
	public void fetchFolderContent(ResourceRequest request, ResourceResponse response) throws Exception  {
		String folderId = ParamUtil.getString(request, "folderId");

		long userId = ParamUtil.getLong(request, "userId");
		long groupId = ParamUtil.getLong(request, "groupId");

		String context =  Utils.getCurrentContext(groupId); 
		String username =  Utils.getCurrentUser(userId).getUsername();

		FileItemsWrapper fileWrapper = getAllFiles(folderId, username, context);

		JSONArray fileArray = JSONFactoryUtil.createJSONArray();
		for (TMFileItem item : fileWrapper.getItems()) {
			JSONObject fileObject = JSONFactoryUtil.createJSONObject();
			fileObject.put("id", item.getWorkspaceItemId());
			fileObject.put("name", item.getFilename());
			fileObject.put("uri", item.getFileDownLoadURL());
			fileObject.put("read", item.isRead());
			StringBuilder sb = new StringBuilder(request.getContextPath()).append("/images/").append(Utils.getIconImage(item.getType().toString()));
			fileObject.put("image", sb.toString());
			fileArray.put(fileObject);

		}
		response.getWriter().println(fileArray);	
	}

	@ResourceMapping(value="setFileRead")
	public void setRead(ResourceRequest request, ResourceResponse response) throws Exception {
		TrainingModuleManager trainingManager = TrainingModuleManager.getInstance();
		long unitId = ParamUtil.getLong(request, "unitId");
		String folderId = ParamUtil.getString(request, "folderId");
		String workspaceItemId = ParamUtil.getString(request, "workspaceItemId");
		long userId = ParamUtil.getLong(request, "userId");
		long groupId = ParamUtil.getLong(request, "groupId");

		String context =  Utils.getCurrentContext(groupId); 
		String username =  Utils.getCurrentUser(userId).getUsername();
		trainingManager.setProgressForUnit(unitId, context, username, workspaceItemId, ItemType.FILE, true);
		String percentageRead = Utils.setReadAndgetPercentageReadFolder(folderId, workspaceItemId, username, context);
		response.getWriter().println(percentageRead);
	}

	public FileItemsWrapper getAllFiles(String folderId, String username, String context) {
		FileItemsWrapper toReturn = null;
		ScopeProvider.instance.set(context);
		String authorizationToken = PortalContext.getConfiguration().getCurrentUserToken(context, username);
		SecurityTokenProvider.instance.set(authorizationToken);

		_log.debug("getAllFiles: token=" + authorizationToken);

		//		Client client = ClientBuilder.newClient();
		//		WebTarget webTarget = client.target("http://workspace-repository1-d.d4science.org/homelibrary-fs-webapp/fs/list/byPath?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");
		//		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		//		List<? extends Item> r =  invocationBuilder.get(ItemList.class).getItemlist();


		ArrayList<TMFileItem> fileItems = new  ArrayList<>();
		Workspace ws = null;
		try {
			ws = HomeLibrary.getUserWorkspace(username);
			String folderName = "";
			List<WorkspaceItem> items = null;

			WorkspaceFolder sharedFolder = (WorkspaceFolder) ws.getItem(folderId);
			folderName = sharedFolder.getName();
			items = sharedFolder.getChildren();
			_log.debug("Unit Folder Name=" + folderName);

			for (WorkspaceItem item : items) {
				String[] splits =  item.getName().split("\\.");
				String extension = "";
				if (splits.length > 0) {
					extension = splits[splits.length-1];
				}

				List<AccountingEntry> entries = item.getAccounting();
				boolean read = false;
				for (AccountingEntry entry : entries) {
					if (entry.getEntryType() == AccountingEntryType.READ && entry.getUser().compareTo(username)==0) {
						read = true;
						break;
					}
				}				
				fileItems.add(
						new TMFileItem(
								item.getId(), 
								item.getName(), 
								item.getOwner().getPortalLogin(), 
								"", 
								item.getLastModificationTime().getTime(), 
								getIconImageType(extension), 
								item.getPublicLink(false),
								read));
			}
			toReturn = new FileItemsWrapper(folderName, folderId, fileItems);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private String getUserProfileLink(String username) {
		return "profile?"+ new String(Base64.getEncoder().encode(USER_PROFILE_OID.getBytes()))+"="+new String(Base64.getEncoder().encode(username.getBytes()));
	}

	private ImageType getIconImageType(String extension) {
		if (extension == null || extension.compareTo("") == 0)
			return ImageType.NONE;

		switch (extension) {
		case "doc":
		case "docx":
		case "rtf":
			return ImageType.DOC;
		case "xls":
		case "xlsx":
			return ImageType.XLS;
		case "ppt":
		case "pptx":
			return ImageType.PPT;
		case "pdf":
			return ImageType.PDF;
		case "jpg":
		case "jpeg":
		case "gif":
		case "bmp":
		case "png":
		case "tif":
		case "tiff":
			return ImageType.IMAGE;
		case "avi":
		case "mp4":
		case "mpeg":
			return ImageType.MOVIE;
		case "html":
		case "htm":
		case "jsp":
			return ImageType.HTML;
		case "rar":
			return ImageType.RAR;
		case "zip":
		case "tar":
		case "tar.gz":
			return ImageType.ZIP;
		default:
			return ImageType.NONE;
		}
	}
	/**
	 * 
	 * @param usernameOrGroupname
	 * @return
	 */
	private boolean isGroup(String usernameOrGroupname) {
		String infraname = PortalContext.getConfiguration().getInfrastructureName();
		return usernameOrGroupname.startsWith(infraname);	
	}
	/**
	 * when an instructor see the progresses of a student the parameter are encoded and encrypted
	 * @param encodedAndEncryptedparam
	 * @return the value of the paramenter
	 */
	private String decodeAndDecryptParameter(String encodedAndEncryptedparam) {
		byte[] cAsBytes = Base64.getDecoder().decode(encodedAndEncryptedparam);
		String decodedBase64EncryptedParam = new String(cAsBytes);
		return decryptParameter(decodedBase64EncryptedParam);
	}
	/**
	 * 
	 * @param encodedParam
	 * @return
	 */
	private String decryptParameter(String encodedParam) {
		_log.debug("Decrypting parameter ...");
		try {
			return org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encodedParam);
		} catch (Exception e) {
			_log.error("Decrypting parameter error...", e);
			return null;
		}
	}
}