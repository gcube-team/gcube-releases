package org.gcube.portlets.user.tdwx.datasource.td.opexecution;

import org.gcube.portlets.user.tdwx.datasource.td.exception.OperationException;

/**
 * Abstract class for build Operation Execution
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
abstract class OpExecutionBuilder {
	protected OpExecutionSpec operationExecutionSpec;
	
	public OpExecutionSpec getOperationExecutionSpec(){
		return operationExecutionSpec;
	}
	public void createSpec(){
		operationExecutionSpec=new OpExecutionSpec();
		
	}
	
	public abstract void buildOpEx() throws OperationException;
	    
	
}
