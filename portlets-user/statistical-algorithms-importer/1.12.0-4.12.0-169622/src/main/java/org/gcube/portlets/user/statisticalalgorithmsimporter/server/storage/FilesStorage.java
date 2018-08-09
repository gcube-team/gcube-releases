package org.gcube.portlets.user.statisticalalgorithmsimporter.server.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspaceSharedFolder;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.OpenResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */

public class FilesStorage {

	private static final String STATISTICAL_ALGORITHM_PROJECT_MIMETYPE = "text/xml";
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
			logger.debug("getPublicLink: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			URL url = shc.open(itemId).asFile().getPublicLink();
			return url.toString();

		} catch (Throwable e) {
			logger.error("Get public link: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @param folderId
	 *            Folder id
	 * @return workspace item
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public WorkspaceItem copyItemOnFolder(String user, String itemId, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Copy item on folder: [user=" + user + ", itemId=" + itemId + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination is not a folder!");
			}

			WorkspaceItem item = ws.copy(itemId, folderId);

			return item;
		} catch (Throwable e) {
			logger.error("Copy item on folder on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @param folderId
	 *            Destination folder id
	 * @param newName
	 *            New name
	 * @return workspace item
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public WorkspaceItem copyItemOnFolderWithNewName(String user, String itemId, String folderId, String newName)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Copy item on folder with new name: [user=" + user + ", itemId=" + itemId + ", folderId="
					+ folderId + ", newName=" + newName + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination is not a folder!");
			}

			WorkspaceItem item = ws.copy(itemId, newName, folderId);

			return item;
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
	public void deleteItemOnFolder(String user, String itemId) throws StatAlgoImporterServiceException {
		try {
			logger.debug("Delete Item: [User=" + user + ", ItemId=" + itemId+"]");
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
		Workspace ws;
		try {
			logger.info("Delete folder: [user=" + user + ", parentId=" + parentId + ", folderName=" + folderName + "]");
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("No valid project folder!");
			}

			WorkspaceItem target = ws.find(folderName, parentId);

			if (target != null) {
				ws.removeItems(target.getId());
			}

			return;
		} catch (Throwable e) {
			logger.error("Delete folder on workspace: " + e.getLocalizedMessage(), e);
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
	 * @param folderDescription
	 *            folder description
	 * @return workspace folder
	 * @throws StatAlgoImporterServiceException
	 *             exception
	 */
	public WorkspaceFolder createFolder(String user, String parentId, String folderName, String folderDescription)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Create folder: [user=" + user + ", parentId=" + parentId + ", folderName=" + folderName
					+ ", folderDescription=" + folderDescription + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("No valid project folder!");
			}

			WorkspaceFolder projectTargetFolder = ws.createFolder(folderName, folderDescription, parentId);

			return projectTargetFolder;
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
	 * @return workspace item
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public WorkspaceItem find(String user, String parentId, String name) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Find: [user=" + user + ", parentId=" + parentId + ", name=" + name + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("No valid project folder!");
			}

			return ws.find(name, parentId);

		} catch (Throwable e) {
			logger.error("Find: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param parentName
	 *            Parent name
	 * @param name
	 *            Name
	 * @return WorkspaceItem
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public WorkspaceItem findInParentName(String user, String parentName, String name)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Find by parent name: [user=" + user + ", parentName=" + parentName + ", name=" + name + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem parentItem = ws.getItem(parentName);
			if (!parentItem.isFolder()) {
				throw new StatAlgoImporterServiceException("No valid folder: " + parentName + "!");
			}

			return ws.find(name, parentItem.getId());

		} catch (Throwable e) {
			logger.error("Find by parent name: " + e.getLocalizedMessage(), e);
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
		Workspace ws;
		try {
			logger.info("Save project: [user=" + user + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination is not a folder!");
			}

			WorkspaceItem projectItem = ws.find(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				ws.createExternalFile(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME,
						STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION, STATISTICAL_ALGORITHM_PROJECT_MIMETYPE,
						inputStream, folderId);
			} else {
				ws.updateItem(projectItem.getId(), inputStream);
			}

			return;
		} catch (Throwable e) {
			logger.error("Save project on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
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
		Workspace ws;
		try {
			logger.info("Save item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", mimeType=" + mimeType + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination is not a folder!");
			}

			WorkspaceItem projectItem = ws.find(name, folderId);

			if (projectItem == null) {
				ws.createExternalFile(name, description, mimeType, inputStream, folderId);
			} else {
				ws.updateItem(projectItem.getId(), inputStream);
			}

			return;
		} catch (Throwable e) {
			logger.error("Save item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
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
	public WorkspaceItem createItemOnWorkspace(String user, InputStream inputStream, String name, String description,
			String mimeType, String folderId) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Create item on workspace: [user=" + user + ", name=" + name + ", description=" + description
					+ ", mimeType=" + mimeType + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);

			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination is not a folder!");
			}

			ExternalFile workspaceItem = ws.createExternalFile(name, description, mimeType, inputStream, folderId);

			return workspaceItem;
		} catch (Throwable e) {
			logger.error("Create item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param inputStream
	 *            InputStream
	 * @param name
	 *            Name
	 * @param description
	 *            Description
	 * @param mimeType
	 *            MimeType
	 * @param folderName
	 *            Folder Path
	 * @return WorkspaceItem
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public WorkspaceItem createItemOnWorkspaceHowAdmin(String user, InputStream inputStream, String name,
			String description, String mimeType, String folderName) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Create item on workspace how admin: [user=" + user + ", name=" + name + ", description="
					+ description + ", mimeType=" + mimeType + ", folderName=" + folderName + "]");

			ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceItem root = ws.getRoot();
			WorkspaceItem folderItem = ws.find(folderName, root.getId());

			if (folderItem == null || !folderItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Destination " + folderName + " is not a valid folder!");
			}

			WorkspaceItem codeJar = ws.find(name, folderItem.getId());

			if (codeJar == null) {
				return ws.createExternalFile(name, description, mimeType, inputStream, folderItem.getId());
			} else {
				ws.updateItem(codeJar.getId(), inputStream);
				return codeJar;
			}

		} catch (Throwable e) {
			logger.error("Create item on workspace how admin: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
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
	public InputStream retrieveProjectItemOnWorkspace(String user, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Retrieve project item on workspace: [user=" + user + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Item is not valid folder!");
			}

			WorkspaceItem projectItem = ws.find(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				throw new StatAlgoImporterServiceException("No project found in this folder!");
			}

			return retrieveInputStream(user, projectItem);

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
		Workspace ws;
		try {
			logger.info("Exist project item on workspace: [user=" + user + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Item is not valid folder!");
			}

			WorkspaceItem projectItem = ws.find(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				return false;
			} else {
				return true;
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
	 * @return Workspace Item
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public WorkspaceItem retrieveItemInfoOnWorkspace(String user, String itemId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Retrieve item info on workspace: [user=" + user + ", itemId=" + itemId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			return workSpaceItem;

		} catch (Throwable e) {
			logger.error("Retrieve item info on workspace: " + e.getLocalizedMessage(), e);
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
	public InputStream retrieveItemOnWorkspace(String user, String itemId) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Retrieve item on workspace: [user=" + user + ", itemId=" + itemId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Folder Item is not valid!");
			}

			return retrieveInputStream(user, workSpaceItem);

		} catch (Throwable e) {
			logger.error("Retieve item on workspace: " + e.getLocalizedMessage(), e);

			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param wi
	 *            Workspace item
	 * @return InputStream
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public InputStream retrieveInputStream(String user, WorkspaceItem wi) throws StatAlgoImporterServiceException {
		InputStream is = null;
		try {
			logger.info("Retrieve input stream: [user=" + user + ", workspaceItem=" + wi + "]");

			org.gcube.common.homelibrary.home.workspace.folder.items.File gcubeItem = ((org.gcube.common.homelibrary.home.workspace.folder.items.File) wi);
			is = gcubeItem.getData();
			return is;

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException("Error retrieving file from storage: " + e.getLocalizedMessage(),
					e);
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
		Workspace ws;
		try {
			logger.info("Save string in item: [user=" + user + ", itemId=" + itemId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem == null) {
				throw new StatAlgoImporterServiceException("No item retrieved on workspace!");
			} else {
				if (workSpaceItem.isFolder()) {
					throw new StatAlgoImporterServiceException("Item is a folder!");
				} else {

				}
			}
			// convert String into InputStream
			InputStream is = new ByteArrayInputStream(data.getBytes());

			ws.updateItem(itemId, is);

			return;

		} catch (Throwable e) {
			logger.error("Save string in item on workspace: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
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
		Workspace ws;
		try {
			logger.info("Save input stream in item: [user=" + user + ", itemId=" + itemId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem == null) {
				throw new StatAlgoImporterServiceException("No item retrieved on workspace!");
			} else {
				if (workSpaceItem.isFolder()) {
					throw new StatAlgoImporterServiceException("Item is a  folder!");
				} else {

				}
			}
			// convert String into InputStream
			ws.updateItem(itemId, is);

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
	 *            Folder Id
	 * @return Zip folder
	 * @throws StatAlgoImporterServiceException
	 *             Exception
	 */
	public File zipFolder(String user, String folderId) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			logger.info("Zip folder: [user=" + user + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Item is not valid folder!");
			}

			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;

			File fileZip = ZipUtil.zipFolder(folder);

			return fileZip;

		} catch (Throwable e) {
			logger.error("Zip folder on workspace: " + e.getLocalizedMessage(), e);
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
		Workspace ws;
		try {
			logger.info("Zip folder with exclude: [user=" + user + ", folderId=" + folderId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Item is not valid folder!");
			}

			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;

			File fileZip = ZipUtil.zipFolder(folder, false, idsToExclude);

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
	public void downloadInputFile(String fileUrl, Path destination) throws StatAlgoImporterServiceException {
		try {
			logger.info("Download input file: [fileUrl=" + fileUrl + ", destination=" + destination + "]");

			URL smpFile = new URL(fileUrl);
			URLConnection uc = (URLConnection) smpFile.openConnection();
			InputStream is = uc.getInputStream();
			inputStreamToFile(is, destination);
			is.close();

		} catch (Throwable e) {
			logger.error("Download input file: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	private void inputStreamToFile(InputStream is, Path destination) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(destination.toFile());
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}

	/**
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
		Workspace ws;
		try {
			logger.info("Get shared info: [user=" + user + ", itemId=" + itemId + "]");

			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workspaceItem = ws.getItem(itemId);
			if (!workspaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException("Attention this is not a folder!");
			}

			List<String> shared = new ArrayList<String>();
			if (workspaceItem.isShared()) {
				JCRWorkspaceSharedFolder sharedFolder = (JCRWorkspaceSharedFolder) workspaceItem;
				shared = sharedFolder.getUsers();
			} else {
				if (workspaceItem.getOwner() != null) {
					String owner = workspaceItem.getOwner().getPortalLogin();
					if (owner != null && !owner.isEmpty()) {
						shared.add(owner);
					} else {

					}
				} else {

				}
			}
			return shared;

		} catch (Throwable e) {
			logger.error("Get shared info: " + e.getLocalizedMessage(), e);
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage(), e);

		}
	}

}
