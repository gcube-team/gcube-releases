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

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 27, 2013
 *
 */
public class NotificationsWorkspaceUploader {


	protected static Logger logger = Logger.getLogger(NotificationsWorkspaceUploader.class);

	/**
	 * Send a notification if an item is added or updated to sharing folder
	 * @param httpSession
	 * @param sourceItem
	 * @param sourceSharedId
	 * @param folderDestinationItem
	 */
	public static void checkSendNotifyChangedItemToShare(NotificationsWorkspaceUploaderProducer np, HttpServletRequest request, HttpSession httpSession, final WorkspaceItem sourceItem, final String sourceSharedId, final WorkspaceItem folderDestinationItem, boolean isOverwrite) {

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

						Workspace workspace = WsUtil.getWorkspace(httpSession);

						List<ContactModel> listContacts = getListUserSharedByFolderSharedId(workspace, folderDestinationItem.getIdSharedFolder());

						WorkspaceItem destinationSharedFolder = workspace.getItem(folderDestinationItem.getIdSharedFolder());

						logger.trace("[4] HttpServletRequest is: URI: "+request.getRequestURI() +", ServerName: "+request.getServerName());

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
	 *
	 * @param workspace
	 * @param idSharedFolder
	 * @return
	 * @throws Exception
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
	 *
	 * @param wsItem
	 * @return
	 */
	public static boolean isASharedFolder(WorkspaceItem wsItem){
		if(wsItem!=null)
			return wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER);
		return false;
	}

	/**
	 *
	 * @param wsItem
	 * @return
	 */
	public static boolean isASharedFolderForId(HttpSession httpSession, String itemId){

		if(itemId==null || itemId.isEmpty())
			return false;

		try {

			Workspace workspace = WsUtil.getWorkspace(httpSession);
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
	 *
	 * @param itemId
	 * @param rootFolderSharedId
	 * @return
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
