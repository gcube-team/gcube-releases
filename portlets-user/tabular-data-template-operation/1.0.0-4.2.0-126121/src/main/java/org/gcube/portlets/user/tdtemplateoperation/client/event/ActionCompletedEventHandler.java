/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public interface ActionCompletedEventHandler extends EventHandler{
	
	/**
	 * @param actionCompletedEvent
	 */
	void onActionCompleted(ActionCompletedEvent actionCompletedEvent);

}
