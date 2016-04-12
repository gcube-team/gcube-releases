/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class BootstrapperDesigner extends Composite {
	
	/** The tree structure holding the available jobs per resource */
    private DesignerNavigator jobsTree;
    
    /** The panel that contains the job editor */
    private DesignerJobEditor jobEditor;
    
	/**
     * Class constructor
     */
	public BootstrapperDesigner(IRBootstrapperPortletG portlet) {
		jobEditor = new DesignerJobEditor(portlet);
		jobsTree = new DesignerNavigator(portlet);
		jobsTree.setJobSelectionListener(jobEditor);
		jobsTree.setJobListUpdatedListener(jobEditor);
		jobEditor.registerJobAttributesChangeListener(jobsTree);
	
		HorizontalSplitPanel splitter = new HorizontalSplitPanel();
		splitter.setSize("99%", "550px");
		splitter.setSplitPosition("30%");
		splitter.setLeftWidget(jobsTree);
		splitter.setRightWidget(jobEditor);
		
		initWidget(splitter);
	}
	
	/**
	 * Initializes the bootstrapper viewer
	 */
	public void initialize() {
		jobsTree.initialize();
	}
	
	/**
	 * Returns the job editor panel
	 * @return
	 */
	public DesignerJobEditor getJobEditor() {
		return jobEditor;
	}
}
