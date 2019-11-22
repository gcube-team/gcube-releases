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
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTWorkflowTemplate extends GWTFolderItem implements IsSerializable {
	
	protected GWTWorkflowTemplate()
	{}
	
	public GWTWorkflowTemplate(String id, String name, String description, String owner, Date creationTime, GWTProperties properties, Date lastModificationTime,
			GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent, long length) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
	}

	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.WORKFLOW_TEMPLATE;
	}

}
