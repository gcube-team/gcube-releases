package org.gcube.portlets.user.reportgenerator.server.servlet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portlets.admin.wfdocslibrary.shared.PermissionType;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.PermissionLocalServiceUtil;
import com.liferay.portal.service.ResourceLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

public class DocLibraryUtil {

	public static final String WORKFLOWS_FOLDER = "Workflow Documents";
	/**
	 * 
	 */
	private static final Logger log = LoggerFactory.getLogger(DocLibraryUtil.class);


	/**
	 * write the Report File (payload) into Liferay DocLibrary in the workflowdocs folder
	 * @param roles
	 * @param start each Step contains a Map<WfRole, ArrayList<PermissionType>> that is needed in the writeFileIntoDocLibrary 
	 * @param fileName
	 * @param buffer the payload as byte array
	 */
	public static boolean writeFileIntoDocLibrary(ASLSession session, List<Role> roles, Step start, String fileName, byte[] buffer) {
		long docfolderid = -1;
		try {
			docfolderid = getWfFolder(session);
			long userId = getUserId(session);
			//write the file into doclib
			DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.addFileEntry(
					userId, 
					getGroupID(session),
					docfolderid, 
					fileName, 
					fileName, 
					"workflow document",
					"",
					"",
					buffer, 
					new ServiceContext());
			log.debug("Wrote file into DocumentLibrary");



			//get the file entry resource id
			long resourceId  = ResourceLocalServiceUtil.getResource(fileEntry.getCompanyId(),
					DLFileEntry.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL, String.valueOf(fileEntry.getFileEntryId())).getResourceId();

			//set the permission on the file for each role 
			for (Role role : roles) {
				String[] actionIds = getPermissionsFromWfStep(role, start);
				PermissionLocalServiceUtil.setRolePermissions(role.getRoleId(), actionIds, resourceId);
				log.debug("set the permissions for Role: " + role.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}  
		log.debug(" WROTE INTO DOCsLib and ADDING permissions for name: " + fileName);
		return true;
	}

	public static DLFileEntry updateFileIntoDocLibrary(ASLSession session, String workflowid, byte[] buffer) {
		long docfolderid = -1;
		DLFileEntry fileEntry = null;
		try {
			fileEntry = getFileEntry(session, workflowid);
			docfolderid = getWfFolder(session);
			long userId = getUserId(session);
			
			String fileName = fileEntry.getTitle();
			log.debug("Update file into DocumentLibrary with Name: " + fileName);

			DLFileEntryLocalServiceUtil.updateFileEntry(
					userId, 
					getGroupID(session), 
					docfolderid, 
					fileEntry.getName(), 
					fileEntry.getName(), 
					fileName, 
					fileEntry.getDescription(), 
					"", 
					true, 
					"", 
					buffer, 
					new ServiceContext());
							
			log.debug("Update file into DocumentLibrary");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fileEntry;  
	}

	public static boolean deleteFileFromDocLibrary(ASLSession session, String workflowid) {
		DLFileEntry fileEntry = null;
		try {
			fileEntry = getFileEntry(session, workflowid);
			DLFileEntryLocalServiceUtil.deleteDLFileEntry(fileEntry);
			log.debug("Deleted");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;  
	}
	/**
	 * 
	 * @param session
	 * @param workflowid
	 * @return
	 * @throws Exception 
	 * @throws SystemException 
	 * @throws PortalException 
	 */
	public static InputStream getFileEntryAsStream(ASLSession session, String workflowid) throws Exception {
		String titleWithExtension = workflowid+".zip";

		DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntryByTitle(getGroupID(session), getWfFolder(session), 
				titleWithExtension);	

		return DLFileEntryLocalServiceUtil.getFileAsStream(fileEntry.getCompanyId(), 
				getUserId(session), getGroupID(session), getWfFolder(session), fileEntry.getName());	

	}

	public static DLFileEntry getFileEntry(ASLSession session, String workflowid) throws PortalException, SystemException, Exception {
		String titleWithExtension = workflowid+".zip";

		return DLFileEntryLocalServiceUtil.getFileEntryByTitle(getGroupID(session), getWfFolder(session), 
				titleWithExtension);	

	}

	/**
	 * 
	 * @param role is the liferay role, for convention is created as ROLENAME_WFID e.g. EDITOR_123
	 * @param step containts the permissions attached to each role (just the name, e.g. EDITOR)
	 * @return the permissions to apply
	 */
	public static String[] getPermissionsFromWfStep(Role role, Step step) {
		ArrayList<PermissionType> toConvert = new ArrayList<PermissionType>();
		for (WfRole steprole : step.getPermissions().keySet()) {
			System.out.println("Steprole: "+ steprole.getRolename());
			String name = role.getName().split("_")[0];  //e.g. EDITOR <- EDITOR_123 
			System.out.println("role Name: "+ name);
			if (steprole.getRolename().equals(name)) {
				toConvert = step.getPermissions().get(steprole);
			}
		}
		String[] toReturn = new String[toConvert.size()];
		int i = 0;
		for (PermissionType p : toConvert) {
			toReturn[i] = getLRActionIdFromWfPermissionType(p);
			i++;
		}
		return toReturn;		
	}
	/**
	 * check the existence of the WFFolder or create it if not exists
	 * @param session
	 * @return true is everything goes ok
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static long getWfFolder(ASLSession session) throws Exception {
		long parentFolderId = 0;
		DLFolder folder = null;
		if (! wfFolderExists(session)) {
			folder = DLFolderLocalServiceUtil.addFolder(getUserId(session), getGroupID(session), parentFolderId, WORKFLOWS_FOLDER, "Folder for Workflow Documents", new ServiceContext());
			log.debug("Folder for WorkflowDocs created: /" + WORKFLOWS_FOLDER);
		} else
			folder = DLFolderLocalServiceUtil.getFolder( getGroupID(session), parentFolderId, WORKFLOWS_FOLDER);
		return folder.getFolderId();
	}
	/**
	 * 
	 * @param session the ASL Session instance
	 * @return true if the workflow docs exists
	 */
	public static boolean wfFolderExists(ASLSession session) {
		try {
			long groupid = 	getGroupID(session);
			long parentfolder = 0;
			if (DLFolderLocalServiceUtil.getFolder(groupid, parentfolder, "Workflow Documents") != null)
				return true;
			else
				return false;
		}
		catch (NoSuchFolderException ex) {
			log.debug("Folder does not exists");
			return false;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
	/**
	 * 
	 * @param session
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static long getGroupID(ASLSession session) throws PortalException, SystemException {
		long organizationid = session.getGroupId();
		Organization myOrg = OrganizationLocalServiceUtil.getOrganization(organizationid);
		return	myOrg.getGroup().getGroupId();
	}
	/**
	 * 
	 * @return the company webid
	 * @throws PortalException
	 * @throws SystemException
	 */
	public static long getCompanyID() throws PortalException, SystemException {		
		return OrganizationsUtil.getCompany().getCompanyId();
	}
	/**
	 * 
	 * @param session the ASL Session instance
	 * @return the list of the root folders
	 * @throws SystemException .
	 * @throws PortalException .
	 */
	public static List<DLFolder> getFolders(ASLSession session) throws SystemException, PortalException {
		long companyid = getCompanyID();
		List<DLFolder> folders = null;
		folders = DLFolderLocalServiceUtil.getFolders(companyid);
		for (DLFolder folder : folders) {
			log.debug("Folder name: " + folder.getName() + " ID: " + folder.getFolderId() + " Parent: " + folder.getParentFolderId() + " Groupid: " + folder.getGroupId());
		}
		return folders;
	}

	/**
	 * 
	 * @param session the ASL Session instance
	 * @return the userid in the liferay system
	 */
	public static long getUserId(ASLSession session) {
		List<User> users = null;
		long userId = 0;
		try {
			users = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
		} catch (SystemException e) {
			e.printStackTrace();
		}
		for(User user: users){
			if(user.getScreenName().equalsIgnoreCase(session.getUsername())){
				userId = user.getUserId();
				break;
			}
		}
		return userId;	
	}


	/**
	 * needed to convert Workflows permission into Lifearay permissions (ActionIDs)
	 * @param type
	 * @return
	 */
	private static String getLRActionIdFromWfPermissionType(PermissionType type) {
		switch (type) {
		case EDIT_PERMISSIONS:
			return "PERMISSIONS";
		case VIEW:
			return "VIEW";
		case UPDATE:
			return "UPDATE";
		case DELETE:
			return "DELETE";
		case ADD_DISCUSSION:
			return "ADD_DISCUSSION";
		case DELETE_DISCUSSION:
			return "DELETE_DISCUSSION";
		case UPDATE_DISCUSSION:
			return "UPDATE_DISCUSSION";			
		default:
			return "";
		}
	}


}
