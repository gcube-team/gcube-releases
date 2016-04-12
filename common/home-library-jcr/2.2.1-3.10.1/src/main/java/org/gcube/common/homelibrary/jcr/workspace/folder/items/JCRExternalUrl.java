package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibrary.util.Util;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

public class JCRExternalUrl extends JCRWorkspaceFolderItem implements
ExternalUrl {

	protected final JCRFile content;


	public JCRExternalUrl(JCRWorkspace workspace, ItemDelegate itemDelegate) throws RepositoryException, InternalErrorException {
		super(workspace,itemDelegate);
		this.content = new JCRFile(workspace, itemDelegate);
	}

	public JCRExternalUrl(JCRWorkspace workspace, ItemDelegate itemDelegate, String name, String description,
			String url) throws RepositoryException, RemoteBackendException, InternalErrorException{
		super(workspace, itemDelegate, name, description);


		Validate.notNull(url, "url must be not null");

		delegate.setContent(new HashMap<NodeProperty, String>());
		delegate.getContent().put(NodeProperty.CONTENT, ContentType.GENERAL.toString());
		//		System.out.println(itemDelegate.toString());
		File tmp = WorkspaceUtil.getTmpFile(new ByteArrayInputStream(url.getBytes()));

		this.content = new JCRFile(workspace, delegate, null, tmp);
		
		if (tmp!=null)
			tmp.delete();
		
		delegate.setProperties(new HashMap<NodeProperty, String>());
		delegate.getProperties().put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_URL.toString());
	}

	@Override
	public long getLength() throws InternalErrorException {
		return content.getLength();	
	}

	@Override
	public String getUrl() throws InternalErrorException {
		try {
			return Util.readStreamAsString((content.getData()));
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.EXTERNAL_URL;
	}

	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}

	public ItemDelegate save() throws RepositoryException {
		return super.save();

	}


}
