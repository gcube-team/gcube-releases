package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;

/**
 * 
 */


import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.Services;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public class WorkflowPanel extends ContentPanel {
	
	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove All Operators";
	private static final boolean WORKFLOW_MODE = false;
	private WorkflowOperatorPanel workflowOperatorPanel;
	private Button removeAllButton = new Button(DELETE_ALL_BUTTON_TOOLTIP); 
	protected  Logger logger = Logger.getLogger("logger");

//	ContentPanel workflowLc = new ContentPanel();

	/**
	 * 
	 */
	public WorkflowPanel() {
		super();
		

		setHeaderVisible(false);
		setScrollMode(Scroll.AUTO);
		
		
		setBorders(false);
		setLayout(new AnchorLayout());
		setIcon(Images.folderExplore());  
		addStyleName("pad-text");
//		workflowLc.add(workflowLc);
//		this.add(workflowLc);
		
		setToolsPanel();
		emptyPanel();
	}
	

	/**
	 * 
	 */
	private void emptyPanel() {
		workflowOperatorPanel = null;
		this.removeAllButton.setEnabled(false);
		this.removeAll();
		this.add(new HTMLPanel("<p align='center'>Select an operator.</p>"));
		this.layout();
		
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
//		workflowLc.setTopComponent(toolBar);
	}

	/**
	 * @param computationTitle 
	 * @param operator 
	 * 
	 */

	public void addOperator(Operator op) {
		
		if (!WORKFLOW_MODE){
			this.removeAll();
//			this.add(new Html("&nbsp;"));
			this.layout();
		}
		
//		this.setSelection(tabWorkFlowLc);
	

		logger.log(Level.SEVERE,"Create workflowOpeartorPanel");

		workflowOperatorPanel = new WorkflowOperatorPanel(op);
		
		this.addConnector(true);
		
		this.add(workflowOperatorPanel); // operator
		logger.log(Level.SEVERE,"add to workflopanel");

		this.layout();
		
		removeAllButton.setEnabled(true);
		workflowOperatorPanel.setVisible(true);
	}

	/**
	 * @param b
	 */
	private void addConnector(boolean first) {
		LayoutContainer lc = new LayoutContainer(new CenterLayout());
		lc.setHeight(first ? 26 : 22);
		lc.add(new Image(
				first ? Services.getResources().workflowConnector1() : Services.getResources().workflowConnector2()
			));
		this.add(lc);
	}
}
