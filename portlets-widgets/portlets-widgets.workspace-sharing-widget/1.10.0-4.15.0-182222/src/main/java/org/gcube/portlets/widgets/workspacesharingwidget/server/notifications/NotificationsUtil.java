/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.portlets.widgets.workspacesharingwidget.server.GWTWorkspaceSharingBuilder;
import org.gcube.portlets.widgets.workspacesharingwidget.server.util.WsUtil;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NotificationsUtil.
 *
 * @author Francesco Mangiacrapa
 * May 27, 2013
 */
public class NotificationsUtil {

	protected static Logger logger = LoggerFactory.getLogger(NotificationsUtil.class);

	/**
	 * Send a notification if an item is added or updated to sharing folder.
	 *
	 * @param request
	 *            the request
	 * @param sourceItem
	 *            the source item
	 * @param sourceSharedId
	 *            the source shared id
	 * @param folderDestinationItem
	 *            the folder destination item
	 * @param isOverwrite
	 *            the is overwrite
	 */
	public void checkSendNotifyChangedItemToShare(HttpServletRequest request, final Item sourceItem,
			final String sourceSharedId, final FolderItem folderDestinationItem, boolean isOverwrite) {

		logger.info("checkSendNotifyAddItemToShare");

		if (folderDestinationItem != null) {

			try {
				// if folder destination is shared folder
				if (folderDestinationItem.isShared()) { // Notify Added Item To
														// Sharing?

					logger.info("checkNotifyAddItemToShare source item: " + sourceItem.getName() + " sourceSharedId: "
							+ sourceSharedId + " folder destination: " + folderDestinationItem.getName()
							+ " folder destination shared folder id: " + folderDestinationItem.getId());

					// share condition is true if source shared folder is not
					// null
					boolean shareChangeCondition = sourceSharedId == null ? false : true;

					// System.out.println("shareChangeCondition add item: "+
					// shareChangeCondition);

					logger.info("shareChangeCondition add item: " + shareChangeCondition);

					// if shareChangeCondition is true.. notifies added item to
					// sharing
					if (shareChangeCondition) {

						List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(
								folderDestinationItem.getId());

						NotificationsProducer np = new NotificationsProducer(request);

						// SWITCH BEETWEEN ADDED OR UPDATED
						if (!isOverwrite)
							np.notifyAddedItemToSharing(listContacts, sourceItem, (SharedFolder) folderDestinationItem);
						else
							np.notifyUpdatedItemToSharing(listContacts, sourceItem,
									(SharedFolder) folderDestinationItem);

						logger.info("The notifies was sent correctly");
						// np.notifyAddedItemToSharing(listContacts,
						// (WorkspaceFolder) folderDestinationItem);
					}
				} else
					logger.info("folder destination is not shared");

			} catch (Exception e) {
				logger.error("An error occurred in  checkSendNotifyAddItemToShare ", e);
			}
		} else
			logger.warn(
					"The notifies is failure in checkSendNotifyAddItemToShare because folder destination item is null");
	}

	/**
	 * Gets the list user shared by folder shared id.
	 *
	 * @param workspace
	 *            the workspace
	 * @param idSharedFolder
	 *            the id shared folder
	 * @return the list user shared by folder shared id
	 * @throws Exception
	 *             the exception
	 */
	/**
	 * 
	 * @param id
	 *            Folder Id
	 * @return List of users
	 * @throws Exception
	 *             Exception
	 */
	public List<InfoContactModel> getListUserSharedByFolderSharedId(String id) throws Exception {

		logger.info("getListUserSharedByFolderSharedId " + id);

		try {
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(id);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared() && folderItem instanceof SharedFolder) {
				SharedFolder sharedFolder = (SharedFolder) folderItem;
				Metadata metadata = sharedFolder.getUsers();
				Map<String, Object> map = metadata.getMap();
				List<String> listPortalLogin = new ArrayList<>(map.keySet());
				logger.info("getListUserSharedByFolderSharedId return " + listPortalLogin.size() + " user");
				GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();

				return builder.buildGxtInfoContactsFromPortalLogins(listPortalLogin);

			} else {
				logger.error("The item with id: " + id + " is not a shared folder.");

				// DEBUG
				// System.out.println("the item with id: "+folderSharedId+ " is
				// not "+WorkspaceItemType.SHARED_FOLDER);
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
	 * @param request
	 *            the request
	 * @param sourceItemIsShared
	 *            the source item is shared
	 * @param oldItemName
	 *            the old item name
	 * @param oldItemId
	 *            the old item id
	 * @param sourceFolderSharedId
	 *            the source folder shared id
	 */
	public void checkSendNotifyRemoveItemToShare(HttpServletRequest request, final boolean sourceItemIsShared,
			final String oldItemName, String oldItemId, final String sourceFolderSharedId) {

		logger.info("checkNotifyRemoveItemToShare:");

		try {

			if (!sourceItemIsShared) {
				logger.info("checkSendNotifyRemoveItemToShare returned, source item is not shared");
				return;
			}

			boolean isRootFolderShared = isRootSharedFolderById(sourceFolderSharedId);
			logger.info("isRootFolderShared is: " + isRootFolderShared);

			if (isRootFolderShared) {
				logger.info("Notification doesn't send because the event is on root shared folder");
				return;
			}

			logger.info("idSharedFolder is: " + sourceFolderSharedId);

			// get contacts
			List<InfoContactModel> listContacts = getListUserSharedByFolderSharedId(sourceFolderSharedId);

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(sourceFolderSharedId);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem instanceof SharedFolder) {
				SharedFolder sharedFolder = (SharedFolder) folderItem;
				NotificationsProducer np = new NotificationsProducer(request);
				np.notifyRemovedItemToSharing(listContacts, oldItemName, sharedFolder);
			}
			logger.info("The notifies was sent correctly");

		} catch (Exception e) {
			logger.error("An error occurred in checkSendNotifyRemoveItemToShare ", e);
		}

	}

	/**
	 * Checks if is a root shared folder.
	 *
	 * @param wsItem
	 *            the ws item
	 * @return true, if is a root shared folder
	 */
	/*
	 * public boolean isARootSharedFolder(Item wsItem) { if (wsItem != null)
	 * return wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER); return
	 * false; }
	 */

	/**
	 * Checks if is a shared folder for id.
	 *
	 * @param request
	 *            the request
	 * @param itemId
	 *            the item id
	 * @return true, if is a shared folder for id
	 */
	/*
	 * public boolean isASharedFolderForId(String itemId) {
	 * 
	 * if (itemId == null || itemId.isEmpty()) return false;
	 * 
	 * try {
	 * 
	 * Workspace workspace = WsUtil.getWorkspace(request); WorkspaceItem wsItem
	 * = workspace.getItem(itemId); if (wsItem != null) return
	 * wsItem.getType().equals(WorkspaceItemType.SHARED_FOLDER); return false;
	 * 
	 * } catch (Exception e) {
	 * logger.error("An errror occurred in isASharedFolderForId", e); return
	 * false; } }
	 */

	private boolean isRootSharedFolderById(String id) throws Exception {
		try {
			if (id == null || id.isEmpty()) {
				logger.error("The item with id: " + id + " is not a shared folder.");
				return false;
			}

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForItem = shc.open(id);
			FolderContainer folderContainer = openResolverForItem.asFolder();
			FolderItem folderItem = folderContainer.get();
			if (folderItem.isShared() && folderItem instanceof SharedFolder) {
				logger.info("The item with id: " + id + " is a shared folder.");
				return true;
			} else {
				logger.error("The item with id: " + id + " is not a shared folder.");
				return false;

			}

		} catch (Exception e) {
			logger.error("Error in isASharedFolderById: " + e.getLocalizedMessage(), e);
			throw new Exception("Error checking the type of folder: [id=" + id + "]");
		}

	}

	/**
	 * Check is root folder shared.
	 *
	 * @param itemId
	 *            the item id
	 * @param rootFolderSharedId
	 *            the root folder shared id
	 * @return true, if successful
	 */
	public boolean checkIsRootFolderShared(String itemId, String rootFolderSharedId) {

		logger.info("checkIsRootFolderShared between [itemid: " + itemId + ",  rootFolderSharedId: "
				+ rootFolderSharedId + "]");
		if (itemId == null)
			return false;

		if (rootFolderSharedId == null)
			return false;

		if (itemId.compareTo(rootFolderSharedId) == 0)
			return true;

		return false;
	}
}
