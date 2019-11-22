package org.gcube.portlets.widgets.workspacesharingwidget.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.exceptions.StorageHubException;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.WorkspaceSharingService;
import org.gcube.portlets.widgets.workspacesharingwidget.server.notifications.NotificationsProducer;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.AccessTypeComparator;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.PortalContextInfo;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.ScopeUtility;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.UserUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.AllowAccess;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ExtendedWorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ReportAssignmentACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.SessionExpiredException;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.UserVRE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa Aug 3, 2015
 */
@SuppressWarnings("serial")
public class WorkspaceSharingServiceImpl extends RemoteServiceServlet implements WorkspaceSharingService {

	private static Logger logger = LoggerFactory.getLogger(WorkspaceSharingServiceImpl.class);

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * Gets the notification producer.
	 *
	 * @param request
	 *            the request
	 * @return the notification producer
	 */
	protected NotificationsProducer getNotificationProducer(HttpServletRequest request) {

		return WsUtil.getNotificationProducer(request);
	}

	/**
	 * Gets the scope util filter.
	 *
	 * @return the scope util filter
	 */
	protected ScopeUtility getScopeUtilFilter() {

		return WsUtil.getScopeUtilFilter(this.getThreadLocalRequest());
	}

	/**
	 * Checks if is test mode.
	 *
	 * @return true, if is test mode
	 */
	protected boolean isTestMode() {
		return !WsUtil.isWithinPortal(); // IS NOT INTO PORTAL
	}

	/*
	 * private Workspace getWorkspace(HttpServletRequest httpServletRequest)
	 * throws org.gcube.common.homelibrary.home.workspace.exceptions.
	 * WorkspaceFolderNotFoundException,
	 * org.gcube.common.homelibrary.home.exceptions.InternalErrorException,
	 * HomeNotFoundException {
	 * 
	 * logger.trace("Get Workspace"); PortalContextInfo info =
	 * WsUtil.getPortalContext(this.getThreadLocalRequest());
	 * logger.trace("PortalContextInfo: " + info);
	 * 
	 * ScopeProvider.instance.set(info.getCurrentScope());
	 * logger.trace("Scope provider instancied");
	 * 
	 * Workspace workspace = HomeLibrary.getUserWorkspace(info.getUsername());
	 * return workspace; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getAllContacts(boolean, boolean)
	 */
	@Override
	public List<InfoContactModel> getAllContacts() throws Exception {

		try {
			logger.info("Call getAllContacts()");

			if (isTestMode()) {
				logger.debug("WORKSPACE PORTLET IS IN TEST MODE - RETURN TEST USERS AND GROUPS");
				GWTWorkspaceSharingBuilder.getHashTestUsers();
				List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();
				for (String key : GWTWorkspaceSharingBuilder.getHashTestUsers().keySet()) {
					InfoContactModel contact = GWTWorkspaceSharingBuilder.getHashTestUsers().get(key);
					listContactsModel.add(contact);
				}
				return listContactsModel;
			}

			PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

			List<InfoContactModel> listContactsModel = builder.buildGXTListContactsModelFromUserModel(info);

			// listContactsModel.addAll(builder.getGXTListContactsModelFromVOs(info));

			logger.debug("Get all Gateway Users returning a list having size: " + listContactsModel.size());
			return listContactsModel;

		} catch (Exception e) {
			logger.error("Error in getAllContacts(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving the contacts!", e);
		}
	}

	@Override
	public List<InfoContactModel> getUsersByKeyword(String keyword) throws Exception {

		try {
			logger.info("Call getUsersByKeyword(): keyword=" + keyword);

			PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

			List<InfoContactModel> listContactsModel = builder.searchUsersByKeyword(info, keyword);

			logger.debug("Get Users by keyword returning a list having size: " + listContactsModel.size());
			return listContactsModel;

		} catch (Exception e) {
			logger.error("Error in getUsersByKeyword(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving list of contacts!", e);
		}
	}

	@Override
	public List<UserVRE> getUserVREList() throws Exception {
		try {

			if (isTestMode()) {
				logger.debug("WORKSPACE PORTLET IS IN TEST MODE - RETURN TEST VREs");

				return GWTWorkspaceSharingBuilder.getUserVREsListTest();
			}

			PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

			List<UserVRE> listContactsModel = builder.getUserVREList(info);

			return listContactsModel;
		} catch (Exception e) {
			logger.error("Error in getUserVREList(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving User VRE List!", e);
		}
	}

	@Override
	public List<InfoContactModel> getAllContactsByVRE(UserVRE gGroup) throws Exception {

		try {
			logger.info("Call getAllContactsByVRE(): " + gGroup);

			if (isTestMode()) {
				logger.debug("WORKSPACE PORTLET IS IN TEST MODE - RETURN TEST USERS AND GROUPS");
				GWTWorkspaceSharingBuilder.getHashTestUsers();
				List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();
				for (String key : GWTWorkspaceSharingBuilder.getHashTestUsers().keySet()) {
					InfoContactModel contact = GWTWorkspaceSharingBuilder.getHashTestUsers().get(key);
					listContactsModel.add(contact);
				}
				return listContactsModel;
			}

			PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

			List<InfoContactModel> listContactsModel = builder.buildGXTListContactsModelByVRE(info, gGroup);

			// listContactsModel.addAll(builder.getGXTListContactsModelFromVOs(info));

			logger.debug("Get all Gateway Users returning a list having size: " + listContactsModel.size());
			return listContactsModel;

		} catch (Exception e) {
			logger.error("Error in getAllContactsByVRE(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving the contacts in the VRE!", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getListUserSharedByFolderSharedId(java.lang.
	 * String)
	 */
	@Override
	public List<InfoContactModel> getListUserSharedByFolderSharedId(String itemId) throws Exception {
		LocalDateTime startTime = LocalDateTime.now();
		logger.info("Call getListUserSharedByFolderSharedId() Start Time: " + startTime.format(formatter));

		try {
			logger.info("Call getListUserSharedByFolderSharedId(): [folderId=" + itemId + "]");
			if (itemId == null || itemId.isEmpty()) {
				logger.error("Error in getListUserSharedByFolderSharedId(), invalid folder id: [id=" + itemId + "]");
				throw new Exception("Invalid item id requested: " + itemId);
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);
			FolderContainer folderContainer = openResolver.asFolder();
			FolderItem folder = folderContainer.get();

			if (folder.isShared()) {
				if (folder instanceof SharedFolder) {
					SharedFolder sharedFolder = (SharedFolder) folder;

					return retrieveUsersListFromSharedFolder(sharedFolder);
				} else {
					FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
					FolderItem rootSharedFolder = rootSharedFolderContainer.get();
					if (rootSharedFolder instanceof SharedFolder) {
						SharedFolder sharedFolder = (SharedFolder) rootSharedFolder;
						return retrieveUsersListFromSharedFolder(sharedFolder);
					} else {
						String error = "The root shared folder has a invalid type: RootSharedFolder[id="
								+ rootSharedFolder.getId() + "]";
						logger.error(error);
						throw new Exception(error);
					}
				}
			} else {
				logger.info("The item with id: " + itemId + " is not shared a folder!");
				return new ArrayList<InfoContactModel>();
			}

		} catch (Exception e) {
			logger.error("Error in getListUserSharedByFolderSharedId(): " + e.getLocalizedMessage(), e);
			throw new Exception(e.getLocalizedMessage(), e);
		} finally {
			LocalDateTime endTime = LocalDateTime.now();
			logger.info("Call getListUserSharedByFolderSharedId() End Time: " + endTime.format(formatter));

			long diff = ChronoUnit.MILLIS.between(startTime, endTime);
			logger.info("Call getListUserSharedByFolderSharedId() Delay: MILLIS=" + diff);

		}
	}

	/**
	 * utility method extract the @domain.com from an email address
	 * return @unknown-domain in case of no emails
	 */
	private String extractDomainFromEmail(String email) {
		int index = email.indexOf('@');
		if (index > 0)
			return email.substring(index);
		else
			return "@unknown-domain";
	}

	private List<InfoContactModel> retrieveUsersListFromSharedFolder(SharedFolder sharedFolder) throws Exception {
		Metadata metadata = sharedFolder.getUsers();
		if (metadata != null) {
			Map<String, Object> map = metadata.getMap();
			if (map != null && !map.isEmpty()) {
				List<InfoContactModel> listShared = new ArrayList<>();
				List<String> keys = new ArrayList<String>(map.keySet());

				LocalDateTime startTime = LocalDateTime.now();
				logger.info("Liferay retrieve users Start Time: " + startTime.format(formatter));

				UserManager um = GWTWorkspaceSharingBuilder.getLiferayUserManager();
				GCubeUser curr;
				for (String username : keys) {
					curr = null;
					try {
						curr = um.getUserByUsername(username);
					} catch (Throwable e) {
						logger.warn("Invalid info for user " + username, e);
					}
					if (curr != null && curr.getFullname() != null && !curr.getFullname().isEmpty()) {
						InfoContactModel userInfo = new InfoContactModel(username, username, curr.getFullname(),
								extractDomainFromEmail(curr.getEmail()), false);
						listShared.add(userInfo);
					}
				}
				LocalDateTime endTime = LocalDateTime.now();
				logger.info("Liferay retrieve users End Time: " + endTime.format(formatter));

				long diff = ChronoUnit.MILLIS.between(startTime, endTime);
				logger.info("Liferay Delay: MILLIS=" + diff);
				return listShared;
			} else {
				logger.info("The folder with id: " + sharedFolder.getId() + " has a invalid map!");
				return new ArrayList<InfoContactModel>();
			}
		} else {
			logger.info("The folder with id: " + sharedFolder.getId() + " has a invalid metadata!");
			return new ArrayList<InfoContactModel>();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getOwnerByItemId(java.lang.String)
	 */
	@Override
	public InfoContactModel getOwnerByItemId(String itemId) throws Exception {

		LocalDateTime startTime = LocalDateTime.now();
		logger.info("Call getOwnerByItemId() Start Time: " + startTime.format(formatter));

		try {
			logger.info("Call getOwnerByItemId(): [itemId=" + itemId + "]");
			if (itemId == null || itemId.isEmpty()) {
				logger.error("Error in getOwnerByItemId(), invalid item id: " + itemId);
				throw new Exception("Invalid request, item id: " + itemId);
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForFile = shc.open(itemId);
			ItemContainer<Item> itemContainer = openResolverForFile.asItem();
			Item item = itemContainer.get();
			logger.debug("Retrieved Item: " + item);
			if (item != null) {
				logger.debug("Owner: " + item.getOwner());
				if (item.getOwner() != null && !item.getOwner().isEmpty()) {
					try {
						UserManager um = GWTWorkspaceSharingBuilder.getLiferayUserManager();
						GCubeUser curr = um.getUserByUsername(item.getOwner());

						if (curr != null) {
							InfoContactModel infoContactModel = new InfoContactModel(String.valueOf(curr.getUserId()),
									curr.getUsername(), curr.getFullname(), extractDomainFromEmail(curr.getEmail()),
									false);
							logger.debug("Owner found: " + infoContactModel);
							return infoContactModel;
						}
					} catch (Throwable e) {

					}
					logger.debug("Owner not found from user model!");
					return new InfoContactModel(item.getOwner(), item.getOwner(), item.getOwner(), "", false);
				} else {
					logger.debug("Owner not found from item");
					return new InfoContactModel();
				}

			} else {
				logger.debug("Item retrieved is null");
				return new InfoContactModel();
			}
		} catch (Exception e) {
			logger.error("Error in getOwnerByItemId(): " + e.getLocalizedMessage(), e);
			throw new Exception(e.getMessage());
		} finally {
			LocalDateTime endTime = LocalDateTime.now();
			logger.info("Call getOwnerByItemId() End Time: " + endTime.format(formatter));

			long diff = ChronoUnit.MILLIS.between(startTime, endTime);
			logger.info("Call getOwnerByItemId() Delay: MILLIS=" + diff);

		}
	}

	/**
	 * Gets the AC ls.
	 *
	 * @return the AC ls
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<WorkspaceACL> getACLs() throws Exception {
		try {
			logger.info("Call getACLs()");
			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			return builder.getWorkspaceACLFromAccessType(Arrays.asList(AccessType.values()));
		} catch (Exception e) {
			logger.error("Error in getACLs(): " + e.getLocalizedMessage(), e);
			String error = ConstantsSharing.SERVER_ERROR + " get ACL rules. " + e.getMessage();
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getMyLogin()
	 */
	@Override
	public String getMyLogin() throws Exception {
		try {
			logger.info("Call getMyLogin()");
			PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());
			logger.debug("Username: " + info.getUsername());
			return info.getUsername();
		} catch (Exception e) {
			logger.error("Error in getMyLogin(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving user information: " + e.getLocalizedMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getFileModelByWorkpaceItemId(java.lang.String)
	 */
	@Override
	public FileModel getFileModelByWorkpaceItemId(String itemId) throws Exception {

		try {
			logger.info("Call getFileModelByWorkpaceItemId(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				logger.error("The item id is invalid: " + itemId);
				throw new Exception("Invalid request item id is invalid: " + itemId);
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			ItemContainer<Item> itemContainer = openResolverForItem.asItem();
			Item item = itemContainer.get();
			logger.debug("Retrieved Item: " + item);
			if (item == null) {
				String error = "Error item not found: [itemId=" + itemId + "]";
				logger.error(error);
				throw new Exception(error);
			}
			if (item.getParentId() == null || item.getParentId().isEmpty()) {
				String error = "Error parent item not found: [itemId=" + itemId + "]";
				logger.error(error);
				throw new Exception(error);
			}

			OpenResolver openResolverForParent = shc.open(itemId);
			ItemContainer<Item> parentContainer = openResolverForParent.asItem();
			Item parentItem = parentContainer.get();
			logger.debug("Retrieved Parent: " + parentItem);

			// Long startTime = System.currentTimeMillis();
			//
			// Long endTime = System.currentTimeMillis() - startTime;
			// String time = String.format("%d msc %d sec", endTime,
			// TimeUnit.MILLISECONDS.toSeconds(endTime));
			// logger.debug("get child for Grid by id returning element in " +
			// time);

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

			FileModel parent = null;
			if (parentItem != null) {
				logger.info("Folder parent has id: " + parentItem.getId() + " and name: " + parentItem.getName());
				parent = builder.buildGXTFileModelItem(parentItem, null);
			} else
				logger.info("Folder parent for item: " + item.getId() + " is null");

			return builder.buildGXTFileModelItem(item, parent);
		} catch (Exception e) {
			logger.error("Error in getFileModelByWorkpaceItemId(): " + e.getLocalizedMessage(), e);
			String error = ConstantsSharing.SERVER_ERROR + " retrieving the item from workspace. "
					+ e.getLocalizedMessage();
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * isSessionExpired()
	 */
	@Override
	public boolean isSessionExpired() throws Exception {
		return WsUtil.isSessionExpired(this.getThreadLocalRequest());
	}

	// DEBUG
	/**
	 * Prints the contacts.
	 *
	 * @param listContacts
	 *            the list contacts
	 */
	// private void printContacts(List<InfoContactModel> listContacts) {
	//
	// boolean testMode = isTestMode();
	// if (testMode)
	// System.out.println("Contacts: ");
	// else
	// logger.debug("Contacts:");
	//
	// for (InfoContactModel infoContactModel : listContacts) {
	// if (testMode)
	// System.out.println("User: " + infoContactModel);
	// else
	// logger.debug("User: " + infoContactModel);
	// }
	// }

	@Override
	public boolean shareFolder(String itemId, String name, String description, String parentId,
			List<InfoContactModel> listContacts, boolean isNewFolder, WorkspaceACL acl) throws Exception {

		if (isSessionExpired())
			throw new SessionExpiredException();

		try {
			logger.info("Call shareFolder(): [id=" + itemId + ", name=" + name + ", description=" + description
					+ ", parentId=" + parentId + ", isNewFolder=" + isNewFolder + "]");
			if (listContacts == null || listContacts.isEmpty()) {
				logger.error("Invalid contacts requested: " + listContacts);
				throw new Exception("Invalid contacts requested");

			}
			logger.debug("ListContacts size: " + listContacts.size());

			List<String> listLogin = UserUtil.getListLoginByInfoContactModel(listContacts);

			boolean created = false;
			if (!listLogin.isEmpty()) {
				FolderContainer folderContainer = null;
				if (!isNewFolder) {
					logger.info("Sharing item: [id=" + itemId + "]");
					if (itemId == null || itemId.isEmpty()) {
						logger.error("Invalid folder id requested: " + itemId);
						throw new Exception("Invalid folder id requested: " + itemId);
					}
					StorageHubClient shc = new StorageHubClient();
					OpenResolver openResolverForItem = shc.open(itemId);
					folderContainer = openResolverForItem.asFolder();
				} else {
					if (parentId == null || parentId.isEmpty()) {
						logger.error("Invalid parent folder id requested: " + parentId);
						throw new Exception("Invalid  parent folder id requested: " + parentId);
					}
					if (name == null || name.isEmpty()) {
						logger.error("Invalid folder name: " + name);
						throw new Exception("Invalid folder name: " + name);
					}

					StorageHubClient shc = new StorageHubClient();
					OpenResolver openResolverForItem = shc.open(parentId);
					FolderContainer parentFolderContainer = openResolverForItem.asFolder();
					folderContainer = parentFolderContainer.newFolder(name, description);
				}
				FolderItem folderItem = folderContainer.get();
				if (folderItem != null) {
					if (folderItem.isShared()) {
						logger.debug("Folder is shared");
						if (folderItem instanceof SharedFolder) {
							logger.debug("Folder type: SharedFolder");
							SharedFolder sharedFolder = (SharedFolder) folderItem;
							String currentUser = getMyLogin();
							logger.debug("Current User: " + currentUser);

							Set<String> alreadySharedUsers = null;
							Metadata usersMetadata = sharedFolder.getUsers();
							if (usersMetadata != null) {
								Map<String, Object> usersMap = usersMetadata.getMap();
								if (usersMap != null) {
									alreadySharedUsers = usersMap.keySet();
									logger.debug("Already shared logins: " + alreadySharedUsers);
									if (alreadySharedUsers.contains(currentUser)) {
										alreadySharedUsers.remove(currentUser);
									}
								}
							}

							Set<String> logins = new HashSet<>(listLogin);
							if (logins.contains(currentUser)) {
								logins.remove(currentUser);
							}

							logger.debug("Share logins: " + logins);
							folderContainer = folderContainer.share(logins, getAccessType(acl));
							logger.debug("Shared done");

							if (alreadySharedUsers != null && !alreadySharedUsers.isEmpty()) {
								alreadySharedUsers.removeAll(logins);
								if (!alreadySharedUsers.isEmpty()) {
									logger.debug("Unshare the logins: " + alreadySharedUsers);
									folderContainer = folderContainer.unshare(alreadySharedUsers);
									logger.debug("Unshare done");
								}
							}

							created = true;
							NotificationsProducer np = getNotificationProducer(this.getThreadLocalRequest());
							np.notifyFolderSharing(listContacts, folderContainer.get());
						} else {
							logger.debug("The folder is already shared");
							throw new Exception("The folder selected is already shared!");
						}
					} else {
						logger.debug("Folder is not shared");
						Set<String> logins = new HashSet<>(listLogin);
						logger.debug("Share logins: " + logins);
						folderContainer = folderContainer.share(logins, getAccessType(acl));
						logger.debug("Shared done");
						created = true;
						NotificationsProducer np = getNotificationProducer(this.getThreadLocalRequest());
						np.notifyFolderSharing(listContacts, folderContainer.get());

					}
				} else {
					logger.error("Invalid folder retrieved: [id=" + itemId + "]");
					throw new Exception("Invalid folder retrieved: [id=" + itemId + "]");
				}
			} else {
				logger.error("The list of users is invalid!");
				throw new Exception("The list of users is invalid");

			}

			return created;

		} catch (Exception e) {
			logger.error("Error in shareFolder(): " + e.getLocalizedMessage(), e);
			String error = ConstantsSharing.SERVER_ERROR + " sharing item.";
			throw new Exception(error);
		}
	}

	private AccessType getAccessType(WorkspaceACL acl) {
		logger.debug("Convert GXTACL to AccessType: " + acl);
		if (acl.getAclType() != null) {
			switch (acl.getAclType()) {
			case READ_ONLY:
				return AccessType.READ_ONLY;
			case WRITE_ALL:
				return AccessType.WRITE_ALL;
			case WRITE_OWNER:
				return AccessType.WRITE_OWNER;
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getInfoContactModelsFromCredential(java.util.
	 * List)
	 */
	@Override
	public List<InfoContactModel> getInfoContactModelsFromCredential(List<CredentialModel> listAlreadySharedContact)
			throws Exception {
		try {
			logger.info("Call getInfoContactModelsFromCredential()");

			if (listAlreadySharedContact == null || listAlreadySharedContact.size() == 0)
				throw new Exception("Credentials list is null or empty");

			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			List<InfoContactModel> listContacts = new ArrayList<InfoContactModel>(listAlreadySharedContact.size());
			for (CredentialModel credentialModel : listAlreadySharedContact) {

				if (!credentialModel.isGroup()) {
					InfoContactModel contact = builder.buildGxtInfoContactFromPortalLogin(credentialModel.getLogin());
					contact.setReferenceCredential(credentialModel);
					listContacts.add(contact);
					logger.trace("Converted contact: " + credentialModel + ", into: " + contact);
				} else if (credentialModel.getName() == null || credentialModel.getName().isEmpty()) {
					InfoContactModel contact = builder.buildGxtInfoContactFromPortalGroup(credentialModel.getLogin());
					contact.setReferenceCredential(credentialModel);
					listContacts.add(contact);
					logger.trace("Converted group: " + credentialModel + ", into: " + contact);
				}
			}

			return listContacts;

		} catch (Exception e) {
			logger.error("Error in getInfoContactModelsFromCredential(): " + e.getLocalizedMessage(), e);
			String error = ConstantsSharing.SERVER_ERROR + " retrieving contacts info from user credentials.";
			throw new Exception(error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getAdministratorsByFolderId(java.lang.String)
	 */
	@Override
	public List<InfoContactModel> getAdministratorsByFolderId(String itemId) throws Exception {

		try {
			logger.info("Call getAdministratorsByFolderId(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				String error = "Invalid shared folder: [id=" + itemId + "]";
				logger.error(error);
				throw new Exception(error);
			}
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					return retrieveAdminContactsFromACLs(folderContainer);

				} else {
					FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
					FolderItem rootSharedFolder = rootSharedFolderContainer.get();
					if (rootSharedFolder instanceof SharedFolder) {
						return retrieveAdminContactsFromACLs(rootSharedFolderContainer);
					} else {
						String error = "The root shared folder has a invalid type: RootSharedFolder[id="
								+ rootSharedFolder.getId() + "]";
						logger.error(error);
						throw new Exception(error);
					}
				}
			} else {
				logger.error("The item requested is not a valid shared folder : [itemId=" + itemId + "]");
				throw new Exception("The item requested is not a valid shared folder : [itemId=" + itemId + "]");
			}

		} catch (Exception e) {
			logger.error("Error in getAdministratorsByFolderId(): " + e.getLocalizedMessage(), e);
			throw new Exception("Sorry an error occurred on getting Administrators");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * addAdministratorsByFolderId(java.lang.String, java.util.List)
	 *
	 * true if administrators have been added, false otherwise
	 */
	/**
	 * Adds the administrators by folder id.
	 *
	 * @param itemId
	 *            the folder id
	 * @param listContactLogins
	 *            the list contact logins
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public boolean addAdministratorsByFolderId(String itemId, List<String> listContactLogins) throws Exception {
		try {
			logger.info("Call addAdministratorsByFolderId(): [itemId=" + itemId + ", listContactLogins"
					+ listContactLogins + "]");
			if (itemId == null || listContactLogins == null || listContactLogins.size() == 0)
				return false;

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();

			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					logger.debug("Folder type: SharedFolder");
					String currentUser = getMyLogin();
					logger.debug("Current User: " + currentUser);

					Set<String> alreadyAdministrators = new HashSet<>();

					List<ACL> acls = folderContainer.getAcls();
					for (ACL acl : acls) {
						if (acl.getPricipal().compareTo(currentUser) != 0) {
							for (AccessType accessType : acl.getAccessTypes()) {
								if (AccessType.ADMINISTRATOR.compareTo(accessType) == 0) {
									alreadyAdministrators.add(acl.getPricipal());
								}
							}
						}
					}
					
					logger.debug("Share with new Administrators");
					HashSet<String> shareUsers = new HashSet<>();
					boolean alreadyShared;
					for (String user : listContactLogins) {
						if (currentUser.compareTo(user) != 0) {
							alreadyShared = false;
							for (ACL acl : acls) {
								if (user.compareTo(acl.getPricipal()) == 0) {
									alreadyShared = true;
									break;
								}
							}
							if (!alreadyShared) {
								shareUsers.add(user);
							}
						}
					}
					
					logger.debug("Update ACL for add Administrators");
					if (shareUsers.isEmpty()) {
						for (String user : listContactLogins) {
							if (currentUser.compareTo(user) != 0) {
								folderContainer = folderContainer.changeAcls(user, AccessType.ADMINISTRATOR);
							}
						}
					} else {
						folderContainer = folderContainer.share(shareUsers, AccessType.ADMINISTRATOR);
						HashSet<String> aclUpdateUsers = new HashSet<>(listContactLogins);
						aclUpdateUsers.removeAll(shareUsers);
						for (String user : aclUpdateUsers) {
							if (currentUser.compareTo(user) != 0) {
								folderContainer = folderContainer.changeAcls(user, AccessType.ADMINISTRATOR);
							}
						}
					}

				
					
					logger.debug("Update Administrators done");
					if (!alreadyAdministrators.isEmpty()) {
						alreadyAdministrators.removeAll(listContactLogins);
						if (!alreadyAdministrators.isEmpty()) {
							logger.debug("Update to WriteOwner the users: " + alreadyAdministrators);
							for (String user : alreadyAdministrators) {
								folderContainer = folderContainer.changeAcls(user, AccessType.WRITE_OWNER);
							}
							logger.debug("Update to WriteOwner done");
						}
					}

					logger.debug("Administrators Updated");
					return true;

				} else {
					String error = "Attention, the set administrators operation can only be done on the root shared folder. "
							+ "Please, select the root shared folder if you want to set the administrators.";
					logger.error(error);
					throw new Exception(error);

				}
			} else {
				logger.error("The item requested is not a valid shared folder : [itemId=" + itemId + "]");
				throw new Exception(
						"The item requested is not a valid shared folder. Impossible set the administrators.");
			}

		} catch (Exception e) {
			logger.error("Error in addAdministratorsByFolderId(): " + e.getLocalizedMessage(), e);
			throw e;
		}

	}

	private List<InfoContactModel> retrieveAdminContactsFromACLs(FolderContainer folderContainer) throws Exception {
		if (folderContainer == null) {
			logger.debug("FolderContainer is null");
			return new ArrayList<InfoContactModel>();
		} else {
			logger.debug("Retrieve Admins for: " + folderContainer.get());
			List<String> admins = new ArrayList<>();

			List<ACL> acls = folderContainer.getAcls();
			logger.debug("Retrieved acls: " + acls);
			for (ACL acl : acls) {
				boolean isAdmin = false;
				for (AccessType accesstype : acl.getAccessTypes()) {
					if (accesstype != null && accesstype == AccessType.ADMINISTRATOR) {
						isAdmin = true;
						break;
					}
				}
				if (isAdmin) {
					admins.add(acl.getPricipal());
				}
			}
			logger.debug("Retrieved admins list: " + admins);
			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			return builder.buildGxtInfoContactsFromPortalLogins(admins);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.widgets.workspacesharingwidget.client.rpc.
	 * WorkspaceSharingService#getACLForSharedItemId(java.lang.String)
	 *
	 * return the WorkspaceACL of shared folder, null otherwise
	 */
	@Override
	public WorkspaceACL getACLsForSharedFolderId(String itemId) throws Exception {
		try {
			logger.info("Call getACLsForSharedFolderId(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				String error = "Invalid shared folder: [itemId=" + itemId + "]";
				logger.error(error);
				return null;
			}
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					return retrieveContactsFromACLs(folderContainer);
				} else {
					FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
					FolderItem rootSharedFolder = rootSharedFolderContainer.get();
					if (rootSharedFolder instanceof SharedFolder) {
						return retrieveContactsFromACLs(rootSharedFolderContainer);
					} else {
						String error = "The root shared folder has a invalid type: RootSharedFolder[id="
								+ rootSharedFolder.getId() + "]";
						logger.error(error);
						throw new Exception(error);
					}
				}
			} else {
				logger.error("The item is not a shared folder: [id=" + itemId + "]");
				throw new Exception("The item is not a shared folder");
			}

		} catch (Exception e) {
			logger.error("Error in getACLsForSharedFolderId(): " + e.getLocalizedMessage(), e);
			String error = "Sorry an error occurred when getting ACL for item: " + itemId;
			throw new Exception(error);
		}
	}

	private WorkspaceACL retrieveContactsFromACLs(FolderContainer folderContainer) throws Exception {
		List<ACL> acls = folderContainer.getAcls();
		logger.debug("Retrieved acls: " + acls);
		GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
		List<WorkspaceACL> wsAcls = builder.getWorkspaceACLFromACLs(acls);
		if (wsAcls == null || wsAcls.isEmpty()) {
			logger.info("Converted ACLBySharedFolderId is null or empty, returning null");
			return null;
		}

		logger.info("Returning first acl with id: " + wsAcls.get(0).getId());
		return wsAcls.get(0);
	}

	@Override
	public String getACLsDescriptionForSharedFolderId(String itemId) throws Exception {
		try {

			logger.info("Call getACLsDescriptionForSharedFolderId(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				String error = "Invalid shared item: [id=" + itemId + "]";
				logger.error(error);
				return null;
			}
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);

			ItemContainer<?> itemContainer = openResolverForItem.asItem();
			Item item = itemContainer.get();
			if (item instanceof AbstractFileItem) {
				FileContainer fileContainer = openResolverForItem.asFile();
				AbstractFileItem file = fileContainer.get();
				if (file.isShared()) {
					String parentId = file.getParentId();
					openResolverForItem = shc.open(parentId);
					return retrieveACLFromFolder(parentId, openResolverForItem);
				} else {
					String error = "This item isn't a shared File: [id=" + itemId + "]";
					logger.error(error);
					return null;
				}

			} else {
				if (item instanceof FolderItem) {
					return retrieveACLFromFolder(itemId, openResolverForItem);
				} else {
					if (item instanceof GCubeItem) {
						GCubeItem gcubeItem = (GCubeItem) item;
						if (gcubeItem.isShared()) {
							String parentId = gcubeItem.getParentId();
							openResolverForItem = shc.open(parentId);
							return retrieveACLFromFolder(parentId, openResolverForItem);
						} else {
							String error = "This item isn't a shared GCubeItem: [id=" + itemId + "]";
							logger.error(error);
							return null;
						}
					} else {
						if (item instanceof TrashItem) {
							TrashItem trashItem = (TrashItem) item;
							if (trashItem.isShared()) {
								String parentId = trashItem.getParentId();
								openResolverForItem = shc.open(parentId);
								return retrieveACLFromFolder(parentId, openResolverForItem);
							} else {
								String error = "This item isn't a shared GCubeItem: [id=" + itemId + "]";
								logger.error(error);
								return null;
							}
						} else {
							String error = "Unknows type of item: [id=" + itemId + "]";
							logger.error(error);
							return null;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in getACLsDescriptionForSharedFolderId(): " + e.getLocalizedMessage(), e);
			String error = "Sorry an error occurred when getting ACL rules for selected item. "
					+ e.getLocalizedMessage();
			throw new Exception(error);
		}
	}

	private String retrieveACLFromFolder(String itemId, OpenResolver openResolverForItem)
			throws Exception, StorageHubException {
		FolderContainer folderContainer = openResolverForItem.asFolder();
		FolderItem folderItem = folderContainer.get();
		if (folderItem.isShared()) {
			if (folderItem instanceof SharedFolder) {
				return retrieveACLsDescription(folderContainer);
			} else {
				FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
				FolderItem rootSharedFolder = rootSharedFolderContainer.get();
				if (rootSharedFolder instanceof SharedFolder) {
					return retrieveACLsDescription(rootSharedFolderContainer);
				} else {
					String error = "The root shared folder has a invalid type: RootSharedFolder[id="
							+ rootSharedFolder.getId() + "]";
					logger.error(error);
					return null;
				}
			}
		} else {
			String error = "This item isn't a shared Folder: [id=" + itemId + "]";
			logger.error(error);
			return null;
		}
	}

	private String retrieveACLsDescription(FolderContainer folderContainer) throws Exception {
		List<ACL> acls = folderContainer.getAcls();
		logger.debug("Retrieved acls: " + acls);
		GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
		return builder.getFormattedHtmlACLFromACLs(acls);
	}

	@Override
	public boolean unSharedFolderByFolderSharedId(String itemId) throws Exception {

		if (isSessionExpired())
			throw new SessionExpiredException();

		try {
			logger.debug("Call unSharedFolderByFolderSharedId(): [id=" + itemId + "]");
			if (itemId == null || itemId.isEmpty()) {
				logger.error("Invalid folder requested: [id=" + itemId + "]");
				throw new Exception("Invalid folder requested: [id=" + itemId + "]");
			}
			boolean unshared = false;
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);
			FolderContainer folderContainer = openResolver.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem != null) {
				if (folderItem.isShared()) {
					logger.debug("Folder is shared");
					if (folderItem instanceof SharedFolder) {
						logger.debug("Folder type: SharedFolder");
						SharedFolder sharedFolder = (SharedFolder) folderItem;
						List<ACL> listACL = folderContainer.getAcls();
						logger.debug("List of ACL: " + listACL);
						String currentUser = getMyLogin();
						logger.debug("Current User: " + currentUser);
						if (listACL != null) {
							ACL userACL = null;
							for (ACL acl : listACL) {
								if (currentUser.compareTo(acl.getPricipal()) == 0) {
									userACL = acl;
									break;
								}
							}

							if (userACL != null) {
								List<AccessType> listOfAccessType = userACL.getAccessTypes();
								if (listOfAccessType != null && !listOfAccessType.isEmpty()) {
									boolean isAdmin = false;
									for (AccessType accessType : listOfAccessType) {
										if (accessType.compareTo(AccessType.ADMINISTRATOR) == 0) {
											isAdmin = true;
											break;
										}
									}

									Set<String> users;
									if (isAdmin) {
										users = sharedFolder.getUsers().getMap().keySet();
										folderContainer.unshare(users);
										logger.info("Unshared folded for users: " + users);
										ArrayList<InfoContactModel> listContacts = new ArrayList<>();
										for (String key : users) {
											InfoContactModel contact = new InfoContactModel(key, key, key, "", false);
											listContacts.add(contact);
										}
										NotificationsProducer np = getNotificationProducer(
												this.getThreadLocalRequest());
										np.notifyFolderUnSharing(listContacts, folderItem);
									} else {
										users = new HashSet<String>();
										users.add(currentUser);
										folderContainer.unshare(users);
										logger.info("Unshared folded for users: " + users);

									}
									unshared = true;

								} else {
									String msg = "Invalid AccessType permission on this folder for the user.";
									logger.error(msg);
									throw new Exception(msg);
								}
							} else {
								String msg = "Invalid permission on this folder for the user.";
								logger.error(msg);
								throw new Exception(msg);
							}
						} else {
							String msg = "Invalid ACL list for this forlder: null.";
							logger.error(msg);
							throw new Exception(msg);

						}
					} else {
						String msg = "The folder requested is not a root shared folder."
								+ "To unshare this folder you have to unshare the root shared folder!";
						logger.debug(msg);
						throw new Exception("The folder requested is not a root shared folder."
								+ "To unshare this folder you have to unshare the root shared folder");
					}
				} else {
					logger.error("Folder requested is not a shared ");
					throw new Exception("The folder requested is not shared!");

				}
			} else {
				logger.error("Invalid folder retrieved: [id=" + itemId + "]");
				throw new Exception("Invalid folder retrieved: [id=" + itemId + "]");
			}

			return unshared;

		} catch (Exception e) {
			logger.error("Error in unSharedFolderByFolderSharedId(): " + e.getLocalizedMessage(), e);
			String error = ConstantsSharing.SERVER_ERROR + " unsharing item.";
			throw new Exception(error);
		}
	}

	/**
	 * Gets the user acl for folder id.
	 *
	 * @param itemId
	 *            the folder id
	 * @return the user acl for folder id
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public List<ExtendedWorkspaceACL> getUserACLForFolderId(String itemId) throws Exception {
		try {
			logger.info("Call getUserACLForFolderId(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				String error = "Invalid shared folder: [itemId=" + itemId + "]";
				logger.error(error);
				return null;
			}
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					logger.debug("Is a shared folder search ACL in it");
					List<WorkspaceACL> acls = retrieveUserACLsFromFolderContainer(folderContainer);
					List<ExtendedWorkspaceACL> listEACL = new ArrayList<ExtendedWorkspaceACL>(acls.size());
					for (WorkspaceACL workspaceACL : acls) {
						boolean isBaseSharedFolder = true;
						ExtendedWorkspaceACL eac = new ExtendedWorkspaceACL(workspaceACL.getId(),
								workspaceACL.getAclType(), workspaceACL.getLabel(), workspaceACL.getDefaultValue(),
								workspaceACL.getUserType(), workspaceACL.getDescription(), folderItem.getOwner(),
								itemId, isBaseSharedFolder);
						logger.debug("ACL " + workspaceACL + " converted in: " + eac);
						listEACL.add(eac);
					}
					return listEACL;
				} else {
					logger.debug("Search ACL in root shared folder");
					FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
					FolderItem rootSharedFolder = rootSharedFolderContainer.get();
					if (rootSharedFolder instanceof SharedFolder) {
						List<WorkspaceACL> acls = retrieveUserACLsFromFolderContainer(rootSharedFolderContainer);
						List<ExtendedWorkspaceACL> listEACL = new ArrayList<ExtendedWorkspaceACL>(acls.size());
						for (WorkspaceACL workspaceACL : acls) {
							boolean isBaseSharedFolder = false; //is false because the root shared folder is always instance of SharedFolder
							ExtendedWorkspaceACL eac = new ExtendedWorkspaceACL(workspaceACL.getId(),
									workspaceACL.getAclType(), workspaceACL.getLabel(), workspaceACL.getDefaultValue(),
									workspaceACL.getUserType(), workspaceACL.getDescription(), folderItem.getOwner(),
									itemId, isBaseSharedFolder);
							logger.debug("ACL " + workspaceACL + " converted in: " + eac);
							listEACL.add(eac);
						}
						return listEACL;
					} else {
						String error = "The root shared folder has a invalid type: RootSharedFolder[id="
								+ rootSharedFolder.getId() + "]";
						logger.error(error);
						throw new Exception(error);
					}
				}
			} else {
				logger.error("The item is not a shared folder: [id=" + itemId + "]");
				throw new Exception("The item is not a shared folder");
			}

		} catch (Exception e) {
			logger.error("Error in getUserACLForFolderId(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving ACL rules for selected folder: " + e.getLocalizedMessage(), e);
		}
	}

	private List<WorkspaceACL> retrieveUserACLsFromFolderContainer(FolderContainer folderContainer) throws Exception {
		String currentUser = getMyLogin();
		logger.debug("Current User: " + currentUser);
		List<ACL> acls = folderContainer.getAcls();
		logger.debug("Retrieved acls: " + acls);
		ACL currentUserACL = null;
		for (ACL acl : acls) {
			if (acl.getPricipal().compareTo(currentUser) == 0) {
				currentUserACL = acl;
				break;
			}
		}

		List<WorkspaceACL> wsAcls = null;
		if (currentUserACL == null) {
			logger.debug("Search if user is in a group");
			List<GCubeGroup> listVRE = retrieveCurrentUserACLFromGroup(currentUser);
			int i = 0;
			while (currentUserACL == null && i < listVRE.size()) {
				GCubeGroup vre = listVRE.get(i);
				String vreName = vre.getGroupName();
				for (ACL acl : acls) {
					String principal = acl.getPricipal();
					String rootVO = PortalContext.getConfiguration().getInfrastructureName();
					if (principal.startsWith(rootVO)) {
						String tempScope = principal.substring(rootVO.length());
						int voIndex = tempScope.indexOf("-");
						if (voIndex != -1) {
							tempScope = tempScope.substring(voIndex + 1);
							int vreIndex = tempScope.indexOf("-");
							if (vreIndex != -1) {
								tempScope = tempScope.substring(vreIndex + 1);
								if (tempScope.compareTo(vreName) == 0) {
									logger.debug("User is member of: " + principal);
									currentUserACL = acl;
									break;
								}
							}
						}
					}
				}
				i++;
			}
			if (currentUserACL != null) {
				logger.debug("Permission found: " + currentUserACL);
				GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
				wsAcls = builder.getWorkspaceACLForUser(currentUserACL);
			} else {
				logger.debug("The user does not have permissions for this item.");
			}
		} else {
			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			wsAcls = builder.getWorkspaceACLForUser(currentUserACL);
		}

		if (wsAcls == null || wsAcls.isEmpty()) {
			logger.info("List of ACL is null or empty, returning null");
			return null;
		} else {
			logger.info("List of ACL is retrieved. Use first acl with id: " + wsAcls.get(0).getId());
			return wsAcls;
		}
	}

	private List<GCubeGroup> retrieveCurrentUserACLFromGroup(String userName) throws Exception {
		try {
			// retrieve the groups to whom a given user belongs (given the user
			// identifier)
			LiferayUserManager liferayUserManager = GWTWorkspaceSharingBuilder.getLiferayUserManager();
			long userId = liferayUserManager.getUserId(userName);

			GroupManager groupManager = GWTWorkspaceSharingBuilder.getGroupManager();
			List<GCubeGroup> listOfGroups = groupManager.listGroupsByUser(userId);

			List<GCubeGroup> vres = new ArrayList<GCubeGroup>();
			for (GCubeGroup g : listOfGroups) {
				if (groupManager.isVRE(g.getGroupId())) {
					vres.add(g);
				}
			}
			logger.debug("VREs of user retrieved");
			return vres;
		} catch (Throwable e) {
			logger.error("Error retrieving User group: " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving User group: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Gets ACLs Description For WorkspaceItem ById.
	 *
	 * @param itemId
	 *            the folder id
	 * @return a description of the ACLs
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public String getACLsDescriptionForWorkspaceItemById(String itemId) throws Exception {
		try {

			logger.info("Call getACLsDescriptionForWorkspaceItemById(): " + itemId);

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			String aclDescription = retrieveACLsDescription(folderContainer);
			logger.debug("Retrieved ACL description: " + aclDescription);
			return aclDescription;

		} catch (Exception e) {
			logger.error("Error in getACLsDescriptionForWorkspaceItemById(): " + e.getLocalizedMessage(), e);
			throw new Exception("Error retrieving ACL descriptions for requested item: " + e.getLocalizedMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#
	 * accessToFolderLink(java.lang.String)
	 */
	/**
	 * Access to folder link.
	 *
	 * @param itemId
	 *            the item id
	 * @return the allow access
	 * @throws SessionExpiredException
	 *             the session expired exception
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public AllowAccess accessToFolderLink(String itemId) throws Exception {
		try {
			logger.info("Call accessToFolderLink(): " + itemId);
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem != null) {

				PortalContextInfo info = WsUtil.getPortalContext(this.getThreadLocalRequest());
				String username = info.getUsername();

				if (folderItem.isPublicItem()) {
					logger.info("The folder is already public. Access granted to " + username);
					return new AllowAccess(itemId, true, "The folder is already public. Access granted to " + username,
							null);
				}

				logger.debug("Owner is: " + folderItem.getOwner());
				logger.info("The current context user: " + username);

				if (folderItem.getOwner() != null && folderItem.getOwner().compareToIgnoreCase(username) == 0) {
					logger.info("Access to Folder Link " + folderItem.getName() + " granted, " + username
							+ " is the owner of: " + itemId);
					return new AllowAccess(itemId, true,
							info.getUserFullName() + " is the owner of: " + folderItem.getName(), null);
				}

				try {
					List<InfoContactModel> admins = null;

					if (folderItem.isShared()) {
						if (folderItem instanceof SharedFolder) {
							admins = retrieveAdminContactsFromACLs(folderContainer);
						} else {
							FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
							FolderItem rootSharedFolder = rootSharedFolderContainer.get();
							if (rootSharedFolder instanceof SharedFolder) {
								admins = retrieveAdminContactsFromACLs(rootSharedFolderContainer);
							} else {
								String error = "The root shared folder has a invalid type: RootSharedFolder[id="
										+ rootSharedFolder.getId() + "]";
								logger.error(error);
								throw new Exception(error);
							}
						}
					} else {
						logger.error("The item requested is not a valid shared folder : [itemId=" + itemId + "]");
						throw new Exception(
								"The item requested is not a valid shared folder : [itemId=" + itemId + "]");
					}

					if (admins != null) {
						for (InfoContactModel infoContactModel : admins) {
							if (infoContactModel.getLogin().compareToIgnoreCase(username) == 0) {
								logger.info("Access to Folder Link " + logger.getName() + " granted, " + username
										+ " is the admin of: " + itemId);
								return new AllowAccess(itemId, true,
										info.getUserFullName() + " is the admin of: " + folderItem.getName(), null);
							}
						}
					} else {
						return new AllowAccess(itemId, false,
								"You have not permission to get Folder Link, you must be owner or administrator to the folder",
								"Permission not found");

					}
				} catch (Exception e) {
					return new AllowAccess(itemId, false,
							"You have not permission to get Folder Link, you must be owner or administrator to the folder",
							e.getMessage());
				}

				return new AllowAccess(itemId, false,
						"You have not permission to get Folder Link, you must be owner or administrator to the folder",
						null);
			} else {
				return new AllowAccess(itemId, false, "The item is not a folder", null);
			}

		} catch (Exception e) {
			logger.error("Error in server FormattedGcubeItemProperties: " + e.getLocalizedMessage(), e);
			String error = "Error when reading access policy to Folder Link: " + itemId + ", Refresh and try again";
			throw new Exception(error, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gcube.portlets.user.workspace.client.rpc.GWTWorkspaceService#setACLs(
	 * java.lang.String, java.util.List, java.lang.String)
	 */
	/**
	 * Sets the ac ls.
	 *
	 * @param itemId
	 *            the folder id
	 * @param listLogins
	 *            the list logins
	 * @param aclType
	 *            the acl type
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void setACLs(String itemId, List<String> listLogins, String aclType) throws Exception {
		try {
			logger.info("Call setACLs(): [itemId=" + itemId + ", listLogins=" + listLogins + ", aclType=" + aclType);
			String error = "Set ACLs is not enabled in StorageHub. You must share, or unshare a root folder for set ACLs.";
			logger.error(error);
			throw new Exception(error);
			// if(folderId == null)
			// throw new Exception("Folder id is null");
			//
			// if(listLogins==null || listLogins.size()==0)
			// throw new Exception("List Logins is null or empty");
			//
			// logger.trace("Setting ACL for folder id: "+folderId);
			// logger.trace("ACL type is: "+aclType);
			// Workspace workspace = getWorkspace();
			// WorkspaceItem wsItem = workspace.getItem(folderId);
			//
			// //IS A WORKSPACE FOLDER?
			// if(wsItem!= null && wsItem.isFolder() && wsItem.isShared()){
			// WorkspaceFolder ite;
			// if(wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER)){
			// //IS ROOT SHARED FOLDER
			// ite = (WorkspaceSharedFolder) wsItem;
			// logger.trace("Folder " +ite.getName()+" is a
			// "+WorkspaceSharedFolder.class.getName());
			// // ite = (WorkspaceSharedFolder)
			// workspace.getItemByPath(wsItem.getPath());
			// }else{
			// // IS SUB FOLDER OF THE SHARING
			// ite = (WorkspaceFolder) wsItem;
			// logger.trace("Folder " +ite.getName()+" is a
			// "+WorkspaceFolder.class.getName());
			// // ite = (WorkspaceSharedFolder)
			// workspace.getItem(wsItem.getIdSharedFolder());
			// }
			//
			// ite.setACL(listLogins, ACLType.valueOf(aclType));
			// }else
			// throw new Exception("Source item is not shared or shared
			// folder");
			//
			// logger.info("Setting ACL for "+wsItem.getName()+" completed,
			// returning");
		} catch (Exception e) {
			logger.error("Error in setACLs(): " + e.getLocalizedMessage(), e);
			String error = "Error setting permissions. " + e.getLocalizedMessage();
			throw new Exception(error, e);
		}
	}

	/**
	 * Validate acl to user.
	 *
	 * @param itemId
	 *            the folder id
	 * @param listLogins
	 *            the list logins
	 * @param aclType
	 *            the acl type
	 * @return the report assignment acl
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public ReportAssignmentACL validateACLToUser(String itemId, List<String> listLogins, String aclType)
			throws Exception {

		try {
			logger.info("Call validateACLToUser(): [itemId=" + itemId + "]");

			if (itemId == null || itemId.isEmpty()) {
				String error = "Invalid folder id: [itemId=" + itemId + "]";
				logger.error(error);
				return null;
			}

			AccessType settingACL = AccessType.valueOf(aclType);
			if (settingACL == null) {
				String error = "Invalid acl type requested: [AccessType=" + aclType + "]";
				logger.error(error);
				return null;
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(itemId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					List<ACL> acls = folderContainer.getAcls();
					logger.debug("Retrieved acls: " + acls);
					return createReportAssignementACL(acls, listLogins, settingACL);

				} else {
					FolderContainer rootSharedFolderContainer = folderContainer.getRootSharedFolder();
					FolderItem rootSharedFolder = rootSharedFolderContainer.get();
					if (rootSharedFolder instanceof SharedFolder) {
						List<ACL> acls = folderContainer.getAcls();
						logger.debug("Retrieved acls: " + acls);
						return createReportAssignementACL(acls, listLogins, settingACL);

					} else {
						String error = "The root shared folder has a invalid type: RootSharedFolder[id="
								+ rootSharedFolder.getId() + "]";
						logger.error(error);
						throw new Exception(error);
					}
				}
			} else {
				logger.error("The item is not a shared folder: [id=" + itemId + "]");
				throw new Exception("The item is not a shared folder");
			}

		} catch (Exception e) {
			logger.error("Error on setting ACLs", e);
			throw new Exception("Sorry, an error occurred when validating ACL assignment, try again later");
		}

	}

	private ReportAssignmentACL createReportAssignementACL(List<ACL> acls, List<String> listLogins,
			AccessType settingACL) {

		logger.debug("Retrieved acls: " + acls);
		List<String> admins = new ArrayList<>();

		// REMOVE ADMINS
		for (ACL acl : acls) {
			boolean isAdmin = false;
			for (AccessType accesstype : acl.getAccessTypes()) {
				if (accesstype != null && accesstype == AccessType.ADMINISTRATOR) {
					isAdmin = true;
					break;
				}
			}
			if (isAdmin) {
				admins.add(acl.getPricipal());
			}
		}

		List<String> errors = new ArrayList<String>();
		if (!admins.isEmpty()) {
			for (String admin : admins) {
				boolean removed = listLogins.remove(admin);
				logger.info("Reject username: " + admin + " as " + AccessType.ADMINISTRATOR);
				if (removed) {
					String fullname = isTestMode() ? admin : UserUtil.getUserFullName(admin);
					errors.add("Unable to grant the privilege " + settingACL + " for " + fullname + ", he/she is an: "
							+ AccessType.ADMINISTRATOR);
				}
			}
		}

		// COMPLETE REPORT
		AccessTypeComparator comparator = new AccessTypeComparator();
		List<String> validLogins = new ArrayList<String>(listLogins);
		ReportAssignmentACL reportValidation = new ReportAssignmentACL();
		logger.debug("\nChecking listLogins: " + listLogins);

		for (String username : listLogins) {
			logger.trace("\nChecking username: " + username);
			String fullname = isTestMode() ? username : UserUtil.getUserFullName(username);
			for (ACL acl : acls) {
				if (acl.getPricipal().compareTo(username) == 0) {
					checkAccessType(settingACL, errors, comparator, validLogins, username, fullname, acl);
					break;
				}
			}
		}

		logger.info("Valid logins: ");
		for (String username : validLogins) {
			logger.info("Set ACL: " + settingACL + " to " + username);
		}

		reportValidation.setAclType(settingACL.name());
		reportValidation.setErrors(errors);
		reportValidation.setValidLogins(validLogins);
		return reportValidation;
	}

	private void checkAccessType(AccessType settingACL, List<String> errors, AccessTypeComparator comparator,
			List<String> validLogins, String username, String fullname, ACL acl) {
		for (AccessType currentAccessType : acl.getAccessTypes()) {
			int cmp = comparator.compare(settingACL, currentAccessType);
			if (cmp == -1) {
				// CHANGE ACL IS NOT VALID
				logger.debug("Reject ACL: " + settingACL + " to " + username);
				validLogins.remove(username);
				errors.add("Unable to grant the privilege " + settingACL + " for " + fullname
						+ ", it's lower than (parent privilege) " + currentAccessType);
				break;
			} else if (cmp == 0) {
				// SAME ACL
				logger.debug("Skipping ACL: " + settingACL + " to " + username);
				errors.add(settingACL + " privilege for " + fullname + " already assigned");
				validLogins.remove(username);
				break;
			} else if (cmp == 1) {
				// CHANGE ACL IS VALID
				logger.debug("Valid ACL: " + settingACL + " to " + fullname);
			}
		}
	}

	/**
	 * Update acl for vre by group name.
	 *
	 * @param folderId
	 *            the folder id
	 * @param aclType
	 *            the acl type
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void updateACLForVREbyGroupName(String folderId, ACL_TYPE aclType) throws Exception {
		try {
			// TODO
			logger.debug("UpdateACLForVREbyGroupName()");
			logger.debug("folderId: " + folderId);
			logger.debug("ACL type is: " + aclType);

			if (folderId == null || folderId.isEmpty()) {
				throw new Exception("Folder id is null");
			}

			if (aclType == null) {
				throw new Exception("ACLType parameter is invalid");
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(folderId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();

			if (folderItem.isShared()) {
				if (folderItem instanceof SharedFolder) {
					logger.debug("Folder type: SharedFolder");
					SharedFolder sharedFolder = (SharedFolder) folderItem;
					if (sharedFolder.isVreFolder()) {
						logger.debug("The folder is a VreFolder");

						String currentUser = getMyLogin();
						logger.debug("Current User: " + currentUser);

						AccessType selectedAccessType = null;

						switch (aclType) {
						case ADMINISTRATOR:
							selectedAccessType = AccessType.ADMINISTRATOR;
							break;
						case READ_ONLY:
							selectedAccessType = AccessType.READ_ONLY;
							break;
						case WRITE_ALL:
							selectedAccessType = AccessType.WRITE_ALL;
							break;
						case WRITE_OWNER:
							selectedAccessType = AccessType.WRITE_OWNER;
							break;
						default:
							break;

						}

						if (selectedAccessType == null) {
							throw new Exception("ACLType parameter is invalid");
						}

						List<ACL> acls = folderContainer.getAcls();
						boolean isAdmin;
						for (ACL acl : acls) {
							if (acl.getPricipal().compareTo(currentUser) != 0) {
								isAdmin = false;
								for (AccessType accessType : acl.getAccessTypes()) {
									if (accessType.compareTo(AccessType.ADMINISTRATOR) == 0) {
										isAdmin = true;
										break;
									}
								}

								if (!isAdmin) {
									folderContainer = folderContainer.changeAcls(acl.getPricipal(), selectedAccessType);
								}
							}
						}

					} else {
						String error = "Attention, the operation can only be done on the VRE folder. "
								+ "Please, select the VRE folder if you want to set the ACL.";
						logger.error(error);
						throw new Exception(error);
					}
				} else {
					String error = "Attention, the operation can only be done on the root VRE folder. "
							+ "Please, select the root VRE folder if you want to set the ACL.";
					logger.error(error);
					throw new Exception(error);

				}
			} else {
				logger.error("The item requested is not a valid shared folder : [itemId=" + folderId + "]");
				throw new Exception("The item requested is not a valid shared folder. Impossible set the ACL.");
			}

			logger.debug("ACL updated");
		} catch (Exception e) {
			logger.error("Error in set ACLs", e);
			String error = "Error updating the permissions. " + e.getMessage();
			throw new Exception(error);
		}
	}

	/**
	 * Check notify add item to share.
	 *
	 * @param destinationItem
	 *            the destination item
	 * @param sourceSharedId
	 *            the source shared id
	 * @param folderDestinationItem
	 *            the folder destination item
	 */
	// private void checkNotifyAddItemToShare(final WorkspaceItem
	// destinationItem,
	// final String sourceSharedId, final WorkspaceItem folderDestinationItem) {
	//
	// logger.trace("checkNotifyAddItemToShare");
	//
	// if(folderDestinationItem!=null){
	//
	// try{
	// //if folder destination is shared folder
	// if(folderDestinationItem.isShared()){ //Notify Added Item To Sharing?
	// logger.trace("checkNotifyAddItemToShare destination item:
	// "+destinationItem.getName()+" sourceSharedId: "+sourceSharedId + " folder
	// destination: "+folderDestinationItem.getName());
	// //share condition is true if source shared folder is null or not equal to
	// destination shared folder
	// boolean shareChangeCondition = sourceSharedId==null ||
	// sourceSharedId.compareTo(folderDestinationItem.getIdSharedFolder())!=0;
	// logger.trace("shareChangeCondition add item: "+shareChangeCondition);
	// //if shareChangeCondition is true.. notifies added item to sharing
	// if(shareChangeCondition){
	//
	// List<InfoContactModel> listContacts =
	// getListUserSharedByFolderSharedId(folderDestinationItem.getIdSharedFolder());
	//
	// //DEBUG
	// //printContacts(listContacts);
	// Workspace workspace = getWorkspace();
	// WorkspaceItem destinationSharedFolder =
	// workspace.getItem(folderDestinationItem.getIdSharedFolder());
	// NotificationsProducer np = getNotificationProducer();
	//
	// if(destinationSharedFolder instanceof WorkspaceSharedFolder){
	// np.notifyAddedItemToSharing(listContacts, destinationItem,
	// (WorkspaceSharedFolder) destinationSharedFolder);
	// logger.trace("The notifies sent correctly");
	// }
	// else
	// logger.warn("Notifies added item: "+destinationItem+ "to share doesn't
	// sent
	// because "+destinationSharedFolder+" is not istance of
	// WorkspaceSharedFolder");
	// // np.notifyAddedItemToSharing(listContacts, (WorkspaceFolder)
	// folderDestinationItem);
	// }
	// }
	// else
	// logger.trace("folder destination is not shared");
	//
	// }catch (Exception e) {
	// logger.error("An error occurred in verifyNotifyAddItemToShare ",e);
	// }
	// }else
	// logger.warn("The notifies is failure in verifyNotifyAddItemToShare
	// because
	// folder destination item is null");
	// }

	/**
	 * Check notify move item from share.
	 *
	 * @param sourceItemIsShared
	 *            the source item is shared
	 * @param sourceItem
	 *            the source item
	 * @param sourceSharedId
	 *            the source shared id
	 * @param folderDestinationItem
	 *            the folder destination item
	 */
	// private void checkNotifyMoveItemFromShare(final boolean
	// sourceItemIsShared,
	// final WorkspaceItem sourceItem, final String sourceSharedId, final
	// WorkspaceItem folderDestinationItem) {
	//
	// logger.trace("checkNotifyMoveItemFromShare:");
	//
	// try{
	//
	// if(folderDestinationItem!=null){
	//
	// String idSharedFolder =
	// folderDestinationItem.getIdSharedFolder()!=null?folderDestinationItem.getIdSharedFolder():"";
	//
	// //share condition is true if source shared folder is not equal to
	// destination
	// shared folder
	// boolean shareChangeCondition =
	// sourceSharedId==null?false:sourceSharedId.compareTo(idSharedFolder)!=0;
	//
	// logger.trace("checkNotifyMoveItemFromShare source item:
	// "+sourceItem.getName()+" sourceSharedId: "+sourceSharedId + " folder
	// destination: "+folderDestinationItem.getName() +" sourceItemIsShared:
	// "+sourceItemIsShared);
	//
	// // System.out.println("shareChangeCondition remove item: "+
	// shareChangeCondition);
	//
	// logger.trace("shareChangeCondition remove item: "+ shareChangeCondition);
	//
	// //Notify Removed Item To Sharing?
	// //if source Item is shared and folder destination is not shared or
	// shareChangeCondition is true.. notifies removed item to sharing
	// if(sourceItemIsShared && (!folderDestinationItem.isShared() ||
	// shareChangeCondition)){
	//
	// //get contacts
	// List<InfoContactModel> listContacts =
	// getListUserSharedByFolderSharedId(sourceSharedId);
	//
	// //DEBUG
	// printContacts(listContacts);
	// Workspace workspace = getWorkspace();
	// WorkspaceItem sourceSharedFolder = workspace.getItem(sourceSharedId);
	// NotificationsProducer np = getNotificationProducer();
	//
	// if(sourceSharedFolder instanceof WorkspaceSharedFolder){
	// np.notifyMovedItemToSharing(listContacts, sourceItem,
	// (WorkspaceSharedFolder)
	// sourceSharedFolder);
	// logger.trace("The notifies was sent correctly");
	// }else
	// logger.warn("Notifies moved item: "+sourceItem+ "from share doesn't sent
	// because "+sourceSharedFolder+" is not istance of WorkspaceSharedFolder");
	// }
	//
	// }else
	// logger.warn("The notifies is failure in checkNotifyMoveItemFromShare
	// because
	// folder destination item is null");
	//
	// }catch (Exception e) {
	// logger.error("An error occurred in checkNotifyMoveItemFromShare ",e);
	// }
	//
	// }

}
