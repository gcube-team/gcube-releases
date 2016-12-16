/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.client.notification;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;


/**
 * The Class WorskpaceExplorerNotification.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 29, 2015
 */
public class WorkspaceExplorerSaveNotification {

	/**
	 * The listener interface for receiving worskpaceExplorerSaveNotification events.
	 * The class that is interested in processing a worskpaceExplorerSaveNotification
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addWorskpaceExplorerSaveNotificationListener<code> method. When
	 * the worskpaceExplorerSaveNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see WorskpaceExplorerSaveNotificationEvent
	 */
	public interface WorskpaceExplorerSaveNotificationListener {

		/**
		 * On saving.
		 *
		 * @param parent the parent
		 * @param fileName the file name
		 */
		void onSaving(Item parent,String fileName);

		/**
		 * On aborted.
		 */
		void onAborted();

	
		/**
		 * On failed.
		 *
		 * @param throwable the throwable
		 */
		void onFailed(Throwable throwable);
	}




	/**
	 * The listener interface for receiving hasWorskpaceExplorerSaveNotification events.
	 * The class that is interested in processing a hasWorskpaceExplorerSaveNotification
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addHasWorskpaceExplorerSaveNotificationListener<code> method. When
	 * the hasWorskpaceExplorerSaveNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see HasWorskpaceExplorerSaveNotificationEvent
	 */
	public interface HasWorskpaceExplorerSaveNotificationListener {

		
		/**
		 * Adds the workspace explorer save notification listener.
		 *
		 * @param handler the handler
		 */
		public void addWorkspaceExplorerSaveNotificationListener(WorskpaceExplorerSaveNotificationListener handler);

		
		/**
		 * Removes the workspace explorer save notification listener.
		 *
		 * @param handler the handler
		 */
		public void removeWorkspaceExplorerSaveNotificationListener(WorskpaceExplorerSaveNotificationListener handler);

	}
	
}