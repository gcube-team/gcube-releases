package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import java.util.ArrayList;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class OpExecutionSpec {
	private OperationExecution op;
	private ArrayList<OperationExecution> ops;

	public OperationExecution getOp() {
		return op;
	}

	public void setOp(OperationExecution op) {
		this.op = op;
	}

	public ArrayList<OperationExecution> getOps() {
		return ops;
	}

	public void setOps(ArrayList<OperationExecution> ops) {
		this.ops = ops;
	}

}
