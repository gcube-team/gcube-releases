/**
 * 
 */
package org.gcube.common.homelibrary.home.task;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum TaskState {
	/**
	 * The task is queued.
	 */
	ONQUEUE,
	/**
	 * The task is running.
	 */
	ONGOING,
	/**
	 * The task is terminated.
	 */
	TERMINATED;
}
