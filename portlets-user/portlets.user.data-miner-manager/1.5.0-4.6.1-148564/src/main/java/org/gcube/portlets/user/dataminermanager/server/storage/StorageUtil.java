package org.gcube.portlets.user.dataminermanager.server.storage;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Properties;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.util.zip.ZipUtil;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
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
	public static Map<String, String> getProperties(String user, String itemId) throws ServiceException {
		try {
			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			Properties properties = workSpaceItem.getProperties();

			return properties.getProperties();

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
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
	public static InputStream getInputStreamForItemOnWorkspace(String user, String itemId) throws ServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new ServiceException("Folder is not valid!");
			}

			return getInputStream(user, workSpaceItem);

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param workspaceItem
	 *            workspace item
	 * @return input stream
	 * @throws ServiceException
	 *             service exception
	 */
	private static InputStream getInputStream(String user, WorkspaceItem workspaceItem) throws ServiceException {
		InputStream is = null;
		try {

			org.gcube.common.homelibrary.home.workspace.folder.items.File gcubeItem = ((org.gcube.common.homelibrary.home.workspace.folder.items.File) workspaceItem);
			is = gcubeItem.getData();
			return is;

		} catch (Throwable e) {
			logger.error("Error retrieving InputStream from storage", e);
			e.printStackTrace();
			throw new ServiceException("Error retrieving file from storage: " + e.getLocalizedMessage(), e);
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
	public static String getPublicLink(String user, String itemId) throws ServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(itemId);
			if (workSpaceItem.isFolder()) {
				throw new ServiceException("Attention this is a folder!");
			}

			return workSpaceItem.getPublicLink(false);

		} catch (Throwable e) {
			logger.error("Error retrieving public link: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());

		}
	}

	/**
	 * 
	 * @param user
	 *            user
	 * @param folderId
	 *            folder id
	 * @return zip folder
	 * @throws ServiceException
	 *             service exception
	 */
	public static File zipFolder(String user, String folderId) throws ServiceException {
		Workspace ws;
		try {
			logger.debug("zipFolder: [user=" + user + ", folderId=" + folderId + "]");
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem workSpaceItem = ws.getItem(folderId);
			if (!workSpaceItem.isFolder()) {
				throw new ServiceException("Item is not valid folder!");
			}

			WorkspaceFolder folder = (WorkspaceFolder) workSpaceItem;

			File fileZip = ZipUtil.zipFolder(folder);

			return fileZip;

		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
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
	public static WorkspaceItem getItemInRootFolderOnWorkspace(String user, String itemName) throws ServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem item = ws.find(itemName, ws.getRoot().getId());

			return item;

		} catch (Throwable e) {
			e.printStackTrace();
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
	public static WorkspaceItem getItemInFolderOnWorkspace(String user, String folderId, String itemName)
			throws ServiceException {
		Workspace ws;
		try {
			ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceItem item = ws.find(itemName, folderId);

			return item;

		} catch (Throwable e) {
			e.printStackTrace();
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
	public static void deleteItem(String user, String itemId) throws ServiceException {
		Workspace ws;
		try {
			logger.debug("User: " + user + ", ItemId:" + itemId);
			ws = HomeLibrary.getUserWorkspace(user);

			ws.removeItems(itemId);

			return;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());

		}

	}

	/**
	 * 
	 * @param is
	 *            input stream
	 * @return public link
	 * @throws ServiceException
	 *             service exception
	 */
	public static String saveOnStorageInTemporalFile(InputStream is) throws ServiceException {
		try {
			logger.debug("SaveOnStorageInTemporalFile()");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			Double v = Math.random() * 10000;
			String tempFile = "P_" + sdf.format(new Date()) + "_" + v.intValue() + ".xml";
			String remotePath = "/DataMiner/AlgoritmsParameters/" + tempFile;
			IClient client = new StorageClient("DataAnalysis", "DataMiner", "DataMiner", AccessType.PUBLIC,
					MemoryType.VOLATILE).getClient();
			String storageId = client.put(true).LFile(is).RFile(remotePath);
			logger.debug("Storage id: " + storageId);
			String publicLink = client.getHttpUrl().RFile(remotePath);
			logger.debug("Storage public link: " + publicLink);
			return publicLink;

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
