package org.gcube.data.analysis.tabulardata.operation.factories.scopes;

import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.TableType;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.factories.types.BaseWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;

public abstract class TableScopedWorkerFactory<T extends Worker<?>> extends BaseWorkerFactory<T> {

	public List<TableType> getAllowedTableTypes(){
		return Collections.emptyList();
	}
	
	@Override
	protected OperationScope getOperationScope() {
		return OperationScope.TABLE;
	}
	
}
