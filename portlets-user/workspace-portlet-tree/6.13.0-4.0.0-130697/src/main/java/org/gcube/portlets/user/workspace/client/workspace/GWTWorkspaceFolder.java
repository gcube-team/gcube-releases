/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.workspace.client.workspace.exceptions.WrongItemTypeException;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class GWTWorkspaceFolder extends GWTWorkspaceItem implements IsSerializable {
	
	protected static final String TREE_WORKSPACE_SHARED_ICON = "tree-workspace-shared-icon";

	protected static final String TREE_ROOT_ICON = "tree-root-icon";

	protected List<GWTWorkspaceItem> children;
	
	protected boolean isShared = false;
	
	protected GWTWorkspaceFolder() {
	}

	public GWTWorkspaceFolder(String id, String name, String description,	String owner, Date creationTime, 
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, List<GWTWorkspaceItem> children) {
		super(parent, id, name, description, owner, creationTime, properties, lastModificationTime, lastAction);
		
		this.children = children;

	}

	public List<GWTWorkspaceItem> getChildren(){
		return children;
	}

	public GWTWorkspaceItemType getType() {
		return GWTWorkspaceItemType.FOLDER;
	}
	

	@Override
	public GWTItemDescription getItemDescription() {
		return getType();
	}
	
	
	@Override
	public String getIconClass()
	{
		if (!isRoot()){
			if (!isShared()) return getType().getIconClass();
			else return TREE_WORKSPACE_SHARED_ICON;
		}
		else return TREE_ROOT_ICON;
	}

	public List<GWTWorkspaceOperation> getEnabledOperations(){
		return null;
	}
	
	protected void addChild(GWTWorkspaceItem child) throws WrongItemTypeException
	{
		children.add( child);
	}

	protected void removeChild(GWTWorkspaceItem child) {
		children.remove(child);	
	}


	/**
	 * @return the isShared
	 */
	public boolean isShared() {
		return isShared;
	}


	/**
	 * @param isShared the isShared to set
	 */
	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

}
