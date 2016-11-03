/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube;

import java.util.Date;
import java.util.Map;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTUrl;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTUrlDocument extends GWTDocument implements GWTUrl,
		IsSerializable {
	
	protected String url;
	
	protected GWTUrlDocument(){}

	/**
	 * @param creationTime
	 * @param id
	 * @param properties
	 * @param name
	 * @param owner
	 * @param description
	 * @param lastModificationTime
	 * @param lastAction
	 * @param parent
	 * @param length
	 * @param oid
	 * @param mimeType
	 * @param metadata
	 * @param annotation
	 * @param collection
	 * @param bridge
	 * @param url
	 */
	public GWTUrlDocument(Date creationTime, String id,
			GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime,
			GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String oid, String mimeType,
			Map<String, GWTDocumentMetadata> metadata,
			Map<String, String> annotation, String collection, 
			int numberOfAlternatives, int numberOfParts,
			String url) {
		super(creationTime, id, properties, name, owner, description,
				lastModificationTime, lastAction, parent, length, oid,
				mimeType, metadata, annotation, collection, numberOfAlternatives, numberOfParts);
		this.url = url;
	}
	
	//Used for external url
	public GWTUrlDocument(Date creationTime, String id,
			GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime,
			GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String oid) {
		
		super(creationTime, id, properties, name, owner, description,
				lastModificationTime, lastAction, parent, length, oid,
				null, null, null, null, 0, 0);
		this.url = url;
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.URL_DOCUMENT;
	}

}
