package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.File;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRFile implements File {

	private static Logger logger = LoggerFactory.getLogger(JCRFile.class);

	public static final String MIME_TYPE 				= 	"jcr:mimeType";
	public static final String DATA 	  				= 	"jcr:data";
	public static final String SIZE 	 				= 	"hl:size";
	public static final String REMOTE_STORAGE_PATH 		=  	"hl:remotePath";
	public static final String STORAGE_PATH 			= 	"hl:storagePath";

	protected ItemDelegate itemDelegate;
	private JCRWorkspace workspace;


	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate){
		this.workspace = workspace;
		this.itemDelegate = itemDelegate;
	}

	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, MetaInfo info) throws InternalErrorException {
		this(workspace, itemDelegate);
		setMeta(info);
	}

	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, InputStream is) throws InternalErrorException, IOException, RemoteBackendException {
		this(workspace, itemDelegate);

		String remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);	
		if (remotePath==null || remotePath.isEmpty()) 
			remotePath = itemDelegate.getPath();

		MetaInfo info = WorkspaceItemUtil.getMetadataInfo(is, getStorage(), remotePath, itemDelegate.getTitle());	

		setMeta(info);
	}

	private void setMeta(MetaInfo info) {

		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.PORTAL_LOGIN, workspace.getOwner().getPortalLogin());
		content.put(NodeProperty.MIME_TYPE, info.getMimeType());
		Long l = Long.valueOf(String.valueOf(info.getSize()));
		content.put(NodeProperty.SIZE, new XStream().toXML(l));
		content.put(NodeProperty.STORAGE_ID, info.getStorageId());
		content.put(NodeProperty.REMOTE_STORAGE_PATH, info.getRemotePath());

		itemDelegate.setContent(content);

		logger.trace("GCUBEStorage ID : " + info.getStorageId());

	}

	@Override
	public String getName() throws InternalErrorException {
		return itemDelegate.getTitle();
	}

	@Override
	public String getMimeType() {
		String mimeType = null;
		try {
			mimeType = itemDelegate.getContent().get(NodeProperty.MIME_TYPE);
		} catch(Exception e) {
			mimeType = MimeTypeUtil.BINARY_MIMETYPE;
		}
		return mimeType;
	}


	@Override
	public InputStream getData() throws InternalErrorException {

		String remotePath = null;
		String storageID = null;
		InputStream stream = null;

		try {	
			storageID = itemDelegate.getContent().get(NodeProperty.STORAGE_ID);
			if (storageID==null)
				try {	
					remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
					logger.trace("Retrieving streaming from Storage by Remote Path " + remotePath);
					storageID = getStorage().getStorageId(remotePath);
					setStorageId(storageID);
				} catch (Exception e1) {
					logger.error("Remote Path not found " + e1.getMessage());
				}
			else
				logger.trace("Retrieving streaming from Storage by storage ID " + storageID);

			if (storageID == null)
				throw new InternalErrorException("No Storage ID and RemotePath found in node ID " + getName());
			// The remote data is stored on GCUBE storage.
			//			if (remotePath != null) {
			//				logger.trace("Retrieving streaming from Storage...");
			//				try{
			stream = getStorage().getRemoteFile(storageID);				
			//				}catch (Exception e) {
			//					logger.error("no payload for " + getName());
			//				}	
			//			} 

		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return stream;
	}

	@Override
	public long getLength() throws InternalErrorException {
		long size = 0;
		try{
			size = (long) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.SIZE));
			if (size <= 0){
				long newsize = getStorage().getRemoteFileSize(getRemotePath());
				if (newsize <= size)
					setLenght(size);
				return newsize;
			}
		}catch (Exception e) {
			logger.error(itemDelegate.getTitle() + " has not size property");
			//			size =	storage.getRemoteFileSize(getRemotePath());
			//			setLenght(size);
		}
		return size;
	}


	public void setLenght(long size) {
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(getPortalLogin(), false);
			itemDelegate.getContent().put(NodeProperty.SIZE, new XStream().toXML(size));
			servlets.saveItem(itemDelegate);
		}catch (Exception e) {
			logger.error("Impossible to set lenght for " + itemDelegate.getPath());
		}finally {
			if (servlets!=null)
				servlets.releaseSession();
		}
	}

	@Override
	public String getPublicLink() throws InternalErrorException {

		try {
			String remotePath = getRemotePath();
			return getStorage().getPublicLink(remotePath);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
	}

	public String getRemotePath() throws InternalErrorException {

		String remotePath = null;
		try {
			remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return remotePath; 
	}

	@Override
	public String getStorageId() throws InternalErrorException {
		String storageId = null;
		try {
			storageId = itemDelegate.getContent().get(NodeProperty.STORAGE_ID);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		}
		return storageId; 
	}


	@Override
	public void getHardLink(String destPath) throws InternalErrorException {
		String remotePath = null;

		try {
			remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
			logger.trace("No public link for file: " + itemDelegate.getTitle());

			getStorage().createHardLink(remotePath, destPath);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
	}

	public void updateInfo(JCRSession servlets, MetaInfo info) throws InternalErrorException {

		long size = Long.valueOf(info.getSize());

		try {
			itemDelegate.getContent().put(NodeProperty.STORAGE_ID, info.getStorageId());
			itemDelegate.getContent().put(NodeProperty.MIME_TYPE, info.getMimeType());
			itemDelegate.getContent().put(NodeProperty.SIZE, new XStream().toXML(size));
			servlets.saveItem(itemDelegate);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
	}


	protected String getPortalLogin(){
		return workspace.getOwner().getPortalLogin();
	}

	protected GCUBEStorage getStorage(){
		return workspace.getStorage();
	}



	public void setStorageId(String storageID) {
		JCRSession servlets = null;
		try {
			servlets = new JCRSession(getPortalLogin(), false);
			itemDelegate.getContent().put(NodeProperty.STORAGE_ID, storageID);
			servlets.saveItem(itemDelegate);
		}catch (Exception e) {
			logger.error("Impossible to storage ID for " + itemDelegate.getPath());
		}finally {
			if (servlets!=null)
				servlets.releaseSession();
		}
	}
}



