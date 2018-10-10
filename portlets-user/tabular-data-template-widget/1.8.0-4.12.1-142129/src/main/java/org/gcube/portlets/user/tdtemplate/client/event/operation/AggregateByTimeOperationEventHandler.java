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
public interface AggregateByTimeOperationEventHandler extends EventHandler{
	

	/**
	 * On aggregate by time op.
	 *
	 * @param aggregateByTimeOperationEvent the aggregate by time operation event
	 */
	void onAggregateByTimeOp(AggregateByTimeOperationEvent aggregateByTimeOperationEvent);

}
