package org.gcube.vremanagement.executor.plugin;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public enum PluginState {
	/**
	 * The Task is created but not still running
	 */
	CREATED(false), 
	/**
	 * The Task is running
	 */
	RUNNING(false), 
	/**
	 * The Task has been stopped
	 */
	STOPPED(true), 
	/**
	 * The Task terminated successfully
	 */
	DONE(true), 
	/**
	 * The Task failed the execution
	 */
	FAILED(true),
	/**
	 * The Task has been discarded by the scheduler. This happen only for 
	 * repetitive or recurrent tasks and only when the launch parameter require 
	 * that the previous task must be completed. 
	 */
	DISCARDED(true);
	
	boolean finalState;
	
	PluginState(boolean finalState){
		this.finalState = finalState;
	}
	
	/**
	 * Return true when the state a is a final state and the job cannot move
	 * in any other state
	 * @return if is a Final State
	 */
	public boolean isFinalState() {
		return finalState;
	}
	
	@Override
	public String toString(){
		return this.name();
	}
	
}
