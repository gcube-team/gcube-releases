package org.gcube.portlets.user.td.taskswidget.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 12, 2013
 * 
 *      Entry point classes define <code>onModuleLoad()</code>.
 */
public class TdTasksWidgetEntry implements EntryPoint {

	/**
	 * This is the entry point method.
	 */

	// private TdTaskController instance = TdTaskController.getInstance();
	// private Window taskWindow;

	public void onModuleLoad() {

		// // INSTANCE EXAMPLE 1
		// TdTaskController instance = TdTaskController.getInstance();
		// EventBus bus = new SimpleEventBus();
		// instance.bindCommonBus(bus);
		// // instance.getMainPanel(900); //This is main pane
		//
		// RootPanel.get("td-tasks-widget-id").add(instance.getWindowTaskMonitor());
		// instance.go(RootPanel.get("td-tasks-widget-id"));

		
		// INSTANCE EXAMPLE 2
		// Button butt = new Button("Task");
		// / EventBus bus = new SimpleEventBus();
		// instance.bindCommonBus(bus);
		//
		// butt.addSelectionListener(new SelectionListener<ButtonEvent>() {
		//
		// @Override
		// public void componentSelected(ButtonEvent ce) {
		//
		// instance.updateTasks(true);
		// taskWindow.show();
		//
		// }
		// });
		//
		//
		// RootPanel.get("td-tasks-widget-id").add(butt);
	}
}
