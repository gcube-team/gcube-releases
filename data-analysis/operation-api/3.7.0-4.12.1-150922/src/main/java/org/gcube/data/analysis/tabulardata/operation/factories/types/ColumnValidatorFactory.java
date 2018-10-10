package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.ColumnScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public abstract class ColumnValidatorFactory extends ColumnScopedWorkerFactory<ValidationWorker> {

	@Override
	protected OperationType getOperationType() {
		return OperationType.VALIDATION;
	}

	@Override
	public Class<ValidationWorker> getWorkerType() {
		return ValidationWorker.class;
	}
	
}

