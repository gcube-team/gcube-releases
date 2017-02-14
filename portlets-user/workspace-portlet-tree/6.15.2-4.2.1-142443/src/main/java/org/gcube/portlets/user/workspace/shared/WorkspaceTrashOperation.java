/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 17, 2014
 *
 */
public enum WorkspaceTrashOperation {
	
	//SHOW TRASH WINDOW
	SHOW("Show", "Show"),
	
	//CALLING OPERATION SERVER
	REFRESH("Refresh", "Refresh trash content"), 
	RESTORE("Restore", "Restore the trash item/s (selected)"), 
	RESTORE_ALL("Restore All", "Restore all trash content"), 
	DELETE_PERMANENTLY("Delete Permanently", "Delete Permanently the trash item/s (selected)"), 
	EMPTY_TRASH("Empty", "Empty definitively trash content");
	
	public String label;
	public String operationDescription;
	
	WorkspaceTrashOperation(String label, String description)
	{
		this.label = label;
		this.operationDescription = description;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	public static List<String> getListLabels(){
		
		List<String> listLabels = new ArrayList<String>();
		
		for (WorkspaceTrashOperation item : WorkspaceTrashOperation.values())
			listLabels.add(item.getLabel());

		return listLabels;
	}
	
	
	public static WorkspaceTrashOperation valueOfLabel(String label)
	{
		for (WorkspaceTrashOperation value:values()) if (value.getLabel().equals(label)) return value;
		return null;
	}

	public String getOperationDescription() {
		return operationDescription;
	}
	
	
}
