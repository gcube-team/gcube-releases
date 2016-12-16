/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.client.view;

import java.util.Set;

import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

/**
 * The Interface SelectionItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 29, 2015
 */
public interface SelectionItem {


	/**
	 * Gets the selected items.
	 *
	 * @return the selected items
	 */
	Set<Item> getSelectedItems();

}