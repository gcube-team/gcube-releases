/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync.client;

import org.gcube.portlets.widgets.wsthreddssync.shared.WsFolder;


/**
 * The Class SyncCompletedNotification.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public class SyncCompletedNotification {


	/**
	 * The Interface SyncCompletedNotificationListner.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 13, 2018
	 */
	public interface SyncCompletedNotificationListner{

		/**
		 * On sync completed.
		 *
		 * @param folder the folder
		 */
		void onSyncCompleted(WsFolder folder);

		/**
		 * On sync error.
		 *
		 * @param folder the folder
		 */
		void onSyncError(WsFolder folder);


		/**
		 * On un sync performed.
		 *
		 * @param folder the folder
		 */
		void onUnSyncPerformed(WsFolder folder);
	}


	/**
	 * The Interface HasWsSyncNotificationListner.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Mar 13, 2018
	 */
	public interface HasWsSyncNotificationListner {

		/**
		 * Adds the sync completed listner.
		 *
		 * @param listner the listner
		 */
		public void addSyncCompletedListner(SyncCompletedNotificationListner listner);

	}
}
