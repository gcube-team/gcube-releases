/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.usermanager.GCubeGroup;
import org.gcube.portlets.widgets.workspacesharingwidget.client.ConstantsSharing;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.UserUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.ACL_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FolderModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.GXTFolderItemTypeEnum;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.WorkspaceACL.USER_TYPE;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VO;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VRE;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GWTWorkspaceSharingBuilder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 25, 2014
 */
public class GWTWorkspaceSharingBuilder {

	protected Logger logger = LoggerFactory.getLogger(GWTWorkspaceSharingBuilder.class);

	private InfoContactModel userLogged;

	protected static HashMap<String, InfoContactModel> hashTestUser = null;

	/**
	 * Used in test mode.
	 *
	 * @return the hash test users
	 */
	public static HashMap<String, InfoContactModel> getHashTestUsers() {

		if (hashTestUser == null) {
			hashTestUser = new HashMap<String, InfoContactModel>();

			hashTestUser.put("federico.defaveri", new InfoContactModel(
					"federico.defaveri", "federico.defaveri",
					"Federico de Faveri", false));

			hashTestUser.put("antonio.gioia", new InfoContactModel(
					"antonio.gioia", "antonio.gioia", "Antonio Gioia", false));

			hashTestUser.put("fabio.sinibaldi", new InfoContactModel(
					"fabio.sinibaldi", "fabio.sinibaldi", "Fabio Sinibaldi",
					false));

			hashTestUser.put("pasquale.pagano", new InfoContactModel(
					"pasquale.pagano", "pasquale.pagano", "Pasquale Pagano",
					false));

			hashTestUser.put("francesco.mangiacrapa", new InfoContactModel(
					"francesco.mangiacrapa", "francesco.mangiacrapa",
					"Francesco Mangiacrapa", false));

			hashTestUser.put("massimiliano.assante", new InfoContactModel(
					"massimiliano.assante", "massimiliano.assante",
					"Massimiliano Assante", false));

			hashTestUser.put("leonardo.candela", new InfoContactModel(
					"leonardo.candela", "leonardo.candela", "Leonardo Candela",
					false));

			hashTestUser.put("valentina.marioli", new InfoContactModel(
					"valentina.marioli", "valentina.marioli",
					"Valentina Marioli", false));

			hashTestUser.put("devVRE", new InfoContactModel(
					"devVRE", "devVRE",
					"devVRE", true));

			// hashTestUser.put(WsUtil.TEST_USER.toString(),
			// new InfoContactModel(
			// WsUtil.TEST_USER, WsUtil.TEST_USER,
			// WsUtil.TEST_USER_FULL_NAME));

		}

		return hashTestUser;
	}

	/**
	 * Sets the user logged.
	 *
	 * @param infoContactModel the new user logged
	 */
	public void setUserLogged(InfoContactModel infoContactModel) {
		this.userLogged = infoContactModel;
	}

	/**
	 * Builds the gxt list contacts model from gcube group.
	 *
	 * @param list the list
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromGcubeGroup(List<GCubeGroup> list) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if (list == null)
			return listContactsModel;

		logger.trace("List<GCubeGroup> size returned from GcubeGroup is: "+ list.size());

		logger.trace("Building list contact model...");

		for (GCubeGroup group : list) {
			try {
				String groupDN = group.getDisplayName();

				if (groupDN == null || groupDN.isEmpty())
					groupDN = group.getName();

				if (group.getName() == null || group.getName().isEmpty())
					logger.warn("Skipping group with null or empty name "+ group);
				else {
					InfoContactModel contact = new InfoContactModel(
							group.getName(), group.getName(), groupDN, true);
					logger.trace("Adding group " + contact);
					listContactsModel.add(contact);
				}
			} catch (InternalErrorException e) {
				logger.warn("Dispaly name is not available to group " + group);
				logger.warn("Adding get name property " + group.getName());

				if (group.getName() == null || group.getName().isEmpty())
					logger.warn("Skipping group with null or empty name "
							+ group);
				else
					listContactsModel.add(new InfoContactModel(group.getName(),
							group.getName(), group.getName(), true));
			}
		}

		logger.trace("List GCubeGroup contact model completed, return "
				+ listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	/**
	 * Builds the gxt list contacts model from v os.
	 *
	 * @param listVO the list vo
	 * @param voPath the vo path
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromVOs(List<VO> listVO, String voPath) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if(voPath==null){
			voPath=ConstantsSharing.PATH_SEPARATOR;
		}

		if(voPath!=null && !voPath.endsWith(ConstantsSharing.PATH_SEPARATOR)){
			voPath+=ConstantsSharing.PATH_SEPARATOR;
		}

		if (listVO == null){
			logger.warn("List<VO> is null, returning");
			return listContactsModel;
		}

		logger.trace("List<VO> size is: "+ listVO.size());
		logger.trace("Building list contact model...");
		logger.trace("voPath is: "+ voPath);

		for (VO vo : listVO) {
			List<VRE> vres = vo.getVres();

			logger.trace("vo getGroupName: "+ vo.getGroupName());
			logger.trace("vo getName: "+ vo.getName());
			logger.trace("Building list contact model...");

			if(vres!=null){
				logger.trace("vres from VO "+vo.getName() +" having size :" +vres.size() +", converting");
				for (VRE vre : vres) {

					if(vre.getName()!=null || !vre.getName().isEmpty()){
						String groupId = voPath+vre.getName();
						logger.trace("adding contact groupId: "+groupId +" VRE name: "+vre.getName());
//						InfoContactModel contact = new InfoContactModel(groupId, groupId, vre.getName(), true);
//						logger.trace("Adding group contact " + contact);
//						listContactsModel.add(contact);
					}
				}
			}else
				logger.trace("vres list is null, skipping VO "+vo.getName());
		}

		logger.trace("List GCubeGroup contact model completed, return "
				+ listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	/**
	 * Builds the gxt list contacts model from user model.
	 *
	 * @param listUsers the list users
	 * @return the list
	 * @throws InternalErrorException the internal error exception
	 */
	public List<InfoContactModel> buildGXTListContactsModelFromUserModel(
			List<UserModel> listUsers) throws InternalErrorException {

		List<InfoContactModel> listContactsModel = new ArrayList<InfoContactModel>();

		if (listUsers == null)
			return listContactsModel;

		logger.trace("List<UserModel> size returned from Portal VO is: "
				+ listUsers.size());

		logger.trace("Building list contact model list user model");
		for (UserModel userModel : listUsers) {
			String fullName = userModel.getFullname();

			if (fullName != null && !fullName.isEmpty())
				listContactsModel.add(new InfoContactModel(userModel
						.getUserId() + "", userModel.getScreenName(), fullName,
						false));
			else
				logger.trace("buildGXTListContactsModel is not returning user: "
						+ userModel.getScreenName()
						+ "because name is null or empty");
		}
		logger.trace("List contact model completed, return "
				+ listContactsModel.size() + " contacts");

		return listContactsModel;
	}

	/**
	 * Builds the gxt info contacts from portal logins.
	 *
	 * @param listPortalLogin the list portal login
	 * @return the list
	 */
	public List<InfoContactModel> buildGxtInfoContactsFromPortalLogins(List<String> listPortalLogin) {

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();
		for (String portalLogin : listPortalLogin)
			listContact.add(buildGxtInfoContactFromPortalLogin(portalLogin));

		return listContact;
	}

	/**
	 * Builds the gxt info contact from portal login.
	 *
	 * @param portalLogin the portal login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalLogin(String portalLogin) {

		if (portalLogin == null) {
			logger.warn("Contact login is null, return empty");
			portalLogin = "";
		}
		return new InfoContactModel(portalLogin, portalLogin,UserUtil.getUserFullName(portalLogin), false);
	}


	/**
	 * Builds the gxt info contact from portal group.
	 *
	 * @param grouoLogin the grouo login
	 * @return the info contact model
	 */
	protected InfoContactModel buildGxtInfoContactFromPortalGroup(String grouoLogin) {

		String groupName = "";
		if (grouoLogin == null) {
			logger.warn("GroupLogin is null, return empty");
			grouoLogin = "";
		}

		//RECOVERING VRE NAME
		if(grouoLogin.contains("/")){
			int start = grouoLogin.lastIndexOf("/");
			int end = grouoLogin.length();
			if(start<end)
				groupName = grouoLogin.substring(start+1, end);
			else
				groupName = grouoLogin;
		}

		return new InfoContactModel(grouoLogin, grouoLogin, groupName , true);
	}

	/**
	 * Used in test mode.
	 *
	 * @param listPortalLogin the list portal login
	 * @return the list
	 */
	protected List<InfoContactModel> buildGxtInfoContactFromPortalLoginTestMode(List<String> listPortalLogin) {

		List<InfoContactModel> listContact = new ArrayList<InfoContactModel>();

		for (String portalLogin : listPortalLogin)
			listContact.add(getHashTestUsers().get(portalLogin));

		logger.trace("returning "+listContact.size()+" info contacts test");
		return listContact;
	}

	/**
	 * Builds the gxt info contact model.
	 *
	 * @param user the user
	 * @return the info contact model
	 * @throws InternalErrorException the internal error exception
	 */
	public InfoContactModel buildGXTInfoContactModel(User user)
			throws InternalErrorException {

		// TODO ISGROUP
		if (user != null)
			return new InfoContactModel(user.getId(), user.getPortalLogin(),
					UserUtil.getUserFullName(user.getPortalLogin()), false);

		return new InfoContactModel();
	}

	/**
	 * Builds the gxt file model item.
	 *
	 * @param item the item
	 * @param parentFolderModel the parent folder model
	 * @return the file model
	 * @throws Exception the exception
	 */
	protected FileModel buildGXTFileModelItem(WorkspaceItem item,
			FileModel parentFolderModel) throws Exception {
		FileModel fileModel = null;

		FileModel parent = parentFolderModel != null ? (FileModel) parentFolderModel
				: null;

		logger.info("Workspace item converting...");

		try {
			switch (item.getType()) {

			case FOLDER:
				logger.info("Workspace item is folder...");
				fileModel = new FolderModel(item.getId(), item.getName(),
						parent, true, false, false);
				fileModel.setType(GXTFolderItemTypeEnum.FOLDER.toString());
				fileModel.setDescription(item.getDescription());
				break;

			case FOLDER_ITEM:
				logger.info("Workspace item is folder item...");
				fileModel = new FileModel(item.getId(), item.getName(), parent,
						false, false);
				FolderItem folderItem = (FolderItem) item;
				fileModel = setFolderItemType(fileModel, folderItem);
				break;

			case SHARED_FOLDER:
				logger.info("Workspace item is shared item...");
				WorkspaceSharedFolder shared = (WorkspaceSharedFolder) item;
				String name = shared.isVreFolder() ? shared.getDisplayName()
						: item.getName();
				fileModel = new FolderModel(item.getId(), name, parent, true,
						true, shared.isVreFolder());
				fileModel.setType(GXTFolderItemTypeEnum.SHARED_FOLDER
						.toString());
				fileModel.setDescription(item.getDescription());
				break;

			default:
				logger.error("gxt conversion return null for item "
						+ item.getName());
				break;

			}
		} catch (Exception e) {
			logger.error("gxt conversion error: ", e);
			throw new Exception("Error on conversion: ", e);
		}

		return fileModel;

	}

	/**
	 * Sets the folder item type.
	 *
	 * @param fileModel the file model
	 * @param worspaceFolderItem the worspace folder item
	 * @return the file model
	 */
	protected FileModel setFolderItemType(FileModel fileModel,
			FolderItem worspaceFolderItem) {

		switch (worspaceFolderItem.getFolderItemType()) {
		case EXTERNAL_IMAGE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_IMAGE);
			ExternalImage extImage = (ExternalImage) worspaceFolderItem;
			fileModel.setType(extImage.getMimeType());
			break;
		case EXTERNAL_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_FILE);
			ExternalFile extFile = (ExternalFile) worspaceFolderItem;
			fileModel.setType(extFile.getMimeType());
			break;
		case EXTERNAL_PDF_FILE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_PDF_FILE);
			ExternalPDFFile pdfExt = (ExternalPDFFile) worspaceFolderItem;
			fileModel.setType(pdfExt.getMimeType());
			break;
		case EXTERNAL_URL:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.EXTERNAL_URL);
			break;
		case REPORT_TEMPLATE:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT_TEMPLATE);
			break;
		case REPORT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.REPORT);
			break;
		case QUERY:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.QUERY);
			break;
		case TIME_SERIES:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.TIME_SERIES);
			break;
		case PDF_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.PDF_DOCUMENT);
			break;
		case IMAGE_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.IMAGE_DOCUMENT);
			GCubeItem imgDoc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
			try {
				fileModel.setType(imgDoc.getMimeType());
			} catch (InternalErrorException e) {
				logger.error("IMAGE_DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
			}
			break;
		case DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.DOCUMENT);
			GCubeItem doc = (GCubeItem) worspaceFolderItem; //Cast GCubeItem
			try {
				fileModel.setType(doc.getMimeType());
			} catch (InternalErrorException e) {
				logger.error("DOCUMENT InternalErrorException when getting MimeType on "+fileModel.getIdentifier());
			}
			break;
		case URL_DOCUMENT:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.URL_DOCUMENT);
			break;
		case METADATA:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.METADATA);
			break;
		case GCUBE_ITEM:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.GCUBE_ITEM);
			break;
		default:
			fileModel.setFolderItemType(GXTFolderItemTypeEnum.UNKNOWN_TYPE);
			fileModel.setType(GXTFolderItemTypeEnum.UNKNOWN_TYPE.toString());
			break;
		}

		return fileModel;
	}

	/**
	 * Gets the workspace acl from ac ls.
	 *
	 * @param types the types
	 * @return the workspace acl from ac ls
	 * @throws Exception the exception
	 */
	public List<WorkspaceACL> getWorkspaceACLFromACLs(List<ACLType> types) throws Exception {

		List<WorkspaceACL> acls = new ArrayList<WorkspaceACL>();

		for (ACLType acl : types) {

			switch (acl) {

			case ADMINISTRATOR:
				// acls.add(new WorkspaceACL(acl.toString(),
				// ACL_TYPE.ADMINISTRATOR, "Admin", false,
				// USER_TYPE.ADMINISTRATOR, ""));
				break;
			case READ_ONLY:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.READ_ONLY,
						"Read Only", false, USER_TYPE.OTHER,
						"Users can read any file but cannot update/delete"));
				break;
			case WRITE_OWNER:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_OWNER,
						"Write Own", true, USER_TYPE.OTHER,
						"Users can update/delete only their files"));
				break;

			case WRITE_ALL:
				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_ANY,
						"Write Any", false, USER_TYPE.OTHER,
						"Any user can update/delete any file"));
				break;

			default:
//				acls.add(new WorkspaceACL(acl.toString(), ACL_TYPE.WRITE_OWNER,
//						acl.toString(), false, USER_TYPE.OTHER, ""));
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
	 * @param aclOwner the acl owner
	 * @return the formatted html acl from ac ls
	 */
	public String getFormattedHtmlACLFromACLs(Map<ACLType, List<String>> aclOwner) {

		String html = "<div style=\"width: 100%; text-align:left; font-size: 10px;\">";

		logger.trace("Formatting "+aclOwner.size() +" ACL/s");

		for (ACLType type : aclOwner.keySet()) {
			List<String> listLogins = aclOwner.get(type);

			html+="<span style=\"font-weight:bold; padding-top: 5px;\">"+type+": </span>";
			html+="<span style=\"font-weight:normal;\">";
			for (String login : listLogins) {
				logger.trace("Adding login "+login);
				 String fullName = UserUtil.getUserFullName(login);
				 if(fullName!=null && !fullName.isEmpty())
					 html+=fullName+"; ";
				 else
					 html+=login+"; ";
			}
			html+="</span><br/>";
		}
		html+="</div>";


		return html;
	}

}
