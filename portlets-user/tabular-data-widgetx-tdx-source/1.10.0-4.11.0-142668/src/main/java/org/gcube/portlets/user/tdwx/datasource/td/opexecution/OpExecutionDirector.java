package org.gcube.portlets.user.tdwx.datasource.td.opexecution;

import java.util.ArrayList;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.portlets.user.tdwx.datasource.td.exception.OperationException;


/**
 * Operation Execution Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OpExecutionDirector {
	OpExecutionBuilder operationExecutionBuilder;

	public void setOperationExecutionBuilder(
			OpExecutionBuilder operationExecutionBuilder) {
		this.operationExecutionBuilder = operationExecutionBuilder;
	}

	public OperationExecution getOperationExecution() {
		return operationExecutionBuilder.getOperationExecutionSpec().getOp();

	}
	
	public ArrayList<OperationExecution> getListOperationExecution() {
		return operationExecutionBuilder.getOperationExecutionSpec().getOps();

	}
	
	public void constructOperationExecution() throws OperationException {
		operationExecutionBuilder.createSpec();
		operationExecutionBuilder.buildOpEx();

	}
}
