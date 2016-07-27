/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTExternalPDFFile extends GWTExternalFile implements IsSerializable, GWTPDF{
	
	protected String author;
	protected int numberOfPages;
	protected String producer;
	protected String title;
	protected String version;
	
	protected GWTExternalPDFFile()
	{}
	
	public GWTExternalPDFFile(Date creationTime, String id, GWTProperties properties, String name, String owner,
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String mimeType, String author, int numberOfPages, String producer, String title, String version) {
		super(creationTime, id, properties, name, owner, description, lastModificationTime, lastAction, parent, length,	mimeType);
		
		this.author = author;
		this.numberOfPages = numberOfPages;
		this.producer = producer;
		this.title = title;
		this.version = version;
	}



	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.EXTERNAL_PDF_FILE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getNumberOfPages() {
		return numberOfPages;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProducer() {
		return producer;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {
		return version;
	}

}
