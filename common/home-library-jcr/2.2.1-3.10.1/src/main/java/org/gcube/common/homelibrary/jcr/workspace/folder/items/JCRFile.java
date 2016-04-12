package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.File;
import org.gcube.common.homelibrary.jcr.repository.external.GCUBEStorage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
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

	String portalLogin;
	ItemDelegate itemDelegate;
	GCUBEStorage storage;


	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate)  throws RepositoryException  {
		this.itemDelegate = itemDelegate;
		this.storage = workspace.getStorage();
		this.portalLogin = workspace.getOwner().getPortalLogin();
	}

	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, String mimeType, java.io.File tmpFile) throws RepositoryException, RemoteBackendException {

		this.itemDelegate = itemDelegate;
		this.storage = workspace.getStorage();
		this.portalLogin = workspace.getOwner().getPortalLogin();

		if (mimeType==null){

			try {
				mimeType = MimeTypeUtil.getMimeType(itemDelegate.getTitle(), tmpFile);
			} catch (Exception e) {
				logger.error("Error getting mimeType of "+ tmpFile.getAbsolutePath());
			}
		}

		String remotePath = itemDelegate.getPath();
		String storageId = null;
		try {
			FileInputStream is = new FileInputStream(tmpFile);
			storageId = workspace.getStorage().putStream(is, remotePath, mimeType);
			is.close();
		} catch (IOException e) {
			logger.error("FileNotFoundException " + e);
		}

		Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
		content.put(NodeProperty.PORTAL_LOGIN, workspace.getOwner().getPortalLogin());
		content.put(NodeProperty.MIME_TYPE, mimeType);
		content.put(NodeProperty.SIZE, new XStream().toXML(tmpFile.length()));
		content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);

		itemDelegate.setContent(content);

		logger.trace("GCUBEStorage ID : " + storageId);
	}



	public JCRFile(JCRWorkspace workspace, ItemDelegate itemDelegate, String mimeType, java.io.File tmpFile, String remotePath) throws RepositoryException, RemoteBackendException {

		this.itemDelegate = itemDelegate;
		this.storage = workspace.getStorage();
		this.portalLogin = workspace.getOwner().getPortalLogin();

		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.PORTAL_LOGIN, workspace.getOwner().getPortalLogin());
		content.put(NodeProperty.MIME_TYPE, mimeType);
		content.put(NodeProperty.SIZE, new XStream().toXML(tmpFile.length()));
		FileInputStream is = null;
		String url = null;
		try {
			is = new FileInputStream(tmpFile);
			url = workspace.getStorage().putStream(is, remotePath, mimeType);
			//			is.close();
			//			throw new RemoteBackendException("error");
			content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			is.close();
		} catch (IOException e) {
			logger.error(itemDelegate.getPath() + " remote path not present" + e);
			throw new RemoteBackendException(e);
		}

		logger.trace("GCUBEStorage URL : " + url);
	}


	protected GCUBEStorage getStorage(){
		return this.storage;
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
		}catch (Exception e) {
			logger.error(itemDelegate.getTitle() + " has not size property");
		}
		return size;
	}

	@Override
	public String getPublicLink() throws InternalErrorException {

		String remotePath = null;
		try {
			remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);
			return getStorage().getPublicLink(remotePath);

		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
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

	public void updateInfo(JCRServlets servlets, java.io.File tmpFile) throws InternalErrorException {

		String mimeType = null;
		try {
			mimeType = MimeTypeUtil.getMimeType(itemDelegate.getTitle(), tmpFile);
		} catch (Exception e1) {
			logger.error("impossible to detect mime type of " + tmpFile.getAbsolutePath());
		}
		long size = tmpFile.length();
		String remotePath = null;
		try {
			remotePath = itemDelegate.getContent().get(NodeProperty.REMOTE_STORAGE_PATH);

			logger.trace(itemDelegate.getPath() + " - mimeType: " + mimeType);
			setProperties(servlets, itemDelegate, remotePath, mimeType, size);

		} catch (Exception e) {	
			logger.trace("No public link for file: " + itemDelegate.getPath());
		} 

	}

	private void setProperties(JCRServlets servlets, ItemDelegate delegate, String remotePath, String mimeType, long size) throws InternalErrorException {

		try {
			delegate.getContent().put(NodeProperty.MIME_TYPE, mimeType);
			delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);
			delegate.getContent().put(NodeProperty.SIZE, new XStream().toXML(size));
			servlets.saveItem(delegate);
		} catch (Exception e) {
			throw new InternalErrorException(e);
		} 
	}



	//	public List<AccountingEntry> getAccounting(ItemDelegate node) throws RepositoryException {
	//		List<AccountingEntry> list = new ArrayList<AccountingEntry>();
	//
	//		JCRServlets servlets = null;
	//		try {
	//			servlets = new JCRServlets(portalLogin);
	//
	//			List<AccountingDelegate> accouting = servlets.getAccountingById(node.getId());
	//			for (AccountingDelegate entry: accouting){
	//				try {
	//					switch (AccountingEntryType.getEnum(
	//							entry.getEntryType().toString())) {
	//							case CUT:
	//								list.add(new JCRAccountingFolderEntryCut(entry));
	//								break;
	//							case PASTE:
	//								list.add(new JCRAccountingEntryPaste(entry));
	//								break;
	//							case REMOVAL:
	//								list.add(new JCRAccountingFolderEntryRemoval(entry));
	//								break;
	//							case RENAMING:
	//								list.add(new JCRAccountingEntryRenaming(entry));
	//								break;
	//							case ADD:
	//								list.add(new JCRAccountingFolderEntryAdd(entry));
	//								break;								
	//							case UPDATE:
	//								list.add(new JCRAccountingEntryUpdate(entry));
	//								break;
	//							case READ:
	//								list.add(new JCRAccountingEntryRead(entry));
	//								break;
	//							case SHARE:
	//								list.add(new JCRAccountingEntryShare(entry));
	//								break;
	//							case UNSHARE:
	//								list.add(new JCRAccountingEntryUnshare(entry));
	//								break;
	//							default:
	//								break;												
	//					}
	//				} catch (Exception e) {
	//					logger.error("Accounting entry skipped "+ entry.getEntryType().toString(),e);
	//				}
	//
	//			}
	//
	//
	//		} catch (RepositoryException e) {
	//			throw new RepositoryException(e.getMessage());
	//		} finally {
	//			servlets.releaseSession();
	//		}
	//		return list;
	//
	//	}



}



