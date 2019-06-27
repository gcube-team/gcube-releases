package org.gcube.portlets.admin.accountingmanager.server.storage;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StorageUtil {

	private static final String ACCOUNTING_MANAGER = "AccountingManager";
	private static Logger logger = LoggerFactory.getLogger(StorageUtil.class);

	/**
	 * 
	 * @param user
	 *            user
	 * @return item id
	 * @throws ServiceException
	 *             service exception
	 */
	public String createAccountingFolderOnWorkspace(String user) throws ServiceException {
		try {
			logger.info("CreateAccountingFolderOnWorkspace: [User=" + user + "]");

			StorageHubClient shc = new StorageHubClient();
			FolderContainer root = shc.getWSRoot();
			ListResolver listResolver = root.findByName(ACCOUNTING_MANAGER);
			List<? extends Item> items = listResolver.getItems();
			if (items == null || items.isEmpty()) {
				logger.debug("No folder found");
				FolderContainer folderContainer = root.newFolder(ACCOUNTING_MANAGER, ACCOUNTING_MANAGER);
				FolderItem folderItem = folderContainer.get();
				return folderItem.getId();
			} else {
				Item item = items.get(0);
				logger.debug("Item: " + item);
				if (item != null) {
					return item.getId();
				} else {
					logger.debug("No folder found");
					FolderContainer folderContainer = root.newFolder(ACCOUNTING_MANAGER, ACCOUNTING_MANAGER);
					FolderItem folderItem = folderContainer.get();
					return folderItem.getId();
				}
			}

		} catch (Throwable e) {
			logger.error("Error in create Accounting folder on workspace: " + e.getLocalizedMessage(), e);
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
	 * @return item description
	 * @throws ServiceException
	 *             service exception
	 */
	public ItemDescription saveOnWorkspace(String user, String folderId, String name, String description,
			InputStream inputStream) throws ServiceException {
		try {
			logger.info("Save item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			FileContainer fileContainer = folderContainer.uploadFile(inputStream, name, description);
			AbstractFileItem item = fileContainer.get();

			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			itemDescription.setMimeType(item.getContent().getMimeType());
			itemDescription.setLenght(String.valueOf(item.getContent().getSize()));

			logger.debug("File saved: " + itemDescription);

			return itemDescription;

		} catch (Throwable e) {
			logger.error("Save item on workspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

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
