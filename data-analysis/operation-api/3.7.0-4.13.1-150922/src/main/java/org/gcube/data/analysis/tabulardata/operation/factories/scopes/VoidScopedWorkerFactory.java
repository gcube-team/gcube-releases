package org.gcube.data.analysis.tabulardata.operation.factories.scopes;

import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.factories.types.BaseWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;

public abstract class VoidScopedWorkerFactory<T extends Worker<?>> extends BaseWorkerFactory<T> {
	
	@Override
	protected OperationScope getOperationScope() {
		return OperationScope.VOID;
	}

}
