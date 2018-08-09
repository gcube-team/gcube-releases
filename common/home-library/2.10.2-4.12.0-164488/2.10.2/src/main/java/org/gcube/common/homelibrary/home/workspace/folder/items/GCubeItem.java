/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;

/**
 * @author Lucio Lelii lucio.lelii@isti.cnr.it
 *
 */
public interface GCubeItem extends FolderItem {
	
	public List<String> getScopes() throws InternalErrorException;
	
	public String getItemType();
	
	public String getCreator();

	public WorkspaceSharedFolder share(List<String> users) throws InternalErrorException;

	public InputStream getData() throws InternalErrorException;
	

}
