/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: PersistenceHandler.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.utils.persistence;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public interface PersistenceHandler<T> {
	/**
	 * The code to execute once the persistent object is refreshed.
	 */
	void onRefresh();

	/**
	 * Automatically invoked by the library at instantiation
	 * of persistent items.
	 * Used to make initializations before starting the persistence
	 * manager.
	 */
	void onLoad();

	/**
	 * When the item need no more to be persisted
	 * the refresh loop is closed and this method is
	 * invoked.
	 */
	void onDestroy();

	/**
	 * Requires the destroy of the persistent resource.
	 * The persistent file will be deleted and the onDestroy
	 * event will be raised.
	 */
	void destroy();

	/**
	 * The implementation to retrieve data from the persistence manager.
	 * @return
	 */
	T getData();

	/**
	 * Sets the new data to persist.
	 * @param data
	 */
	void setData(final T data);
}
