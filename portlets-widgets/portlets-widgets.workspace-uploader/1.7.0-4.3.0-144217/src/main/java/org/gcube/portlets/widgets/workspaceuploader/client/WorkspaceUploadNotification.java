/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client;

/**
 * The Class WorkspaceUploadNotification.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Oct 2, 2015
 */
public class WorkspaceUploadNotification {

	/**
	 * The listener interface for receiving worskpaceUploadNotification events.
	 * The class that is interested in processing a worskpaceUploadNotification
	 * event implements this interface, and the object created with that class
	 * is registered with a component using the component's
	 * <code>addWorskpaceUploadNotificationListener<code> method. When
	 * the worskpaceUploadNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see WorskpaceUploadNotificationEvent
	 */
	public interface WorskpaceUploadNotificationListener {

		/**
		 * On upload completed.
		 *
		 * @param parentId
		 *            the parent id
		 * @param itemId
		 *            the item id
		 */
		void onUploadCompleted(String parentId, String itemId);

		/**
		 * On upload aborted.
		 *
		 * @param parentId
		 *            the parent id
		 * @param itemId
		 *            the item id
		 */
		void onUploadAborted(String parentId, String itemId);

		/**
		 * On error.
		 *
		 * @param parentId
		 *            the parent id
		 * @param itemId
		 *            the item id
		 * @param throwable
		 *            the throwable
		 */
		void onError(String parentId, String itemId, Throwable throwable);

		/**
		 * On overwrite completed.
		 *
		 * @param parentId the parent id
		 * @param itemId the item id
		 */
		void onOverwriteCompleted(String parentId, String itemId);
	}

	/**
	 * The listener interface for receiving hasWorskpaceUploadNotification
	 * events. The class that is interested in processing a
	 * hasWorskpaceUploadNotification event implements this interface, and the
	 * object created with that class is registered with a component using the
	 * component's
	 * <code>addHasWorskpaceUploadNotificationListener<code> method. When
	 * the hasWorskpaceUploadNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see HasWorskpaceUploadNotificationEvent
	 */
	public interface HasWorskpaceUploadNotificationListener {

		/**
		 * Adds the workspace upload notification listener.
		 *
		 * @param handler
		 *            the handler
		 */
		public void addWorkspaceUploadNotificationListener(
				WorskpaceUploadNotificationListener handler);

		/**
		 * Removes the workspace upload notification listener.
		 *
		 * @param handler
		 *            the handler
		 */
		public void removeWorkspaceUploadNotificationListener(
				WorskpaceUploadNotificationListener handler);

	}

}