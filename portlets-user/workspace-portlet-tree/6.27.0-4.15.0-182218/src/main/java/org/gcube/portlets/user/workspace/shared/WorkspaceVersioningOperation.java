/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Feb 17, 2014
 *
 */
public enum WorkspaceVersioningOperation {

	//SHOW TRASH WINDOW
	SHOW("Show", "Show"),

	//CALLING OPERATION SERVER
	REFRESH("Refresh", "Refresh history of versioning"),
	//RESTORE("Restore", "Restore the file to the selected version"),
	DOWNLOAD("Download", "Download the version of the selected file"),
	PUBLIC_LINK("Public Link", "Get the public link of the file at the selected version"),
	DELETE_PERMANENTLY("Delete Permanently", "Delete Permanently the version for the selected file"),
	DELETE_ALL_OLDER_VERSIONS("Delete all versions permanently", "Delete definitively all versions of the file");

	public String label;
	public String operationDescription;

	WorkspaceVersioningOperation(String label, String description)
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

		for (WorkspaceVersioningOperation item : WorkspaceVersioningOperation.values())
			listLabels.add(item.getLabel());

		return listLabels;
	}


	public static WorkspaceVersioningOperation valueOfLabel(String label)
	{
		for (WorkspaceVersioningOperation value:values()) if (value.getLabel().equals(label)) return value;
		return null;
	}

	public String getOperationDescription() {
		return operationDescription;
	}


}
