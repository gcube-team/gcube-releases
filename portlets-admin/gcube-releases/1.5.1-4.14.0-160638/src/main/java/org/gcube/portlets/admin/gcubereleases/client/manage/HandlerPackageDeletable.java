/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import org.gcube.portlets.admin.gcubereleases.shared.Package;

/**
 * The Interface HandlerPackageDeletable.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface HandlerPackageDeletable {
	
	/**
	 * Delete.
	 *
	 * @param pck the pck
	 */
	void delete(Package pck);
	
	/**
	 * Undelete.
	 *
	 * @param pck the pck
	 */
	void undelete(Package pck);

}
