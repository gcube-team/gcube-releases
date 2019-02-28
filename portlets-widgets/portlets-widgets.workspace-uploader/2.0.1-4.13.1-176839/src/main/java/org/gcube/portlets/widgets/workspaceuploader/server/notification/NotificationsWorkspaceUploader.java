/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.notification;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.types.WorkspaceItemType;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.gcube.common.storagehubwrapper.server.tohl.Workspace;
import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.portlets.widgets.workspaceuploader.server.util.UserUtil;
import org.gcube.portlets.widgets.workspaceuploader.shared.ContactModel;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class NotificationsWorkspaceUploader.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jul 2, 2018
 */
public class NotificationsWorkspaceUploader {


	protected static Logger logger = LoggerFactory.getLogger(NotificationsWorkspaceUploader.class);


	/**
	 * Check send notify changed item to share.
	 *
	 * @param storageWrapper the storage wrapper
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
	public static void checkSendNotifyChangedItemToShare(StorageHubWrapper storageWrapper, HttpServletRequest request, GCubeUser currUser, String scopeGroupId, NotificationsWorkspaceUploaderProducer np, HttpSession httpSession, final Item sourceItem, final String sourceSharedId, final FolderItem folderDestinationItem, boolean isOverwrite) {

		logger.trace("checkSendNotifyAddItemToShare");

		if(folderDestinationItem!=null){

			try{
				if(folderDestinationItem.isShared()){ 	//Notify Added Item To Sharing?

					 //TODO folderDestinationItem.getIdSharedFolder()
					logger.trace("checkNotifyAddItemToShare source item: "+sourceItem.getName()+" sourceSharedId: "+sourceSharedId + " folder destination name: "+folderDestinationItem.getName() + " folder destination id: "+folderDestinationItem.getId());

					//share condition is true if source shared folder is not null
					boolean shareChangeCondition = sourceSharedId==null?false:true;

					//System.out.println("shareChangeCondition add item: "+  shareChangeCondition);

					logger.trace("shareChangeCondition add item: "+shareChangeCondition);

					//if shareChangeCondition is true.. notifies added item to sharing
					if(shareChangeCondition){

						SharedFolder folderDest = (SharedFolder) folderDestinationItem;
						SharedFolder rootSharedFolder = null;
						try{
							FolderItem sharedFolder = storageWrapper.getStorageHubClientService().getRootSharedFolder(folderDest.getId());
							rootSharedFolder = (SharedFolder) sharedFolder;
						}catch(Exception e){
							//silent
						}

						//Reading memmbers from rootSharedFolder
						List<String> listLogins  = storageWrapper.getWorkspace().getSharedFolderMembers(rootSharedFolder.getId());
						List<ContactModel> listContacts = new ArrayList<ContactModel>(listLogins.size());
						for (String login : listLogins) {
							listContacts.add(new ContactModel(login, login, false, UserUtil.getUserFullName(login)));
						}

						SocialFileItem socialItem = NotificationMapper.toSocialItem(storageWrapper, sourceItem);

						//TO folderDest or rootSharedFolder??
						SocialSharedFolder socialFolder = NotificationMapper.toSocialFolder(rootSharedFolder);

						//SWITCH BEETWEEN ADDED OR UPDATED
						if(!isOverwrite)
							np.notifyAddedItemToSharing(listContacts, socialItem, socialFolder);
						else
							np.notifyUpdatedItemToSharing(listContacts, socialItem, socialFolder);
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
	 * @param workspace the workspace
	 * @param itemId the item id
	 * @return true, if is a shared folder for id
	 */
	public static boolean isASharedFolderForId(Workspace workspace, String itemId){

		if(itemId==null || itemId.isEmpty())
			return false;

		try {

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
