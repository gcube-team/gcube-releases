package org.gcube.portlets.user.td.gwtservice.server.storage;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */

public class FilesStorage {

	private static final Logger logger = LoggerFactory.getLogger(FilesStorage.class);

	/**
	 * 
	 * @param user
	 *            User
	 * @param file
	 *            File
	 * @return File id
	 */
	public String storageCSVTempFile(String user, File file) {
		try {
			IClient client = new StorageClient(user, AccessType.PUBLIC, MemoryType.VOLATILE).getClient();
			String remotePath = "/CSVimport/" + file.getName();
			logger.debug("remotePath: " + remotePath);
			String id = client.put(true).LFile(file.getAbsolutePath()).RFile(remotePath);
			//client.close();

			return id;

		} catch (Throwable e) {
			logger.error("Error no csv file loaded on storage" + e.getLocalizedMessage());
			throw e;
		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param file
	 *            File
	 * @return File id
	 */
	public String storageCodelistMappingTempFile(String user, File file) {
		try {
			IClient client = new StorageClient(user, AccessType.PUBLIC, MemoryType.VOLATILE).getClient();
			String remotePath = "/CodelistMappingImport/" + file.getName();
			logger.debug("remotePath: " + remotePath);
			String id = client.put(true).LFile(file.getAbsolutePath()).RFile(remotePath);
			//client.close();
			return id;
		} catch (Throwable e) {
			logger.error("Error no codelist mapping file loaded on storage" + e.getLocalizedMessage());
			throw e;
		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param url
	 *            Url
	 * @return File id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String storageCodelistMappingTempFile(String user, String url) throws TDGWTServiceException {
		try {
			String id = null;

			URL address = new URL(url);
			try (InputStream is = address.openStream()) {

				IClient client = new StorageClient(user, AccessType.PUBLIC, MemoryType.VOLATILE).getClient();
				String remotePath = "/CodelistMappingImport/" + address.getFile();
				logger.debug("remotePath: " + remotePath);
				id = client.put(true).LFile(is).RFile(remotePath);
				//client.close();
			}

			return id;
		} catch (Throwable e) {
			logger.error("Error no codelist mapping file loaded on storage" + e.getLocalizedMessage());
			throw new TDGWTServiceException(
					"Error no codelist mapping file loaded on storage" + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @param user
	 *            User
	 * 
	 * @param itemId
	 *            Workspace item id
	 * @return Input stream
	 * @throws TDGWTServiceException
	 *             Exception
	 */

	public InputStream retrieveInputStream(String user, String itemId) throws TDGWTServiceException {
		InputStream is = null;
		try {
			logger.debug("retrieveFile: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			FileContainer fileContainer = shc.open(itemId).asFile();
			StreamDescriptor streamDescriptor = fileContainer.download();
			is = streamDescriptor.getStream();

		} catch (Throwable e) {
			logger.error("Error retrieving file from StorageHub", e);
			throw new TDGWTServiceException("Error retrieving file from StorageHub: " + e.getLocalizedMessage(), e);
		}

		return is;

	}

	/**
	 * 
	 * @param uri
	 *            Uri
	 * @return Input stream
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public InputStream retrieveInputStream(String uri) throws TDGWTServiceException {
		InputStream is = null;
		try {

			// SMPUrl smsHome = new SMPUrl(uri);
			URL url = new URL(uri);

			logger.debug("smsHome: [host:" + url.getHost() + " path:" + url.getPath() + " ref:" + url.getRef()
					+ " userinfo:" + url.getUserInfo() + " ]");
			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			is = uc.getInputStream();

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage", e);
			throw new TDGWTServiceException("Error retrieving file from storage: " + e.getLocalizedMessage(), e);
		}

		return is;

	}

	/**
	 * 
	 * @param uri
	 *            Uri
	 * @param user
	 *            User
	 * @param name
	 *            Item name
	 * @param description
	 *            Item description
	 * @param mimetype
	 *            Item mime type
	 * @param folderId
	 *            Destination folder
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void createItemOnWorkspace(String uri, String user, String name, String description, String mimetype,
			String folderId) throws TDGWTServiceException {

		try {

			URL url = new URL(uri);

			logger.debug("smsHome: [host:" + url.getHost() + " path:" + url.getPath() + " ref:" + url.getRef()
					+ " userinfo:" + url.getUserInfo() + " ]");

			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			try (InputStream is = uc.getInputStream()) {

				StorageHubClient shc = new StorageHubClient();
				FolderContainer folderContainer = shc.open(folderId).asFolder();
				FileContainer fileContainer = folderContainer.uploadFile(is, name, description);
				AbstractFileItem afi = fileContainer.get();
				logger.debug("Created file on workspace: " + afi.getId());
			}

		} catch (Throwable e) {
			logger.error("Error creating item on workspace", e);
			throw new TDGWTServiceException("Error creating item on workspace: " + e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * 
	 * @param storageId
	 *            Storage id
	 * @param user
	 *            User
	 * @param name
	 *            Item name
	 * @param description
	 *            Item description
	 * @param mimetype
	 *            Item mime type
	 * @param folderId
	 *            Destination Folder
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void createItemOnWorkspaceByStorageId(String storageId, String user, String name, String description,
			String mimetype, String folderId) throws TDGWTServiceException {

		try {
			logger.debug("CreateItemOnWorkspaceByStorageId: [storageId=" + storageId + ", user=" + user + ", name="
					+ name + ", description=" + description + ", mimeType=" + mimetype + ",  folderId=" + folderId
					+ "]");
			IClient client = new StorageClient(user, AccessType.PUBLIC, MemoryType.PERSISTENT).getClient();

			try (InputStream is = client.get().RFileAsInputStream(storageId)) {
				if(is==null){
					logger.error("Error in storage input stream is null for storageId :"+storageId);
					throw new TDGWTServiceException("Error in storage input stream is null for storageId :"+storageId);
				}
				StorageHubClient shc = new StorageHubClient();
				FolderContainer folderContainer = shc.open(folderId).asFolder();
				FileContainer fileContainer = folderContainer.uploadFile(is, name, description);
				AbstractFileItem afi = fileContainer.get();
				logger.debug("Created file on workspace: " + afi.getId());
			}
			//client.close();

		} catch (Throwable e) {
			logger.error("Error creating item on workspace: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error creating item on workspace: " + e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * 
	 * @param user
	 *            User
	 * @param scope
	 *            Scope
	 * @return Folder id
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public String getVREFolderIdByScope(String user, String scope) throws TDGWTServiceException {

		try {
			logger.debug("GetVREFolderIdByScope: [user=" + user + ", scope=" + scope + "]");
			StorageHubClient shc = new StorageHubClient();
			FolderContainer folderContainer = shc.openVREFolder();
			FolderItem vreFolder = folderContainer.get();

			if (vreFolder != null) {
				logger.debug("VRE folder id: " + vreFolder.getId());
				return vreFolder.getId();
			} else {
				logger.debug("VRE folder id: " + vreFolder);
				return null;
			}

		} catch (Throwable e) {
			logger.error("Error retrieving VRE folder by scope! " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving VRE folder by scope: " + e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param itemId
	 *            Item Id
	 * @return Item Description
	 * @throws TDGWTServiceException
	 *             Error
	 */
	public ItemDescription getItem(String user, String itemId) throws TDGWTServiceException {
		try {
			logger.debug("GetItem: [user=" + user + ", itemId=" + itemId + "]");
			StorageHubClient shc = new StorageHubClient();
			FileContainer fileContainer = shc.open(itemId).asFile();
			AbstractFileItem item = fileContainer.get();
			ItemDescription itemDescription = new ItemDescription(item.getId(), item.getName(), item.getOwner(),
					item.getPath(), item.getClass().getSimpleName());
			logger.debug("Item retrieved: " + itemDescription);

			return itemDescription;

		} catch (Throwable e) {
			logger.error("Error retrieving Item from StorageHub: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving Item from StorageHub: " + e.getLocalizedMessage(), e);
		}
	}

}
