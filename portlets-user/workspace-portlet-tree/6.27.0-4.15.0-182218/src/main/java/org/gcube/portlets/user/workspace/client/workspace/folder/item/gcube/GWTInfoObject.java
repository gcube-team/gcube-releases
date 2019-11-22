/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public abstract class GWTInfoObject extends GWTFolderItem implements IsSerializable {
	
	protected String oid;

	public GWTInfoObject() {}

	
	public GWTInfoObject(String id, String name, String description, String owner, Date creationTime,
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent,
			long length, String oid) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);

		this.oid = oid;
	}

	/**
	 * @return the oid
	 */
	public String getOid() {
		return oid;
	}
}
