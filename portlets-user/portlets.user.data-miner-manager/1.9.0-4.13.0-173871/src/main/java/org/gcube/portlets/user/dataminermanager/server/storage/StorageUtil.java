package org.gcube.portlets.user.dataminermanager.server.storage;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class StorageUtil {

	private static Logger logger = LoggerFactory.getLogger(StorageUtil.class);

	/**
	 * 
	 * @param user
	 *            user
	 * @param itemId
	 *            item id
	 * @return map properties
	 * @throws ServiceException
	 *             service exception
	 */
	public Map<String, String> getProperties(String user, String itemId) throws ServiceException {
		try {
			StorageHubClient shc = new StorageHubClient();
			Item item = shc.open(itemId).asItem().get();
			Metadata metadata = item.getPropertyMap();
			Map<String, String> itemMap = new HashMap<String, String>();
			Map<String, Object> metadataMap = metadata.getValues();
			for (String key : metadataMap.keySet()) {
				String s = String.valueOf(metadataMap.get(key));
				itemMap.put(key, s);
			}
			return itemMap;

		} catch (Throwable e) {
			logger.error("Error retrieving properties: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param itemId
	 *            item id
	 * @return input stream
	 * @throws ServiceException
	 *             service exception
	 */
	public InputStream getFileOnWorkspace(String user, String itemId) throws ServiceException {
		try {
			logger.debug("getInputStreamForItemOnWorkspace: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			FileContainer fileContainer = shc.open(itemId).asFile();
			StreamDescriptor streamDescriptor = fileContainer.download();
			return streamDescriptor.getStream();

		} catch (Throwable e) {
			logger.error("Error retrieving InputStream for File: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	public ItemDownload getItemDownload(String user, String itemId) throws ServiceException {
		try {
			logger.debug("getItemDownload: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);
			ItemContainer<Item> itemContainer = openResolver.asItem();
			Item item = itemContainer.get();

			StreamDescriptor streamDescriptor = null;
			if (item instanceof AbstractFileItem) {
				FileContainer fileContainer = openResolver.asFile();
				streamDescriptor = fileContainer.download();
			} else {
				if (item instanceof FolderItem) {
					FolderContainer folderContainer = openResolver.asFolder();
					streamDescriptor = folderContainer.download();
				} else {
				}
			}

			if (streamDescriptor == null) {
				logger.error("This type of item does not support download: " + itemId);
				return null;
			} else {
				ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
						item.getPath(), getItemType(item));
				ItemDownload itemDownload = new ItemDownload(itemDescription, streamDescriptor.getStream());
				return itemDownload;
			}
		} catch (Throwable e) {
			logger.error("Error retrieving InputStream for item: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Item description
	 * @throws ServiceException
	 *             Error
	 */
	public ItemDescription getItemDescription(String user, String itemId) throws ServiceException {
		try {
			logger.info("Retrieve file info on workspace: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);
			ItemContainer<Item> itemContainer = openResolver.asItem();
			Item item = itemContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Retrieve file info on workspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param itemId
	 *            item id
	 * @return public link
	 * @throws ServiceException
	 *             service exception
	 */
	public String getPublicLink(String user, String itemId) throws ServiceException {
		try {
			logger.debug("getPublicLink: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			URL url = shc.open(itemId).asFile().getPublicLink();
			return url.toString();

		} catch (Throwable e) {
			logger.error("Error retrieving public link: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param folderId
	 *            folder id
	 * @return input stream
	 * @throws ServiceException
	 *             service exception public InputStream zipFolder(String user,
	 *             String folderId) throws ServiceException { try {
	 *             logger.debug("zipFolder: [user=" + user + ", folderId=" +
	 *             folderId + "]"); StorageHubClient shc = new
	 *             StorageHubClient(); OpenResolver openResolver =
	 *             shc.open(folderId);
	 * 
	 *             ItemContainer<Item> itemContainer = openResolver.asItem();
	 *             Item item = itemContainer.get(); if (item instanceof
	 *             FolderItem) { StreamDescriptor streamDescriptor =
	 *             openResolver.asFolder().download(); InputStream is =
	 *             streamDescriptor.getStream(); return is; } else { throw new
	 *             ServiceException("Is not a valid folder!"); }
	 * 
	 *             } catch (Throwable e) { logger.error("Error in zip Folder: "
	 *             + e.getLocalizedMessage(), e); throw new
	 *             ServiceException(e.getLocalizedMessage(), e); }
	 * 
	 *             }
	 */

	/**
	 * 
	 * @param user
	 *            user
	 * @param itemName
	 *            item name
	 * @return workspace item
	 * @throws ServiceException
	 *             service exception
	 */
	public ItemDescription getItemInRootFolderOnWorkspace(String user, String itemName) throws ServiceException {
		try {
			logger.debug("GetItemInRootFolder: [user=" + user + ", itemName=" + itemName + "]");
			StorageHubClient shc = new StorageHubClient();
			ListResolver listResolver = shc.getWSRoot().findByName(itemName);
			List<? extends Item> items = listResolver.getItems();

			if (items == null || items.isEmpty()) {
				logger.debug("No item found");
				return null;
			} else {
				Item item = items.get(0);
				logger.debug("Item: " + item);
				if (item != null) {
					logger.debug("Item Id=" + item.getId());
					ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
							item.getPath(), getItemType(item));
					return itemDescription;
				} else {
					return null;
				}
			}

		} catch (Throwable e) {
			logger.error("Error in get Item in RootFolder: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param folderId
	 *            folder id
	 * @param itemName
	 *            item name
	 * @return workspace item
	 * @throws ServiceException
	 *             service exception
	 */
	public ItemDescription getItemInFolderOnWorkspace(String user, String folderId, String itemName)
			throws ServiceException {
		try {
			logger.debug("GetItemInFolder: [user=" + user + ", folderId=" + folderId + ", itemName=" + itemName + "]");
			StorageHubClient shc = new StorageHubClient();
			ListResolver listResolver = shc.open(folderId).asFolder().findByName(itemName);
			List<? extends Item> items = listResolver.getItems();

			if (items == null || items.isEmpty()) {
				logger.debug("No item found");
				return null;
			} else {
				Item item = items.get(0);
				logger.debug("Item: " + item);
				if (item != null) {
					logger.debug("Item Id=" + item.getId());
					ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
							item.getPath(), getItemType(item));
					return itemDescription;
				} else {
					return null;
				}
			}

		} catch (Throwable e) {
			logger.error("Error in get Item in Folder: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param itemId
	 *            item id
	 * @throws ServiceException
	 *             service exception
	 */
	public void deleteItem(String user, String itemId) throws ServiceException {
		try {
			logger.debug("Delete Item: [User=" + user + ", ItemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			ItemContainer<Item> itemContainer = openResolver.asItem();
			itemContainer.delete();

			return;
		} catch (Throwable e) {
			logger.error("Error deleting Item: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());

		}

	}

	private String getItemType(Item item) {
		if (item instanceof AbstractFileItem) {
			return AbstractFileItem.class.getSimpleName();
		} else {
			if (item instanceof FolderItem) {
				return FolderItem.class.getSimpleName();
			} else {
				if (item instanceof GCubeItem) {
					return GCubeItem.class.getSimpleName();
				} else {
					if (item instanceof TrashItem) {
						return TrashItem.class.getSimpleName();
					} else {
						return null;
					}
				}
			}
		}
	}

}
