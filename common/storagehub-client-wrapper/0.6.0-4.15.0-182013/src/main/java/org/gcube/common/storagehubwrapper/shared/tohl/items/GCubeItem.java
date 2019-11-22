/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import org.gcube.common.storagehubwrapper.shared.tohl.WorkspaceItem;



/**
 * The Interface GCubeItem.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface GCubeItem extends WorkspaceItem {


	/**
	 * @return the scopes
	 */
	public String[] getScopes();


	/**
	 * @return the creator
	 */
	public String getCreator();


	/**
	 * @return the itemType
	 */
	public String getItemType();


	/**
	 * @return the properties
	 */
	public String getProperties();


	/**
	 * @return the shared
	 */
	public boolean isShared();

	/**
	 * @return the property
	 */
	public PropertyMap getProperty();
}
