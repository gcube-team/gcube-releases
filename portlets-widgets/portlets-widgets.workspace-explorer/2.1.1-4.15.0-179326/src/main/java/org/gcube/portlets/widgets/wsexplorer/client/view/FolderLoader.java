/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;


/**
 * The Interface FolderLoader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 11, 2017
 */
public interface FolderLoader {

	/**
	 * Load folder.
	 *
	 * @param item the item
	 * @param loadGcubeProperties the load gcube properties
	 * @param startIdx the start idx. Used for pagination
	 * @param limit the limit. Used for pagination
	 * @param resetStore the reset store.  Used for pagination
	 * @throws Exception the exception
	 */
	public void loadFolder(final Item item, boolean loadGcubeProperties, int startIdx, int limit, boolean resetStore) throws Exception;
}
