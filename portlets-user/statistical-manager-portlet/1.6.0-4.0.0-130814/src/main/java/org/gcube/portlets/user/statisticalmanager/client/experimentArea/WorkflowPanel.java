/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.events.ResubmitJobEvent;
import org.gcube.portlets.user.statisticalmanager.client.experimentArea.WorkflowOperatorPanel.WorkflowOperatorPanelHandler;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public class WorkflowPanel extends TabPanel {
	
	public static final String DEFAULT_OPERATOR = "AQUAMAPS_SUITABLE";
	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove All Operators";
	private static final boolean WORKFLOW_MODE = false;
	private WorkflowOperatorPanel workflowOperatorPanel;
	private Button removeAllButton = new Button(DELETE_ALL_BUTTON_TOOLTIP); 
	private ComputationPanel computationPanel;
	private TabItem tabWorkFlowLc, tabComputationPanel;
	
	ContentPanel workflowLc = new ContentPanel();

	/**
	 * 
	 */
	public WorkflowPanel() {
		super();
		
		bind();
		this.setBodyBorder(false);
		this.computationPanel = new ComputationPanel();

		workflowLc.setHeaderVisible(false);
		workflowLc.setScrollMode(Scroll.AUTO);
		
	    tabWorkFlowLc = new TabItem(".: Computation");
	    tabWorkFlowLc.setBorders(false);
	    tabWorkFlowLc.setLayout(new FitLayout());
	    tabWorkFlowLc.setIcon(Images.folderExplore());  
	    tabWorkFlowLc.addStyleName("pad-text");
	    tabWorkFlowLc.add(workflowLc);
	    this.add(tabWorkFlowLc);
		
	    tabComputationPanel = new TabItem(".: Computations Execution");  
	    tabComputationPanel.setLayout(new FitLayout());
	    tabComputationPanel.setIcon(Images.folderExplore());  
	    tabComputationPanel.addStyleName("pad-text");
	    tabComputationPanel.setScrollMode(Scroll.AUTO);
	    tabComputationPanel.add(computationPanel);
	    this.add(tabComputationPanel);
		
		setToolsPanel();
		emptyPanel();
	}
	
	/**
	 * 
	 */
	private void bind() {
		EventBusProvider.getInstance().addHandler(ResubmitJobEvent.getType(), new ResubmitJobEvent.ResubmitJobHandler() {
			@Override
			public void onResubmitJob(ResubmitJobEvent event) {
				setSelection(tabComputationPanel);
			}
		});
	}

	/**
	 * 
	 */
	private void emptyPanel() {
		workflowOperatorPanel = null;
		this.removeAllButton.setEnabled(false);
		workflowLc.removeAll();
		workflowLc.add(new Html("<br><br><br><br><br><br><br><br><br><br><br><p align='center'>Select an operator.</p>"));
		workflowLc.layout();
		
	}
	
	private void setToolsPanel() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Label("Tools"));
		
		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setIcon(Images.removeAll());
		removeAllButton.setScale(ButtonScale.MEDIUM);
		removeAllButton.setEnabled(false);
		removeAllButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				emptyPanel();
			}
		});

		toolBar.add(removeAllButton);
		workflowLc.setTopComponent(toolBar);
	}

	/**
	 * @param computationTitle 
	 * @param operator 
	 * 
	 */
	protected void startComputation(String computationTitle, String computationDescription) {

//		if (operatorPanels!=null && operatorPanels.size()==1) {
		if (workflowOperatorPanel!=null) {	
//			this.add(computationPanel, northPanelData);
			this.layout();
			
//			WorkflowOperatorPanel workflowOperatorPanel = operatorPanels.get(0);
			workflowOperatorPanel.updateOperatorParametersValues();
			Operator op = workflowOperatorPanel.getOperator();
			
			computationPanel.startNewComputation(op, computationTitle, computationDescription);
		}
	}

	public void addOperator(Operator op) {
		
		if (!WORKFLOW_MODE)
			workflowOperatorPanel = null;
		
		this.setSelection(tabWorkFlowLc);
		
		if (workflowOperatorPanel==null) {
			workflowLc.removeAll();
			workflowLc.add(new Html("&nbsp;"));
			workflowLc.layout();
		}

		workflowOperatorPanel = new WorkflowOperatorPanel(op);
		workflowOperatorPanel.setHandler(new WorkflowOperatorPanelHandler() {
			@Override
			public void startComputation(String computationTitle, String computationDescription) {
				WorkflowPanel.this.startComputation(computationTitle, computationDescription);
				WorkflowPanel.this.setSelection(tabComputationPanel);
			}
		});
		
		this.addConnector(true);
		
		workflowLc.add(workflowOperatorPanel); // operator
		workflowLc.layout();
		
		removeAllButton.setEnabled(true);
	}

	/**
	 * @param b
	 */
	private void addConnector(boolean first) {
		LayoutContainer lc = new LayoutContainer(new CenterLayout());
		lc.setHeight(first ? 26 : 22);
		Image connector=new Image(
				first ? StatisticalManager.resources.workflowConnector1() : StatisticalManager.resources.workflowConnector2()
			);
		connector.setStyleName("workflowConnector");
		lc.add(connector);
		workflowLc.add(lc);
	}
}
