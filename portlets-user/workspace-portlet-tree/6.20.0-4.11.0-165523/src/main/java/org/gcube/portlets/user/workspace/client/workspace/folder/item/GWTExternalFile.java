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
public class GWTExternalFile extends GWTFolderItem implements IsSerializable{
	
	protected String mimeType;
	
	protected GWTExternalFile()
	{}
	
	public GWTExternalFile(Date creationTime, String id, GWTProperties properties, String name, String owner, 
			String description, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, long length, String mimeType) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		

		this.mimeType = mimeType;
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.EXTERNAL_FILE;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

}
