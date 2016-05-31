/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public interface JobAttributesChangeListener {

	public void jobNameChanged(JobUIElement job, String oldName, String newName);
	public void jobTypeChanged(JobUIElement job, String oldType, String newType);
	public void newJobCreated(JobUIElement job);
}
