package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;

public abstract class TableMetadataWorkerFactory extends TableScopedWorkerFactory<MetadataWorker> {

	@Override
	protected OperationType getOperationType() {
		return OperationType.METADATA;
	}

	@Override
	public Class<MetadataWorker> getWorkerType() {
		return MetadataWorker.class;
	}
}
