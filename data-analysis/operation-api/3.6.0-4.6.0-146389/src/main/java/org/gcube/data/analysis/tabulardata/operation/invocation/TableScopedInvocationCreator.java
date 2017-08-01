package org.gcube.data.analysis.tabulardata.operation.invocation;

import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;

public class TableScopedInvocationCreator extends InvocationCreator {
	
	public TableScopedInvocationCreator(OperationDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public OperationInvocation create() {
		if (targetTableId==null) throw new RuntimeException("A target table is required for this descriptor");
		updateReferredTableId();
		return new ImmutableOperationInvocation(targetTableId, null, descriptor, parameters);
	}


}
