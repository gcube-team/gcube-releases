package org.gcube.data.analysis.tabulardata.operation.worker.exceptions;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;

public class InvalidParameterException extends InvalidInvocationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8254370871155651148L;

//	public InvalidParameterException(String parameterId) {
//		super(String.format("Parameter with id '%s' is missing or invalid",parameterId));
//	}
	
	public InvalidParameterException(OperationInvocation invocation, String parameterId ){
		super(invocation, String.format("Parameter with id '%s' is missing or invalid",parameterId));
	}

}
