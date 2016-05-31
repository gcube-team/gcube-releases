/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.results;


/**
 * @author ceras
 *
 */
public interface JobsGridHandler {
	
	public void jobSelected(JobItem jobItem);

	public void removeComputation(JobItem jobItem);
	
}
