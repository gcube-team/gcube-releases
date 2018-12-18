package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
import org.gcube.common.storagehub.model.items.SharedFolder;
import org.gcube.common.storagehub.model.items.TrashItem;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */

public class FilesStorage {

	// private static final String STATISTICAL_ALGORITHM_PROJECT_MIMETYPE =
	// "text/xml";
	private static final String STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION = "Statistical Algorithm Project File";
	private static final String STATISTICAL_ALGORITHM_PROJECT_FILE_NAME = "stat_algo.project";

	public static final Logger logger = LoggerFactory.getLogger(FilesStorage.class);

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return public link
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public String getPublicLink(String user, String itemId) throws StatAlgoImporterServiceException {

		try {
			logger.info("getPublicLink: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			URL url = shc.open(itemId).asFile().getPublicLink();
			logger.debug("getPublicLink: "+url);
			return url.toString();

		} catch (Throwable e) {
			logger.error("Get public link: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @param folderId
	 *            Folder id
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public ItemDescription copyItemOnFolder(String user, String itemId, String folderId)
			throws StatAlgoImporterServiceException {

		try {
			logger.info("Copy item on folder: [user=" + user + ", itemId=" + itemId + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForFile = shc.open(itemId);
			FileContainer fileContainer = openResolverForFile.asFile();
			OpenResolver openResolverForFolder = shc.open(folderId);
			FolderContainer folderContainer = openResolverForFolder.asFolder();
			FileContainer fileCreatedContainer = fileContainer.copy(folderContainer, fileContainer.get().getName());
			AbstractFileItem item = fileCreatedContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			logger.debug("New item copied: "+itemDescription);
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Copy item on folder on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * 
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @param folderId
	 *            Destination folder id
	 * @param newName
	 *            New name
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public ItemDescription copyItemOnFolderWithNewName(String user, String itemId, String folderId, String newName)
			throws StatAlgoImporterServiceException {
		try {
			logger.info("Copy item on folder with new name: [user=" + user + ", itemId=" + itemId + ", folderId="
					+ folderId + ", newName=" + newName + "]");

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolverForFile = shc.open(itemId);
			FileContainer fileContainer = openResolverForFile.asFile();
			OpenResolver openResolverForFolder = shc.open(folderId);
			FolderContainer folderContainer = openResolverForFolder.asFolder();
			FileContainer fileCreatedContainer = fileContainer.copy(folderContainer, newName);
			AbstractFileItem item = fileCreatedContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));

			logger.debug("File created: " + itemDescription);
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Copy item on folder with new name on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public void deleteItem(String user, String itemId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Delete Item: [User=" + user + ", ItemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			ItemContainer<Item> itemContainer = openResolver.asItem();
			itemContainer.delete();

			return;
		} catch (Throwable e) {
			logger.error("Delete Item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param parentId
	 *            parent id
	 * @param folderName
	 *            folder name
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public void deleteFolder(String user, String parentId, String folderName) throws StatAlgoImporterServiceException {
		try {
			logger.info("Delete folder: [user=" + user + ", parentId=" + parentId + ", folderName=" + folderName + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openParentResolver = shc.open(parentId);

			FolderContainer parentFolderContainer = openParentResolver.asFolder();
			ListResolver listResolver = parentFolderContainer.findByName(folderName);
			List<? extends Item> items = listResolver.getItems();
			if (items == null || items.isEmpty()) {
				logger.debug("No folder found");
				return;
			} else {
				Item item = items.get(0);
				logger.debug("Item: " + item);
				if (item != null) {
					logger.debug("Item Id=" + item.getId());
					OpenResolver openResolver = shc.open(item.getId());
					FolderContainer folderContainer = openResolver.asFolder();
					folderContainer.delete();
					return;
				} else {
					logger.debug("No folder found");
					return;
				}
			}
		} catch (Throwable e) {
			logger.error("Delete folder on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * 
	 * @param user
	 *            user
	 * @param parentId
	 *            parent id
	 * @param folderName
	 *            folder name
	 * @param folderDescription
	 *            folder description
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public ItemDescription createFolder(String user, String parentId, String folderName, String folderDescription)
			throws StatAlgoImporterServiceException {
		try {
			logger.info("Create folder: [user=" + user + ", parentId=" + parentId + ", folderName=" + folderName
					+ ", folderDescription=" + folderDescription + "]");

			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(parentId);

			FolderContainer folderContainer = openResolver.asFolder();
			FolderContainer folderContainerNew = folderContainer.newFolder(folderName, folderDescription);
			FolderItem folderCreated = folderContainerNew.get();

			ItemDescription itemDescription = new ItemDescription(folderCreated.getId(), folderCreated.getName(),
					folderCreated.getOwner(), folderCreated.getPath(), getItemType(folderCreated));
			logger.debug("Folder created: "+itemDescription);
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Create folder on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param parentId
	 *            Parent id
	 * @param name
	 *            Name
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public ItemDescription find(String user, String parentId, String name) throws StatAlgoImporterServiceException {
		try {
			logger.info("Find: [user=" + user + ", parentId=" + parentId + ", name=" + name + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(parentId);

			FolderContainer folderContainer = openResolver.asFolder();
			ListResolver listResolver = folderContainer.findByName(name);
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
			logger.error("Find: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param inputStream
	 *            Input stream
	 * @param folderId
	 *            Folder id
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public void saveStatisticalAlgorithmProject(String user, InputStream inputStream, String folderId)
			throws StatAlgoImporterServiceException {
		try {
			logger.info("Save project: [user=" + user + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			ListResolver listResolver = folderContainer.findByName(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME);
			List<? extends Item> items = listResolver.getItems();
			if (items == null || items.isEmpty()) {
				logger.debug("No item found");
				FileContainer fileContainer = folderContainer.uploadFile(inputStream,
						STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION);
				logger.debug("Item uploaded: " + fileContainer.get().getId());
			} else {
				Item item = items.get(0);
				logger.debug("Item found: " + item);
				FileContainer fileContainer = folderContainer.uploadFile(inputStream,
						STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION);
				logger.debug("Item updated: " + fileContainer.get().getId());
			}
		} catch (Throwable e) {
			logger.error("Save project on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param inputStream
	 *            Input stream
	 * @param name
	 *            Name
	 * @param description
	 *            Description
	 * @param mimeType
	 *            Mimetype
	 * @param folderId
	 *            Folder destination
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public void saveItemOnWorkspace(String user, InputStream inputStream, String name, String description,
			String mimeType, String folderId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Save item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", mimeType=" + mimeType + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			folderContainer.uploadFile(inputStream, name, description);
			logger.debug("Item saved");
			return;
		} catch (Throwable e) {
			logger.error("Save item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 *
	 * 
	 * @param user
	 *            User
	 * @param inputStream
	 *            Input stream
	 * @param name
	 *            Item name
	 * @param description
	 *            Item description
	 * @param mimeType
	 *            Item mimetype
	 * @param folderId
	 *            Destination folder
	 * @return Workspace item
	 * @throws StatAlgoImporterServiceException
	 *             Exceptioon
	 */
	public ItemDescription createItemOnWorkspace(String user, InputStream inputStream, String name, String description,
			String mimeType, String folderId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Create item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", mimeType=" + mimeType + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			FileContainer fileContainerNew = folderContainer.uploadFile(inputStream, name, description);

			AbstractFileItem fileCreated = fileContainerNew.get();

			ItemDescription itemDescription = new ItemDescription(fileCreated.getId(), fileCreated.getName(),
					fileCreated.getOwner(), fileCreated.getPath(), getItemType(fileCreated));
			logger.debug("Item created: "+itemDescription);
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Create item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param folderId
	 *            Folder id
	 * @return Input stream
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 * 
	 */
	public InputStream getProjectItemOnWorkspace(String user, String folderId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Retrieve project item on workspace: [user=" + user + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			ListResolver listResolver = folderContainer.findByName(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME);
			List<ItemContainer<? extends Item>> itemsContainer = listResolver.getContainers();
			if (itemsContainer == null || itemsContainer.isEmpty()) {
				logger.debug("No project found in this folder!");
				throw new StatAlgoImporterServiceException("No project found in this folder!");
			} else {
				ItemContainer<? extends Item> itemContainer = itemsContainer.get(0);
				logger.debug("ItemContainer: " + itemContainer);
				if (itemContainer != null) {
					logger.debug("Item Id: " + itemContainer.get().getId());
					StreamDescriptor streamDescr = itemContainer.download();
					logger.debug("Stream Descriptor: " + streamDescr);
					return streamDescr.getStream();
				} else {
					logger.debug("No project found in this folder!");
					throw new StatAlgoImporterServiceException("No project found in this folder!");
				}
			}

		} catch (Throwable e) {
			logger.error("Retrieve project item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param folderId
	 *            Folder id
	 * @return boolean True if exist
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public boolean existProjectItemOnWorkspace(String user, String folderId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Exist project item on workspace: [user=" + user + ", folderId=" + folderId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(folderId);

			FolderContainer folderContainer = openResolver.asFolder();
			ListResolver listResolver = folderContainer.findByName(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME);
			List<? extends Item> items = listResolver.getItems();
			if (items == null || items.isEmpty()) {
				return false;
			} else {
				Item item = items.get(0);
				logger.debug("Item: " + item);
				if (item != null) {
					logger.debug("Item Id=" + item.getId());
					return true;

				} else {
					return false;
				}
			}

		} catch (Throwable e) {
			logger.error("Exist project item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public ItemDescription getFileInfoOnWorkspace(String user, String itemId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Retrieve file info on workspace: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			FileContainer fileContainer = openResolver.asFile();
			AbstractFileItem item = fileContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			itemDescription.setMimeType(item.getContent().getMimeType());
			itemDescription.setLenght(String.valueOf(item.getContent().getSize()));
			logger.debug("File info: "+itemDescription);
			return itemDescription;

		} catch (Throwable e) {
			logger.error("Retrieve file info on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Item description
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public ItemDescription getFolderInfoOnWorkspace(String user, String itemId)
			throws StatAlgoImporterServiceException {

		try {
			logger.info("Retrieve folder info on workspace: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			FolderItem item = openResolver.asFolder().get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			logger.debug("Folder info: "+itemDescription);
			return itemDescription;

		} catch (Throwable e) {
			logger.error("Retrieve folder info on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Input stream
	 * @throws StatAlgoImporterServiceException
	 *             Excetpion
	 */
	public InputStream getFileOnWorkspace(String user, String itemId) throws StatAlgoImporterServiceException {

		try {
			logger.info("Retrieve file on workspace: [user=" + user + ", itemId=" + itemId + "]");

			StorageHubClient shc = new StorageHubClient();

			StreamDescriptor streamDescr = shc.open(itemId).asFile().download();

			InputStream is = streamDescr.getStream();
			return is;

		} catch (Throwable e) {
			logger.error("Retieve file on workspace: " + e.getLocalizedMessage(), e);

			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item on workspace
	 * @param data
	 *            String to save
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public void saveStringInItem(String user, String itemId, String data) throws StatAlgoImporterServiceException {
		try {
			logger.info("Save string in item: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			FileContainer fileContainer = openResolver.asFile();
			AbstractFileItem abstractFileItem = fileContainer.get();
			if (abstractFileItem == null) {
				throw new StatAlgoImporterServiceException("No item retrieved on workspace!");
			}

			String parentId = abstractFileItem.getParentId();
			FolderContainer parentContainer = shc.open(parentId).asFolder();

			// convert String into InputStream
			try (InputStream is = new ByteArrayInputStream(data.getBytes())) {
				parentContainer.uploadFile(is, abstractFileItem.getName(), abstractFileItem.getDescription());
			}
			return;

		} catch (Throwable e) {
			logger.error("Save string in item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @param is
	 *            Input stream
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public void saveInputStreamInItem(String user, String itemId, InputStream is)
			throws StatAlgoImporterServiceException {
		try {
			logger.info("Save input stream in item: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			FileContainer fileContainer = openResolver.asFile();
			AbstractFileItem abstractFileItem = fileContainer.get();

			String parentId = abstractFileItem.getParentId();
			FolderContainer parentContainer = shc.open(parentId).asFolder();
			parentContainer.uploadFile(is, abstractFileItem.getName(), abstractFileItem.getDescription());

			return;

		} catch (Throwable e) {
			logger.error("Save input stream in item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param folderId
	 *            Folder id
	 * @param idsToExclude
	 *            List of ids to exclude
	 * @return Zip folder
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public File zipFolder(String user, String folderId, List<String> idsToExclude)
			throws StatAlgoImporterServiceException {
		try {
			logger.info("Zip folder with exclude: [user=" + user + ", folderId=" + folderId + ", idsToExclude="
					+ idsToExclude + "]");
			String[] idsArray = new String[idsToExclude.size()];
			idsArray = idsToExclude.toArray(idsArray);

			StorageHubClient shc = new StorageHubClient();
			StreamDescriptor streamDescr = shc.open(folderId).asFolder().download(idsArray);

			File fileZip = Files.createTempFile(streamDescr.getFileName(), "").toFile();
			logger.debug("File zip: " + fileZip.getAbsolutePath());

			try (FileOutputStream fos = new FileOutputStream(fileZip); InputStream is = streamDescr.getStream()) {
				byte[] buf = new byte[1024];
				int read = -1;
				while ((read = is.read(buf)) != -1) {
					fos.write(buf, 0, read);
				}
			}

			return fileZip;

		} catch (Throwable e) {
			logger.error("Zip folder with exclude on workspace: " + e.getLocalizedMessage(), e);

			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * @param fileUrl
	 *            Url
	 * @param destination
	 *            Destination
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public void downloadExternalInputFile(String fileUrl, Path destination) throws StatAlgoImporterServiceException {
		try {
			logger.info("Download input file: [fileUrl=" + fileUrl + ", destination=" + destination + "]");

			URL smpFile = new URL(fileUrl);
			URLConnection uc = (URLConnection) smpFile.openConnection();

			try (FileOutputStream out = new FileOutputStream(destination.toFile());
					InputStream is = uc.getInputStream();) {
				byte buf[] = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) > 0)
					out.write(buf, 0, len);
			}

		} catch (Throwable e) {
			logger.error("Download input file: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item
	 * @return The list of users with whom a folder is shared, including owner
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public List<String> getSharedList(String user, String itemId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Get shared info: [user=" + user + ", itemId=" + itemId + "]");
			List<String> shared = new ArrayList<String>();
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);

			FolderContainer folderContainer = openResolver.asFolder();
			FolderItem folderItem = folderContainer.get();

			if (folderItem != null) {
				if (folderItem.isShared()) {
					if (folderItem instanceof SharedFolder) {
						SharedFolder sharedFolder = (SharedFolder) folderItem;
						Metadata metadata = sharedFolder.getUsers();
						shared = new ArrayList<>(metadata.getValues().keySet());
					} else {
						logger.error("The folder is shared but is not of type SharedFolder: [itemId=" + itemId + "]");
						if (folderItem.getOwner() != null && !folderItem.getOwner().isEmpty()) {
							shared.add(folderItem.getOwner());
						} else {
							logger.error("Invalid owner for: [itemId=" + itemId + "]");
						}
					}
				} else {
					if (folderItem.getOwner() != null && !folderItem.getOwner().isEmpty()) {
						shared.add(folderItem.getOwner());
					} else {
						logger.error("Invalid owner for: [itemId=" + itemId + "]");
					}
				}
			} else {
				logger.debug("Folder null: [itemId=" + itemId + "]");
			}

			logger.debug("Shared: " + shared);
			return shared;

		} catch (Throwable e) {
			logger.error("Get shared info: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return ItemDownload
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public ItemDownload getItemDownload(String user, String itemId) throws StatAlgoImporterServiceException {
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
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Item Description
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public ItemDescription getItemDescription(String user, String itemId) throws StatAlgoImporterServiceException {
		try {
			logger.info("Retrieve file info on workspace: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			OpenResolver openResolver = shc.open(itemId);
			ItemContainer<Item> itemContainer = openResolver.asItem();
			Item item = itemContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), getItemType(item));
			logger.debug("Item: "+itemDescription);
			return itemDescription;
		} catch (Throwable e) {
			logger.error("Retrieve file info on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param item
	 *            Item
	 * @return Item type
	 */
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
