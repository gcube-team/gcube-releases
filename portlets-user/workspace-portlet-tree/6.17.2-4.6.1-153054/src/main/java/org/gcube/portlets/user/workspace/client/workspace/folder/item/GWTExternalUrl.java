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
public class GWTExternalUrl extends GWTFolderItem implements GWTUrl, IsSerializable{
	
	protected String url;
	
	protected GWTExternalUrl()
	{}
	
	public GWTExternalUrl(Date creationTime, String id, GWTProperties properties, String name, String owner, 
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, long length, String url) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		
		this.url = url;
	}
	
	//ADDED FRANCESCO
	public GWTExternalUrl(String url) {
		this.url = url;
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.EXTERNAL_URL;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

}
