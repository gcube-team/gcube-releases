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
import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */

public class FilesStorage {

	private static final String STATISTICAL_ALGORITHM_PROJECT_MIMETYPE = "text/xml";
	private static final String STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION = "Statistical Algorithm Project File";
	private static final String STATISTICAL_ALGORITHM_PROJECT_FILE_NAME = "stat_algo.project";

	public static final Logger logger = LoggerFactory
			.getLogger(FilesStorage.class);

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Public link
	 * @throws StatAlgoImporterServiceException
	 */
	public String getPublicLink(String user, String itemId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Attention this is a folder!");
			}

			if (workSpaceItem instanceof FolderItem) {
				return workSpaceItem.getPublicLink(false);
			} else {
				throw new StatAlgoImporterServiceException(
						"Attention no public link for this item!");
			}

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
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
	 * @throws StatAlgoImporterServiceException
	 */
	public WorkspaceItem copyItemOnFolder(String user, String itemId,
			String folderId) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Destination is not a folder!");
			}

			WorkspaceItem item = ws.copy(itemId, folderId);

			return item;
		} catch (WrongDestinationException | ItemAlreadyExistException
				| InsufficientPrivilegesException
				| WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
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
	 * 
	 * @throws StatAlgoImporterServiceException
	 */
	public WorkspaceItem copyItemOnFolderWithNewName(String user,
			String itemId, String folderId, String newName)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Destination is not a folder!");
			}

			WorkspaceItem item = ws.copy(itemId, newName, folderId);

			return item;
		} catch (WrongDestinationException | ItemAlreadyExistException
				| InsufficientPrivilegesException
				| WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
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
	 */
	public void deleteItemOnFolder(String user, String itemId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			ws.removeItems(itemId);

			return;
		} catch (InsufficientPrivilegesException
				| WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param parentId
	 *            Foler id
	 * @throws StatAlgoImporterServiceException
	 */
	public void deleteFolder(String user, String parentId, String folderName)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"No valid project folder!");
			}

			WorkspaceItem target = ws.find(folderName, parentId);

			if (target != null) {
				ws.removeItems(target.getId());
			}

			return;
		} catch (WrongItemTypeException | WorkspaceFolderNotFoundException
				| InternalErrorException | HomeNotFoundException
				| ItemNotFoundException | InsufficientPrivilegesException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param parentId
	 *            Destination folder id
	 * @return Folder
	 * @throws StatAlgoImporterServiceException
	 */
	public WorkspaceFolder createFolder(String user, String parentId,
			String folderName, String folderDescription)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"No valid project folder!");
			}

			WorkspaceFolder projectTargetFolder = ws.createFolder(folderName,
					folderDescription, parentId);

			return projectTargetFolder;
		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException
				| WrongDestinationException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param parentId
	 *            Destination folder id
	 * @return Folder
	 * @throws StatAlgoImporterServiceException
	 */
	public WorkspaceItem find(String user, String parentId, String name)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(parentId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"No valid project folder!");
			}

			return ws.find(name, parentId);
			
		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| WrongItemTypeException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param inputStream
	 *            input stream
	 * @param folderId
	 *            destination folder
	 * @throws StatAlgoImporterServiceException
	 */
	public void saveStatisticalAlgorithmProject(String user,
			InputStream inputStream, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Destination is not a folder!");
			}

			WorkspaceItem projectItem = ws.find(
					STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				ws.createExternalFile(STATISTICAL_ALGORITHM_PROJECT_FILE_NAME,
						STATISTICAL_ALGORITHM_PROJECT_FILE_DESCRIPTION,
						STATISTICAL_ALGORITHM_PROJECT_MIMETYPE, inputStream,
						folderId);
			} else {
				ws.updateItem(projectItem.getId(), inputStream);
			}

			return;
		} catch (WrongItemTypeException | WorkspaceFolderNotFoundException
				| InternalErrorException | HomeNotFoundException
				| ItemNotFoundException | InsufficientPrivilegesException
				| ItemAlreadyExistException | WrongDestinationException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param inputStream
	 *            input stream
	 * @param name
	 *            item name
	 * @param description
	 *            item description
	 * @param mimeType
	 *            item mimetype
	 * @param folderId
	 *            destination folder
	 * @throws StatAlgoImporterServiceException
	 */
	public void saveItemOnWorkspace(String user, InputStream inputStream,
			String name, String description, String mimeType, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Destination is not a folder!");
			}

			WorkspaceItem projectItem = ws.find(name, folderId);

			if (projectItem == null) {
				ws.createExternalFile(name, description, mimeType, inputStream,
						folderId);
			} else {
				ws.updateItem(projectItem.getId(), inputStream);
			}

			return;
		} catch (WrongItemTypeException | WorkspaceFolderNotFoundException
				| InternalErrorException | HomeNotFoundException
				| ItemNotFoundException | InsufficientPrivilegesException
				| ItemAlreadyExistException | WrongDestinationException e) {
			e.printStackTrace();
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
	 */
	public WorkspaceItem createItemOnWorkspace(String user,
			InputStream inputStream, String name, String description,
			String mimeType, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Destination is not a folder!");
			}

			ExternalFile workspaceItem = ws.createExternalFile(name,
					description, mimeType, inputStream, folderId);

			return workspaceItem;
		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException
				| WrongDestinationException e) {
			e.printStackTrace();
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
	 */
	public InputStream retrieveProjectItemOnWorkspace(String user,
			String folderId) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Item is not valid folder!");
			}

			WorkspaceItem projectItem = ws.find(
					STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				throw new StatAlgoImporterServiceException(
						"No project found in this folder!");
			}

			return retrieveImputStream(user, projectItem);

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| WrongItemTypeException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 * @param folderId
	 * @return boolean
	 * @throws StatAlgoImporterServiceException
	 */
	public boolean existProjectItemOnWorkspace(String user, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Item is not valid folder!");
			}

			WorkspaceItem projectItem = ws.find(
					STATISTICAL_ALGORITHM_PROJECT_FILE_NAME, folderId);

			if (projectItem == null) {
				return false;
			} else {
				return true;
			}

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| WrongItemTypeException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 * @param itemId
	 * @return WorkspaceItem
	 * @throws StatAlgoImporterServiceException
	 */
	public WorkspaceItem retrieveItemInfoOnWorkspace(String user, String itemId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			return workSpaceItem;

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
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
	 */
	public InputStream retrieveItemOnWorkspace(String user, String itemId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Folder Item is not valid!");
			}

			return retrieveImputStream(user, workSpaceItem);

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param wi
	 *            WorkspaceItem
	 * @return InputStream
	 * @throws StatAlgoImporterServiceException
	 */
	public InputStream retrieveImputStream(String user, WorkspaceItem wi)
			throws StatAlgoImporterServiceException {
		InputStream is = null;
		try {

			org.gcube.common.homelibrary.home.workspace.folder.items.File gcubeItem = ((org.gcube.common.homelibrary.home.workspace.folder.items.File) wi);
			is = gcubeItem.getData();
			return is;

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage", e);
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(
					"Error retrieving file from storage: "
							+ e.getLocalizedMessage(), e);
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
	 */
	public void saveStringInItem(String user, String itemId, String data)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem == null) {
				throw new StatAlgoImporterServiceException(
						"No item retrieved on workspace!");
			} else {
				if (workSpaceItem.isFolder()) {
					throw new StatAlgoImporterServiceException(
							"Item is a folder!");
				} else {

				}
			}
			// convert String into InputStream
			InputStream is = new ByteArrayInputStream(data.getBytes());

			ws.updateItem(itemId, is);

			return;

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException
				| WrongDestinationException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	public void saveInputStreamInItem(String user, String itemId, InputStream is)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem == null) {
				throw new StatAlgoImporterServiceException(
						"No item retrieved on workspace!");
			} else {
				if (workSpaceItem.isFolder()) {
					throw new StatAlgoImporterServiceException(
							"Item is a  folder!");
				} else {

				}
			}
			// convert String into InputStream
			ws.updateItem(itemId, is);

			return;

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException
				| WrongDestinationException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	public File zipFolder(String user, String folderId)
			throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Item is not valid folder!");
			}

			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;

			File fileZip = ZipUtil.zipFolder(folder);

			return fileZip;

		} catch (IOException | InternalErrorException
				| WorkspaceFolderNotFoundException | HomeNotFoundException
				| ItemNotFoundException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	public File zipFolder(String user, String folderId,
			List<String> idsToExclude) throws StatAlgoImporterServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new StatAlgoImporterServiceException(
						"Item is not valid folder!");
			}

			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;

			File fileZip = ZipUtil.zipFolder(folder, false, idsToExclude);

			return fileZip;

		} catch (IOException | InternalErrorException
				| WorkspaceFolderNotFoundException | HomeNotFoundException
				| ItemNotFoundException e) {
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}

	}

	public void downloadInputFile(String fileUrl, Path destination)
			throws StatAlgoImporterServiceException {
		try {

			URL smpFile = new URL(fileUrl);
			URLConnection uc = (URLConnection) smpFile.openConnection();
			InputStream is = uc.getInputStream();
			inputStreamToFile(is, destination);
			is.close();

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new StatAlgoImporterServiceException(e.getLocalizedMessage());
		}
	}

	private void inputStreamToFile(InputStream is, Path destination)
			throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(destination.toFile());
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}

}
