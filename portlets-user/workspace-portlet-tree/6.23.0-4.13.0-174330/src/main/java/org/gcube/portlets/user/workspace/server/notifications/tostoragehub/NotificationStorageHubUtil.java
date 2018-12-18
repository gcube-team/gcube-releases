/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications.tostoragehub;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.storagehub.model.types.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceBuilder;
import org.gcube.portlets.user.workspace.server.util.WsUtil;


/**
 * The Class NotificationFromStorageHub.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 2, 2018
 */
public class NotificationStorageHubUtil {

	private static Logger logger = Logger.getLogger(NotificationStorageHubUtil.class);


	/**
	 * Check notify add item to share.
	 *
	 * @param workspaceItem the workspace item
	 * @param sourceRootSharedFolderId the source shared id
	 * @param parentFolderItem the parent folder item
	 * @param workspace the workspace
	 * @param np the NotificationsProducerToStorageHub
	 */
	public static void checkNotifyAddItemToShare(final WorkspaceItem workspaceItem, final String sourceRootSharedFolderId, final WorkspaceFolder parentFolderItem, org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, NotificationsProducerToStorageHub np) {

		logger.trace("checkNotifyAddItemToShare");

		if(parentFolderItem!=null){

			try{
				//if folder destination is shared folder
				if(parentFolderItem.isShared()){ 	//Notify Added Item To Sharing?

					WorkspaceItem rootSharedFolder = workspace.getRootSharedFolder(parentFolderItem.getId());
					logger.trace("checkNotifyAddItemToShare source item: "+workspaceItem.getName()+" sourceRootSharedFolderId: "+sourceRootSharedFolderId + " folder destination: "+parentFolderItem.getName());
					//share condition is true if source shared folder is null or not equal to destination shared folder
					boolean shareChangeCondition = sourceRootSharedFolderId==null || sourceRootSharedFolderId.compareTo(rootSharedFolder.getId())!=0;
					//System.out.println("shareChangeCondition add item: "+  shareChangeCondition);
					logger.trace("shareChangeCondition add item: "+shareChangeCondition);

					//if shareChangeCondition is true.. notifies added item to sharing
					if(shareChangeCondition){

						List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(rootSharedFolder, workspace);
						//WorkspaceItem destinationSharedFolder = workspace.getItem(rootSharedFolder.getId());
						WorkspaceItem destinationSharedFolder = rootSharedFolder;

						if(destinationSharedFolder instanceof WorkspaceSharedFolder){
							np.notifyAddedItemToSharing(workspace, listContacts, workspaceItem, (WorkspaceSharedFolder) destinationSharedFolder);
							logger.trace("The notifies sent correctly");
						}
						else
							logger.warn("Notifies added item: "+workspaceItem+ "to share doesn't sent because "+destinationSharedFolder+" is not istance of WorkspaceSharedFolder");
						//							np.notifyAddedItemToSharing(listContacts, (WorkspaceFolder) folderDestinationItem);
					}
				}
				else
					logger.trace("folder destination is not shared");

			}catch (Exception e) {
				logger.error("An error occurred in  verifyNotifyAddItemToShare ",e);
			}
		}else
			logger.warn("The notifies is failure in verifyNotifyAddItemToShare because folder destination item is null");
	}


	/**
	 * Check notify move item from share.
	 *
	 * @param sourceItemIsShared the source item is shared
	 * @param sourceItem the source item
	 * @param sourceRootSharedFolderId the source root shared folder id
	 * @param folderDestination the folder destination
	 * @param workspace the workspace
	 * @param np the NotificationsProducerToStorageHub
	 */
	public static void checkNotifyMoveItemFromShare(
		boolean sourceItemIsShared,
		org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem sourceItem,
		String sourceRootSharedFolderId,
		org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder folderDestination, org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, NotificationsProducerToStorageHub np) {

		try{

			String rootDestSharedFolderId = null;
			WorkspaceItem rootDestSharedFolder = null;

			if(folderDestination.isShared()){
				rootDestSharedFolder = workspace.getRootSharedFolder(folderDestination.getId());
				rootDestSharedFolderId = rootDestSharedFolder.getId();
			}

			boolean shareChangeCondition = false;

			//share condition is true if source shared folder is not equal to destination shared folder
			if(rootDestSharedFolderId!=null)
				shareChangeCondition = sourceRootSharedFolderId==null?false:sourceRootSharedFolderId.compareTo(rootDestSharedFolderId)!=0;

			logger.trace("checkNotifyMoveItemFromShare source item: "+sourceItem.getName()+" rootSharedFolderId: "+sourceRootSharedFolderId + " folder destination: "+folderDestination.getName() +" sourceItemIsShared: "+sourceItemIsShared);
			logger.trace("shareChangeCondition remove item: "+  shareChangeCondition);

			//Notify Removed Item To Sharing?
			//if source Item is shared and folder destination is not shared or shareChangeCondition is true.. notifies removed item to sharing
			if(sourceItemIsShared && (!folderDestination.isShared() || shareChangeCondition)){

				//get contacts
				List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(rootDestSharedFolder, workspace);

				WorkspaceItem sourceRootSharedFolder = workspace.getItem(sourceRootSharedFolderId);

				if(sourceRootSharedFolder instanceof WorkspaceSharedFolder){
					np.notifyMovedItemToSharing(workspace, listContacts, sourceItem, (WorkspaceSharedFolder) sourceRootSharedFolder);
					logger.trace("The notifies was sent correctly");
				}else
					logger.warn("Notifies moved item: "+sourceItem+ "from share doesn't sent because "+sourceRootSharedFolder+" is not istance of WorkspaceSharedFolder");
			}

		}catch (Exception e) {
			logger.error("An error occurred in checkNotifyMoveItemFromShare ",e);
		}

	}


	/**
	 * Gets the list user shared by folder shared id.
	 *
	 * @param rootSharedFolder the root shared folder
	 * @param workspace the workspace
	 * @return the list user shared by folder shared id
	 * @throws Exception the exception
	 */
	public static List<InfoContactModel> getListUserSharedByFolderSharedId(WorkspaceItem rootSharedFolder, org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace) throws Exception{

		logger.debug("getListUserSharedByFolderSharedId "+ rootSharedFolder);

		try {

			if(isASharedFolder(rootSharedFolder)){ //JUST TO BE SURE

				List<String> listPortalLogin = workspace.getSharedFolderMembers(rootSharedFolder.getId());
				logger.debug("StorageHub returned "+ listPortalLogin.size() + " user/s");

				if(!WsUtil.isWithinPortal())
					return GWTWorkspaceBuilder.buildGxtInfoContactFromPortalLoginTestMode(listPortalLogin);

				return GWTWorkspaceBuilder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);
			}
			else
				logger.debug("the item with id: "+rootSharedFolder.getId()+ " is not  "+WorkspaceItemType.SHARED_FOLDER);

			return new ArrayList<InfoContactModel>();

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
