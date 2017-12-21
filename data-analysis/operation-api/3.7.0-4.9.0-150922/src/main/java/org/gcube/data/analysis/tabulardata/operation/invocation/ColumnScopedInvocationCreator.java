package org.gcube.data.analysis.tabulardata.operation.invocation;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;

public class ColumnScopedInvocationCreator extends InvocationCreator {
	

	public ColumnScopedInvocationCreator(OperationDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public OperationInvocation create() {
		if (targetTableId==null) throw new RuntimeException("A target table is required for this descriptor");
		if (targetColumnId==null) throw new RuntimeException("A target column is required for this descriptor");
		updateReferredTableId();
		return new ImmutableOperationInvocation(targetTableId, targetColumnId, descriptor, parameters);
	}
	
}
