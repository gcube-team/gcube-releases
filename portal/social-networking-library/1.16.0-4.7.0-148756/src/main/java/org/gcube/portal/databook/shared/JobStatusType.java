package org.gcube.portal.databook.shared;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Dec 2012
 *
 */
public enum JobStatusType {
	/**
	 * The job has been cancelled.
	 */
	CANCELLED,
	/**
	 *  The job is in the process of being cancelled.
	 */
	CANCELLING,
	/**
	 * The job has been deleted.
	 */
	DELETED,
	/**
	 * The job is in the process of being deleted.
	 */
	DELETING,//	 
	/**
	 * The job is being executed by job processor.
	 */
	EXECUTING,//	 
	/**
	 * he job execution has failed.
	 */
	FAILED,
	/**
	 * The job is new.
	 */
	NEW,//	
	/**
	 * The job is submitted for execution.
	 */
	SUBMITTED,
	/**
	 *  The job has completed successfully
	 */
	SUCCEEDED,
	/**
	 * The job execution has timed out.
	 */
	TIMED_OUT,
	/**
	 *  The job is waiting for available job processor.
	 */
	WAITING
}
