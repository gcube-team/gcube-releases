/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface AggregateByTimeOperationEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 25, 2015
 */
public interface UndoLastOperationEventHandler extends EventHandler{
	
	/**
	 * @param undoLastOperationEvent
	 */
	void onUndoLastOperation(UndoLastOperationEvent undoLastOperationEvent);

}
