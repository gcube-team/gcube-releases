/**
 * 
 */
package org.gcube.portlets.user.gcubegisviewer.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface SaveHandler extends EventHandler {
	
	/**
	 * Called when the save procedure starts.
	 * @param event the {@link SaveEvent} that was fired
	 */
	void onSave(SaveEvent event);
	
	/**
	 * Called when the save procedure is completed successfully.
	 * @param event the {@link SaveEvent} that was fired
	 */
	void onSaveSuccess(SaveEvent event);
	
	/**
	 * Called when the save procedure fails.
	 * @param event the {@link SaveEvent} that was fired
	 */
	void onSaveFailure(SaveEvent event);

}
