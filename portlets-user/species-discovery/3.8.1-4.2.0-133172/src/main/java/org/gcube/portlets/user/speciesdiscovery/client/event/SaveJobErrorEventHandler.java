/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface SaveJobErrorEventHandler extends EventHandler {
	/**
	 * @param saveErrorJobEvent
	 */
	public void onSaveJobError(SaveJobErrorEvent saveErrorJobEvent);

}
