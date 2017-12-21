package org.gcube.portlets.user.td.gwtservice.server.opexecution;

import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;

/**
 * Abstract class for build Operation Execution
 * 
 * @author Giancarlo Panichi
 * 
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
	
	public abstract void buildOpEx() throws TDGWTServiceException;
	    
	
}
