/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import com.google.gwt.event.shared.EventHandler;



/**
 * The Interface AddColumnOperationEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 9, 2015
 */
public interface DeleteColumnOperationEventHandler extends EventHandler{

	/**
	 * @param deleteColumnOperationEvent
	 */
	void onDeleteColumnOperation(DeleteColumnOperationEvent deleteColumnOperationEvent);


}
