package org.gcube.data.analysis.tabulardata.operation.worker.exceptions;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;

public class InvalidInvocationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6521898195083700337L;

	private OperationInvocation invocation;

	private String reason = null;

	//
	// @Deprecated
	// public InvalidInvocationException(String reason) {
	// super(reason);
	// }

	public InvalidInvocationException(OperationInvocation invocation, String reason) {
		super(String.format("Invalid invocation was provided for operation '%s' , reason: %s", invocation
				.getOperationDescriptor().getName(), reason));
		this.invocation = invocation;
		this.reason = reason;
	}

	public InvalidInvocationException(OperationInvocation invocation, String reason, Exception e) {
		super(String.format("Invalid invocation was provided for operation '%s' , reason: %s", invocation
				.getOperationDescriptor().getName(), reason), e);
		this.invocation = invocation;
		this.reason = reason;
	}

	public InvalidInvocationException(OperationInvocation invocation, Exception e){
		super(String.format("Invocation for operation with name '%s' caused exception: %s", invocation.getOperationDescriptor().getName()
		,e));
		this.invocation = invocation;
		
	}

	// public InvalidInvocationException(OperationInvocation invocation,
	// OperationDescriptor descriptor, String reason) {
	// super(String.format("Invalid invocation was provided for the operation %s, reason: %s",
	// descriptor.getName(),
	// reason));
	// this.invocation = invocation;
	// this.operationDescriptor = descriptor;
	// this.reason = reason;
	// }

	// public InvalidInvocationException(OperationInvocation invocation,
	// OperationDescriptor descriptor) {
	//
	// super(String.format("Invalid invocation was provided for the operation %s:\n%s",
	// descriptor.getName(),
	// invocation));
	// this.invocation = invocation;
	// this.operationDescriptor = descriptor;
	// }

	public OperationInvocation getInvocation() {
		return invocation;
	}

	// public OperationDescriptor getOperationDescriptor() {
	// return operationDescriptor;
	// }

	public String getReason() {
		return reason;
	}

}
