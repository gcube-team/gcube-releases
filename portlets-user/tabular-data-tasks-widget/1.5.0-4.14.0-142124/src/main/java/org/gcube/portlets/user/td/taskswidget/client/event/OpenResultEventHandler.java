/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 29, 2013
 *
 */
public interface OpenResultEventHandler extends EventHandler {
	/**
	 * @param openResultEvent
	 */
	public void onResultOpenSelect(OpenResultEvent openResultEvent);

}
