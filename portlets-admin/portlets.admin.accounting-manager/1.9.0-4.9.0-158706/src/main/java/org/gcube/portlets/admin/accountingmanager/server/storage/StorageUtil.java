package org.gcube.portlets.admin.accountingmanager.server.storage;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class StorageUtil {

	private static final String ACCOUNTING_MANAGER = "AccountingManager";
	private static Logger logger = Logger.getLogger(StorageUtil.class);

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
	public static String getPublicLink(String user, String itemId) throws ServiceException {
		Workspace ws;
		try {
			logger.info("Get public link: [user=" + user + ", itemId=" + itemId + "]");
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new ServiceException("Attention this is a folder!");
			}

			if (workSpaceItem instanceof FolderItem) {
				return workSpaceItem.getPublicLink(false);
			} else {
				throw new ServiceException("Attention no public link for this item!");
			}

		} catch (Throwable e) {
			logger.error("getPublicLink: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @return item id
	 * @throws ServiceException
	 *             service exception
	 */
	public static String createAccountingFolderOnWorkspace(String user) throws ServiceException {
		try {
			logger.debug("CreateAccountingFolderOnWorkspace: [User=" + user + "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceItem workspaceItem = ws.find(ACCOUNTING_MANAGER);
			if (workspaceItem == null) {
				workspaceItem = ws.createFolder(ACCOUNTING_MANAGER, ACCOUNTING_MANAGER, ws.getRoot().getId());
			}
			return workspaceItem.getId();

		} catch (Throwable e) {
			logger.error("CreateAccountingFolderOnWorkspace: " + e.getLocalizedMessage(), e);
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
	 * @return item description
	 * @throws ServiceException
	 *             service exception
	 */
	public static ItemDescription saveOnWorkspace(String user, String destinationFolderId, String fileName,
			String fileDescription, InputStream is) throws ServiceException {
		try {
			logger.debug("saveOnWorkspace: [User=" + user + ", FolderId:" + destinationFolderId + ", fileName="
					+ fileName + ", fileDescription=" + fileDescription + "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workspaceItem = ws.getItem(destinationFolderId);
			if (workspaceItem.isFolder()) {
				ExternalFile externalfile = ws.createExternalFile(fileName, fileDescription, null, is,
						destinationFolderId);
				ItemDescription itemDescription = new ItemDescription(externalfile.getId(), externalfile.getName(),
						externalfile.getOwner().getId(), externalfile.getPath(), externalfile.getType().name());

				return itemDescription;
			} else {
				throw new ServiceException("Invalid destination folder!");
			}

		} catch (Throwable e) {
			logger.error("SaveOnWorkspace: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}
}
