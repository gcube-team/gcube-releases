/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.shared;


/**
 * The Interface IntemInterface.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 22, 2016
 */
public interface ItemInterface {

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	ItemInterface getParent();

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();
}
