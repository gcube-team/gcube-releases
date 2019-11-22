/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.gcube.common.homelibrary.home.User;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.ExternalURL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.GenericFileItem;
import org.gcube.common.storagehub.model.items.ImageFile;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.PDFFileItem;
import org.gcube.common.storagehub.model.items.SharedFolder;
//import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.PortalContextInfo;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.ScopeUtility;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.UserUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FolderModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.UserVRE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.GXTFolderItemTypeEnum;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL.USER_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VO;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VRE;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class GWTWorkspaceSharingBuilder.
 *
 * @author Francesco Mangiacrapa Feb 25, 2014
 */
public class GWTWorkspaceSharingBuilder {

	private static Logger logger = LoggerFactory.getLogger(GWTWorkspaceSharingBuilder.class);

	private static HashMap<String, InfoContactModel> hashUserTest = null;
	private static List<UserVRE> vresListTest;

	private static LiferayUserManager liferayUserManager;
	private static GroupManager groupManager;

	public static LiferayUserManager getLiferayUserManager() {
		if (liferayUserManager == null) {
			liferayUserManager = new LiferayUserManager();
			return liferayUserManager;
		} else {
			return liferayUserManager;
		}
	}

	public static GroupManager getGroupManager() {
		if (groupManager == null) {
			groupManager = new LiferayGroupManager();
			return groupManager;
		} else {
			return groupManager;
		}

	}

	/**
	 * Used in test mode.
	 *
	 * @return the hash test users
	 */
	public static List<UserVRE> getUserVREsListTest() {
		if (hashUserTest == null) {
			vresListTest = new ArrayList<>();
			vresListTest.add(new UserVRE(21660, 21657, "NextNext", "NextNext test VRE"));
			vresListTest.add(new UserVRE(21678, 21663, "devVRE", "devVRE test VRE"));
		}
		return vresListTest;
	}

	/**
	 * Used in test mode.
	 *
	 * @return the hash test users
	 */
	public static HashMap<String, InfoContactModel> getHashTestUsers() {

		if (hashUserTest == null) {
			hashUserTest = new HashMap<String, InfoContactModel>();

			hashUserTest.put("federico.defaveri", new InfoContactModel("federico.defaveri", "federico.defaveri",
					"Federico de Faveri", "@isti.cnr.it", false));

			hashUserTest.put("antonio.gioia",
					new InfoContactModel("antonio.gioia", "antonio.gioia", "Antonio Gioia", "@isti.cnr.it", false));

			hashUserTest.put("fabio.sinibaldi", new InfoContactModel("fabio.sinibaldi", "fabio.sinibaldi",
					"Fabio Sinibaldi", "@isti.cnr.it", false));

			hashUserTest.put("pasquale.pagano", new InfoContactModel("pasquale.pagano", "pasquale.pagano",
					"Pasquale Pagano", "@isti.cnr.it", false));

			hashUserTest.put("francesco.mangiacrapa", new InfoContactModel("francesco.mangiacrapa",
					"francesco.mangiacrapa", "Francesco Mangiacrapa", "@isti.cnr.it", false));

			hashUserTest.put("massimiliano.assante", new InfoContactModel("massimiliano.assante",
					"massimiliano.assante", "Massimiliano Assante", "@isti.cnr.it", false));

			hashUserTest.put("leonardo.candela", new InfoContactModel("leonardo.candela", "leonardo.candela",
					"Leonardo Candela", "@isti.cnr.it", false));

			hashUserTest.put("valentina.marioli", new InfoContactModel("valentina.marioli", "valentina.marioli",
					"Valentina Marioli", "@isti.cnr.it", false));

			hashUserTest.put("giancarlo.panichi", new InfoContactModel("giancarlo.panichi", "giancarlo.panichi",
					"Giancarlo Panichi", "@isti.cnr.it", false));

			hashUserTest.put("devVRE", new InfoContactModel("devVRE", "devVRE", "devVRE", "", true));

			// hashTestUser.put(WsUtil.TEST_USER.toString(),
			// new InfoContactModel(
			// WsUtil.TEST_USER, WsUtil.TEST_USER,
			// WsUtil.TEST_USER_FULL_NAME));

		}

		return hashUserTest;
	}

	/**
	 * Sets the user logged.
	 * 
	 * @param info
	 *            Portal Context info
	 * @return List of contacts
	 */
	public List<InfoContactModel> getGXTListContactsModelFromVOs(PortalContextInfo info) {
		logger.info("Reading group names as scopes from Infrastructure..");
		List<InfoContactModel> listContactsModel = new ArrayList<>();
		PortalContext context = PortalContext.getConfiguration();
		logger.info("context.getInfrastructureName(): " + context.getInfrastructureName());
		// String groupName = info.getCurrentGroupName(request);
		logger.info("context.getGroupId(): " + info.getCurrGroupId());
		logger.info("context.getScope(): " + info.getCurrentScope());
		try {
			ScopeUtility scopeUtility = new ScopeUtility(info.getCurrentScope());

			if (scopeUtility.getVoName() != null) {
				logger.info("VO name is not null, trying to calculate List VO");
				List<VO> lsVOs = WsUtil.getVresFromInfrastructure(context.getInfrastructureName(),
						scopeUtility.getVoName());
				listContactsModel.addAll(buildGXTListContactsModelFromVOs(lsVOs, scopeUtility.getVo()));
			} else
				logger.warn("VO name is null, skipping list VO " + info.getCurrGroupId());

		} catch (Exception e) {
			logger.warn("An error occurred on recovering vo name, skipping list VO " + info.getCurrGroupId());
		}
		return listContactsModel;
	}

	/**
	 * Builds the gxt list contacts model from gcube group.
	 *
	 * @param list
	 *            the list
	 * @return the list
	 * @throws Exception
	 *             the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromGcubeGroup(List<GCubeGroup> list) throws Exception {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if (list == null)
			return listContactsModel;

		logger.trace("List<GCubeGroup> size returned from GcubeGroup is: " + list.size());

		logger.trace("Building list contact model...");

		for (GCubeGroup group : list) {
			try {
				String groupDN = group.getGroupName();

				if (groupDN == null || groupDN.isEmpty())
					groupDN = group.getGroupName();

				if (group.getGroupName() == null || group.getGroupName().isEmpty())
					logger.warn("Skipping group with null or empty name " + group);
				else {
					InfoContactModel contact = new InfoContactModel(group.getGroupName(), group.getGroupName(), groupDN,
							"", true);
					logger.trace("Adding group " + contact);
					listContactsModel.add(contact);
				}
			} catch (Exception e) {
				logger.warn("Dispaly name is not available to group " + group);
				logger.warn("Adding get name property " + group.getGroupName());

				if (group.getGroupName() == null || group.getGroupName().isEmpty())
					logger.warn("Skipping group with null or empty name " + group);
				else
					listContactsModel.add(new InfoContactModel(group.getGroupName(), group.getGroupName(),
							group.getGroupName(), "", true));
			}
		}

		logger.trace("List GCubeGroup contact model completed, return " + listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	/**
	 * Builds the gxt list contacts model from v os.
	 *
	 * @param listVO
	 *            the list vo
	 * @param voPath
	 *            the vo path
	 * @return the list
	 * @throws Exception
	 *             the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromVOs(List<VO> listVO, String voPath) throws Exception {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if (voPath == null) {
			voPath = ConstantsSharing.PATH_SEPARATOR;
		}

		if (voPath != null && !voPath.endsWith(ConstantsSharing.PATH_SEPARATOR)) {
			voPath += ConstantsSharing.PATH_SEPARATOR;
		}

		if (listVO == null) {
			logger.warn("List<VO> is null, returning");
			return listContactsModel;
		}

		logger.trace("List<VO> size is: " + listVO.size());
		logger.trace("Building list contact model...");
		logger.trace("voPath is: " + voPath);

		for (VO vo : listVO) {
			List<VRE> vres = vo.getVres();

			logger.trace("vo getGroupName: " + vo.getGroupName());
			logger.trace("vo getName: " + vo.getName());
			logger.trace("Building list contact model...");

			if (vres != null) {
				logger.trace("vres from VO " + vo.getName() + " having size :" + vres.size() + ", converting");
				for (VRE vre : vres) {

					if (vre.getName() != null || !vre.getName().isEmpty()) {
						String groupId = voPath + vre.getName();
						logger.trace("adding contact groupId: " + groupId + " VRE name: " + vre.getName());
						// InfoContactModel contact = new
						// InfoContactModel(groupId, groupId, vre.getName(),
						// true);
						// logger.trace("Adding group contact " + contact);
						// listContactsModel.add(contact);
					}
				}
			} else
				logger.trace("vres list is null, skipping VO " + vo.getName());
		}

		logger.trace("List GCubeGroup contact model completed, return " + listContactsModel.size() + " contacts");

		return listContactsModel;
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

	/**
	 * 
	 * @param info
	 *            Portal Context info
	 * @return the list of users
	 * @throws Exception
	 *             Error
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromUserModel(PortalContextInfo info) throws Exception {

		List<GCubeUser> listUsers = getLiferayUserManager().listUsersByGroup(info.getCurrGroupId());

		if (listUsers == null) {
			logger.error("No users found in: " + info.getCurrentScope());
			throw new Exception("An error occurred on recovering users from Portal, try again later");
		}

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		logger.trace("List<UserModel> size returned from Portal VO is: " + listUsers.size());

		logger.trace("Building list contact model list user model");
		for (GCubeUser userModel : listUsers) {
			String fullName = userModel.getFullname();

			if (fullName != null && !fullName.isEmpty())
				listContactsModel.add(new InfoContactModel(userModel.getUserId() + "", userModel.getUsername(),
						fullName, extractDomainFromEmail(userModel.getEmail()), false));
			else
				logger.trace("buildGXTListContactsModel is not returning user: " + userModel.getUsername()
						+ "because name is null or empty");
		}
		logger.trace("List contact model completed, return " + listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	/**
	 * 
	 * @param info
	 *            Portal Context info
	 * @return the list of users
	 * @throws Exception
	 *             Error
	 */
	public List<InfoContactModel> buildGXTListContactsModelByVRE(PortalContextInfo info, UserVRE gGroup)
			throws Exception {

		List<GCubeUser> listUsers = getLiferayUserManager().listUsersByGroup(gGroup.getGroupId());

		if (listUsers == null) {
			logger.error("No users found in: " + info.getCurrentScope());
			throw new Exception("An error occurred on recovering users from Portal, try again later");
		}

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		logger.trace("List<UserModel> size returned from Portal is: " + listUsers.size());

		logger.trace("Building list contact model list user model");
		for (GCubeUser userModel : listUsers) {
			String fullName = userModel.getFullname();

			if (fullName != null && !fullName.isEmpty())
				listContactsModel.add(new InfoContactModel(userModel.getUserId() + "", userModel.getUsername(),
						fullName, extractDomainFromEmail(userModel.getEmail()), false));
			else
				logger.trace("buildGXTListContactsModel is not returning user: " + userModel.getUsername()
						+ "because name is null or empty");
		}

		logger.trace("List contact model completed, return " + listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	public ArrayList<UserVRE> getUserVREList(PortalContextInfo info) throws Exception {

		try {
			logger.info("Current context is " + info.getCurrGroupId());
			ArrayList<UserVRE> contexts = new ArrayList<>();

			// GCubeGroup currentGroup = gm.getGroup(info.getCurrGroupId());
			logger.info("Current user: " + info.getUsername());

			long userId = getLiferayUserManager().getUserId(info.getUsername());

			logger.info("UserId is: " + userId);
			// Get Gateways
			List<GCubeGroup> listOfGroups = getGroupManager().listGroupsByUser(userId);
			if (listOfGroups != null && !listOfGroups.isEmpty()) {
				for (GCubeGroup gCubeGroup : listOfGroups) {
					// Test is VRE
					if (getGroupManager().isVRE(gCubeGroup.getGroupId())) {
						UserVRE gGroup = new UserVRE(gCubeGroup.getGroupId(), gCubeGroup.getParentGroupId(),
								gCubeGroup.getGroupName(), gCubeGroup.getDescription());
						contexts.add(gGroup);
					}
				}
			}

			Collections.sort(contexts, new Comparator<UserVRE>() {

				public int compare(UserVRE vre1, UserVRE vre2) {
					if (vre1.getGroupName() == null) {
						return -1;
					} else {
						if (vre2.getGroupName() == null) {
							return +1;
						} else {
							return vre1.getGroupName().compareTo(vre2.getGroupName());
						}
					}
				}
			});
			logger.debug("VRE retrieved: " + contexts.size());
			return contexts;

		} catch (Exception e) {
			logger.error("Error retrieving the list of user VRE!", e);
			throw new Exception("Error retrieving the list of VRE!", e);
		}

	}

	public List<InfoContactModel> searchUsersByKeyword(PortalContextInfo info, String keyword) throws Exception {
		try {
			logger.debug("Search use keyword: " + keyword);
			List<InfoContactModel> listInfoContactModel = new ArrayList<>();

			List<GCubeUser> users = getLiferayUserManager().searchUsersByGroup(keyword, info.getCurrGroupId());

			for (int i = 0; i < users.size() && i < 30; i++) {
				GCubeUser user = users.get(i);
				InfoContactModel icm = new InfoContactModel(String.valueOf(user.getUserId()), user.getUsername(),
						user.getFullname(), extractDomainFromEmail(user.getEmail()), false);
				listInfoContactModel.add(icm);
			}
			return listInfoContactModel;
		} catch (Exception e) {
			logger.error("Error retrieving the users by keyword: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	/**
	 * Builds the gxt info contacts from portal logins.
	 *
	 * @param listPortalLogin
	 *            the list portal login
	 * @return the list
	 */
	public List<InfoContactModel> buildGxtInfoContactsFromPortalLogins(List<String> listPortalLogin) {
		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		try {
			for (String portalLogin : listPortalLogin) {
				listContact.add(buildGxtInfoContactFromPortalLogin(portalLogin));
			}
		} catch (Exception e) {
			logger.error("Error in  build list of contacts from portal logins: " + e.getMessage(), e);
		}

		return listContact;
	}

	/**
	 * Builds the gxt info contact from portal login.
	 *
	 * @param portalLogin
	 *            the portal login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalLogin(String portalLogin) {

		if (portalLogin == null) {
			logger.warn("Contact login is null, return empty");
			portalLogin = "";
		}
		return new InfoContactModel(portalLogin, portalLogin, UserUtil.getUserFullName(portalLogin), "", false);
	}

	/**
	 * Builds the gxt info contact from portal group.
	 *
	 * @param groupLogin
	 *            the grouo login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalGroup(String groupLogin) {

		String groupName = "";
		if (groupLogin == null) {
			logger.warn("GroupLogin is null, return empty");
			groupLogin = "";
		}

		// RECOVERING VRE NAME
		if (groupLogin.contains("/")) {
			int start = groupLogin.lastIndexOf("/");
			int end = groupLogin.length();
			if (start < end)
				groupName = groupLogin.substring(start + 1, end);
			else
				groupName = groupLogin;
		}

		return new InfoContactModel(groupLogin, groupLogin, groupName, "", true);
	}

	/**
	 * Used in test mode.
	 *
	 * @param listPortalLogin
	 *            the list portal login
	 * @return the list
	 */
	protected List<InfoContactModel> buildGxtInfoContactFromPortalLoginTestMode(List<String> listPortalLogin) {

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		for (String portalLogin : listPortalLogin)
			listContact.add(getHashTestUsers().get(portalLogin));

		logger.trace("returning " + listContact.size() + " info contacts test");
		return listContact;
	}

	/**
	 * Builds the gxt file model item.
	 *
	 * @param item
	 *            the item
	 * @param parent
	 *            the parent folder model
	 * @return the file model
	 * @throws Exception
	 *             the exception
	 */
	public FileModel buildGXTFileModelItem(Item item, FileModel parent) throws Exception {
		try {

			FileModel fileModel = null;

			logger.info("Workspace item converting...");

			if (item instanceof SharedFolder) {
				logger.info("Workspace item is a SharedFolder...");
				SharedFolder shared = (SharedFolder) item;
				String name = shared.isVreFolder() ? shared.getDisplayName() : item.getName();
				fileModel = new FolderModel(item.getId(), name, item.getDescription(), parent, true, true,
						shared.isVreFolder());
				fileModel.setType(GXTFolderItemTypeEnum.SHARED_FOLDER.toString());
				fileModel.setDescription(item.getDescription());
			} else {
				if (item instanceof FolderItem) {
					logger.info("Workspace item is a Folder...");
					fileModel = new FolderModel(item.getId(), item.getName(), item.getDescription(), parent, true,
							false, false);
					fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
					fileModel.setDescription(item.getDescription());
				} else {
					if (item instanceof AbstractFileItem) {
						logger.info("Workspace item is a AbstractFileItem...");
						fileModel = new FileModel(item.getId(), item.getName(), item.getDescription(), parent, false,
								false);
						AbstractFileItem abstractFileItem = (AbstractFileItem) item;
						fileModel = setFolderItemType(fileModel, abstractFileItem);
					} else {
						if (item instanceof GCubeItem) {
							logger.info("Workspace item is a GCubeItem...");
							fileModel = new FileModel(item.getId(), item.getName(), item.getDescription(), parent,
									false, false);
							fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
						} else {
							logger.error("gxt conversion return null for item " + item.getName());
						}

					}
				}
			}

			return fileModel;

		} catch (Exception e) {
			logger.error("Build GXTFileModelItem error: " + e.getLocalizedMessage(), e);
			throw new Exception("Error building item model: " + e.getLocalizedMessage(), e);
		}

	}

	/**
	 * Sets item type.
	 *
	 * @param fileModel
	 *            the file model
	 * @param abstractfileItem
	 *            the worspace folder item
	 * @return the file model
	 */
	protected FileModel setFolderItemType(FileModel fileModel, AbstractFileItem abstractfileItem) {

		if (abstractfileItem instanceof ExternalURL) {
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
		} else {
			if (abstractfileItem instanceof GenericFileItem) {
				fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
				GenericFileItem doc = (GenericFileItem) abstractfileItem;
				String mimetype = doc.getContent().getMimeType();
				if (mimetype != null && !mimetype.isEmpty()) {
					fileModel.setType(mimetype);
				} else {
					logger.error("Error retrieving MimeType on file id: " + abstractfileItem.getId());
				}
			} else {
				if (abstractfileItem instanceof ImageFile) {
					fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
					ImageFile img = (ImageFile) abstractfileItem;
					String mimetype = img.getContent().getMimeType();
					if (mimetype != null && !mimetype.isEmpty()) {
						fileModel.setType(mimetype);
					} else {
						logger.error("Error retrieving MimeType on file id: " + abstractfileItem.getId());
					}
				} else {
					if (abstractfileItem instanceof PDFFileItem) {
						fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
					} else {
						fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
						fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());

					}

				}

			}

		}

		return fileModel;
	}

	/**
	 * Gets the workspace acl from ac ls.
	 *
	 * @param types
	 *            the types
	 * @return the workspace acl from ac ls
	 * @throws Exception
	 *             the exception
	 */
	public List<WorkspaceACL> getWorkspaceACLFromAccessType(List<AccessType> types) throws Exception {

		List<WorkspaceACL> acls = new ArrayList<WorkspaceACL>();

		for (AccessType acl : types) {

			switch (acl) {

			case ADMINISTRATOR:
				// acls.add(new WorkspaceACL(acl.toString(),
				// ACL_TYPE.ADMINISTRATOR, "Admin", false,
				// USER_TYPE.ADMINISTRATOR, ""));
				break;
			case READ_ONLY:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.READ_ONLY, "Read Only", false, USER_TYPE.OTHER,
						"Users can read any file but cannot update/delete"));
				break;
			case WRITE_OWNER:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_OWNER, "Write Own", true, USER_TYPE.OTHER,
						"Users can update/delete only their files"));
				break;

			case WRITE_ALL:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_ALL, "Write Any", false, USER_TYPE.OTHER,
						"Any user can update/delete any file"));
				break;

			default:
				// acls.add(new WorkspaceACL(acl.toString(),
				// ACL_TYPE.WRITE_OWNER,
				// acl.toString(), false, USER_TYPE.OTHER, ""));
				break;
			}
		}

		if (acls.size() == 0)
			throw new Exception("No ACLs rules found!");

		return acls;
	}

	/**
	 * Gets the workspace acl from list of acl.
	 *
	 * @param listACL
	 *            the types
	 * @return the workspace acl from ac ls
	 * @throws Exception
	 *             the exception
	 */
	public List<WorkspaceACL> getWorkspaceACLFromACLs(List<ACL> listACL) throws Exception {

		List<WorkspaceACL> acls = new ArrayList<WorkspaceACL>();

		for (ACL acl : listACL) {
			List<AccessType> listAccessType = acl.getAccessTypes();
			for (AccessType accessType : listAccessType) {
				switch (accessType) {
				case ADMINISTRATOR:
					// acls.add(new WorkspaceACL(acl.toString(),
					// ACL_TYPE.ADMINISTRATOR, "Administrator", false,
					// USER_TYPE.ADMINISTRATOR, "Users are administrator and can
					// update/delete any file"));
					break;
				case READ_ONLY:
					acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.READ_ONLY, "Read Only", false, USER_TYPE.OTHER,
							"Users can read any file but cannot update/delete"));
					break;
				case WRITE_OWNER:
					acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_OWNER, "Write Own", true, USER_TYPE.OTHER,
							"Users can update/delete only their files"));
					break;

				case WRITE_ALL:
					acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_ALL, "Write All", false, USER_TYPE.OTHER,
							"Any user can update/delete any file"));
					break;
				default:
					// acls.add(new WorkspaceACL(acl.toString(),
					// ACL_TYPE.WRITE_OWNER,
					// acl.toString(), false, USER_TYPE.OTHER, ""));
					break;
				}
			}
		}

		if (acls.size() == 0)
			throw new Exception("No ACLs rules found!");

		return acls;

	}

	/**
	 * Gets the workspace acl from list of acl.
	 *
	 * @param acl
	 *            the types
	 * @return the workspace acl from acl
	 * @throws Exception
	 *             the exception
	 */
	public List<WorkspaceACL> getWorkspaceACLForUser(ACL acl) throws Exception {

		List<WorkspaceACL> acls = new ArrayList<WorkspaceACL>();

		List<AccessType> listAccessType = acl.getAccessTypes();
		for (AccessType accessType : listAccessType) {
			switch (accessType) {
			case ADMINISTRATOR:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.ADMINISTRATOR, "Admin", false,
						USER_TYPE.ADMINISTRATOR, "Users are administrator and can update/delete any file"));
				break;
			case READ_ONLY:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.READ_ONLY, "Read Only", false, USER_TYPE.OTHER,
						"Users can read any file but cannot update/delete"));
				break;
			case WRITE_OWNER:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_OWNER, "Write Own", true, USER_TYPE.OTHER,
						"Users can update/delete only their files"));
				break;

			case WRITE_ALL:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_ALL, "Write All", false, USER_TYPE.OTHER,
						"Any user can update/delete any file"));
				break;
			default:
				// acls.add(new WorkspaceACL(acl.toString(),
				// ACL_TYPE.WRITE_OWNER,
				// acl.toString(), false, USER_TYPE.OTHER, ""));
				break;
			}
		}

		if (acls.size() == 0)
			throw new Exception("No ACLs rules found!");

		return acls;

	}

	/**
	 * Gets the formatted html acl from ac ls.
	 *
	 * @param acls
	 *            the acl owner
	 * @return the formatted html acl from ac ls
	 */
	public String getFormattedHtmlACLFromACLs(List<ACL> acls) {

		String html = "<div style=\"width: 100%; text-align:left; font-size: 10px;\">";

		logger.info("ACL/s found " + acls.size());
		PortalContext context = PortalContext.getConfiguration();

		logger.info("context.getInfrastructureName(): " + context.getInfrastructureName());
		String infrastructureName = context.getInfrastructureName();

		Map<AccessType, List<String>> aclOwner = new HashMap<>();

		for (ACL acl : acls) {
			for (AccessType accessType : acl.getAccessTypes()) {
				if (aclOwner.containsKey(accessType)) {
					List<String> users = aclOwner.get(accessType);
					boolean notfound = true;
					for (String user : users) {
						if (user.compareTo(acl.getPricipal()) == 0) {
							notfound = false;
							break;
						}
					}
					if (notfound) {
						users.add(acl.getPricipal());
						aclOwner.put(accessType, users);
					}
				} else {
					List<String> users = new ArrayList<String>();
					users.add(acl.getPricipal());
					aclOwner.put(accessType, users);
				}
			}
		}

		for (AccessType type : aclOwner.keySet()) {
			List<String> listLogins = aclOwner.get(type);

			html += "<span style=\"font-weight:bold; padding-top: 5px;\">" + type + ": </span>";
			html += "<span style=\"font-weight:normal;\">";
			for (String login : listLogins) {
				logger.trace("Adding login " + login);
				String fullName = UserUtil.getUserFullName(login);
				if (fullName != null && !fullName.isEmpty()) {
					if (infrastructureName != null && !infrastructureName.isEmpty()
							&& fullName.startsWith(infrastructureName) && infrastructureName.length()<fullName.length()) {
						fullName = fullName.substring(infrastructureName.length()+1);
						int indexOfFirstDash=fullName.indexOf("-");
						if(indexOfFirstDash>-1&&fullName.length()>indexOfFirstDash){
							fullName = fullName.substring(indexOfFirstDash+1);
							html += fullName + "; ";
						} else {
							html += fullName + "; ";
						}
					} else {
						html += fullName + "; ";
					}
				} else {
					html += login + "; ";
				}
			}
			html += "</span><br/>";
		}
		html += "</div>";

		logger.info("Retrieved: " + html);
		return html;
	}

}
