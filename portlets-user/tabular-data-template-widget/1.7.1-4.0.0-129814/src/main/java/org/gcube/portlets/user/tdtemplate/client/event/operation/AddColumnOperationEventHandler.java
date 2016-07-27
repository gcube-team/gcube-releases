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
public interface AddColumnOperationEventHandler extends EventHandler{
	

	/**
	 * On add column operation.
	 *
	 * @param addColumnOperationEvent the add column operation event
	 */
	void onAddColumnOperation(AddColumnOperationEvent addColumnOperationEvent);

}
