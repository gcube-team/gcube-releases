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
	STATUS_CANCELLED,
	/**
	 *  The job is in the process of being cancelled.
	 */
	STATUS_CANCELLING,
	/**
	 * The job has been deleted.
	 */
	STATUS_DELETED,
	/**
	 * The job is in the process of being deleted.
	 */
	STATUS_DELETING,//	 
	/**
	 * The job is being executed by job processor.
	 */
	STATUS_EXECUTING,//	 
	/**
	 * he job execution has failed.
	 */
	STATUS_FAILED,
	/**
	 * The job is new.
	 */
	STATUS_NEW,//	
	/**
	 * The job is submitted for execution.
	 */
	STATUS_SUBMITTED,
	/**
	 *  The job has completed successfully
	 */
	STATUS_SUCCEEDED,
	/**
	 * The job execution has timed out.
	 */
	STATUS_TIMED_OUT,
	/**
	 *  The job is waiting for available job processor.
	 */
	STATUS_WAITING
}
