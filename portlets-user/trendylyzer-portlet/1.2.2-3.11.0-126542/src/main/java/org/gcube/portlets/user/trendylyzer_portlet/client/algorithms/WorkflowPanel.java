/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.WorkflowAlgorithmPanel.WorkflowAlgorithmPanelHandler;
import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.ComputationPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.EventBusProvider;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.ResubmitJobEvent;


import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Image;


public class WorkflowPanel extends TabPanel {
	
	public static final String DEFAULT_OPERATOR = "";
	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove Parameters";
	private static final boolean WORKFLOW_MODE = false;
	private WorkflowAlgorithmPanel workflowOperatorPanel;
	private Button removeAllButton = new Button(DELETE_ALL_BUTTON_TOOLTIP); 
	private ComputationPanel computationPanel;
	private TabItem tabWorkFlowLc, tabComputationPanel;
	
	ContentPanel workflowLc = new ContentPanel();

	/**
	 * 
	 */
	public WorkflowPanel() {
		super();
		this.setWidth("1300px");
		bind();
		this.setBodyBorder(false);
		this.computationPanel = new ComputationPanel();

		workflowLc.setHeaderVisible(false);
		workflowLc.setScrollMode(Scroll.AUTO);
		
	    tabWorkFlowLc = new TabItem("Algorithm Parameters");
	    tabWorkFlowLc.setBorders(false);
	    tabWorkFlowLc.setLayout(new FitLayout());
	    tabWorkFlowLc.setIcon(Images.folderExplore());  
	    tabWorkFlowLc.addStyleName("pad-text");
	    tabWorkFlowLc.add(workflowLc);
	    this.add(tabWorkFlowLc);
		
	    tabComputationPanel = new TabItem("Results");  
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
		//workflowLc.add(new Html("<br><p align='center'>Select an operator.</p>"));
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

		if (workflowOperatorPanel!=null) {	
			this.layout();
			
			workflowOperatorPanel.updateOperatorParametersValues();
			Algorithm op = workflowOperatorPanel.getOperator();
			
			computationPanel.startNewComputation(op, computationTitle, computationDescription);
		}
	}

	public void addAlgorithm(Algorithm op) {
		
		if (!WORKFLOW_MODE)
			workflowOperatorPanel = null;
		
		this.setSelection(tabWorkFlowLc);
		
		if (workflowOperatorPanel==null) {
			workflowLc.removeAll();
			workflowLc.add(new Html("&nbsp;"));
			workflowLc.layout();
		}

		workflowOperatorPanel = new WorkflowAlgorithmPanel(op);
		workflowOperatorPanel.setHandler(new WorkflowAlgorithmPanelHandler() {
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
		lc.add(new Image(
				first ? TrendyLyzer_portlet.resources.workflowConnector1() : TrendyLyzer_portlet.resources.workflowConnector2()
			));
		workflowLc.add(lc);
	}
}
