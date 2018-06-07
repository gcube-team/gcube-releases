package org.gcube.data.analysis.tabulardata.task.executor.operation.listener;

import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;


public interface ExecutionListener {

	
	/**
	 * called when validation are failed, return true if the execution has to continue false otherwise
	 * 
	 * 
	 * @param operationContext operation context
	 * @return boolan
	 */
	boolean onStop(OperationContext operationContext);
}
