/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public interface JobsDesignerNavigatorListener {

	public boolean beforeJobSelected(JobUIElement job);
	public void onJobSelected(JobUIElement job);
}
