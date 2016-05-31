/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.ExecutionEntityUIElement.UIExecutionState;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public interface JobStatusChangeListener {

	public void onJobStatusChanged(JobUIElement job, UIExecutionState newStatus);
}
