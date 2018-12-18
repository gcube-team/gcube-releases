package org.gcube.portlets.widgets.githubconnector.server.storage;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.portlets.widgets.githubconnector.shared.exception.ServiceException;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StorageUtil {

	private static Logger logger = Logger.getLogger(StorageUtil.class);

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
			logger.info("Delete Item: [User=" + user + ", ItemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			ItemContainer<Item> itemContainer = openResolver.asItem();
			itemContainer.delete();

			return;
		} catch (Throwable e) {
			logger.error("Delete Item on workspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param destinationFolderId
	 *            destination folder id
	 * @param folderName
	 *            folder name
	 * @param folderDescription
	 *            folder description
	 * @return folder id
	 * @throws ServiceException
	 *             service exception
	 */
	public String createFolderOnWorkspace(String user, String destinationFolderId, String folderName,
			String folderDescription) throws ServiceException {
		try {
			logger.info("CreateFolderOnWorkspace: [User=" + user + ", FolderId:" + destinationFolderId + ", folderName="
					+ folderName + ", folderDescription=" + folderDescription + "]");

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(destinationFolderId);

			FolderContainer parentFolderContainer = openResolver.asFolder();
			FolderContainer folderContainer = parentFolderContainer.newFolder(folderName, folderDescription);
			FolderItem folderItem = folderContainer.get();

			logger.debug("Folder created: " + folderItem.getId());
			return folderItem.getId();
		} catch (Throwable e) {
			logger.error("Error in create folder on workspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param folderId
	 *            destination folder id
	 * @param name
	 *            file name
	 * @param description
	 *            file description
	 * @param inputStream
	 *            input stream
	 * @return file id
	 * @throws ServiceException
	 *             service exception
	 */
	public String saveOnWorkspace(String user, String folderId, String name, String description,
			InputStream inputStream) throws ServiceException {
		try {
			logger.info("Save item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			FileContainer fileContainer = folderContainer.uploadFile(inputStream, name, description);
			AbstractFileItem abstractFileItem = fileContainer.get();

			logger.debug("File saved id: " + abstractFileItem.getId());
			return abstractFileItem.getId();
		} catch (Throwable e) {
			logger.error("Save item on workspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

}
