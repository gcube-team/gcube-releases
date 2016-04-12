/**
 * 
 */
package org.gcube.common.homelibrary.home.task;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface TaskProgress {
	
	/**
	 * @return the task percentage.
	 */
	public float getPercentage();
	
	/**
	 * @return the task progress message.
	 */
	public String getProgressMessage();
	
	/**
	 * @return the task total. 
	 */
	public long getTotal();
	
	/**
	 * @return the task completed.
	 */
	public long getCompleted();

}
