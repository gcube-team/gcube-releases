package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.MimeTypeUtil;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.itextpdf.text.log.SysoCounter;


public class JCRExternalFile extends JCRWorkspaceFolderItem implements ExternalFile {

	protected JCRFile file;
	protected ItemDelegate itemDelegate;

	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate itemDelegate)throws RepositoryException {
		this(workspace,itemDelegate,ContentType.GENERAL);
		this.itemDelegate = itemDelegate;
	}

	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate itemDelegate, String name, String description,
			String mimeType, File tmpFile) throws RepositoryException, IOException, RemoteBackendException    {
		this(workspace,itemDelegate,name,description,mimeType,ContentType.GENERAL,tmpFile);
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
			String mimeType, ContentType contentType, InputStream data) throws RepositoryException, IOException, RemoteBackendException {

		super(workspace, delegate, name, description);

		Validate.notNull(data, "Data must be not null");
		this.itemDelegate = delegate;

		File tmpFile = null;
		try{
			tmpFile = WorkspaceUtil.getTmpFile(data);	
			createFile(workspace, delegate, name, description, mimeType, contentType, tmpFile);
		}catch (Exception e){
			logger.error("Error creating JCRExternalFile from " + delegate.getPath());
		}finally{
			if (tmpFile!= null)
				tmpFile.delete();
		}
	}


	public JCRExternalFile(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String mimeType,
			ContentType contentType, File tmpFile) throws RepositoryException, IOException, RemoteBackendException {
		super(workspace, delegate, name, description);
		this.itemDelegate = delegate;
		createFile(workspace, delegate, name, description, mimeType, contentType, tmpFile);

	}



	private void createFile(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String mimeType,
			ContentType contentType, File tmpFile) throws RepositoryException, IOException, RemoteBackendException {
		//		super(workspace, node, name, description);
		Validate.notNull(tmpFile, "tmpFile must be not null");
		this.itemDelegate = delegate;

		if (mimeType==null){
			try{				
				mimeType = MimeTypeUtil.getMimeType(name, tmpFile);
			}catch (Exception e) {
				logger.error("Error detecting mimeType of " + tmpFile.getAbsolutePath());
			}
		}

		switch(contentType) {
		case GENERAL: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.GENERAL.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_FILE.toString());
			this.file = new JCRFile(workspace, delegate,
					(mimeType != null)?mimeType:MimeTypeUtil.BINARY_MIMETYPE,tmpFile, delegate.getPath());
			break;
		}
		case IMAGE: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
			this.file = new JCRImage(workspace, delegate, mimeType, tmpFile, delegate.getPath());
			break;
		}
		case PDF: {
			delegate.setContent(new HashMap<NodeProperty, String>());
			delegate.getContent().put(NodeProperty.CONTENT, ContentType.PDF.toString());
			delegate.getContent().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_PDF_FILE.toString());
			this.file = new JCRPDFFile(workspace, delegate, mimeType, tmpFile, delegate.getPath());
			break;
		}
		default:
			this.file = null;
		}

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

		File tmpFile = null;	
		JCRServlets servlets = null;
		try {
			servlets = new JCRServlets(workspace.getOwner().getPortalLogin());
			tmpFile = WorkspaceUtil.getTmpFile(data);	
			ItemDelegate delegate = servlets.getItemById(getId());
			FolderItemType type =  FolderItemType.valueOf(delegate.getProperties().get(NodeProperty.FOLDER_ITEM_TYPE));

			switch (type) {
			case EXTERNAL_FILE:
				this.file = new JCRFile(workspace, delegate, file.getMimeType(), tmpFile);
				break;
			case EXTERNAL_IMAGE:
				this.file = new JCRImage(workspace, delegate, file.getMimeType(), tmpFile);
				break;
			case EXTERNAL_PDF_FILE:
				this.file = new JCRPDFFile(workspace, delegate, file.getMimeType(), tmpFile);
				break;
			default:
				throw new InternalErrorException("Item type wrong" + type);
			}

		} catch (Exception e) {
			throw new InternalErrorException("Content appears to be not valid: " + e.getMessage());
		} finally {
			servlets.releaseSession();
			if (tmpFile!=null)
				tmpFile.delete();
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

	
	public void updateInfo(JCRServlets servlets, java.io.File tmpFile) throws InternalErrorException {
		this.file.updateInfo(servlets, tmpFile);

	}
}

