package org.gcube.common.homelibrary.jcr.repository.external;

import java.io.IOException;
import java.io.InputStream;

import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.util.Utils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCUBEStorage {

	private static Logger logger = LoggerFactory.getLogger(GCUBEStorage.class);

	IClient storage;
	String portalLogin;
	String callerScope;

	public GCUBEStorage(String portalLogin){

		logger.trace("Calling GCUBEStorage from user: " + portalLogin);
		this.portalLogin = portalLogin;
		this.callerScope = ScopeProvider.instance.get();

		getStorage();
	}



	/**
	 * Set storage
	 * @return the storage
	 */
	public IClient getStorage() throws RemoteBackendException {

		if (storage==null)
			try {

				String scope = Utils.getRootScope(callerScope);			
				ScopeProvider.instance.set(scope);	

				logger.info("Get GCUBE Storage Client - user: " + portalLogin + " - scope: " + scope);

				this.storage = new StorageClient("org.gcube.portlets.user", JCRRepository.serviceName,
						portalLogin, AccessType.SHARED, callerScope, false).getClient();	

				Handler.activateProtocol();	

			} catch (Exception e) {
				throw new RemoteBackendException("Error initializing GCUBE Storage: " + e.getMessage());
			}finally{
				if (callerScope!=null)
					ScopeProvider.instance.set(callerScope);	
			}

		return storage;
	}


	/**
	 * Save inpustream into storage setting mimetype
	 * @param is inputstream to store
	 * @param remotePath
	 * @param portalLogin
	 * @param mimeType
	 * @return storage id
	 * @throws RemoteBackendException
	 * @throws IOException
	 */
	public String putStream(InputStream is, String remotePath, String mimeType) throws RemoteBackendException, IOException {

		logger.trace("GCUBE Storage - PutStream - remotePath: " + remotePath + " - mimetype: " + mimeType);
		String storageId = null;
		try {
			storageId = getStorage().put(true, mimeType).LFile(is).RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException("GCUBE Storage error in putStream operation: " + e.getMessage());		
		}finally{
			if (is!=null){
				is.close();
				logger.trace("GCUBE Storage inputStream closed");
			}
		}
		return storageId;
	}

/**
 * Save inputstream
 * @param is
 * @param remotePath
 * @return
 * @throws RemoteBackendException
 * @throws IOException
 */
	public String putStream(InputStream is, String remotePath) throws RemoteBackendException, IOException {

		logger.trace("GCUBE Storage - putStream - remotePath: " + remotePath);
		String storageId = null;
		try {
			storageId = getStorage().put(true).LFile(is).RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}finally{
			if (is!=null){
				is.close();
				logger.trace("GCUBE Storage inputStream closed");
			}
		}
		return storageId;
	}

	/**
	 * Save inputstream from an old remotepath and set mimetype
	 * @param old_remotePath
	 * @param new_remotePath
	 * @param mimeType
	 * @return
	 * @throws RemoteBackendException
	 */
	public String putStream(String old_remotePath, String new_remotePath, String mimeType) throws RemoteBackendException {
		logger.trace("GCUBE Storage -putStream - remotePath: " + new_remotePath);
		String storageID = null;
		try {
			storageID = getStorage().put(true, mimeType).LFile(old_remotePath).RFile(new_remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return storageID;
	}

	public long getRemoteFileSize(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage getRemoteFileSize - storage.getSize().RFile(" + remotePath+ ")");
		long size = 0;
		try {	
			size = getStorage().getSize().RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return size;
	}

	/**
	 * delete file	
	 * @param remotePath
	 * @param portalLogin
	 * @throws RemoteBackendException
	 */
	public void removeRemoteFile(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - removeRemoteFile - remotePath: " + remotePath);
		try {	
			getStorage().remove().RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}


	/**
	 * Get size of a file by remotepath
	 * @param remotePath
	 * @param portalLogin
	 * @param serviceName
	 * @return size of a file
	 * @throws RemoteBackendException
	 */
	public long getRemoteFileSize(String remotePath, String serviceName) throws RemoteBackendException {
		logger.trace("GCUBE Storage getRemoteFileSize - storage.getSize().RFile(" + remotePath+ ")");
		try{
			return getStorage().getSize().RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}

	/**
	 * Get the disk usage amount of a user 
	 * @param portalLogin
	 * @return the disk usage amount of a user 
	 * @throws RemoteBackendException
	 */
	public long getDiskUsageByUser() throws RemoteBackendException {
		logger.trace("GCUBE Storage - get volume used by user: " + portalLogin + "");
		long diskUsage = 0;
		try{
			diskUsage = Long.parseLong(getStorage().getTotalUserVolume());
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return diskUsage;
	}

	/**
	 * Get tot items of a user
	 * @param portalLogin
	 * @return tot items of a user
	 * @throws RemoteBackendException
	 */
	public int getTotalItemsByUser() throws RemoteBackendException {
		logger.trace("GCUBE Storage - get tot items of user " + portalLogin + "");
		int totalItem = 0;
		try{
			totalItem = Integer.parseInt(getStorage().getUserTotalItems());
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return totalItem;
	}
	/**
	 * Get folder size
	 * @param remotePath
	 * @param portalLogin
	 * @return folder size
	 * @throws RemoteBackendException
	 */
	public long getFolderTotalVolume(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - get volume (" + remotePath + ")");
		long volume = 0;
		try{
			volume = Long.parseLong(getStorage().getFolderTotalVolume().RDir(remotePath).toString());
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return volume;
	}

	/**
	 * Get last update of a folder
	 * @param remotePath of the folder
	 * @param portalLogin
	 * @return folder size
	 * @throws RemoteBackendException
	 */
	public String getFolderLastUpdate(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - get volume (" + remotePath + ")");;
		String lastUpdate;
		try{
			lastUpdate = getStorage().getFolderLastUpdate().RDir(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return lastUpdate;
	}

	/**
	 * Get total items in a folder
	 * @param remotePath of the folder
	 * @param portalLogin
	 * @return total items in a folder
	 * @throws RemoteBackendException
	 */
	public int getFolderTotalItems(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - get volume (" + remotePath + ")");
		int totalItems;
		try{
			totalItems = Integer.parseInt(getStorage().getFolderTotalItems().RDir(remotePath).toString());
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return totalItems;
	}

	/**
	 * Get the inpustream by a remote path
	 * @param remotePath
	 * @param portalLogin
	 * @return the inpustream by a remote path
	 */
	public InputStream getRemoteFile(String remotePath) throws RemoteBackendException{
		logger.trace("GCUBE Storage -getRemoteFile - stream = storage.get().RFileAsInputStream(" + remotePath+ ")");
		InputStream stream = null;
		try{
			stream = getStorage().get().RFileAsInputStream(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return stream;
	}


	/**
	 * Get public link by remote path
	 * @param remotePath
	 * @param portalLogin
	 * @return public link 
	 */
	public String getPublicLink(String remotePath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - Generate Public Link for " + remotePath);
		String publicLink;
		try{
			publicLink =  getStorage().getHttpUrl().RFile(remotePath);
			//			System.out.println(publicLink);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return publicLink;
	}



	/**
	 * Get storage ID by remote path
	 * @param remotePath
	 * @param portalLogin
	 * @return storage ID
	 */
	public String getStorageId(String remotePath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - Get Storage  Id for " + remotePath);
		String id;
		try{
			MyFile file = getStorage().getMetaFile().RFile(remotePath);
			id = file.getId();
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return id;
	}

	/**
	 * move dir
	 * @param oldPath
	 * @param newPath
	 * @param portalLogin
	 */
	public void moveRemoteFolder(String oldPath, String newPath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - storage.moveDir().from(" + oldPath + ").to(" + newPath + ")");
		try{
			getStorage().moveDir().from(oldPath).to(newPath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}

	}

	/**
	 * move file
	 * @param oldPath
	 * @param newPath
	 * @param portalLogin
	 */
	public void moveRemoteFile(String oldPath, String newPath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - storage.moveFile().from(" + oldPath + ").to(" + newPath + ")");
		try {
			getStorage().moveFile().from(oldPath).to(newPath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}

	/**
	 * copy file
	 * @param oldPath
	 * @param newPath
	 * @param portalLogin
	 */
	public void copyRemoteFile(String oldPath, String newPath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - storage.copyFile().from(" + oldPath + ").to(" + newPath + ")");
		try {
			getStorage().copyFile().from(oldPath).to(newPath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}

	/**
	 * use remotepath to access storage
	 * @param srcPath
	 * @param destPath
	 * @param portalLogin
	 */
	public void createHardLink(String srcPath, String destPath) throws RemoteBackendException{
		logger.trace("GCUBE Storage - storage.linkFile().from(" + srcPath + ").to(" + destPath + ")");
		try {
			getStorage().linkFile().from(srcPath).to(destPath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}


	/**
	 * Delete folder
	 * @param remotePath
	 * @param portalLogin
	 * @throws RemoteBackendException
	 */
	public void removeRemoteFolder(String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - storage.removeDir().RDir(" + remotePath + ")");
		try {
			getStorage().removeDir().RDir(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}


	/**
	 * Save inpustream in storage using a remotepath
	 * @param is
	 * @param new_remotePath
	 * @param portalLogin
	 * @param serviceName
	 * @return remote path
	 * @throws RemoteBackendException
	 */
	public String putStream(InputStream is,  String new_remotePath, String serviceName, String mimeType) throws RemoteBackendException {

		logger.trace("GCUBE Storage - putStream - remotePath: " + new_remotePath);
		String storageId = null;
		try {
			if (is!=null)
				is.close();
			getStorage().getUrl().RFile(new_remotePath);	
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}

		return storageId;
	}

	/**
	 * Get metadata info by remotepath
	 * @param field
	 * @param remotePath
	 * @return metadata info
	 * @throws RemoteBackendException
	 */
	public String getMetaInfo(String field, String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - getMetaInfo: field: " + field + "; remotePath: " + remotePath);
		String metadata = null;
		try {
			metadata = getStorage().getMetaInfo(field).RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
		return metadata;
	}

	/**
	 * Set metadata info
	 * @param field is the property name
	 * @param value the property value to set
	 * @param remotePath
	 * @throws RemoteBackendException
	 */
	public void setMetaInfo(String field, String value, String remotePath) throws RemoteBackendException {
		logger.trace("GCUBE Storage - getMetaInfo: field: " + field + "; remotePath: " + remotePath);
		try {
			getStorage().setMetaInfo(field, value).RFile(remotePath);
		} catch (Exception e) {
			throw new RemoteBackendException(e.getMessage());
		}
	}



	public String getScope() {
		return callerScope;

	}

}
