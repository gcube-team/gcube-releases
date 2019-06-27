/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl.items;

import java.util.Calendar;


/**
 * The Interface WorkspaceVersion.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 12, 2018
 */
public interface WorkspaceVersion {


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the created.
	 *
	 * @return the created
	 */
	public Calendar getCreated();

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getOwner();

	/**
	 * Gets the remote path.
	 *
	 * @return the remote path
	 */
	public String getRemotePath();

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public Long getSize();

	/**
	 * Checks if is current version.
	 *
	 * @return true, if is current version
	 */
	public boolean isCurrentVersion();


}
