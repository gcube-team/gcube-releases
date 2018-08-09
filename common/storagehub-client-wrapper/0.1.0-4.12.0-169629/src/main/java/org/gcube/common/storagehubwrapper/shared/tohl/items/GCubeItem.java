/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import java.io.InputStream;
import java.util.List;

import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceSharedFolder;
import org.gcube.common.storagehubwrapper.shared.tohl.exceptions.InternalErrorException;


/**
 * The Interface GCubeItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface GCubeItem extends FileItem {

	/**
	 * Gets the scopes.
	 *
	 * @return the scopes
	 * @throws InternalErrorException the internal error exception
	 */
	public List<String> getScopes() throws InternalErrorException;

	/**
	 * Gets the item type.
	 *
	 * @return the item type
	 */
	public String getItemType();

	/**
	 * Gets the creator.
	 *
	 * @return the creator
	 */
	public String getCreator();

	/**
	 * Share.
	 *
	 * @param users the users
	 * @return the workspace shared folder
	 * @throws InternalErrorException the internal error exception
	 */
	public WorkspaceSharedFolder share(List<String> users) throws InternalErrorException;

	/**
	 * Gets the data.
	 *
	 * @return the data
	 * @throws InternalErrorException the internal error exception
	 */
	public InputStream getData() throws InternalErrorException;


}
