/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder;


import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface FolderItem extends WorkspaceItem {
	
	/**
	 * The folder item type.
	 * @return the type;
	 */
	public FolderItemType getFolderItemType();
	
	/**
	 * The folder item length.
	 * @return the length.
	 * @throws InternalErrorException if an internal error occurs.
	 */
	public long getLength() throws InternalErrorException;
	
	/**
	 * The folder myme type
	 * @return the myme type
	 * @throws InternalErrorException
	 */
	public String getMimeType() throws InternalErrorException;


}
