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
import org.gcube.portlets.user.workspace.client.workspace.folder.item.GWTPDF;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTPDFDocument extends GWTDocument implements IsSerializable, GWTPDF {
	
	protected String url;
	
	protected String author;
	protected int numberOfPages;
	protected String producer;
	protected String title;
	protected String version;

	public GWTPDFDocument() {
		super();
	}

	public GWTPDFDocument(Date creationTime, String id, GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String oid, String mimeType, Map<String, GWTDocumentMetadata> metadata, Map<String, String> annotation, 
			String collection, int numberOfAlternatives, int numberOfParts,
			String author, int numberOfPages, String producer, String title, String version, String url) {
		super(creationTime, id, properties, name, owner, description, lastModificationTime, lastAction, parent, length, oid, 
				mimeType, metadata,	annotation, collection, numberOfAlternatives, numberOfParts);
		this.author = author;
		this.numberOfPages = numberOfPages;
		this.producer = producer;
		this.title = title;
		this.version = version;
		
		this.url = url;
		
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the numberOfPages
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * @return the producer
	 */
	public String getProducer() {
		return producer;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the htmlUrl
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.PDF_DOCUMENT;
	}

}
