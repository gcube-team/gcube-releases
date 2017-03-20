package gr.uoa.di.madgik.execution.engine.queue;

import gr.uoa.di.madgik.execution.engine.ExecutionHandle;

/**
 * A queue element used to store a plan before it is executed. Plans are
 * evaluated for requested resources and if not qualified to start executing
 * they miss their turn and following plans are evaluated. Every queue element
 * has a threshold of required utilization percentage and the number of
 * allowed times it can miss turn.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class PlanQueueElement {
	/** Percentage of required utilization of the execution job */
	private float util;
	
	/** Maximum number of missed turns */
	private int ttl;

	/** Handler of execution plan */
	private ExecutionHandle Handle;

	/**
	 * Default constructor.
	 * 
	 * @param util
	 *            Number of maximum concurrent execution jobs
	 * @param ttl
	 *            Maximum number of missed turns
	 * @param handle
	 *            Handler of execution plan
	 */
	public PlanQueueElement(float util, int ttl, ExecutionHandle handle) {
		this.util = util;
		this.ttl = ttl;
		Handle = handle;
	}

	/**
	 * @return the util
	 */
	public float getUtil() {
		return util;
	}

	/**
	 * @return the ttl
	 */
	public int getTtl() {
		return ttl;
	}

	/**
	 * Decrease ttl value by one if it is positive
	 * 
	 * @return true if decreased otherwise false
	 */
	public boolean decreaseTtl() {
		if (ttl > 0) {
			ttl--;
			return true;
		}
		return false;
	}

	/**
	 * Decrease ttl value by one if it is positive
	 * 
	 * @return true if decreased otherwise false
	 */
	public boolean canGetDecreaseTtl() {
		if (ttl > 0)
			return true;
		return false;
	}

	/**
	 * @return the handle
	 */
	public ExecutionHandle getHandle() {
		return Handle;
	}
}
