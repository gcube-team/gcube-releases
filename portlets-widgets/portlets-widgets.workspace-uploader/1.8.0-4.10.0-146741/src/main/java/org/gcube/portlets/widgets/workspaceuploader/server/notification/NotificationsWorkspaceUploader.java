/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.notification;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.portlets.widgets.workspaceuploader.server.util.UserUtil;
import org.gcube.portlets.widgets.workspaceuploader.server.util.WsUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.ContactModel;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

/**
 * The Class NotificationsWorkspaceUploader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 27, 2013
 */
public class NotificationsWorkspaceUploader {


	protected static Logger logger = Logger.getLogger(NotificationsWorkspaceUploader.class);

	/**
	 * Check send notify changed item to share.
	 *
	 * @param request the request
	 * @param currUser the curr user
	 * @param scopeGroupId the scope group id
	 * @param np the np
	 * @param httpSession the http session
	 * @param sourceItem the source item
	 * @param sourceSharedId the source shared id
	 * @param folderDestinationItem the folder destination item
	 * @param isOverwrite the is overwrite
	 */
	public static void checkSendNotifyChangedItemToShare(HttpServletRequest request, GCubeUser currUser, String scopeGroupId, NotificationsWorkspaceUploaderProducer np, HttpSession httpSession, final WorkspaceItem sourceItem, final String sourceSharedId, final WorkspaceItem folderDestinationItem, boolean isOverwrite) {

		logger.trace("checkSendNotifyAddItemToShare");

		if(folderDestinationItem!=null){

			try{
				if(folderDestinationItem.isShared()){ 	//Notify Added Item To Sharing?

					logger.trace("checkNotifyAddItemToShare source item: "+sourceItem.getName()+" sourceSharedId: "+sourceSharedId + " folder destination: "+folderDestinationItem.getName() + " folder destination shared folder id: "+folderDestinationItem.getIdSharedFolder());

					//share condition is true if source shared folder is not null
					boolean shareChangeCondition = sourceSharedId==null?false:true;

					//System.out.println("shareChangeCondition add item: "+  shareChangeCondition);

					logger.trace("shareChangeCondition add item: "+shareChangeCondition);

					//if shareChangeCondition is true.. notifies added item to sharing
					if(shareChangeCondition){

						Workspace workspace = WsUtil.getWorkspace(request, scopeGroupId, currUser);

						List<ContactModel> listContacts = getListUserSharedByFolderSharedId(workspace, folderDestinationItem.getIdSharedFolder());

						WorkspaceItem destinationSharedFolder = workspace.getItem(folderDestinationItem.getIdSharedFolder());

						if(destinationSharedFolder instanceof WorkspaceSharedFolder){

							//SWITCH BEETWEEN ADDED OR UPDATED
							if(!isOverwrite)
								np.notifyAddedItemToSharing(listContacts, sourceItem, (WorkspaceSharedFolder) destinationSharedFolder);
							else
								np.notifyUpdatedItemToSharing(listContacts, sourceItem, (WorkspaceSharedFolder) destinationSharedFolder);


							logger.trace("The notifies was sent correctly");
						}else
							logger.trace("The notifies doesn't sent because "+destinationSharedFolder+ " is not instance of WorkspaceSharedFolder");
//							np.notifyAddedItemToSharing(listContacts, (WorkspaceFolder) folderDestinationItem);
					}
				}
				else
					logger.trace("folder destination is not shared");

			}catch (Exception e) {
				logger.error("An error occurred in  checkSendNotifyAddItemToShare ",e);
			}
		}else
			logger.warn("The notifies is failure in checkSendNotifyAddItemToShare because folder destination item is null");
	}

	/**
	 * Gets the list user shared by folder shared id.
	 *
	 * @param workspace the workspace
	 * @param idSharedFolder the id shared folder
	 * @return the list user shared by folder shared id
	 * @throws Exception the exception
	 */
	public static List<ContactModel> getListUserSharedByFolderSharedId(Workspace workspace, String idSharedFolder) throws Exception {

		logger.trace("getListUserSharedByFolderSharedId "+ idSharedFolder);

		try {

			WorkspaceItem wsItem = workspace.getItem(idSharedFolder);

			if(isASharedFolder(wsItem)){

				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) wsItem;
				List<String> listPortalLogin = wsFolder.getUsers();

				ArrayList<ContactModel> users = new ArrayList<ContactModel>(listPortalLogin.size());

				for (String login : listPortalLogin) {
					users.add(new ContactModel(login, login, false, UserUtil.getUserFullName(login)));
				}

				return users;
			}
			else{
				logger.info("the item with id: "+idSharedFolder+ " is not  "+WorkspaceItemType.SHARED_FOLDER);
			}
			return new ArrayList<ContactModel>();

		} catch (Exception e) {
			logger.error("Error in getListUserSharedByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Checks if is a shared folder.
	 *
	 * @param wsItem the ws item
	 * @return true, if is a shared folder
	 */
	public static boolean isASharedFolder(WorkspaceItem wsItem){
		if(wsItem!=null)
			return wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER);
		return false;
	}


	/**
	 * Checks if is a shared folder for id.
	 *
	 * @param user the user
	 * @param scopeGroupId the scope group id
	 * @param request the request
	 * @param itemId the item id
	 * @return true, if is a shared folder for id
	 */
	public static boolean isASharedFolderForId(GCubeUser user, String scopeGroupId, HttpServletRequest request, String itemId){

		if(itemId==null || itemId.isEmpty())
			return false;

		try {

			Workspace workspace = WsUtil.getWorkspace(request, scopeGroupId, user);
			WorkspaceItem wsItem = workspace.getItem(itemId);

			if(wsItem!=null)
				return wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER);
			return false;

		} catch (Exception e) {
			logger.error("An errror occurred in isASharedFolderForId", e);
			return false;
		}
	}


	/**
	 * Check is root folder shared.
	 *
	 * @param itemId the item id
	 * @param rootFolderSharedId the root folder shared id
	 * @return true, if successful
	 */
	public static boolean checkIsRootFolderShared(String itemId, String rootFolderSharedId) {

		logger.trace("checkIsRootFolderShared between [itemid: "+itemId +",  rootFolderSharedId: "+rootFolderSharedId+"]");
		if(itemId==null)
			return false;

		if(rootFolderSharedId==null)
			return false;

		if(itemId.compareTo(rootFolderSharedId)==0)
			return true;

		return false;
	}
}
