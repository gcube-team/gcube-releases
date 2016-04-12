package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public class JCRExternalImage extends JCRExternalFile implements
		ExternalImage {
	
	public JCRExternalImage(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException  {
		super(workspace, delegate, ContentType.IMAGE);
	}
	

	public JCRExternalImage(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String mimeType, File tmpFile)  throws RepositoryException, IOException, RemoteBackendException   {
		super(workspace,delegate,name,description,mimeType,ContentType.IMAGE,tmpFile);
	}
	
	public JCRExternalImage(JCRWorkspace workspace, ItemDelegate delegate,
			String name, String description, String mimeType,
			InputStream data) throws RepositoryException, IOException, RemoteBackendException   {
		super(workspace,delegate,name,description,mimeType,ContentType.IMAGE,data);
	}
	@Override
	public int getWidth() {
		return ((JCRImage)file).getWidth();
	}

	@Override
	public int getHeight() {
		return ((JCRImage)file).getHeight();
	}

	@Override
	public InputStream getThumbnail() throws InternalErrorException {
		return ((JCRImage)file).getThumbnail();
	}

	@Override
	public int getThumbnailWidth() {
		return ((JCRImage)file).getThumbnailWidth();
	}

	@Override
	public int getThumbnailHeight() {
		return ((JCRImage)file).getThumbnailHeight();
	}

//	@Override
//	public long getThumbnailLength() throws InternalErrorException {
//		return ((JCRImage)content).getThumbnailLength();
//	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.EXTERNAL_IMAGE;
	}


	public void updateInfo(JCRServlets servlets, java.io.File tmp) throws InternalErrorException {
		super.updateInfo(servlets, tmp);
	}
	

}
