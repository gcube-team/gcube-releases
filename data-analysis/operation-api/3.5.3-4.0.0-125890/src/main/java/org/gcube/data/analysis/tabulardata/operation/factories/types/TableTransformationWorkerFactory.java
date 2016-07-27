package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public abstract class TableTransformationWorkerFactory extends TableScopedWorkerFactory<DataWorker> {
	
	@Override
	protected OperationType getOperationType() {
	 return OperationType.TRANSFORMATION;
	}
	
	@Override
	public Class<DataWorker> getWorkerType() {
		return DataWorker.class;
	}
}
