/**
 *
 */
package org.gcube.portlets.user.workspace.server.notifications.tostoragehub;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.storagehub.model.types.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.portlets.user.workspace.server.tostoragehub.StorageHubToWorkpaceConverter;
import org.gcube.portlets.user.workspace.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class NotificationStorageHubUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 31, 2019
 */
public class NotificationStorageHubUtil {

	private static Logger logger = LoggerFactory.getLogger(NotificationStorageHubUtil.class);

	
	/**
	 * Check notify add item to share.
	 *
	 * @param workspaceItem the workspace item
	 * @param sourceRootSharedFolderId the source root shared folder id
	 * @param parentFolderItem the parent folder item
	 * @param workspace the workspace
	 * @param np the np
	 */
	public static void checkNotifyAddItemToShare(final WorkspaceItem workspaceItem, final String sourceRootSharedFolderId, final WorkspaceFolder parentFolderItem, org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, NotificationsProducerToStorageHub np) {

		if(parentFolderItem!=null){
			
			logger.debug("Sending notification added item to share is running...");
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
			logger.debug("Sending notification moved item from share is running...");
			
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

		logger.debug("called getListUserSharedByFolderSharedId on: "+ rootSharedFolder);

		try {

			if(isASharedFolder(rootSharedFolder)){ //JUST TO BE SURE

				List<String> listPortalLogin = workspace.getSharedFolderMembers(rootSharedFolder.getId());
				logger.debug("StorageHub returned "+ listPortalLogin.size() + " user/s");

				if(!WsUtil.isWithinPortal())
					return StorageHubToWorkpaceConverter.buildGxtInfoContactFromPortalLoginTestMode(listPortalLogin);

				List<InfoContactModel> listContacts = new ArrayList<InfoContactModel>(listPortalLogin.size());
				for (String login : listPortalLogin) {
					listContacts.add(StorageHubToWorkpaceConverter.buildGxtInfoContactFromPortalLogin(login));
				}
				
				return listContacts;
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
	 * Check send notify removed item from share.
	 *
	 * @param request the request
	 * @param sourceItemIsShared the source item is shared
	 * @param oldItemName the old item name
	 * @param oldItemId the old item id
	 * @param sourceFolderSharedId the source folder shared id
	 * @param workspace the workspace
	 * @param np the np
	 */
	public static void checkSendNotifyRemovedItemFromShare(HttpServletRequest request, final boolean sourceItemIsShared, final String oldItemName, String oldItemId, final String sourceFolderSharedId, org.gcube.common.storagehubwrapper.server.tohl.Workspace workspace, NotificationsProducerToStorageHub np) {

		try{
			logger.debug("Sending notification removed item from share is running...");
			if(!sourceItemIsShared){
				logger.trace("checkSendNotifyRemoveItemToShare returned, source item is not shared");
				return;
			}

			String idSharedFolder = sourceFolderSharedId!=null?sourceFolderSharedId:"";
			boolean isRootFolderShared = checkIsRootFolderShared(oldItemId, idSharedFolder);
			logger.trace("isRootFolderShared is: "+  isRootFolderShared);

			WorkspaceItem sourceSharedFolder = workspace.getItem(idSharedFolder);
			boolean isSharedFolder = isASharedFolder(sourceSharedFolder);
			if(isSharedFolder){

				logger.trace("idSharedFolder is: "+  idSharedFolder +" is shared folder: "+isSharedFolder);
				//get contacts
				List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(sourceSharedFolder, workspace);

				//Notify Removed Item To Sharing?
				if(!isRootFolderShared){
					if(sourceSharedFolder instanceof org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder){
						np.notifyRemovedItemToSharing(listContacts, oldItemName, (org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder) sourceSharedFolder);
						logger.debug("Notification was sent correctly");
					}
					else
						logger.debug("The notifies doesn't sent because "+sourceSharedFolder+ " is not instance of WorkspaceSharedFolder");

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
	 * Notify shared folder deleted.
	 *
	 * @param listContacts the list contacts
	 * @param folderNameDeleted the folder name deleted
	 * @param userId the user id
	 */
	public void notifySharedFolderDeleted(final List<InfoContactModel> listContacts, final String folderNameDeleted, final String userId) {


		new Thread() {
			  @Override
			  public void run() {
				  logger.trace("Send notification shared folder deleted is running...");
				  for (InfoContactModel infoContactModel : listContacts) {
						try{
							//NOTIFIES ONLY THE USERS THAT ARE DIFFERENT FROM CURRENT USER
							if(infoContactModel.getLogin().compareTo(userId)!=0){
								logger.debug("Notification to user "+infoContactModel.getLogin() +" deleted shared folder "+folderNameDeleted +" HAS BEEN REMOVED!");
							}
						}catch (Exception e) {
							logger.error("An error occured in notifySharedFolderDeleted ", e);
						}
					}
				  logger.trace("notifies of deleted shared foder is completed");
			  }

		}.start();

	}
	
	
	/**
	 * Checks if is a shared folder.
	 *
	 * @param wsItem the ws item
	 * @return true, if is a shared folder
	 */
	public static boolean isASharedFolder(WorkspaceItem wsItem){
		if(wsItem!=null) {
			//return wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER);
			if(wsItem.isFolder() && wsItem.isShared()) {
				return true;
			}
			
			if(wsItem.getType().equals(org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItemType.SHARED_FOLDER)) {
				return true;
			}
		}
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
