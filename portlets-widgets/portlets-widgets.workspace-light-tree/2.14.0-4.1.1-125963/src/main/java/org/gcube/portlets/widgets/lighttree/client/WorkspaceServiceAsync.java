/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface WorkspaceServiceAsync{
	
	public void getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria, AsyncCallback<Item> callback);

	public void checkName(String name, AsyncCallback<Boolean> callback);

	public void getFolder(String folderId, List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria, AsyncCallback<Item> callback);
}
