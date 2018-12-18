/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;


import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 * The Interface FolderItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface FileItem extends WorkspaceItem, File {

	/**
	 * Get Current version.
	 *
	 * @return the current version
	 * @throws InternalErrorException the internal error exception
	 */
	public WorkspaceVersion getCurrentVersion() throws InternalErrorException;

}
