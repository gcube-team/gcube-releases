/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces;

import java.util.List;


/**
 * @author Spyros Boutsis, NKUA
 *
 */
public interface JobListUpdatedListener {
	public void onJobsInfoLoaded(List<String> availableJobTypeNames, List<String[]> availableJobNames);
}
