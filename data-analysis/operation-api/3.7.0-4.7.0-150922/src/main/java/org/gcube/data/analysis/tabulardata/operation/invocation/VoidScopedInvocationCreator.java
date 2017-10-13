package org.gcube.data.analysis.tabulardata.operation.invocation;

import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;

public class VoidScopedInvocationCreator extends InvocationCreator {

	public VoidScopedInvocationCreator(OperationDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public OperationInvocation create() {
		updateReferredTableId();
		return new ImmutableOperationInvocation(null, null, descriptor, parameters);
	}

}
