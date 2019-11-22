/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public enum GWTWorkspaceItemType implements GWTItemDescription {
	//icon class calculated from the state
	FOLDER("Folder", "tree-folder-icon"),
	//icon class derived from the folder item type
	FOLDER_ITEM("Folder Item","");
	
	protected String iconClass;
	protected String label;
	
	GWTWorkspaceItemType(String label, String iconClass)
	{
		this.label = label;
		this.iconClass = iconClass;
	}
	
	public String getIconClass() {
		return iconClass;
	}

	public String getLabel()
	{
		return label;
	}

}
