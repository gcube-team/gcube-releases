package org.gcube.portlets.user.dataminermanager.server.storage;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
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
	public InputStream getInputStreamForItemOnWorkspace(String user, String itemId) throws ServiceException {
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
	 *             service exception
	 */
	public InputStream zipFolder(String user, String folderId) throws ServiceException {
		try {
			logger.debug("zipFolder: [user=" + user + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			ItemContainer<Item> itemContainer = openResolver.asItem();
			Item item = itemContainer.get();
			if (item instanceof FolderItem) {
				StreamDescriptor streamDescriptor = openResolver.asFolder().download();
				InputStream is = streamDescriptor.getStream();
				return is;
			} else {
				throw new ServiceException("Is not a valid folder!");
			}

		} catch (Throwable e) {
			logger.error("Error in zip Folder: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

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
	public Item getItemInRootFolderOnWorkspace(String user, String itemName) throws ServiceException {
		try {
			logger.debug("GetItemInRootFolder: [user=" + user + ", itemName=" + itemName + "]");
			StorageHubClient shc = new StorageHubClient();
			ListResolver listResolver = shc.getWSRoot().findByName(itemName);
			List<? extends Item> items = listResolver.getItems();

			if (items != null && !items.isEmpty()) {
				return items.get(0);
			} else {
				return null;
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
	public Item getItemInFolderOnWorkspace(String user, String folderId, String itemName) throws ServiceException {
		try {
			logger.debug("GetItemInFolder: [user=" + user + ", folderId=" + folderId + ", itemName=" + itemName + "]");
			StorageHubClient shc = new StorageHubClient();
			ListResolver listResolver = shc.open(folderId).asFolder().findByName(itemName);
			List<? extends Item> items = listResolver.getItems();

			if (items != null && !items.isEmpty()) {
				return items.get(0);
			} else {
				return null;
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
			logger.debug("Delete Item: [User=" + user + ", ItemId=" + itemId+"]");
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

}
