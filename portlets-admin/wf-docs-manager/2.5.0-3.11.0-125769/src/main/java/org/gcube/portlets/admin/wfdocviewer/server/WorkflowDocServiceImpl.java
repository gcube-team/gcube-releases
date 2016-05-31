package org.gcube.portlets.admin.wfdocviewer.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.wfdocslibrary.server.db.MyDerbyStore;
import org.gcube.portlets.admin.wfdocslibrary.server.db.Store;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.LogAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserInfo;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraphDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wfdocviewer.client.WorkflowDocService;
import org.gcube.portlets.admin.wfdocviewer.server.loggers.CreatedWorkflowReportLogEntry;
import org.gcube.portlets.admin.wfdocviewer.server.loggers.DeletedWorkflowLogEntry;
import org.gcube.portlets.admin.wfdocviewer.shared.ActionLogBean;
import org.gcube.portlets.admin.wfdocviewer.shared.UserBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfDocumentBean;
import org.gcube.portlets.admin.wfdocviewer.shared.WfTemplateBean;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.Role;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WorkflowDocServiceImpl extends RemoteServiceServlet implements	WorkflowDocService {

	private static final Logger log = LoggerFactory.getLogger(WorkflowDocServiceImpl.class);
	/**
	 * 
	 */
	private static final int LIFERAY_ORGANIZATION_ROLE_ID = 3;

	/**
	 * 
	 */
	public static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";

	/**
	 * the WF DB Store
	 */
	private Store store;
	/**
	 * object serializer
	 */
	private XStream xstream;

	/**
	 * init method
	 */
	public void init() {
		store = new MyDerbyStore();
		xstream = new XStream(new DomDriver());
	}
	boolean isTesting = false;
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
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devNext");
			isTesting = true;
		}
		//Logger.debug("\n\nsession ID= *" + sessionID + "*  user= *" + user + "*" );
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	@Override
	public ArrayList<WfDocumentBean> getAllWfDocuments() {
		ArrayList<WfDocumentBean> toRet = new ArrayList<WfDocumentBean>();
		Workspace root = null;
		String serverName = this.getThreadLocalRequest().getServerName();  //check if it belongs to this server
		try {
			root = getWorkspaceArea();
			for (WorkspaceItem item : root.getRoot().getChildren()) {
				if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
					FolderItem fi  = (FolderItem) item;
					if (fi.getFolderItemType() == FolderItemType.WORKFLOW_REPORT) {
						log.debug("Check if workflow report belongs to " + serverName);
						String storedServerName = fi.getDescription();
						//remove first part of domain to avoid aliases problem (e.g. www.d4science.org != portal.d4science.org)
						storedServerName = storedServerName.substring(storedServerName.indexOf(".")+1, storedServerName.length());
						String currServerName = serverName.substring(serverName.indexOf(".")+1, serverName.length());
						if (storedServerName.compareTo(currServerName) == 0) {  //if belongs to the same server shows it
							log.debug("workflow report does belong to " + currServerName);
							String wfId = fi.getProperties().getProperties().get((NodeProperty.WORKFLOW_ID.toString()));
							WfGraphDetails g = store.getWorkflowById(wfId);
							//System.out.println("Reading.. " + fi.getName() +  " ID: " + fi.getWorkflowId() + "status: " + g.getStatus());
							ArrayList<ActionLogBean> actions = fetchActionsByWorkflowId(wfId);
							ActionLogBean lastAction = null;
							if (actions.size() > 0)
								lastAction = actions.get(actions.size()-1);
							else 
								lastAction = new ActionLogBean(wfId, fi.getCreationTime().getTime(), getDisplaynameByUsername(getASLSession().getUsername()), "Created");
							WfDocumentBean toAdd = new WfDocumentBean(wfId, fi.getName(), g.getStatus(), fi.getId(), fi.getCreationTime().getTime(), lastAction.getDate(), lastAction.getAction());
							toRet.add(toAdd);
						}
						else
							log.debug("workflow report belongs to " + storedServerName +  " SKIPPING");
					}						
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return toRet;
	}
	@Override
	public ArrayList<WfTemplateBean> getAllTemplates() {
		ArrayList<WfTemplateBean> templates = new ArrayList<WfTemplateBean>();
		for (WfGraphDetails g : store.getAllWorkflowTemplates()) {
			templates.add(new WfTemplateBean(g.getId(), g.getName(), g.getAuthor(), "unknown date")); 
		}		
		return templates;
	}
	@Override
	public WfTemplate getWfTemplate(String id) {
		WfGraphDetails g = store.getWfTemplateById(id);
		WfGraph graph = (WfGraph) xstream.fromXML(g.getGraph());
		return new WfTemplate(g.getId(), g.getName(), g.getAuthor(), g.getDateCreated(), graph);  //TODO: Set the correct date
	}
	@Override
	public WfTemplate getWfReport(String id) {
		WfTemplate toRet = null;
		try {
			WfGraphDetails fi = store.getWorkflowById(id);
			WfGraph graph = (WfGraph) xstream.fromXML(fi.getGraph());
			toRet = new WfTemplate(fi.getId(), fi.getName(), fi.getAuthor(), fi.getDateCreated(), graph);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return toRet;
	}
	/**
	 * @return all the wfroles present in the db
	 */
	@Override
	public ArrayList<WfRoleDetails> getRoleDetails() {
		log.debug("Getting Workflow Roles from DB");
		ArrayList<WfRoleDetails> toReturn = new ArrayList<WfRoleDetails>();
		for (WfRole r : store.getAllRoles()) {
			toReturn.add(new WfRoleDetails(r.getRoleid(), r.getRolename()));
		} 
		return toReturn;
	}
	/**
	 * retrieves the lis of user from the current VO/VRE
	 */
	public ArrayList<UserBean> getVREUsers() {
		ArrayList<UserBean> toRet = new ArrayList<UserBean>();
		if (isTesting) {
			toRet.add(new UserBean("1", "Giorgino De Benedicits", "giorgino.degazz"));
			toRet.add(new UserBean("2", "Birmbo Barumbo", "birimbo.barumbo"));
			toRet.add(new UserBean("3", "Pippo De Pippa", "pippo.pippa"));
			toRet.add(new UserBean("4", "Foo De Fie", "foo.fie"));
		}
		else {
			UserManager um = new LiferayUserManager();
			try {
				List<UserModel> users = um.listUsersByGroup(""+getASLSession().getGroupId());
				for (UserModel user : users) {
					toRet.add(new UserBean(user.getUserId(), user.getFullname(), user.getScreenName()));
				}
			} catch (UserManagementSystemException e) {
				e.printStackTrace();
			} catch (GroupRetrievalFault e) {
				e.printStackTrace();
			} catch (UserRetrievalFault e) {
				e.printStackTrace();
			}
		}
		return toRet;
	}
	@Override
	public Boolean saveWorkflow(String selectedReportid, String selectedReportName, WfGraph toSave,	HashMap<String, List<UserBean>> rolesAndUsersToCreate) {

		/*
		 * ADDING USERS TO FORWARD ACTIONS PART 
		 */
		Step[] steps = toSave.getSteps();
		for (int i = 0; i < steps.length; i++) {
			Step curStep = steps[i];
			ArrayList<ForwardAction> fwactions = toSave.getForwardActions(curStep);
			for (ForwardAction fa : fwactions) {
				//the new actions map
				Map<WfRole, Map<UserInfo, Boolean>> toAdd = new HashMap<WfRole, Map<UserInfo,Boolean>>();
				//the roles in the actions map
				ArrayList<WfRole> roles = fa.getRoles();
				//for each role get the users
				for (WfRole wfRole : roles) {
					toAdd.put(wfRole, getUsernamesGivenRole(wfRole, rolesAndUsersToCreate));
				}
				//set the new actions Map
				fa.setActions(toAdd);
			}
		}
		/*
		 * Store this workflow in the WorkflowDB
		 */
		log.info("Attempting to Save Workflow Report ..." + selectedReportName);
		String wfXML = xstream.toXML(toSave);
		//log.debug("Serialized WorkflowReport ...\n" + wfXML);
		//System.out.println("Serialized Workflow ...\n" + wfXML);
		log.info("Saving WfReport into DB ...");
		String firstStatus = steps[0].getLabel();
		String workflowid =  store.addWorkflowReport(selectedReportid, selectedReportName, firstStatus, getASLSession().getUsername(), wfXML);
		log.info("Saving into DB SUCCESSFUL, returning id: " +  workflowid);		

		/*
		 * Saving in HL, add server name to check if is the same server in the description field
		 *  
		 *  
		 */
		String serverName = this.getThreadLocalRequest().getServerName();
		boolean resultHL = saveToWorkspace(selectedReportName, serverName, workflowid, firstStatus, wfXML, 1);



		/*
		 * Creating roles in liferay DB using UserManagemnt 
		 */
		List<Role> createdRoles = commitChangesInLiferayDB(selectedReportid, selectedReportName, workflowid, rolesAndUsersToCreate);


		Report toWrite = getReportFromHL(selectedReportid);
		//use the workflow id as filename
		String filename = workflowid + ".zip";
		System.out.println("Attempting Writing in DocLib name: " + filename);
		/*
		 * Start step is always in position 0 in the array 
		 * each Step contains a Map<WfRole, ArrayList<PermissionType>> that is needed in the writeFileIntoDocLibrary 
		 * to assign roles permission on the file instance
		 */
		Step start = toSave.getSteps()[0]; 

		boolean resultLRDoc = false;
		try {
			DocLibraryUtil.writeFileIntoDocLibrary(getASLSession(), createdRoles, start, filename, getBytesFromInputStream(toWrite.getData()));
			resultLRDoc = true;
		} catch (InternalErrorException e) {
			resultLRDoc = false;
			e.printStackTrace();
		}


		boolean overAllResult = resultHL && resultLRDoc && (createdRoles.size() > 0);

		if (overAllResult) {
			//log the creation
			AccessLogger log = AccessLogger.getAccessLogger();
			CreatedWorkflowReportLogEntry logEntry = new CreatedWorkflowReportLogEntry(selectedReportName, workflowid, steps.length);
			log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

			/*
			 * send the notification to the users involved in the first step 
			 * is not trivial you need to get all the info again
			 */

			//roles associated to the Start Step			
			for (WfRole	wfRole : start.getPermissions().keySet()) {
				//rolesAndUsersToCreate contains all the Roles / Users 
				//associations but it is Step Independent and you need to see who had that role
				for (String rolename : rolesAndUsersToCreate.keySet()) { 
					if (rolename.equals(wfRole.getRolename()) ) { //then these users needs to be notified
						List<UserBean> usersForThisRole = rolesAndUsersToCreate.get(rolename);
						for (UserBean userBean : usersForThisRole) {
							String user2Notify = userBean.getScreenName();
							NotificationsManager nm = new ApplicationNotificationsManager(getASLSession(), "org.gcube.portlets.user.workflowdocuments.server.WfDocumentsLibraryServiceImpl");
							nm.notifyDocumentWorkflowFirstStepRequest(user2Notify, workflowid, selectedReportName, rolename);
						}
					}
				}
			}

		}
		return overAllResult;
	}




	/**
	 * 
	 * @param reportid
	 * @return
	 */
	private Report getReportFromHL(String reportid) {
		Workspace root = null;
		Report report = null;
		try {
			root = getWorkspaceArea();
			WorkspaceItem item = root.getItem(reportid);
			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
				FolderItem fItem = (FolderItem) item;
				if (fItem.getFolderItemType() == FolderItemType.REPORT) {
					report = (Report) fItem;
				}
			}


		}
		catch (Exception e) {
			e.printStackTrace(); }
		return report;
	}

	/**
	 * This method cares about reflecting the changes in the LiferayDB
	 * create the roles and assign them to the users
	 * @param selectedReportid
	 * @param selectedReportName
	 * @param workflowid
	 * @param rolesAndUsersToCreate
	 * @return
	 */
	private List<Role> commitChangesInLiferayDB(String selectedReportid, String selectedReportName, 
			String workflowid, HashMap<String, List<UserBean>> rolesAndUsersToCreate) {
		ArrayList<Role> createdRoles = new ArrayList<Role>();
		UserManager uman = new LiferayUserManager();
		try {
			for (String rolename : rolesAndUsersToCreate.keySet()) {
				Role created = createRole(rolename,selectedReportName, workflowid);
				createdRoles.add(created);
				log.debug("Created Role: " + created.getName() + " with id " + created.getRoleId());
				Set<UserInfo> users = getUsernamesGivenRole(new WfRole("", rolename, ""), rolesAndUsersToCreate).keySet();
				for (UserInfo userInfo : users) {
					UserModel user = uman.getUserByScreenName(userInfo.getUserName());	
					long[] roleids = {created.getRoleId()};
					RoleLocalServiceUtil.addUserRoles(Long.parseLong(user.getUserId()), roleids);
					log.debug("Assigned role: " +  created.getName() +  " to " + user.getFullname());
				}
			} 
		}
		catch (Exception e) {	
			e.printStackTrace();
			return null;
		}
		return createdRoles;
	}




	/**
	 * Create an Organization role for tht yet created Workflow report 
	 * @param vreName
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private Role createRole(String roleName, String workflowname, String workflowid){		
		try {
			Company company =  OrganizationsUtil.getCompany();				
			String roletoAdd = roleName+"_" + workflowid;	
			return RoleLocalServiceUtil.addRole(0L, company.getCompanyId(), roletoAdd, null, roleName +" of " + workflowname+" ("+workflowid+")", LIFERAY_ORGANIZATION_ROLE_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * helper method that initializes the actions field of a ForwardAction (put the roles and the corresponding selected users)
	 * @param role
	 * @return
	 */
	private HashMap<UserInfo, Boolean> getUsernamesGivenRole(WfRole role, HashMap<String, List<UserBean>> rolesAndUsersToCreate) {
		HashMap<UserInfo, Boolean> toReturn = new HashMap<UserInfo, Boolean>();
		for (String roleName : rolesAndUsersToCreate.keySet()) {
			if (roleName.compareTo(role.getRolename()) == 0) {
				List<UserBean> users = rolesAndUsersToCreate.get(roleName);
				for (UserBean user : users) {
					toReturn.put(new UserInfo(user.getId(), user.getDysplayName(), user.getScreenName(), ""), false);
				}
			}			
		}
		return toReturn;
	}
	/**
	 * save the placeholder of the wfreport in the workspace
	 */
	private boolean saveToWorkspace(String name, String description, String workflowid, String status, String data, int i) {
		Workspace root = null;
		try {
			root = getWorkspaceArea();
			root.createWorkflowReport(name, description, workflowid, status, data, root.getRoot().getId());
			return true;
		} 
		catch (ItemAlreadyExistException ex) {
			i+= 1;
			if (name.charAt(name.length()-2) == ' ')
				name = name.substring(0, name.length()-2);
			name += " " + i;
			saveToWorkspace(name, description, workflowid, status, data, i);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return an instance of the user WorkspaceArea
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws WorkspaceNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException 	{
		return HomeLibrary.getUserWorkspace(getASLSession().getUsername());	
	}

	/**
	 */
	byte[] getBytesFromInputStream(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			org.apache.commons.io.IOUtils.copy(is, os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	/**
	 * fetchActionsByWorkflowId
	 */
	@Override
	public ArrayList<ActionLogBean> fetchActionsByWorkflowId(String workflowid) {
		ArrayList<ActionLogBean> actions = new ArrayList<ActionLogBean>();
		ArrayList<LogAction> storeActions= store.getLogActionsByWorkflowId(workflowid);
		for (LogAction logAction : storeActions) {
			actions.add(new ActionLogBean(
					logAction.getWorkflowid(), 
					logAction.getDate(), 
					getDisplaynameByUsername(logAction.getAuthor()),
					logAction.getActiontype())
					);
		}
		return actions;
	}

	/**
	 * 
	 * @param username screename in db
	 * @return the display name of the user
	 */
	private String getDisplaynameByUsername(String username) {
		try {
			ArrayList<UserBean> users = getVREUsers();
			for (UserBean userBean : users) {
				if (userBean.getScreenName().compareTo(username) == 0)
					return userBean.getScreenName();
			}
		}
		catch (Exception e) {
			return username;
		}
		return username;
	}

	@Override
	public Boolean deleteWorkflowDocument(WfDocumentBean docBean) {
		String workflowid = docBean.getId();
		//delete file from Doc Library
		boolean del1 = DocLibraryUtil.deleteFileFromDocLibrary(getASLSession(), workflowid);
		//delete the place-holder from HL
		boolean del2 = deleteFromHL(docBean.getHomeLibraryId());

		WfGraphDetails fi = store.getWorkflowById(workflowid);
		WfGraph graph = (WfGraph) xstream.fromXML(fi.getGraph());
		//delete from liferay db
		boolean del3 = deleteRolesFromLR(graph, workflowid);
		//delete from workflow db
		boolean del4 = store.deleteWorkflowReport(workflowid);	

		//log the deletion
		AccessLogger log = AccessLogger.getAccessLogger();
		DeletedWorkflowLogEntry logEntry = new DeletedWorkflowLogEntry(docBean.getName(), docBean.getId(), docBean.getStatus());
		log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

		return del1 && del2 && del3 && del4;
	}

	private boolean deleteRolesFromLR(WfGraph g, String workflowid) {		
		try {
			List<Role> rolesToDelete = getWorkflowLiferayRoles(workflowid, g);
			for (Role role : rolesToDelete) {
				RoleLocalServiceUtil.deleteRole(role);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 
	 * @param workflowid
	 * @param graph
	 * @return
	 * @throws SystemException
	 */
	private List<Role> getWorkflowLiferayRoles(String workflowid, WfGraph graph) throws Exception {
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
	 * 
	 * @param idToDelete
	 * @return
	 */
	private boolean deleteFromHL(String idToDelete)  {
		Workspace wp;
		try {
			wp = getWorkspaceArea();
			wp.removeItem(idToDelete);	
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}	
		return true;
	}
}
