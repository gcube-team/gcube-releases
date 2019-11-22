/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.workspace.GWTItemDescription;
import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public abstract class GWTFolderItem extends GWTWorkspaceItem implements IsSerializable{

	protected GWTWorkspaceFolder parent;
	protected long length;
	protected String workflowStatus;
	
	protected GWTFolderItem()
	{}
	
	public GWTFolderItem(String id, String name, String description, String owner, Date creationTime,
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, long length) {
		super(parent, id, name, description, owner, creationTime, properties, lastModificationTime, lastAction);
		this.length = length;
	}

	/**
	 * @return the workflowStatus
	 */
	public String getWorkflowStatus() {
		return workflowStatus;
	}

	/**
	 * @param workflowStatus the workflowStatus to set
	 */
	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	@Override
	public GWTItemDescription getItemDescription() {
		return getFolderItemType();
	}

	public abstract GWTFolderItemType getFolderItemType();

	public GWTWorkspaceItemType getType() {
		return GWTWorkspaceItemType.FOLDER_ITEM;
	}

	@Override
	public List<? extends GWTWorkspaceItem> getChildren() {
		return new LinkedList<GWTWorkspaceItem>();
	}
	
	public long getLength(){
		return length;
	}

}
