/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTMetadata extends GWTInfoObject implements IsSerializable{

	protected String schema;
	protected String language;
	protected String collectionName;
	
	protected String htmlUrl;
	protected String xmlRawUrl;
	
	
	protected GWTMetadata(){}

	
	public GWTMetadata(String id, String name, String description,
			String owner, Date creationTime, GWTProperties properties,
			Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, long length, String oid, String schema, String language, String collectionName,
			String htmlUrl, String xmlRawUrl) {
		super(id, name, description, owner, creationTime, properties,
				lastModificationTime, lastAction, parent, length, oid);
		this.schema = schema;
		this.language = language;
		this.collectionName = collectionName;
		this.htmlUrl = htmlUrl;
		this.xmlRawUrl = xmlRawUrl;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}


	/**
	 * @return the collectionName
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * @return the htmlUrl
	 */
	public String getHtmlUrl() {
		return htmlUrl;
	}

	/**
	 * @return the xmlRawUrl
	 */
	public String getXmlRawUrl() {
		return xmlRawUrl;
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.METADATA;
	}

}
