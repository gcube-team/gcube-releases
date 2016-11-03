/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;


/**
 * The Interface ShowableTypes.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 29, 2015
 */
public interface ShowableTypes {
	

	/**
	 * Gets the showable types.
	 *
	 * @return the showable types
	 */
	public List<ItemType> getShowableTypes();

	/**
	 * Sets the showable types.
	 *
	 * @param showableTypes the new showable types
	 */
	public void setShowableTypes(ItemType ... showableTypes);
}
