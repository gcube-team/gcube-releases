package org.gcube.portlets.widgets.githubconnector.server.storage;

import java.io.InputStream;

import org.apache.log4j.Logger;
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
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
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
	public static void deleteItem(String user, String itemId) throws ServiceException {
		Workspace ws;
		try {
			logger.debug("User: " + user + ", ItemId:" + itemId);
			ws = HomeLibrary.getUserWorkspace(user);

			ws.removeItems(itemId);

			return;
		} catch (InsufficientPrivilegesException | WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
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
	public static String createFolderOnWorkspace(String user, String destinationFolderId, String folderName,
			String folderDescription) throws ServiceException {
		try {
			logger.debug("CreateFolderOnWorkspace: [User=" + user + ", FolderId:" + destinationFolderId
					+ ", folderName=" + folderName + ", folderDescription=" + folderDescription + "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceFolder workspaceFolder = ws.createFolder(folderName, folderDescription, destinationFolderId);
			return workspaceFolder.getId();

		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException | WrongDestinationException
				| ItemNotFoundException e) {
			logger.error("CreateFolderOnWorkspace: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param destinationFolderId
	 *            destination folder id
	 * @param fileName
	 *            file name
	 * @param fileDescription
	 *            file description
	 * @param is
	 *            input stream
	 * @return file id
	 * @throws ServiceException
	 *             service exception
	 */
	public static String saveOnWorkspace(String user, String destinationFolderId, String fileName,
			String fileDescription, InputStream is) throws ServiceException {
		try {
			logger.debug("saveOnWorkspace: [User=" + user + ", FolderId:" + destinationFolderId + ", fileName="
					+ fileName + ", fileDescription=" + fileDescription + "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workspaceItem = ws.getItem(destinationFolderId);
			if (workspaceItem.isFolder()) {
				ExternalFile externalfile = ws.createExternalFile(fileName, fileDescription, null, is,
						destinationFolderId);
				return externalfile.getId();
			} else {
				throw new ServiceException("Invalid destination folder!");
			}

		} catch (WorkspaceFolderNotFoundException | InternalErrorException | HomeNotFoundException
				| InsufficientPrivilegesException | ItemAlreadyExistException | WrongDestinationException
				| ItemNotFoundException e) {
			logger.error("SaveOnWorkspace: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

}
