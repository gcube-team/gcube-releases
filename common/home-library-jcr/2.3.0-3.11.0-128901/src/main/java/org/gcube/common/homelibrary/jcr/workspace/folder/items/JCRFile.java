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
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
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

	public MetaInfo info;

	String portalLogin;
	ItemDelegate itemDelegate;
	GCUBEStorage storage;
	MetaInfo meta;



	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate)  throws RepositoryException  {
		this.itemDelegate = itemDelegate;
		this.storage = workspace.getStorage();
		this.portalLogin = workspace.getOwner().getPortalLogin();
	}

	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, InputStream is) throws InternalErrorException, IOException, RemoteBackendException {

		String remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);		
		MetaInfo info = WorkspaceItemUtil.getMetadataInfo(is, storage, remotePath, itemDelegate.getTitle());	

		setMeta(workspace, itemDelegate, remotePath, info);
	}

	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, String remotePath, MetaInfo info) throws InternalErrorException {
		setMeta(workspace, itemDelegate, remotePath, info);
	}

	private void setMeta(JCRWorkspace workspace, ItemDelegate itemDelegate, String remotePath, MetaInfo info) {

		this.itemDelegate = itemDelegate;
		this.storage = workspace.getStorage();
		this.portalLogin = workspace.getOwner().getPortalLogin();
		this.meta = info;

		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.PORTAL_LOGIN, workspace.getOwner().getPortalLogin());
		content.put(NodeProperty.MIME_TYPE, info.getMimeType());
		Long l = Long.valueOf(String.valueOf(info.getSize()));
		content.put(NodeProperty.SIZE, new XStream().toXML(l));
		content.put(NodeProperty.STORAGE_ID, info.getStorageId());
		content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);

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
		InputStream stream = null;
		try {
			try {	
				remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
			} catch (Exception e) {
				logger.trace("Remote Path not found");
			}
			// The remote data is stored on GCUBE storage.
			if (remotePath != null) {
				logger.trace("Retrieving streaming from Storage...");
				try{
					stream = getStorage().getRemoteFile(remotePath);				
				}catch (Exception e) {
					logger.error("no payload for " + getName());
				}	
			} 

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
				long newsize = storage.getRemoteFileSize(getRemotePath());
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
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(portalLogin, false);
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

	private String getRemotePath() throws InternalErrorException {

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

	protected GCUBEStorage getStorage() {
		return storage;
	}


	public void updateInfo(JCRServlets servlets, MetaInfo info) throws InternalErrorException {

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






}



