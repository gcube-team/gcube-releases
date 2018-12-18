/**
 *
 */
package org.gcube.common.workspacetaskexecutor.shared;


/**
 * The Interface BaseTaskComputation.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 3, 2018
 */
public interface BaseTaskComputation {


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	String getId();

	/**
	 * Gets the start time.
	 *
	 * @return the start time
	 */
	Long getStartTime();


	/**
	 * Gets the end time.
	 *
	 * @return the end time
	 */
	Long getEndTime();
}
