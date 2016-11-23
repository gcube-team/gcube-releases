/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.manage;

import org.gcube.portlets.admin.gcubereleases.shared.Release;

/**
 * The Interface HandlerReleaseOperation.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public interface HandlerReleaseOperation {
	
	/**
	 * Delete.
	 *
	 * @param rls the rls
	 */
	void delete(Release rls);
	
	/**
	 * Update.
	 *
	 * @param rls the rls
	 */
	void update(Release rls);
	
	/**
	 * Gets the release selected.
	 *
	 * @return the release selected
	 */
	Release getReleaseSelected();

}
