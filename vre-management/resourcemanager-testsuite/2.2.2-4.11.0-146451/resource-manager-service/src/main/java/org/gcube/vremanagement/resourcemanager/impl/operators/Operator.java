package org.gcube.vremanagement.resourcemanager.impl.operators;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

/**
 * 
 * Base class for manager's operators
 * .
 * A manage operator is devoted to execute certain assigned tasks. 
 * The {@link #exec()} method is asynchronously called
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class Operator {

	protected final GCUBELog logger=new GCUBELog(this.getClass());
	
	protected ACTION action;		
	
	protected OperatorConfig configuration;
	
	protected ScopeState scopeState;
	
	public enum ACTION {ADD,REMOVE}
	
	public final void run() {
		//new Thread() {
		//	 public void run() {
				 try {
					Operator.this.exec();
				} catch (Exception e) {
					logger.error("The operator was unable to manage the request",e);
				}
		//	 }
		//}.start();
		
	}
	
	/**
	 * Executes the operator's tasks
	 * 
	 * @throws Exception if any of the tasks fails
	 */
	public abstract void exec() throws Exception;

}