/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsNavigatorListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class BootstrapperViewer extends Composite implements JobsNavigatorListener {

	/** The tree structure holding the available jobs per resource */
    private JobsNavigator jobsTree;
    
    /** The panel where the selected job is visualized */
    private JobVisualization jobViz;
    
    /**
     * Class constructor
     */
	public BootstrapperViewer(IRBootstrapperPortletG portlet) {
		jobsTree = new JobsNavigator(portlet);
		jobsTree.setJobSelectionListener(this);
		jobViz = new JobVisualization(null);
		jobViz.setJobStatusChangeListener(jobsTree);
		
		HorizontalSplitPanel splitter = new HorizontalSplitPanel();
		splitter.setSize("99%", "550px");
		splitter.setSplitPosition("30%");
		splitter.setLeftWidget(jobsTree);
		splitter.setRightWidget(jobViz);
		
		initWidget(splitter);
	}
	
	/**
	 * Initializes the bootstrapper viewer
	 */
	public void initialize() {
		jobsTree.initialize();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobsNavigatorListener#onJobSelected(org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement, org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.interfaces.JobViewMode)
	 */
	public void onJobSelected(JobUIElement job, JobViewMode viewMode) {
		jobViz.setJobToVisualize(job, viewMode);
	}
}
