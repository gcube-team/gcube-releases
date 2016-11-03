package org.gcube.portlets.admin.accountingmanager.server.storage;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class StorageUtil {

	private static final String ACCOUNTING_MANAGER = "AccountingManager";
	private static Logger logger = Logger.getLogger(StorageUtil.class);

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item id
	 * @return Public link
	 * @throws StatAlgoImporterServiceException
	 */
	public static String getPublicLink(String user, String itemId)
			throws ServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new ServiceException("Attention this is a folder!");
			}

			if (workSpaceItem instanceof FolderItem) {
				return workSpaceItem.getPublicLink(false);
			} else {
				throw new ServiceException(
						"Attention no public link for this item!");
			}

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | ItemNotFoundException e) {
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 * @param destinationFolderId
	 * @param folderName
	 * @param folderDescription
	 * @return
	 * @throws ServiceException
	 */
	public static String createAccountingFolderOnWorkspace(String user)
			throws ServiceException {
		try {
			logger.debug("CreateAccountingFolderOnWorkspace: [User=" + user
					+ "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceItem workspaceItem = ws.find(ACCOUNTING_MANAGER);
			if (workspaceItem == null) {
				workspaceItem = ws.createFolder(ACCOUNTING_MANAGER,
						ACCOUNTING_MANAGER, ws.getRoot().getId());
			}
			return workspaceItem.getId();

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | InsufficientPrivilegesException
				| ItemAlreadyExistException | WrongDestinationException
				| ItemNotFoundException e) {
			logger.error("CreateAccountingFolderOnWorkspace: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 
	 * @param user
	 * @param destinationFolderId
	 * @param fileName
	 * @param fileDescription
	 * @param mimeType
	 * @param is
	 * @return
	 * @throws ServiceException
	 */
	public static ItemDescription saveOnWorkspace(String user,
			String destinationFolderId, String fileName,
			String fileDescription, InputStream is) throws ServiceException {
		try {
			logger.debug("saveOnWorkspace: [User=" + user + ", FolderId:"
					+ destinationFolderId + ", fileName=" + fileName
					+ ", fileDescription=" + fileDescription + "]");
			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workspaceItem = ws.getItem(destinationFolderId);
			if (workspaceItem.isFolder()) {
				ExternalFile externalfile = ws.createExternalFile(fileName,
						fileDescription, null, is, destinationFolderId);
				ItemDescription itemDescription=new ItemDescription(externalfile.getId(), externalfile.getName(), 
						externalfile.getOwner().getId(), externalfile.getPath(), 
						externalfile.getType().name());
				
				return itemDescription;
			} else {
				throw new ServiceException("Invalid destination folder!");
			}

		} catch (WorkspaceFolderNotFoundException | InternalErrorException
				| HomeNotFoundException | InsufficientPrivilegesException
				| ItemAlreadyExistException | WrongDestinationException
				| ItemNotFoundException e) {
			logger.error("SaveOnWorkspace: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}
}
