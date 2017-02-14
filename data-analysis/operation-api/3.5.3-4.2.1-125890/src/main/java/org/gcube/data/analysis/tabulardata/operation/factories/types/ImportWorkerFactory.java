package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.VoidScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public abstract class ImportWorkerFactory extends VoidScopedWorkerFactory<DataWorker> {
	
	@Override
	protected OperationType getOperationType() {
		return OperationType.IMPORT;
	}
		
	@Override
	public Class<DataWorker> getWorkerType() {
		return DataWorker.class;
	}
	
}
