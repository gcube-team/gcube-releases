/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.client;

import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;



/**
 * The Class TaskCompletedNotification.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2018
 */
public class TaskCompletedNotification {


	/**
	 * The Interface TaskCompletedNotificationListner.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * May 4, 2018
	 */
	public interface TaskCompletedNotificationListner{


		/**
		 * On task computation completed.
		 *
		 * @param folder the folder
		 */
		void onTaskComputationCompleted(WSItem folder);

		/**
		 * On task computation error.
		 *
		 * @param folder the folder
		 */
		void onTaskComputationError(WSItem folder);

		/**
		 * On remove task configurations performed.
		 *
		 * @param folder the folder
		 */
		void onRemoveTaskConfigurationsPerformed(WSItem folder);
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
		public void addTaskCompletedListner(TaskCompletedNotificationListner listner);

	}
}
