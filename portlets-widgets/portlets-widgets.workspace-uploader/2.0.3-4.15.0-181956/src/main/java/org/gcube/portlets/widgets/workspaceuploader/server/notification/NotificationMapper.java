/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.server.notification;


import org.gcube.applicationsupportlayer.social.shared.SocialFileItem;
import org.gcube.applicationsupportlayer.social.shared.SocialSharedFolder;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.VreFolder;
import org.gcube.common.storagehubwrapper.server.StorageHubWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class NotificationMapper.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 26, 2018
 */
public class NotificationMapper {

	protected static Logger logger = LoggerFactory.getLogger(NotificationMapper.class);

	
	/**
	 * To social folder.
	 *
	 * @param sharedFolder the shared folder
	 * @return the social shared folder
	 */
	public static SocialSharedFolder toSocialFolder(SharedFolder sharedFolder){

		return new SocialSharedFolder(sharedFolder.getId(), sharedFolder.getName(), sharedFolder.getTitle(), sharedFolder.getDisplayName(), sharedFolder.getPath(), sharedFolder.getParentId(), sharedFolder.isVreFolder());
	}
	
	/**
	 * To social folder.
	 *
	 * @param folderItem the folder item
	 * @return the social shared folder
	 */
	public static SocialSharedFolder toSocialFolder(FolderItem folderItem){

		return new SocialSharedFolder(folderItem.getId(), folderItem.getName(), folderItem.getTitle(), folderItem.getName(), folderItem.getPath(), folderItem.getParentId(), false);
	}

	/**
	 * To social item.
	 *
	 * @param storageWrapper the storage wrapper
	 * @param item the item
	 * @return the social file item
	 */
	public static SocialFileItem toSocialItem(StorageHubWrapper storageWrapper, Item item){

		Item theFolder = null;
		try {
			theFolder = storageWrapper.getStorageHubClientService().getItem(item.getParentId());
		}
		catch (Exception e) {
			logger.warn("Impossible to read the item parent by using the parent id: "+item.getParentId(), e);
		}

		SocialSharedFolder parent = null;
		if(theFolder!=null) {
			if(theFolder instanceof SharedFolder || theFolder instanceof VreFolder) {
				parent = toSocialFolder((SharedFolder) theFolder);
			}else if(theFolder instanceof FolderItem){
				parent = toSocialFolder((FolderItem) theFolder);
			}
		}

		return 	new SocialFileItem(item.getId(), item.getName(), item.getTitle(), item.getPath(), parent);
	}
}
