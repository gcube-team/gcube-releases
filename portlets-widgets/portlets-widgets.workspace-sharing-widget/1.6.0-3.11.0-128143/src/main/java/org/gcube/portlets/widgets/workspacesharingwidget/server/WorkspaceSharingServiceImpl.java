package org.gcube.portlets.widgets.workspacesharingwidget.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.common.portal.PortalContext;
import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService;
import org.gcube.portlets.widgets.workspacesharingwidget.server.notifications.NotificationsProducer;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.ScopeUtility;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.UserUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.SessionExpiredException;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VO;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VRE;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
@SuppressWarnings("serial")
public class WorkspaceSharingServiceImpl extends RemoteServiceServlet implements
    WorkspaceSharingService {

	protected static Logger logger = LoggerFactory.getLogger(WorkspaceSharingServiceImpl.class);


	/**
	 * Gets the GWT workspace builder.
	 *
	 * @return the GWT workspace builder
	 */
	protected GWTWorkspaceSharingBuilder getGWTWorkspaceBuilder()
	{
		return WsUtil.getGWTWorkspaceSharingBuilder(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 * @throws InternalErrorException the internal error exception
	 * @throws HomeNotFoundException the home not found exception
	 * @throws WorkspaceFolderNotFoundException the workspace folder not found exception
	 */
	protected Workspace getWorkspace() throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		return WsUtil.getWorkspace(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Gets the notification producer.
	 *
	 * @return the notification producer
	 */
	protected NotificationsProducer getNotificationProducer(){

		return WsUtil.getNotificationProducer(WsUtil.getAslSession(this.getThreadLocalRequest().getSession()));
	}

	/**
	 * Gets the scope util filter.
	 *
	 * @return the scope util filter
	 */
	protected ScopeUtility getScopeUtilFilter(){

		return WsUtil.getScopeUtilFilter(this.getThreadLocalRequest().getSession());
	}

	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	protected boolean isTestMode(){
		return !WsUtil.isWithinPortal(); //IS NOT INTO PORTAL
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getAllContacts(boolean, boolean)
	 */
	@Override
	public List<InfoContactModel> getAllContacts(boolean readGroupsFromHL, boolean readGroupsFromPortal) throws Exception {

		try {
			ASLSession aslSession = WsUtil.getAslSession(this.getThreadLocalRequest().getSession()); //THIS FORCE THE SESSION CREATION
			logger.info("Get all contacts");

			GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();

			if(isTestMode()){
				logger.warn("WORKSPACE PORTLET IS IN TEST MODE - RETURN TEST USERS AND GROUPS");
				GWTWorkspaceSharingBuilder.getHashTestUsers();
				List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();
				for (String key : GWTWorkspaceSharingBuilder.getHashTestUsers().keySet()) {
					InfoContactModel contact = GWTWorkspaceSharingBuilder.getHashTestUsers().get(key);
					listContactsModel.add(contact);
				}
				return listContactsModel;
			}
			//READING USERS FROM VRE
			/*UserManager userManag = new LiferayUserManager();
			GroupManager gm = new LiferayGroupManager();
			String aslSessionGN = aslSession.getGroupName();
			logger.info("aslSession.getGroupName(): "+ aslSessionGN);
			String groupId = gm.getGroupId(aslSessionGN);
			logger.info("groupId from GroupManager: "+ groupId);
			logger.info("Liferay User Manager getting list users by group: "+groupId);
			List<InfoContactModel> listContactsModel = builder.buildGXTListContactsModelFromUserModel(userManag.listUsersByGroup(groupId));
			*/
			List<UserModel> users = UserUtil.getOrganizationUsers(aslSession.getScope());

			if(users==null)
				throw new Exception("An error occurred on recovering users from Portal, try again later");

			List<InfoContactModel> listContactsModel = builder.buildGXTListContactsModelFromUserModel(users);

			if(readGroupsFromHL){
				logger.info("Reading group names from HL..");
				org.gcube.common.homelibrary.home.workspace.usermanager.UserManager hlUserManager = HomeLibrary.getHomeManagerFactory().getUserManager();
				logger.trace("Home Library User Manager getting list Gcube Group");
				List<GCubeGroup> groups = hlUserManager.getGroups();
				if(groups!=null){
					logger.info("Read group names from HL, return "+groups.size()+" groups, converting");
					listContactsModel.addAll(builder.buildGXTListContactsModelFromGcubeGroup(groups));
				}else
					logger.warn("Read group names from HL, return list null, skipping");
			}

			if(readGroupsFromPortal){
				logger.info("Reading group names as scopes from Infrastructure..");

				PortalContext context = PortalContext.getConfiguration();
				logger.info("context.getInfrastructureName(): "+context.getInfrastructureName());
				logger.info("aslSession.getGroupName(): "+ aslSession.getGroupName());
				logger.info("aslSession.getScope().toString(): "+ aslSession.getScope());
				try{
					ScopeUtility scopeUtility = new ScopeUtility(aslSession.getScope());

					if(scopeUtility.getVoName()!=null){
						logger.info("VO name is not null, trying to calculate List VO");
						List<VO> lsVOs = WsUtil.getVresFromInfrastructure(context.getInfrastructureName(), scopeUtility.getVoName());
//						String voPath = ConstantsSharing.PATH_SEPARATOR+context.getInfrastructureName() + ConstantsSharing.PATH_SEPARATOR + aslSession.getGroupName() + ConstantsSharing.PATH_SEPARATOR;
						listContactsModel.addAll(builder.buildGXTListContactsModelFromVOs(lsVOs, scopeUtility.getVo()));
					}else
						logger.warn("VO name is null, skipping list VO "+ aslSession.getGroupName());

				}catch(Exception e){
					logger.warn("An error occurred on recovering vo name, skipping list VO "+ aslSession.getGroupName());
				}
			}

			logger.info("Get all contacts returning a list having size: "+listContactsModel.size());
			return listContactsModel;

		} catch (Exception e) {
			logger.error("Error in server get all contacts ", e);
//			return new ArrayList<InfoContactModel>();
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getListUserSharedByFolderSharedId(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getListUserSharedByFolderSharedId(String folderSharedId) throws Exception{

		logger.info("getListUserSharedByFolderSharedId "+ folderSharedId);

		try {
			Workspace workspace = getWorkspace();

			WorkspaceItem wsItem = workspace.getItem(folderSharedId);

			//TODO CHANGE TO READ ACL FROM SHARED SUBFOLDER
			if(isASharedFolder(wsItem, false)){

				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) workspace.getItem(wsItem.getIdSharedFolder());

				GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();

				List<String> listPortalLogin = wsFolder.getUsers();

				logger.info("getListUserSharedByFolderSharedId return "+ listPortalLogin.size() + " user");

				if(isTestMode())
					return builder.buildGxtInfoContactFromPortalLoginTestMode(listPortalLogin);

				return builder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);

			}
			else{
				logger.info("the item with id: "+folderSharedId+ " is not  "+WorkspaceItemType.SHARED_FOLDER);

				//DEBUG
				//System.out.println("the item with id: "+folderSharedId+ " is not  "+WorkspaceItemType.SHARED_FOLDER);
			}
			return new ArrayList<InfoContactModel>();
		}catch (ItemNotFoundException e){
			logger.error("Error in server during item retrieving, getListUserSharedByFolderSharedId", e);
			throw new Exception("The Item id "+folderSharedId+" not found in workspace or is not a shared folder");
		} catch (Exception e) {
			logger.error("Error in getListUserSharedByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getOwnerByItemId(java.lang.String)
	 */
	@Override
	public InfoContactModel getOwnerByItemId(String itemId) throws Exception {

		logger.info("get Owner By ItemId "+ itemId);
		try {

			Workspace workspace = getWorkspace();

			WorkspaceItem wsItem = workspace.getItem(itemId);

			GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();

			return builder.buildGXTInfoContactModel(wsItem.getOwner());

		} catch (Exception e) {
			logger.error("Error in getOwnerByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Gets the AC ls.
	 *
	 * @return the AC ls
	 * @throws Exception the exception
	 */
	@Override
	public List<WorkspaceACL> getACLs() throws Exception{
		try {

			GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();
			return builder.getWorkspaceACLFromACLs(Arrays.asList(ACLType.values()));
		} catch (Exception e) {
			logger.error("Error in server get ACLs", e);
			String error = ConstantsSharing.SERVER_ERROR +" get ACL rules. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getMyLogin()
	 */
	@Override
	public String getMyLogin(){
		ASLSession asl = WsUtil.getAslSession(this.getThreadLocalRequest().getSession());
		return asl.getUsername();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getFileModelByWorkpaceItemId(java.lang.String)
	 */
	@Override
	public FileModel getFileModelByWorkpaceItemId(String itemId) throws Exception {

		try {

//			System.out.println("Get file model by itemId: "+itemId);

			if(itemId == null || itemId.isEmpty())
				throw new Exception("Identifier is null or empty");

			Workspace workspace = getWorkspace();

			logger.info("Get file model by itemId: "+itemId);

			WorkspaceItem wsItem =  workspace.getItem(itemId);

			GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();

//			Long startTime =  System.currentTimeMillis();
//
//			Long endTime = System.currentTimeMillis() - startTime;
//			String time = String.format("%d msc %d sec", endTime, TimeUnit.MILLISECONDS.toSeconds(endTime));
//			logger.debug("get child for Grid by id returning element in " + time);

			logger.info("Getting folder parent");

			WorkspaceFolder folderParent = null;
			if(wsItem!=null)
				folderParent = wsItem.getParent(); 	//BUILD PARENT
			else
				throw new Exception("Workspace item not found");


			FileModel parent = null;
			if(folderParent!=null){
				logger.info("Folder parent has id: "+folderParent.getId() + " and name: "+folderParent.getName());
				parent = builder.buildGXTFileModelItem(folderParent, null);
			}
			else
				logger.info("Folder parent for item: "+wsItem.getId() +" is null");

			//BUILD ITEM
			return builder.buildGXTFileModelItem(wsItem, parent);
		}catch (ItemNotFoundException e){
			logger.error("Error in server during item retrieving, getFileModelByWorkpaceItemId", e);
			throw new Exception("The Item id "+itemId+" not found in workspace");
		} catch (Exception e) {
			logger.error("Error in server during item retrieving, getFileModelByWorkpaceItemId", e);
			String error =  ConstantsSharing.SERVER_ERROR + " retrieving the item from workspace, "+e.getMessage();
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#isSessionExpired()
	 */
	@Override
	public boolean isSessionExpired() throws Exception {
		return WsUtil.isSessionExpired(this.getThreadLocalRequest().getSession());
	}


	// DEBUG
	/**
	 * Prints the contacts.
	 *
	 * @param listContacts the list contacts
	 */
	private void printContacts(List<InfoContactModel> listContacts) {

		boolean testMode = isTestMode();
		if (testMode)
			System.out.println("Contacts: ");
		else
			logger.debug("Contacts:");

		for (InfoContactModel infoContactModel : listContacts) {
			if (testMode)
				System.out.println("User: " + infoContactModel);
			else
				logger.debug("User: " + infoContactModel);
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#shareFolder(org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel, java.util.List, boolean, org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL)
	 */
	@Override
	public boolean shareFolder(FileModel folder, List<InfoContactModel> listContacts, boolean isNewFolder, WorkspaceACL acl) throws Exception {

		if(isSessionExpired())
			throw new SessionExpiredException();

		try {

			Workspace workspace = getWorkspace();

			logger.info("sharing item id: "+ folder.getIdentifier()
					+ " name: "+ folder.getName()
//					+ " parent name: " + folder.getParentFileModel().getName()
					+ " listContacts size: " + listContacts.size());

//			//DEBUG
			//System.out.println("shareFolder "+ folder.getIdentifier() + " name: "+ folder.getName() + " parent name: " + folder.getParentFileModel().getName() + " listContacts size: " + listContacts.size());
//			for (InfoContactModel infoContactModel : listContacts) {
//				System.out.println("share with "+ infoContactModel.getLogin());
//			}
//			printContacts(listContacts);

			List<String> listLogin = UserUtil.getListLoginByInfoContactModel(listContacts);

			WorkspaceSharedFolder sharedFolder = null;

			List<InfoContactModel> listSharedContact = null;

			boolean sourceFolderIsShared = folder.isShared();

			if(sourceFolderIsShared){ //if source folder is already share... retrieve old list of sharing to notify
				listSharedContact = getListUserSharedByFolderSharedId(folder.getIdentifier());
			}

			if(listLogin.size()>0){

				if(!isNewFolder){
					sharedFolder = workspace.shareFolder(listLogin, folder.getIdentifier());
					sharedFolder.setDescription(folder.getDescription()); //SET NEW DESCRIPTION
				}
				else
					sharedFolder = workspace.createSharedFolder(folder.getName(), folder.getDescription(), listLogin, folder.getParentFileModel().getIdentifier());
			}

			boolean created = sharedFolder==null?false:true;

			if(acl!=null)
				setACLs(sharedFolder.getId(), listLogin, acl.getId().toString());

			if(created){
				NotificationsProducer np = getNotificationProducer();
				if(!sourceFolderIsShared) {//if source folder is not already shared

					//TODO ADD NOTIFICATION
//					np.notifyFolderSharing(listContacts, sharedFolder);

				}else{
					/*System.out.println("SHARED CONTACS: ");
					printContacts(listSharedContact);
					System.out.println("NEW CONTACS: ");
					printContacts(listContacts);*/

					//TODO ADD NOTIFICATION
//					np.notifyAddedUsersToSharing(listSharedContact, listContacts, sharedFolder);
				}
			}

			return created;

		} catch (InsufficientPrivilegesException e) {
			logger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (ItemAlreadyExistException e) {
			logger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (WrongDestinationException e) {
			logger.error("Error in shareFolder ", e);
			String error = "An error occurred on creating shared folder. "+ e.getMessage();
			throw new Exception(error);

		} catch (Exception e) {
			logger.error("Error in shareFolder ", e);
			e.printStackTrace();
			String error = ConstantsSharing.SERVER_ERROR+" sharing item.";
			throw new Exception(error);
		}
	}

	/**
	 * Sets the ac ls.
	 *
	 * @param folderId the folder id
	 * @param listLogins the list logins
	 * @param aclType the acl type
	 * @throws Exception the exception
	 */
	public void setACLs(String folderId, List<String> listLogins, String aclType) throws Exception{
		try {

			if(folderId == null)
				throw new Exception("Folder id is null");

			if(listLogins==null || listLogins.size()==0)
				throw new Exception("List Logins is null or empty");

			logger.info("Setting ACL for folder id: "+folderId);
			logger.info("ACL type is: "+aclType);

			Workspace workspace = getWorkspace();
			WorkspaceItem wsItem = workspace.getItem(folderId);

			//IS A WORKSPACE FOLDER?
			if(wsItem!= null && wsItem.isFolder() && wsItem.isShared()){
				WorkspaceFolder ite;
				if(wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
					//IS ROOT SHARED FOLDER
					ite = (WorkspaceSharedFolder) wsItem;
					logger.trace("Folder " +ite.getName()+" is a "+WorkspaceSharedFolder.class.getName());
				}else{
					// IS SUB FOLDER OF THE SHARING
					ite = (WorkspaceFolder) wsItem;
					logger.trace("Folder " +ite.getName()+" is a "+WorkspaceFolder.class.getName());
				}

				ite.setACL(listLogins, ACLType.valueOf(aclType));
			}else
				throw new Exception("Source item is not shared or shared folder");

			logger.info("Setting ACL for "+wsItem.getName()+" completed, returning");
		} catch (Exception e) {
			logger.info("Error in set ACLs", e);
			String error = ConstantsSharing.SERVER_ERROR +" setting permissions. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getInfoContactModelsFromCredential(java.util.List)
	 */
	@Override
	public List<InfoContactModel> getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact) throws Exception {

		if(listAlreadySharedContact==null || listAlreadySharedContact.size()==0)
			throw new Exception("Credentials list is null or empty");


		GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();
		List<InfoContactModel> listContacts = new ArrayList<InfoContactModel>(listAlreadySharedContact.size());
		for (CredentialModel credentialModel : listAlreadySharedContact) {

			if(!credentialModel.isGroup()){
				InfoContactModel contact = builder.buildGxtInfoContactFromPortalLogin(credentialModel.getLogin());
				contact.setReferenceCredential(credentialModel);
				listContacts.add(contact);
				logger.trace("Converted contact: "+credentialModel +", into: "+contact);
			}else if(credentialModel.getName()==null || credentialModel.getName().isEmpty()){
				InfoContactModel contact = builder.buildGxtInfoContactFromPortalGroup(credentialModel.getLogin());
				contact.setReferenceCredential(credentialModel);
				listContacts.add(contact);
				logger.trace("Converted group: "+credentialModel +", into: "+contact);
			}
		}

		return listContacts;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getAdministratorsByFolderId(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getAdministratorsByFolderId(String sharedFolderId) throws Exception {

		if(sharedFolderId==null || sharedFolderId.isEmpty())
			throw new Exception("Shared Folder id is null or empty");

		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(sharedFolderId);
			logger.info("Getting administator/s to folder: "+sharedFolderId);

			if(item!=null && item.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
				WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) item;
				GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();
				List<String> admins = sharedFolder.getAdministrators();
				logger.info("Converting "+admins.size()+" administator/s List<InfoContactModel>");

				if(isTestMode())
					return builder.buildGxtInfoContactFromPortalLoginTestMode(admins);

				return builder.buildGxtInfoContactsFromPortalLogins(admins);
			}else
				throw new Exception("The item is null or not instanceof "+WorkspaceItemType.SHARED_FOLDER);
		} catch (ItemNotFoundException e){
			logger.error("Error in server getAdministratorsByFolderId", e);
			throw new Exception("The Item id "+sharedFolderId+" not found in workspace or is not a shared folder");
		} catch (Exception e) {
			logger.error("Error in server getAdministratorsByFolderId", e);
			String error = "Sorry an error occurred on getting Administrators";
			throw new Exception(error);
		}
	}


	/**
	 * DEBUG.
	 *
	 * @return the fake groups
	 */
	private List<InfoContactModel> getFakeGroups(){

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		PortalContext context = PortalContext.getConfiguration();
		System.out.println("context.getInfrastructureName(): "+context.getInfrastructureName());
//		System.out.println("context.getVOsAsString(): "+context.getVOsAsString());
		List<VO> vos = WsUtil.getVresFromInfrastructure(context.getInfrastructureName(), "devsec");

		for (VO vo : vos) {
			System.out.println("vo name "+vo.getName());

			for (VRE vre : vo.getVres()) {
				System.out.println("vre name "+vre.getName());
				listContactsModel.add(new InfoContactModel(vre.getName(), vre.getName(), vre.getName(), true));
			}
		}

		return listContactsModel;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService#getACLForSharedItemId(java.lang.String)
	 *
	 * return the WorkspaceACL of shared folder, null otherwise
	 */
	@Override
	public WorkspaceACL getACLsForSharedFolderId(String itemID) throws Exception{
		logger.info("Getting ACLBySharedFolderId: "+itemID);
		if(itemID==null || itemID.isEmpty()){
			logger.warn("Getting ACLBySharedFolderId identifier is null or empty, returning null");
			return null;
		}
		try {

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemID);

			if(isASharedFolder(item, false)){ //IS A SHARED FOLDER

				//TODO REMOVE PARENT SHARED FOLDER
//				WorkspaceSharedFolder parentSharedFolder = (WorkspaceSharedFolder) workspace.getItem(item.getIdSharedFolder());
				WorkspaceSharedFolder sharedFolder = (WorkspaceSharedFolder) item;
				GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();

				logger.info("Read getPrivilege from HL: "+sharedFolder.getACLUser());
				List<WorkspaceACL> wsAcls = builder.getWorkspaceACLFromACLs(Arrays.asList(sharedFolder.getACLUser()));

				if(wsAcls==null || wsAcls.isEmpty()){
					logger.info("Converted ACLBySharedFolderId is null or empty, returning null");
					return null;
				}

				logger.info("Returning first acl with id: "+wsAcls.get(0).getId());
				return wsAcls.get(0);

			}else
				throw new Exception("The item is null or not a shared folder");

		} catch (Exception e) {
			logger.error("Error in server getACLForSharedItemId", e);
			String error = "Sorry an error occurred when getting ACL for item: "+itemID;
			throw new Exception(error);
		}
	}

	/**
	 * Gets the AC ls description for shared folder id.
	 *
	 * @param folderId the folder id
	 * @return Formatted HTML DIV containing ACLs description for folderId
	 * @throws Exception the exception
	 */
	@Override
	public String getACLsDescriptionForSharedFolderId(String folderId) throws Exception{
		try {

			WorkspaceFolder wsFolder = getSharedFolderForId(folderId);
			GWTWorkspaceSharingBuilder builder = getGWTWorkspaceBuilder();
			return builder.getFormattedHtmlACLFromACLs(wsFolder.getACLOwner());
		} catch (Exception e) {
			logger.error("Error in server get getACLForFolderId", e);
			String error = "Sorry an error occurred when getting ACL rules for selected folder. "+e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Gets the shared folder for id.
	 *
	 * @param folderId the folder id
	 * @return the shared folder for id
	 * @throws Exception the exception
	 */
	private WorkspaceFolder getSharedFolderForId(String folderId) throws Exception{

		if(folderId == null)
			throw new Exception("Folder id is null");

		logger.trace("Get SharedFolderForId: "+folderId);

		Workspace workspace = getWorkspace();
		WorkspaceItem wsItem = null;

		try{
			wsItem = workspace.getItem(folderId);
		}catch(Exception e){
			logger.error("Get SharedFolderForId error on folder id: "+folderId, e);
			throw new Exception("Sorry, an error has occurred on the server when retrieving item with id: "+folderId+". Try again later!");
		}

		if(isASharedFolder(wsItem, false)){
			logger.trace("Get SharedFolderForId: folder id "+folderId+" is shared");

			//TODO REMOVE wsItem.getIdSharedFolder()
//			WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) workspace.getItem(wsItem.getIdSharedFolder());
			WorkspaceFolder wsFolder = (WorkspaceFolder) workspace.getItem(wsItem.getId());

			if(wsFolder!=null){
				logger.info("Get SharedFolderForId return name: "+wsFolder.getName());
				return wsFolder;

			//TODO USE THIS
//			return (WorkspaceFolder) wsItem;

			}else{
				logger.warn("Source item is not a shared folder, throw exception");
				throw new Exception("Source item is not a shared folder");
			}
		}else{
			logger.warn("Source item is null or not shared, throw exception");
			throw new Exception("Source item is null or not shared for id: "+folderId);
		}
	}

	/**
	 * Checks if is a shared folder.
	 *
	 * @param itemID the item id
	 * @param asRoot true check if itemID is root, not otherwise
	 * @return true, if is a shared folder
	 */

	public boolean isASharedFolder(String itemID, boolean asRoot){
		try {

			if(itemID==null)
				throw new Exception("ItemId is null");

			Workspace workspace = getWorkspace();
			WorkspaceItem item = workspace.getItem(itemID);

			return isASharedFolder(item, asRoot);

		}catch(Exception e){
			logger.error("Error in server isASharedFolder", e);
			return false;
		}
	}

	/**
	 * Checks if is a shared folder.
	 *
	 * @param item the item
	 * @param asRoot the as root
	 * @return true, if is a shared folder
	 */
	public boolean isASharedFolder(WorkspaceItem item, boolean asRoot){
		try {

			if(item!=null && item.isFolder() && item.isShared()){ //IS A SHARED SUB-FOLDER
				if(asRoot)
					return item.getType().equals(WorkspaceItemType.SHARED_FOLDER); //IS ROOT?

				return true;
			}

			return false;
		}catch(Exception e){
			logger.error("Error in server isASharedFolder", e);
			return false;
		}
	}

	/**
	 * Check list share name for group.
	 *
	 * @param listAlreadySharedContact the list already shared contact
	 * @return the list
	 */
	/*private static List<CredentialModel> checkShareNameForGroup(List<CredentialModel> listAlreadySharedContact){

		if(listAlreadySharedContact==null)
			return null;

		for (CredentialModel credentialModel : listAlreadySharedContact) {
			if(credentialModel.isGroup() && (credentialModel.getName()==null || credentialModel.getName().isEmpty())){

				//RECOVERING VRE NAME
				if(credentialModel.getLogin().contains("/")){

					int start = credentialModel.getLogin().lastIndexOf("/");
					int end = credentialModel.getLogin().length();

					if(start<end){
						credentialModel.setName(credentialModel.getLogin().substring(start+1, end));
					}
				}
			}
		}

		return listAlreadySharedContact;
	}*/

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		/*try {
			String root="gcube";
			String nameVO = "devsec";
			String vrePath = ConstantsSharing.PATH_SEPARATOR+root + ConstantsSharing.PATH_SEPARATOR + nameVO + ConstantsSharing.PATH_SEPARATOR;

			List<VO> lsVOs = WsUtil.getVresFromInfrastructure(root, nameVO);
			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			List<InfoContactModel> groups = builder.buildGXTListContactsModelFromVOs(lsVOs, vrePath);

			for (VO vo : lsVOs) {
				System.out.println(vo.getName());

				for (VRE vre : vo.getVres()) {
					System.out.println("VRE: "+vre.getName());
				}
			}

//			for (InfoContactModel infoContactModel : groups) {
//				System.out.println(infoContactModel);
//			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		/*List<CredentialModel> list = new ArrayList<CredentialModel>();
		list.add(new CredentialModel(null, "test.user", false));
		list.add(new CredentialModel(null, "/gcube/devsec/devVRE", true));
		list.add(new CredentialModel(null, "francesco.mangiacrapa", false));
		list.add(new CredentialModel(null, "devVRE", true));
		list.add(new CredentialModel(null, "/gcube/devsec/NextNext", true));

		list = checkListShareNameForGroup(list);

		for (CredentialModel credentialModel : list) {
			System.out.println(credentialModel);
		}*/
	}
}
