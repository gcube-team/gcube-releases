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
 * Filename: TaskbarRegister.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar;

import java.util.HashMap;

import org.gcube.portlets.admin.resourcemanagement.client.widgets.console.ConsoleMessageBroker;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;

import com.google.gwt.core.client.GWT;

/**
 * Manages the widgets registered in the taskbar.
 * Useful to implement the "close all" and to avoid
 * duplicates.
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class TaskbarRegister {
	final static HashMap<String, TaskbarItem> registeredItems = new HashMap<String, TaskbarItem>();
	static TaskbarWindow lastOpenedWindow = null;
	
	public static int getCurrAddScopeReportsNumber() {
		int count = 0;
		for (String item : registeredItems.keySet()) {
			GWT.log(item);
			if (registeredItems.get(item).getType() == ResourceTypeDecorator.AddScopeReport )
					count++;
		}
		return count;	
	}
	
	public static synchronized void setLastOpenedWindow(TaskbarWindow lastOpenedWindow) {
		TaskbarRegister.lastOpenedWindow = lastOpenedWindow;
	}
	
	public static synchronized TaskbarWindow getLastOpenedWindow() {
		return TaskbarRegister.lastOpenedWindow;
	}
	
	public static synchronized void registerTaskbarWidget(final String id, final TaskbarItem item) {
		ConsoleMessageBroker.info(TaskbarRegister.class, "Registering res: " + id);
		registeredItems.put(id, item);
	}

	public static synchronized void unregisterTaskbarWidget(final String id) {
		ConsoleMessageBroker.info(TaskbarRegister.class, "Unregistering res: " + id);
		registeredItems.remove(id);
	}

	public static synchronized TaskbarItem getTaskbarItem(final String id) {
		if (registeredItems.containsKey(id)) {
			return registeredItems.get(id);
		}
		return null;
	}

	public static synchronized boolean contains(final String key) {
		return registeredItems.containsKey(key);
	}
	
	public static synchronized void minimizeAll() {
		Object[] items = registeredItems.values().toArray();
		ConsoleMessageBroker.trace(TaskbarRegister.class, "Elems to remove: " + items.length);
		TaskbarItem curItem = null;
		for (Object elem : items){
			try {
				if (elem != null) {
					curItem = ((TaskbarItem)elem);
					if (!curItem.getRelatedWindow().isMinimized()) {
						curItem.getRelatedWindow().doMinimize();
					}
				}

			} catch (Exception e) {
				GWT.log("During remove all profiles", e);
				ConsoleMessageBroker.error(TaskbarRegister.class, e.getMessage());
			}
		}
		ConsoleMessageBroker.info(TaskbarRegister.class, "Unregistering all resources");
	}

	public static synchronized void closeAll() {
		Object[] items = registeredItems.values().toArray();
		ConsoleMessageBroker.trace(TaskbarRegister.class, "Elems to remove: " + items.length);
		for (Object elem : items){
			try {
				if (elem != null) {
					((TaskbarItem)elem).getRelatedWindow().doClose();
				}

			} catch (Exception e) {
				GWT.log("During remove all profiles", e);
				ConsoleMessageBroker.error(TaskbarRegister.class, e.getMessage());
			}
		}
		ConsoleMessageBroker.info(TaskbarRegister.class, "Unregistering all resources");
		registeredItems.clear();
	}
}
