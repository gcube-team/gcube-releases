/**
 *
 */
package org.gcube.common.storagehubwrapper.shared.tohl;

import java.util.Calendar;

/**
 * The Interface AccountingEntry.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jun 15, 2018
 */
public interface AccountingEntry {

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	String getUser();

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	Calendar getDate();

	/**
	 * Gets the entry type.
	 *
	 * @return the entry type
	 */
	String getEntryType();

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	String getVersion();
}
