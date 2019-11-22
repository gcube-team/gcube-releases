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
public class WorkspaceExplorerSelectNotification {



	/**
	 * The listener interface for receiving worskpaceExplorerSelectNotification events.
	 * The class that is interested in processing a worskpaceExplorerSelectNotification
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addWorskpaceExplorerSelectNotificationListener<code> method. When
	 * the worskpaceExplorerSelectNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see WorskpaceExplorerSelectNotificationEvent
	 */
	public interface WorskpaceExplorerSelectNotificationListener {
		
		/**
		 * On selected item.
		 *
		 * @param item the item
		 */
		void onSelectedItem(Item item);

		
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
		
		
		/**
		 * On not valid selection.
		 */
		void onNotValidSelection();
	}



	/**
	 * The listener interface for receiving hasWorskpaceExplorerSelectNotification events.
	 * The class that is interested in processing a hasWorskpaceExplorerSelectNotification
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addHasWorskpaceExplorerSelectNotificationListener<code> method. When
	 * the hasWorskpaceExplorerSelectNotification event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see HasWorskpaceExplorerSelectNotificationEvent
	 */
	public interface HasWorskpaceExplorerSelectNotificationListener {

		
		/**
		 * Adds the workspace explorer select notification listener.
		 *
		 * @param handler the handler
		 */
		public void addWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectNotificationListener handler);

		
		
		/**
		 * Removes the workspace explorer select notification listener.
		 *
		 * @param handler the handler
		 */
		public void removeWorkspaceExplorerSelectNotificationListener(WorskpaceExplorerSelectNotificationListener handler);

	}
	
}