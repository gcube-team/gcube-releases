/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.events;

import org.gcube.portlets.user.statisticalmanager.client.bean.JobItem;

/**
 * @author ceras
 *
 */
public interface JobsGridHandler {
	
	public void jobSelected(JobItem jobItem);

	public void removeComputation(JobItem jobItem);
	
}
