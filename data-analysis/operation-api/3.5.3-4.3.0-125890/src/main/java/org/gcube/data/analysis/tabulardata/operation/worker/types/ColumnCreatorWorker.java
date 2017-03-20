package org.gcube.data.analysis.tabulardata.operation.worker.types;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;

public abstract class ColumnCreatorWorker extends DataWorker {

	public ColumnCreatorWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}

	public abstract List<ColumnLocalId> getCreatedColumns();

}
