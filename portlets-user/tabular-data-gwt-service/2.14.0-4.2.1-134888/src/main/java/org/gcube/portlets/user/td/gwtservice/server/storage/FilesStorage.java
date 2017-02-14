package org.gcube.portlets.user.td.gwtservice.server.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */

public class FilesStorage {

	public static final Logger logger = LoggerFactory
			.getLogger(FilesStorage.class);

	/**
	 * 
	 * @param user
	 * @param file
	 * @return
	 */
	public String storageCSVTempFile(String user, File file) {
		try {
			IClient client = new StorageClient(user, AccessType.PUBLIC,
					MemoryType.VOLATILE).getClient();
			String remotePath = "/CSVimport/" + file.getName();
			logger.debug("remotePath: " + remotePath);
			String id = client.put(true).LFile(file.getAbsolutePath())
					.RFile(remotePath);
			return id;

		} catch (Throwable e) {
			logger.error("Error no csv file loaded on storage"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 
	 * @param user
	 * @param file
	 * @return
	 */
	public String storageCodelistMappingTempFile(String user, File file) {
		try {
			IClient client = new StorageClient(user, AccessType.PUBLIC,
					MemoryType.VOLATILE).getClient();
			String remotePath = "/CodelistMappingImport/" + file.getName();
			logger.debug("remotePath: " + remotePath);
			String id = client.put(true).LFile(file.getAbsolutePath())
					.RFile(remotePath);
			return id;
		} catch (Throwable e) {
			logger.error("Error no codelist mapping file loaded on storage"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 
	 * @param user
	 * @param url
	 * @return
	 * @throws TDGWTServiceException
	 */
	public String storageCodelistMappingTempFile(String user, String url)
			throws TDGWTServiceException {
		InputStream is = null;
		try {
			URL address = new URL(url);
			is = address.openStream();

			IClient client = new StorageClient(user, AccessType.PUBLIC,
					MemoryType.VOLATILE).getClient();
			String remotePath = "/CodelistMappingImport/" + address.getFile();
			logger.debug("remotePath: " + remotePath);
			String id = client.put(true).LFile(is).RFile(remotePath);
			is.close();

			return id;
		} catch (IOException e) {
			logger.error("Error no codelist mapping file loaded on storage"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error no codelist mapping file loaded on storage"
							+ e.getLocalizedMessage());
		} catch (Throwable e) {
			logger.error("Error no codelist mapping file loaded on storage"
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * user
	 * 
	 * @param user
	 *            User
	 * @param remotePath
	 *            File path on storage
	 * @param file
	 *            Destination file
	 * @throws TDGWTServiceException
	 */
	public void retrieveFile(String user, WorkspaceItem wi, File file)
			throws TDGWTServiceException {
		InputStream is = null;
		try {

			org.gcube.common.homelibrary.home.workspace.folder.items.File gcubeItem = ((org.gcube.common.homelibrary.home.workspace.folder.items.File) wi);
			// SMPUrl smsHome = new SMPUrl(gcubeItem.getPublicLink());
			URL url = new URL(gcubeItem.getPublicLink());

			logger.debug("smsHome: [host:" + url.getHost() + " path:"
					+ url.getPath() + " ref:" + url.getRef() + " userinfo:"
					+ url.getUserInfo() + " ]");
			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			is = uc.getInputStream();

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving file from storage: "
							+ e.getLocalizedMessage(), e);
		}

		try {
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}
			bos.close();
		} catch (Throwable e) {
			logger.error(
					"Error trasferring file from storage: "
							+ e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error trasferring file from storage: "
							+ e.getLocalizedMessage(), e);

		}
	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param remotePath
	 *            File path on storage
	 * @return InputStream back to read the file
	 * @throws TDGWTServiceException
	 */
	public InputStream retrieveInputStream(String user, WorkspaceItem wi)
			throws TDGWTServiceException {
		InputStream is = null;
		try {

			org.gcube.common.homelibrary.home.workspace.folder.items.File gcubeItem = ((org.gcube.common.homelibrary.home.workspace.folder.items.File) wi);
			// SMPUrl smsHome = new SMPUrl(gcubeItem.getPublicLink());
			URL url = new URL(gcubeItem.getPublicLink());

			logger.debug("smsHome: [host:" + url.getHost() + " path:"
					+ url.getPath() + " ref:" + url.getRef() + " userinfo:"
					+ url.getUserInfo() + " ]");
			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			is = uc.getInputStream();

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving file from storage: "
							+ e.getLocalizedMessage(), e);
		}

		return is;

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param remotePath
	 *            File path on storage
	 * @return InputStream back to read the file
	 * @throws TDGWTServiceException
	 */
	public InputStream retrieveInputStream(String uri)
			throws TDGWTServiceException {
		InputStream is = null;
		try {

			// SMPUrl smsHome = new SMPUrl(uri);
			URL url = new URL(uri);

			logger.debug("smsHome: [host:" + url.getHost() + " path:"
					+ url.getPath() + " ref:" + url.getRef() + " userinfo:"
					+ url.getUserInfo() + " ]");
			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			is = uc.getInputStream();

		} catch (Throwable e) {
			logger.error("Error retrieving file from storage", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving file from storage: "
							+ e.getLocalizedMessage(), e);
		}

		return is;

	}

	/**
	 * 
	 * @param user
	 *            User
	 * @param remotePath
	 *            File path on storage
	 * @return InputStream back to read the file
	 * @throws TDGWTServiceException
	 */
	public void createItemOnWorkspace(String uri, String user,
			String item_name, String item_description, String item_mimetype,
			String item_folder) throws TDGWTServiceException {
		InputStream is = null;
		try {

			// SMPUrl smsHome = new SMPUrl(uri);
			URL url = new URL(uri);

			logger.debug("smsHome: [host:" + url.getHost() + " path:"
					+ url.getPath() + " ref:" + url.getRef() + " userinfo:"
					+ url.getUserInfo() + " ]");

			URLConnection uc = null;
			uc = (URLConnection) url.openConnection();
			is = uc.getInputStream();

			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(item_folder);
			String uniqueName = WorkspaceUtil.getUniqueName(item_name, folder);

			logger.debug("ws.createExternalFile [folder: " + folder
					+ ", uniqueName: " + uniqueName + ", description: "
					+ item_description + ", mimetype: " + item_mimetype
					+ ",  InputStream: " + is + "]");
			WorkspaceUtil.createExternalFile(folder, uniqueName,
					item_description, item_mimetype, is);

			is.close();

		} catch (Throwable e) {
			logger.error("Error creating item on workspace", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error creating item on workspace: "
							+ e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * @param storageId
	 * @param user
	 * @param item_name
	 * @param item_description
	 * @param item_mimetype
	 * @param item_folder
	 * @throws TDGWTServiceException
	 */
	public void createItemOnWorkspaceByStorageId(String storageId, String user,
			String item_name, String item_description, String item_mimetype,
			String item_folder) throws TDGWTServiceException {

		try {

			Workspace ws = HomeLibrary.getUserWorkspace(user);

			WorkspaceFolder folder = (WorkspaceFolder) ws.getItem(item_folder);
			String uniqueName = WorkspaceUtil.getUniqueName(item_name, folder);

			logger.debug("ws.createExternalFile [folder: " + folder
					+ ", uniqueName: " + uniqueName + ", description: "
					+ item_description + ", mimetype: " + item_mimetype
					+ ",  StorageId: " + storageId + "]");
			WorkspaceUtil.createExternalFile(folder, uniqueName,
					item_description, item_mimetype, storageId);

		} catch (Throwable e) {
			logger.error("Error creating item on workspace", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error creating item on workspace: "
							+ e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 
	 * @param user
	 * @param scope
	 * @throws TDGWTServiceException
	 */
	public String getVREFolderIdByScope(String user, String scope)
			throws TDGWTServiceException {

		try {

			Workspace ws = HomeLibrary.getUserWorkspace(user);
			WorkspaceSharedFolder folder = ws.getVREFolderByScope(scope);
			if(folder!=null){
				return folder.getId();
			} else {
				return null;
			}
			
		} catch (Throwable e) {
			logger.error("Error retrieving VRE folder by scope!", e);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error retrieving VRE folder by scope: "
							+ e.getLocalizedMessage(), e);
		}

	}

}
