/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;

import java.util.List;

import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

/**
 * The Interface SelectableTypes.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 29, 2015
 */
public interface SelectableTypes {
	

	/**
	 * Sets the selectable types.
	 *
	 * @param selectableTypes the new selectable types
	 */
	public void setSelectableTypes(ItemType ... selectableTypes);
	
	
	/**
	 * Gets the selectable types.
	 *
	 * @return the selectable types
	 */
	public List<ItemType> getSelectableTypes();

}
