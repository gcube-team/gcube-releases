package org.gcube.data.analysis.tabulardata.task.executor.operation.listener;

import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;

public class PostOperationListener implements ExecutionListener {

	private boolean valid = true;
	
	private ExecutionListener parentLisetner;
		
	public PostOperationListener(ExecutionListener parentLisetner) {
		super();
		this.parentLisetner = parentLisetner;
	}

	@Override
	public boolean onStop(OperationContext operationContext) {
		return this.parentLisetner.onStop(operationContext);
	}

	public boolean isValid() {
		return valid;
	}
	
}
