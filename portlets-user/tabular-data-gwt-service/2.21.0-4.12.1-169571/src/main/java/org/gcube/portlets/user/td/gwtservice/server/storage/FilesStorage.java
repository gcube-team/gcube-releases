package org.gcube.portlets.user.td.gwtservice.server.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
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
			}

			return id;
		} catch (Throwable e) {
			logger.error("Error no codelist mapping file loaded on storage" + e.getLocalizedMessage());
			throw new TDGWTServiceException(
					"Error no codelist mapping file loaded on storage" + e.getLocalizedMessage());
		}
	}

	/**
	 * user
	 * 
	 * @param user
	 *            User
	 * @param wi
	 *            Workspace item
	 * @param file
	 *            File
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void retrieveFile(String user, WorkspaceItem wi, File file) throws TDGWTServiceException {
		try {
			logger.debug("retrieveFile: [user=" + user + ", item=" + wi + "]");
			StorageHubClient shc = new StorageHubClient();
			FileContainer fileContainer = shc.open(wi.getId()).asFile();
			StreamDescriptor streamDescriptor = fileContainer.download();
			try (BufferedInputStream bis = new BufferedInputStream(streamDescriptor.getStream())) {
				try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
					
					byte[] buffer = new byte[1024];
					int readCount;
					while ((readCount = bis.read(buffer)) > 0) {
						bos.write(buffer, 0, readCount);
					}
				} catch (Throwable e) {
					logger.error("Error trasferring file from storage: " + e.getLocalizedMessage(), e);
					throw new TDGWTServiceException("Error trasferring file from storage: " + e.getLocalizedMessage(),
							e);
				}
			}

		} catch (Throwable e) {
			logger.error("Error retrieving file from StorageHub", e);
			throw new TDGWTServiceException("Error retrieving file from StorageHub: " + e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * @param user
	 *            User
	 * 
	 * @param wi
	 *            Workspace item
	 * @return Input stream
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public InputStream retrieveInputStream(String user, WorkspaceItem wi) throws TDGWTServiceException {
		InputStream is = null;
		try {
			logger.debug("retrieveFile: [user=" + user + ", item=" + wi + "]");
			StorageHubClient shc = new StorageHubClient();
			FileContainer fileContainer = shc.open(wi.getId()).asFile();
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
	 *            Usi
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
	 * @param itemName
	 *            Item name
	 * @param itemDescription
	 *            Item description
	 * @param itemMimetype
	 *            Item mime type
	 * @param folderId
	 *            Destination folder
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void createItemOnWorkspace(String uri, String user, String itemName, String itemDescription,
			String itemMimetype, String folderId) throws TDGWTServiceException {

		try {

			URL url = new URL(uri);

			logger.debug("smsHome: [host:" + url.getHost() + " path:" + url.getPath() + " ref:" + url.getRef()
					+ " userinfo:" + url.getUserInfo() + " ]");

			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			try (InputStream is = uc.getInputStream()) {

				StorageHubClient shc = new StorageHubClient();
				FolderContainer folderContainer = shc.open(folderId).asFolder();
				FileContainer fileContainer = folderContainer.uploadFile(is, itemName, itemDescription);
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
	 * @param storageId
	 *            Storage id
	 * @param user
	 *            User
	 * @param itemName
	 *            Item name
	 * @param itemDescription
	 *            Item description
	 * @param itemMimetype
	 *            Item mime type
	 * @param itemFolder
	 *            Destination Folder
	 * @throws TDGWTServiceException
	 *             Exception
	 */
	public void createItemOnWorkspaceByStorageId(String storageId, String user, String itemName,
			String itemDescription, String itemMimetype, String itemFolder) throws TDGWTServiceException {

		try {

			Workspace ws = HomeLibrary.getUserWorkspace(user);
 
			WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(itemFolder);
			String uniqueName = WorkspaceUtil.getUniqueName(itemName, folder);

			logger.debug("ws.createExternalFile [folder: " + folder + ", uniqueName: " + uniqueName + ", description: "
					+ itemDescription + ", mimetype: " + itemMimetype + ",  StorageId: " + storageId + "]");
			WorkspaceUtil.createExternalFile(folder, uniqueName, itemDescription, itemMimetype, storageId);

		} catch (Throwable e) {
			logger.error("Error creating item on workspace: " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error creating item on workspace: " + e.getLocalizedMessage(), e);
		}

	}

	/**
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

			Workspace ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceSharedFolder folder = ws.getVREFolderByScope(scope);
			if (folder != null) {
				return folder.getId();
			} else {
				return null;
			}

		} catch (Throwable e) {
			logger.error("Error retrieving VRE folder by scope! " + e.getLocalizedMessage(), e);
			throw new TDGWTServiceException("Error retrieving VRE folder by scope: " + e.getLocalizedMessage(), e);
		}

	}

}
