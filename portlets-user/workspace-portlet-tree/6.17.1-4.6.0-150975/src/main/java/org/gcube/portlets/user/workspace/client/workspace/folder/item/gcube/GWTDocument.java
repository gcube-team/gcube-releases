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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTDocument extends GWTInfoObject implements IsSerializable {
	
	protected String mimeType;
	protected Map<String, GWTDocumentMetadata> metadata;
	protected Map<String, String> annotation;
	protected String collectionName;
	
	protected int numberOfParts = 0;
	protected int numberOfAlternatives = 0;

	public GWTDocument() {}
	
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
	 * @param oid
	 * @param bridge
	 */
	public GWTDocument(Date creationTime, String id, GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String oid, String mimeType, Map<String, GWTDocumentMetadata> metadata, Map<String, String>  annotation, 
			String collection, int numberOfAlternatives, int numberOfParts) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length, oid);
		
		this.mimeType = mimeType;
		this.metadata = metadata;
		this.annotation = annotation;
		this.collectionName = collection;
		this.numberOfAlternatives = numberOfAlternatives;
		this.numberOfParts = numberOfParts;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @return the metadata
	 */
	public Map<String, GWTDocumentMetadata> getMetadata() {
		return metadata;
	}

	/**
	 * @return the annotations
	 */
	public Map<String, String> getAnnotation() {
		return annotation;
	}

	/**
	 * @return the collectionName
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * @return the numberOfParts
	 */
	public int getNumberOfParts() {
		return numberOfParts;
	}

	/**
	 * @return the numberOfAlternatives
	 */
	public int getNumberOfAlternatives() {
		return numberOfAlternatives;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.DOCUMENT;
	}

}
