/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.client.view;



/**
 * The Interface SelectionItem.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 28, 2016
 */
public interface SelectionItem {

	/**
	 * Gets the selected item.
	 *
	 * @param <T> the generic type
	 * @return the selected item
	 */
	<T> T getSelectedItem();

}