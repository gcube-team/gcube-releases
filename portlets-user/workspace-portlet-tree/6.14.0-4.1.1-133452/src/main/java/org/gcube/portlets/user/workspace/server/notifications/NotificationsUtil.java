/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.util.WsUtil;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 27, 2013
 *
 */
public class NotificationsUtil {


	protected static Logger logger = Logger.getLogger(NotificationsUtil.class);

	/**
	 * Send a notification if an item is added or updated to sharing folder.
	 *
	 * @param request the request
	 * @param httpSession the http session
	 * @param sourceItem the source item
	 * @param sourceSharedId the source shared id
	 * @param folderDestinationItem the folder destination item
	 * @param isOverwrite the is overwrite
	 */
	public static void checkSendNotifyChangedItemToShare(HttpServletRequest request, HttpSession httpSession, final WorkspaceItem sourceItem, final String sourceSharedId, final WorkspaceItem folderDestinationItem, boolean isOverwrite) {

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
						List<InfoContactModel> listContacts = getListUsersSharedByFolderSharedId(workspace, folderDestinationItem.getIdSharedFolder());
						WorkspaceItem destinationSharedFolder = workspace.getItem(folderDestinationItem.getIdSharedFolder());
						NotificationsProducer np = new NotificationsProducer(WsUtil.getAslSession(httpSession), request);

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
	public static List<InfoContactModel> getListUsersSharedByFolderSharedId(Workspace workspace, String idSharedFolder) throws Exception {

		logger.trace("getListUsersSharedByFolderSharedId "+ idSharedFolder);

		try {

			WorkspaceItem wsItem = workspace.getItem(idSharedFolder);

			if(isASharedFolder(wsItem)){

				WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) wsItem;
				GWTWorkspaceBuilder builder = new GWTWorkspaceBuilder();
				List<String> listPortalLogin = wsFolder.getUsers();
				logger.trace("getListUserSharedByFolderSharedId return "+ listPortalLogin.size() + " user");
				return builder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);
			}
			else{
				logger.trace("the item with id: "+idSharedFolder+ " is not  "+WorkspaceItemType.SHARED_FOLDER);

				//DEBUG
				//System.out.println("the item with id: "+folderSharedId+ " is not  "+WorkspaceItemType.SHARED_FOLDER);
			}
			return new ArrayList<InfoContactModel>();

		} catch (Exception e) {
			logger.error("Error in getListUserSharedByItemId ", e);
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Check send notify remove item to share.
	 *
	 * @param request the request
	 * @param httpSession the http session
	 * @param sourceItemIsShared the source item is shared
	 * @param oldItemName the old item name
	 * @param oldItemId the old item id
	 * @param sourceFolderSharedId the source folder shared id
	 */
	public static void checkSendNotifyRemoveItemToShare(HttpServletRequest request, HttpSession httpSession, final boolean sourceItemIsShared, final String oldItemName, String oldItemId, final String sourceFolderSharedId) {

		logger.trace("checkNotifyRemoveItemToShare:");

		try{

			if(!sourceItemIsShared){
				logger.trace("checkSendNotifyRemoveItemToShare returned, source item is not shared");
				return;
			}

			String idSharedFolder = sourceFolderSharedId!=null?sourceFolderSharedId:"";
			boolean isRootFolderShared = checkIsRootFolderShared(oldItemId, idSharedFolder);
			logger.trace("isRootFolderShared is: "+  isRootFolderShared);
			/*
			if(isRootFolderShared){
				logger.trace("Notification doesn't sent because the event is on root shared folder");
				return;
			}*/
			boolean isSharedFolder = isASharedFolderForId(httpSession,idSharedFolder);
			if(isSharedFolder){

				logger.trace("idSharedFolder is: "+  idSharedFolder +" is shared folder: "+isSharedFolder);
				Workspace workspace = WsUtil.getWorkspace(httpSession);
				//get contacts
				List<InfoContactModel> listContacts = getListUsersSharedByFolderSharedId(workspace, idSharedFolder);
				WorkspaceItem sourceSharedFolder = workspace.getItem(idSharedFolder);
				//System.out.println(" name sourceSharedFolder:  "+ sourceSharedFolder.getName());
				NotificationsProducer np = new NotificationsProducer(WsUtil.getAslSession(httpSession), request);

				//Notify Removed Item To Sharing?
				if(!isRootFolderShared){
					if(sourceSharedFolder instanceof WorkspaceSharedFolder){
						np.notifyRemovedItemToSharing(listContacts, oldItemName, (WorkspaceSharedFolder) sourceSharedFolder);
						logger.trace("The notifies was sent correctly");
					}
					else
						logger.trace("The notifies doesn't sent because "+sourceSharedFolder+ " is not instance of WorkspaceSharedFolder");

				}else{
					//Case removed shared folder
					np.notifySharedFolderDeleted(listContacts, oldItemName);
				}
			}

		}catch (Exception e) {
			logger.error("An error occurred in checkSendNotifyRemoveItemToShare ",e);
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
