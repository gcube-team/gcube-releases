/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.workspace.client.workspace.exceptions.WrongItemTypeException;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class GWTWorkspaceFolder.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jul 17, 2019
 */
public class GWTWorkspaceFolder extends GWTWorkspaceItem implements IsSerializable {
	
	protected static final String TREE_WORKSPACE_SHARED_ICON = "tree-workspace-shared-icon";

	protected static final String TREE_ROOT_ICON = "tree-root-icon";

	protected List<GWTWorkspaceItem> children;
	
	protected boolean isShared = false;
	
	/**
	 * Instantiates a new GWT workspace folder.
	 */
	protected GWTWorkspaceFolder() {
	}

	/**
	 * Instantiates a new GWT workspace folder.
	 *
	 * @param id the id
	 * @param name the name
	 * @param description the description
	 * @param owner the owner
	 * @param creationTime the creation time
	 * @param properties the properties
	 * @param lastModificationTime the last modification time
	 * @param lastAction the last action
	 * @param parent the parent
	 * @param children the children
	 */
	public GWTWorkspaceFolder(String id, String name, String description,	String owner, Date creationTime, 
			GWTProperties properties, Date lastModificationTime, GWTWorkspaceItemAction lastAction,
			GWTWorkspaceFolder parent, List<GWTWorkspaceItem> children) {
		super(parent, id, name, description, owner, creationTime, properties, lastModificationTime, lastAction);
		
		this.children = children;

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem#getChildren()
	 */
	public List<GWTWorkspaceItem> getChildren(){
		return children;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem#getType()
	 */
	public GWTWorkspaceItemType getType() {
		return GWTWorkspaceItemType.FOLDER;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem#getItemDescription()
	 */
	@Override
	public GWTItemDescription getItemDescription() {
		return getType();
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItem#getIconClass()
	 */
	@Override
	public String getIconClass()
	{
		if (!isRoot()){
			if (!isShared()) return getType().getIconClass();
			else return TREE_WORKSPACE_SHARED_ICON;
		}
		else return TREE_ROOT_ICON;
	}

	/**
	 * Gets the enabled operations.
	 *
	 * @return the enabled operations
	 */
	public List<GWTWorkspaceOperation> getEnabledOperations(){
		return null;
	}
	
	/**
	 * Adds the child.
	 *
	 * @param child the child
	 * @throws WrongItemTypeException the wrong item type exception
	 */
	protected void addChild(GWTWorkspaceItem child) throws WrongItemTypeException
	{
		children.add( child);
	}

	/**
	 * Removes the child.
	 *
	 * @param child the child
	 */
	protected void removeChild(GWTWorkspaceItem child) {
		children.remove(child);	
	}


	/**
	 * Checks if is shared.
	 *
	 * @return the isShared
	 */
	public boolean isShared() {
		return isShared;
	}


	/**
	 * Sets the shared.
	 *
	 * @param isShared the isShared to set
	 */
	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

}
