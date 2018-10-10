/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTReport extends GWTFolderItem implements IsSerializable {
	
	protected Date created;
	protected Date lastEdit;
	protected String author;
	protected String lastEditBy;
	protected String templateName;
	protected int numberOfSections;
	protected String status;
	
	protected GWTReport()
	{}
	
	public GWTReport(String id, String name, String description, String owner, Date creationTime, GWTProperties properties, Date lastModificationTime,
			GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent, long length, Date created, Date lastEdit, String author, String lastEditBy,
			String templateName, int numberOfSections, String status) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		this.created = created;
		this.lastEdit = lastEdit;
		this.author = author;
		this.lastEditBy = lastEditBy;
		this.templateName = templateName;
		this.numberOfSections = numberOfSections;
		this.status = status;
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.REPORT;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @return the lastEdit
	 */
	public Date getLastEdit() {
		return lastEdit;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the lastEditBy
	 */
	public String getLastEditBy() {
		return lastEditBy;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @return the numberOfSections
	 */
	public int getNumberOfSections() {
		return numberOfSections;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

}
