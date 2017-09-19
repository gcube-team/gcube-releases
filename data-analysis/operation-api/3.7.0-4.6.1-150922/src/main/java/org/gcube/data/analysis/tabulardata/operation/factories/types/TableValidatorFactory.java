package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public abstract class TableValidatorFactory extends TableScopedWorkerFactory<ValidationWorker> {

	@Override
	protected OperationType getOperationType() {
		return OperationType.VALIDATION;
	}

	@Override
	public Class<ValidationWorker> getWorkerType() {
		return ValidationWorker.class;
	}
	
}
