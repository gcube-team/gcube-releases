package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.util.Map;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.common.homelibary.model.items.type.ContentType;

public class JCRExternalPDFFile extends JCRExternalFile implements
		ExternalPDFFile {

	
	public JCRExternalPDFFile(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException {
		super(workspace, delegate, ContentType.PDF);
			
	}
	
	public JCRExternalPDFFile(JCRWorkspace workspace, ItemDelegate node, String name,
			String description, MetaInfo info, Map<String, String> properties) throws RepositoryException, IOException, RemoteBackendException {
		super(workspace,node,name,description,ContentType.PDF,info, properties);
	}
	
	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.EXTERNAL_PDF_FILE;	
	}

	@Override
	public int getNumberOfPages() {	
		return ((JCRPDFFile)file).getNumberOfPages();
	}

	@Override
	public String getVersion() {
		return ((JCRPDFFile)file).getVersion();
	}

	@Override
	public String getAuthor() {
		return ((JCRPDFFile)file).getAuthor();
	}

	@Override
	public String getTitle() {
		return ((JCRPDFFile)file).getTitle();
	}

	@Override
	public String getProducer() {
		return ((JCRPDFFile)file).getProducer();
	}

	@Override
	public void updateInfo(JCRSession servlets, MetaInfo info) throws InternalErrorException {
		super.updateInfo(servlets, info);
	}
	
}
