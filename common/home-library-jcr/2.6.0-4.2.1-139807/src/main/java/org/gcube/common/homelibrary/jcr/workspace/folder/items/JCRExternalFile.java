package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public class JCRExternalFile extends JCRWorkspaceFolderItem implements ExternalFile {

	protected JCRFile file;
	protected ItemDelegate itemDelegate;

	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate itemDelegate)throws RepositoryException {
		this(workspace,itemDelegate,ContentType.GENERAL);
		this.itemDelegate = itemDelegate;
	}

	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate itemDelegate, String name, String description, MetaInfo info, Map<String, String> properties) throws RepositoryException, IOException, RemoteBackendException    {
		this(workspace, itemDelegate, name, description, ContentType.GENERAL, info, properties);
		this.itemDelegate = itemDelegate;
	}

	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate itemDelegate, ContentType contentType) throws RepositoryException {
		super(workspace, itemDelegate);
		this.itemDelegate = itemDelegate;

		switch(contentType) {
		case GENERAL:
			this.file = new JCRFile(workspace, itemDelegate);
			break;
		case IMAGE:
			this.file = new JCRImage(workspace, itemDelegate);
			break;
		case PDF:
			this.file = new JCRPDFFile(workspace, itemDelegate);
			break;
		default:
			this.file = null;
		}

	}

	
	protected JCRExternalFile(JCRWorkspace workspace, ItemDelegate delegate, String name, String description,
			ContentType contentType, MetaInfo info, Map<String, String> properties) throws RepositoryException, IOException, RemoteBackendException {

		super(workspace, delegate, name, description, properties);

		this.itemDelegate = delegate;

		try{
			createFile(workspace, delegate, name, description, info, contentType);
		}catch (Exception e){
			logger.error("Error creating JCRExternalFile from " + delegate.getPath());
		}finally{

		}
	}

	
	private void createFile(final JCRWorkspace workspace, ItemDelegate delegate,
			final String name, String description, MetaInfo info, ContentType contentType) throws InternalErrorException {

		//		Validate.notNull(stream, "stream must be not null");
		this.itemDelegate = delegate;

		switch(contentType) {
		case GENERAL: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.GENERAL.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_FILE.toString());
			try {
				this.file = new JCRFile(workspace, delegate, info);
			} catch (RemoteBackendException e) {
				throw new InternalErrorException(e.getMessage());
			}
			break;
		}
		case IMAGE: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
			try {
				this.file = new JCRImage(workspace, delegate, info);
			} catch (RemoteBackendException e) {
				throw new InternalErrorException(e.getMessage());
			}
			break;
		}
		case PDF: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.PDF.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_PDF_FILE.toString());
			try {
				this.file = new JCRPDFFile(workspace, delegate, info);
			} catch (RemoteBackendException e) {
				throw new InternalErrorException(e.getMessage());
			}
			break;
		}
		default:
			this.file = null;
		}

	}

	@Override
	public String getStorageId() throws InternalErrorException {
		return file.getStorageId();
	}


	public void setStorageId(String storageID) throws InternalErrorException {
		file.setStorageId(storageID);
	}
	
	@Override
	public String getMimeType() {
		return file.getMimeType();
	}

	@Override
	public InputStream getData() throws InternalErrorException {
		return file.getData();
	}

	@Override
	public long getLength() throws InternalErrorException {
		return file.getLength();
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.EXTERNAL_FILE;
	}



	@Override
	public void setData(InputStream data) throws InternalErrorException {

		Validate.notNull(data, "Data must be not null");

		JCRSession servlets = null;
		try {
			servlets = new JCRSession(workspace.getOwner().getPortalLogin(), false);
			ItemDelegate delegate = servlets.getItemById(getId());
			FolderItemType type =  FolderItemType.valueOf(delegate.getProperties().get(NodeProperty.FOLDER_ITEM_TYPE));

			switch (type) {
			case EXTERNAL_FILE:
				this.file = new JCRFile(workspace, delegate, data);
				break;
			case EXTERNAL_IMAGE:
				this.file = new JCRImage(workspace, delegate, data);
				break;
			case EXTERNAL_PDF_FILE:
				this.file = new JCRPDFFile(workspace, delegate, data);
				break;
			default:
				throw new InternalErrorException("Item type wrong" + type);
			}

		} catch (Exception e) {
			throw new InternalErrorException("Content appears to be not valid: " + e.getMessage());
		} finally {
			servlets.releaseSession();
		}

	}

	@Override
	public String getPublicLink() throws InternalErrorException {
		return this.file.getPublicLink();
	}

	@Override
	public void getHardLink(String linkName) throws InternalErrorException {
		this.file.getHardLink(linkName);

	}


	public void updateInfo(JCRSession servlets, MetaInfo info) throws InternalErrorException {
		this.file.updateInfo(servlets, info);

	}
}
