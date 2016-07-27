/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
@RemoteServiceRelativePath("WorkspaceLightService")
public interface WorkspaceService extends RemoteService {
	
	public Item getRoot(List<ItemType> showableTypes, boolean purgeEmpyFolders,	FilterCriteria filterCriteria) throws WorkspaceLightTreeServiceException;
	
	public Item getFolder(String folderId, List<ItemType> showableTypes, boolean purgeEmpyFolders, FilterCriteria filterCriteria) throws WorkspaceLightTreeServiceException;
	
	public boolean checkName(String name) throws WorkspaceLightTreeServiceException;
}
