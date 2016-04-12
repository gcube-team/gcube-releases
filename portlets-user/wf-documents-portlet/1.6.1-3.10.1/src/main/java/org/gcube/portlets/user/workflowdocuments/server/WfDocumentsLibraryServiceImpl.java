package org.gcube.portlets.user.workflowdocuments.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.wfdocslibrary.client.WfDocsLibrary;
import org.gcube.portlets.admin.wfdocslibrary.server.db.MyDerbyStore;
import org.gcube.portlets.admin.wfdocslibrary.server.db.Store;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardActionWithDest;
import org.gcube.portlets.admin.wfdocslibrary.shared.LogAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserInfo;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraphDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.user.workflowdocuments.client.WfDocumentsLibraryService;
import org.gcube.portlets.user.workflowdocuments.shared.LockInfo;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Lock;
import com.liferay.portal.model.Permission;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LockLocalServiceUtil;
import com.liferay.portal.service.PermissionLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WfDocumentsLibraryServiceImpl extends RemoteServiceServlet implements WfDocumentsLibraryService {

	private static final Logger _log = LoggerFactory.getLogger(WfDocumentsLibraryServiceImpl.class);



	public static int ORGANIZATION_ROLE_TYPE = 3;
	/**
	 * 
	 */
	public static final String LIFERAY_COMPANY_WEB_ID = "liferay.com";
	/**
	 * the WF DB Store
	 */
	private Store store;
	/**
	 * object serializer
	 */
	private XStream xstream;
	private boolean isTesting = false;
	/**
	 * init method
	 */
	public void init() {
		store = new MyDerbyStore();
		xstream = new XStream(new DomDriver());
	}
	/**
	 * the current D4SSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user = "federico.defaveri";
			this.getThreadLocalRequest().getSession().setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec");
			_log.warn("ASL session instanciated with " + user);
			isTesting = true;
		}
		//Logger.debug("\n\nsession ID= *" + sessionID + "*  user= *" + user + "*" );
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	@Override
	public ArrayList<WorkflowDocument> getUserWfDocuments() {
		/**
		 * the status has been changed from LR api 5.2.3 to LR6 from 1 to -1, without documenting it
		 */
		int status = -1;
		ArrayList<WorkflowDocument> toReturn = new ArrayList<WorkflowDocument>();
		ASLSession session = getASLSession();
		if (isTesting) {
			toReturn.add(new WorkflowDocument("11", "pippo", "START", "Desc", "100", "EDITOR", "Created", new Date(System.currentTimeMillis()-10000), new Date(System.currentTimeMillis()-10000), true, true, true, true, true, true, true, false, "ciccio", "10 Mins", true));
			toReturn.add(new WorkflowDocument("22", "ciccio", "EDIT", "Desc", "1001", "AUTHOR", "Created", new Date(System.currentTimeMillis()-10000), new Date(), false, true, false, false, false, false, false, false, "ciccio", "10 Mins", true));
			toReturn.add(new WorkflowDocument("33", "rippo", "CONSUME", "Desc", "1020", "EDITOR", "Created", new Date(System.currentTimeMillis()-10000), new Date(System.currentTimeMillis()-1000000), true, true, true, false, false, false, false, false, "ciccio", "10 Mins", true));
			toReturn.add(new WorkflowDocument("34", "fioliippo", "START", "DESC", "1040", "EDITOR", "Created", new Date(System.currentTimeMillis()-10000), new Date(), false, true, false, false, false, false, false, false, "ciccio", "10 Mins", true));
		}
		else {
			long folderId;
			try {
				ArrayList<Long> folderIDs = new ArrayList<Long>();
				folderId = DocLibraryUtil.getWfFolder(session);
				folderIDs.add(folderId);
				int n = DLFolderLocalServiceUtil.getFileEntriesAndFileShortcutsCount(DocLibraryUtil.getGroupID(session), folderIDs, status);

				_log.info("WfFolder id: " + folderId + " items number: " + n);

				//files in the workflows folder
				List<Object> raw = DLFolderLocalServiceUtil.getFileEntriesAndFileShortcuts(DocLibraryUtil.getGroupID(session), folderId, status, 0, n);
				List<DLFileEntry> files = new ArrayList<DLFileEntry>();
				System.out.println("Scanning files into wf folder...");
				for (Object rawObj : raw) {
					if (rawObj instanceof DLFileEntry) {
						DLFileEntry toAdd = (DLFileEntry) rawObj;
						files.add(toAdd);
						System.out.println("Found file:" + toAdd.getTitle());
					}

				}
				List<Role> userRoles = getUserRoles(DocLibraryUtil.getUserId(getASLSession()));
				_log.info("Checking Permissions:");
				for (DLFileEntry file : files) {
					//get the file entry resource id
					long resourceId  = ResourceLocalServiceUtil.getResource(file.getCompanyId(),
							DLFileEntry.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(file.getFileEntryId())).getResourceId();

					_log.info("Checking user roles..");
					for (Role role : userRoles) {
						String roleName = role.getName(); 
						String[] tokens = roleName.split("_");  //Role is EDITOR_45, 45 is also the filetitle of the file associated to that role
						if (tokens != null && tokens.length > 1) {
							String fileName = DocLibraryUtil.getFileNameWithoutExt(file.getTitle());
							if (tokens[1].equals(fileName)) {
								List<Permission> permissions = PermissionLocalServiceUtil.getRolePermissions(role.getRoleId(), resourceId);
								//to view this doc it must at least have the view permission or update permission
								if (isView(permissions) || (isUpdate(permissions))) {
									toReturn.add(getWorkflowDocument(file, role, permissions));
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}


	/**
	 * 
	 * @param file
	 * @param role
	 * @param permissions
	 * @return
	 */
	private WorkflowDocument getWorkflowDocument(DLFileEntry file, Role role, List<Permission> permissions) {
		WorkflowDocument toRet = null;
		String workflowid = DocLibraryUtil.getFileNameWithoutExt(file.getTitle());

		WfGraphDetails wfdb = store.getWorkflowById(workflowid);

		//check if is locked
		boolean isLocked = false;
		try {
			isLocked = LockLocalServiceUtil.isLocked(DLFileEntry.class.getName(), file.getFileEntryId());
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		long millis = 0;
		String lockedBy = "";
		boolean isOwner = false;
		if (isLocked)
			try {
				Lock lock = LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), file.getFileEntryId());
				isOwner = (lock.getUserId() == DocLibraryUtil.getUserId(getASLSession())) ? true : false;
				millis = lock.getExpirationTime();
				lockedBy = lock.getOwner();
				Date now = new Date();
				Date lockExpirationTime =  new Date(millis);
				if (now.after(lockExpirationTime)) {//then unlock it
					LockLocalServiceUtil.unlock(DLFileEntry.class.getName(), file.getFileEntryId());
					System.out.println("File:" + file.getTitle() + " Unlocked due to expiration time");
					isLocked = false;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		Date lockExpirationTime = new Date(millis);
		System.out.println("File:" + file.getTitle()  +  " locked: " + isLocked + " By: " + lockedBy+  " : " + lockExpirationTime);

		ArrayList<LogAction> logs = store.getLogActionsByWorkflowId(workflowid);
		Date lastActionDate = (logs.size() == 0) ? wfdb.getDateCreated() : logs.get(0).getDate();  //TODO: CHeck if is really the last one
		String lastAction = (logs.size() == 0) ? "Created" : logs.get(0).getActiontype();  //TODO: CHeck if is really the last one

		boolean hasComments = ! (store.getCommentsByWorkflowId(workflowid).isEmpty());


		String lockExpiration = "";
		if (isLocked) {
			Date remainingTime = new Date(lockExpirationTime.getTime() - new Date().getTime());
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(remainingTime);
			lockExpiration = (cal.get(Calendar.MINUTE)+1) + " mins";
		}
		else {
			lockedBy = "no-one";
		}
		String rolename = role.getName().split("_")[0];
		toRet = new WorkflowDocument(wfdb.getId(), wfdb.getName(), wfdb.getStatus(), getStatusDescription(wfdb), wfdb.getId(), 
				rolename, lastAction, lastActionDate, wfdb.getDateCreated(), hasComments,
				isView(permissions), isUpdate(permissions), isDelete(permissions), 
				isAddComment(permissions), isUpdateComment(permissions), isDeleteComment(permissions), isLocked, lockedBy, lockExpiration, isOwner);
		return toRet;
	}


	private String getStatusDescription(WfGraphDetails wfdb) {
		String currStatus = wfdb.getStatus();
		_log.info("Trying to read Status description for status = " + currStatus);
		WfGraph graph = (WfGraph) xstream.fromXML(wfdb.getGraph());

		for (int i = 0; i < graph.getSteps().length; i++) {
			Step step = graph.getSteps()[i];
			if (step.getDescription() != null) {
				if (step.getLabel().trim().compareTo(currStatus.trim()) == 0) {
					_log.info("Found desc curr status, returning = " + currStatus);
					return step.getDescription();
				}
			}

			
		}
		return "";
	}
	/**
	 * get the Workflow By Id
	 */
	@Override
	public WfTemplate getWorkflowById(String workflowid) {
		WfGraphDetails g = store.getWorkflowById(workflowid);
		WfGraph graph = (WfGraph) xstream.fromXML(g.getGraph());
		return new WfTemplate(g.getId(), g.getName(), g.getAuthor(), g.getDateCreated(), graph);  
	}
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isView(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("VIEW"))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isUpdate(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("UPDATE"))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isDelete(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("DELETE"))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isAddComment(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("ADD_DISCUSSION"))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isUpdateComment(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("UPDATE_DISCUSSION"))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param permissions
	 * @return
	 */
	private boolean isDeleteComment(List<Permission> permissions) {
		for (Permission p : permissions) {
			if (p.getActionId().equals("DELETE_DISCUSSION"))
				return true;
		}
		return false;
	}
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SystemException
	 */
	private List<Role> getUserRoles(long userId) throws SystemException {
		System.out.println("User Organization Roles: ");
		List<Role> roles = new ArrayList<Role>();
		roles = RoleLocalServiceUtil.getUserRoles(userId);
		for (Role role : roles) {
			if (role.getType() == ORGANIZATION_ROLE_TYPE) {
				System.out.println(role.getName());
			}

		}
		return roles;
	}
	@Override
	public Boolean addUserComment(String workflowdocid, String comment) {
		String username = (isTesting) ? "test.user" : getASLSession().getUsername();
		store.addWorkflowComment(workflowdocid, username, comment);
		return null;
	}
	@Override
	public ArrayList<UserComment> getUserComments(String workflowid) {
		if (isTesting) {
			ArrayList<UserComment> cs = new ArrayList<UserComment>();
			cs.add(new UserComment("1", new Date(), "Massi", "Test Comment"));
			cs.add(new UserComment("1", new Date(), "Gino", "Test Comment 2"));
			cs.add(new UserComment("1", new Date(), "Puccio","Test Comment 3"));
			cs.add(new UserComment("1", new Date(), "Pino", "Test Comment 4"));
			return cs;
		}
		else
			return store.getCommentsByWorkflowId(workflowid);
	}
	/**
	 * thie method performs the forward of a user, 
	 * then checks if the status has to be advanced (all users belonging to a role have forwarded)
	 * @param workflowid workflowid
	 * @return true if the status has to be and has been advanced, false if it just has update the step with the user forward
	 */
	public Boolean forward(WorkflowDocument wfDoc, String stepForwardedTo) {
		ASLSession session = getASLSession();
		String workflowid = wfDoc.getId();
		String username = "massimiliano.assante";
		if (! isTesting)
			username = session.getUsername();

		WfGraphDetails workflowInfo = store.getWorkflowById(workflowid);

		WfGraph graph = (WfGraph) xstream.fromXML(workflowInfo.getGraph());

		Step curStatus = new Step(workflowInfo.getStatus(), null);

		Step curStep = graph.getSteps()[graph.indexOf(curStatus)];
		//get the forward action to edit
		ArrayList<ForwardActionWithDest> fwaWithDest =  getForwardActionsWithDestination(curStep, graph);
		ForwardAction toEdit = null;
		for (ForwardActionWithDest fwdt : fwaWithDest) {
			if (fwdt.getToStepLabel().equals(stepForwardedTo))
				toEdit = fwdt.getFwAction();
		}

		Map<UserInfo, Boolean> users = null;
		String userRole = wfDoc.getCurRole();
		for (WfRole role :toEdit.getActions().keySet()) {
			boolean foundit = false;
			if (role.getRolename().equals(userRole)) {	
				users = toEdit.getActions().get(role);
				for (UserInfo userinfo : users.keySet()) {
					if (userinfo.getUserName().equals(username)) {
						//System.out.println("Found it");
						toEdit.getActions().get(role).put(userinfo, true);
						foundit = true;
						break;
					}
				}
			}
			if (foundit) break;
		}

		boolean allForwarded = true;
		for (UserInfo userinfo : users.keySet()) {
			if (! users.get(userinfo).booleanValue())
				allForwarded = false;
		}


		boolean returnValue = false;

		//UPDATE DATABASE
		String wfXML = xstream.toXML(graph);
		if (allForwarded) {
			String newStatus = stepForwardedTo;
			store.updateWorkflowStatusAndGraph(workflowid, newStatus, wfXML);
			changeDLFileEntryPermissions(workflowid,workflowInfo.getName(), graph, stepForwardedTo);
			returnValue = true;
		}
		else {
			store.updateWorkflowGraph(workflowid, wfXML);
			returnValue =  false;
		}
		//log the action
		store.addWorkflowLogAction(workflowid, getASLSession().getUsername(), "Forwarded");

		//send the notification to the owner that this user has forwarded
		NotificationsManager nm = new ApplicationNotificationsManager(session, "org.gcube.admin.portlet.wfdocviewer.server.WorkflowDocServiceImpl");
		WfGraphDetails wfdb = store.getWorkflowById(workflowid);
		nm.notifyDocumentWorkflowUserForward(wfdb.getAuthor(), workflowid, wfDoc.getName(), curStep.getLabel(), stepForwardedTo);
		//then send the notification that the document workflow was moved toward another step
		if (allForwarded) {
			nm.notifyDocumentWorkflowStepForwardComplete(wfdb.getAuthor(), workflowid, wfDoc.getName(), curStep.getLabel(), stepForwardedTo);
			/*
			 * send the notification to the users involved in this new step 
			 */
			Step newStep = graph.getSteps()[graph.indexOf(new Step(stepForwardedTo, null))];



			//instantiate for the WFDocumentLibrary App
			nm = new ApplicationNotificationsManager(getASLSession(), "org.gcube.portlets.user.workflowdocuments.server.WfDocumentsLibraryServiceImpl");
			//roles associated to the new Step			
			for (WfRole	wfRole : newStep.getPermissions().keySet()) {
				try {
					//and here you have to ask Liferay who are the users having this role
					List<Role> liferayRoles = getWorkflowLiferayRoles(workflowid, graph);
					for (Role role : liferayRoles) {
						if (role.getName().compareTo(wfRole.getRolename()+"_"+workflowid) == 0) {
							_log.trace("Found Role Match: " + wfRole.getRolename() + " - LR: "+ role.getName());
							//now you have the correct role id
							for (User userHavingRole : UserLocalServiceUtil.getRoleUsers(role.getRoleId())) {
								nm.notifyDocumentWorkflowTaskRequest(userHavingRole.getScreenName(), workflowid, workflowInfo.getName(), wfRole.getRolename());
							}
						}
					}

				} catch (SystemException e) {
					e.printStackTrace();
				}
			}
		}



		return returnValue;
	}
	/**
	 * 
	 * @param workflowid
	 * @param newStatus
	 */
	private void changeDLFileEntryPermissions(final String workflowid, String workflowname, WfGraph graph, String newStep) {
		int curStepIndex = graph.indexOf(new Step(newStep, null));
		Step stepToMoveOn = graph.getSteps()[curStepIndex];
		String titleWithExtension = workflowid+".zip";
		try {
			DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntryByTitle(DocLibraryUtil.getGroupID(getASLSession()), DocLibraryUtil.getWfFolder(getASLSession()), titleWithExtension);
			//get the file entry resource id
			long resourceId  = ResourceLocalServiceUtil.getResource(fileEntry.getCompanyId(),
					DLFileEntry.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(fileEntry.getFileEntryId())).getResourceId();

			System.out.println("resourceId="+resourceId);
			List<Role> rolesToUpdate = getWorkflowLiferayRoles(workflowid, graph);

			//set the permission on the file for each role 
			for (Role role : rolesToUpdate) {
				String[] actionIds = DocLibraryUtil.getPermissionsFromWfStep(role, stepToMoveOn);
				PermissionLocalServiceUtil.setRolePermissions(role.getRoleId(), actionIds, resourceId);
				System.out.println("set the permissions for Role: " + role.getName());
			}
		}  
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * return the forward actions associated to a given source step
	 * @param source
	 * @return
	 */
	private ArrayList<ForwardActionWithDest> getForwardActionsWithDestination(Step source, WfGraph graph) {
		ArrayList<ForwardActionWithDest> fwActions = new ArrayList<ForwardActionWithDest>();
		ForwardAction[][] matrix = graph.getMatrix();
		Step[] steps = graph.getSteps();
		int i = graph.indexOf(source);
		if (i < 0) {
			throw new AssertionError("The source step doesn not belong to this graph");
		}
		for (int j = 0; j < steps.length; j++) {
			if (matrix[i][j] != null) 
				fwActions.add(new ForwardActionWithDest(matrix[i][j], steps[j].getLabel()));
		}
		return fwActions;
	}

	private List<Role> getWorkflowLiferayRoles(String workflowid, WfGraph graph) throws SystemException {
		List<Role> toReturn = new ArrayList<Role>();
		ArrayList<WfRole> worflowRoles = getAllRolesFromWorkflow(graph);
		List<Role> allRoles = RoleLocalServiceUtil.getRoles(0, RoleLocalServiceUtil.getRolesCount());
		for (WfRole wfRole : worflowRoles) {	
			String roleToFind = wfRole.getRolename()+"_"+workflowid;
			for (Role role : allRoles) {
				if (role.getName().equals(roleToFind)) 
					toReturn.add(role);
			}
		}

		return toReturn;
	}

	/**
	 * helper method that retrieves all the roles definied in a workflow report
	 * @return
	 */
	private ArrayList<WfRole> getAllRolesFromWorkflow(WfGraph graph) {
		ArrayList<WfRole> toRet = new ArrayList<WfRole>();
		Step[] steps = graph.getSteps();
		for (int i = 0; i < steps.length; i++) {
			Step curStep = steps[i];
			if (curStep.getPermissions() != null) {
				GWT.log("step : " + curStep.getLabel());
				for (WfRole role : curStep.getPermissions().keySet()) {
					boolean found = false;
					for (WfRole retRole : toRet) {
						if (retRole.getRoleid().equals(role.getRoleid())) {
							found = true;
							break;
						}						
					}			
					if (! found) {
						toRet.add(role);
					}
				}
			}
		}
		return toRet;
	}
	/**
	 * Sets the workflow in session to open it lately and tries to acquire a lock if readonly is false
	 * @param documentName .
	 * @param workflowid workflow identifier for this document
	 * @param readonly specifies if it has to be open readonly or write mode
	 */
	@Override
	public LockInfo setWorkflowInSession(String documentName, String workflowid, boolean readonly) {
		LockInfo toReturn = new LockInfo(false, "", new Date());
		if (! readonly) {
			String titleWithExtension = workflowid+".zip";
			DLFileEntry fileEntry = null;
			try {
				long fifteenMin = 900000;
				long currTimePlus15 = new Date().getTime() + fifteenMin;
				fileEntry = DLFileEntryLocalServiceUtil.getFileEntryByTitle(DocLibraryUtil.getGroupID(getASLSession()),DocLibraryUtil.getWfFolder(getASLSession()), titleWithExtension);
				//check if is locked
				boolean isLocked = LockLocalServiceUtil.isLocked(DLFileEntry.class.getName(), fileEntry.getFileEntryId());
				long userid = DocLibraryUtil.getUserId(getASLSession());	
				if (! isLocked) {
					String userFullName = UserLocalServiceUtil.getUser(userid).getFullName();	
					LockLocalServiceUtil.lock(userid, DLFileEntry.class.getName(), fileEntry.getFileEntryId(), userFullName, false, currTimePlus15);
					Lock lockObject = LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), fileEntry.getFileEntryId());
					Date expTime = new Date(lockObject.getExpirationTime());
					System.out.println("****** Locked  " + documentName +  " for 15 mins, until : " + expTime );
				}
				else {
					Lock lockObject = LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), fileEntry.getFileEntryId());
					Date expTime = new Date(lockObject.getExpirationTime());
					//the file is locked but the user has permission to write, sets it readonly
					if (lockObject.getUserId() == userid) {
						System.out.println("**** user has permission to write and he locked " + documentName +  "");
						toReturn.setLocked(false);
					}
					else {
						System.out.println("**** user has permission to write but " + documentName +  " file is locked until: " + expTime);
						//forces readonly
						readonly = true;
						toReturn.setLocked(true);
					}

					toReturn.setLockedby(lockObject.getOwner());
					toReturn.setExpirationTime(expTime);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}	

		}
		getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE, workflowid);
		getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_READONLY_ATTRIBUTE, readonly);	
		getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_GIVEN_NAME, documentName);	
		System.out.println("****\n\n****** WROTE WORKFLOID IN SESSION: " + workflowid + " NAME: " + documentName);
		return toReturn;
	}
}
