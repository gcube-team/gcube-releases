package org.gcube.portlets.user.trendylyzer_portlet.client.species_info;

import org.gcube.portlets.user.trendylyzer_portlet.client.occurences.EventBusProvider;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.ResubmitJobEvent;

import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class InfoFlowPanel extends LayoutContainer {
//	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove Parameters";
//	private Button removeAllButton = new Button(DELETE_ALL_BUTTON_TOOLTIP); 
//	ContentPanel workflowLc = new ContentPanel();
//	
	public InfoFlowPanel() {
		super();
		
		bind();
//		this.setBodyBorder(false);
//		this.computationPanel = new ComputationPanel();

//		workflowLc.setHeaderVisible(false);
//		workflowLc.setScrollMode(Scroll.AUTO);
		
	    
		
	
		setToolsPanel();
		emptyPanel();
	}
	private void emptyPanel() {
		//workflowOperatorPanel = null;
//		this.removeAllButton.setEnabled(false);
//		workflowLc.removeAll();
//		workflowLc.add(new Html("<br><br><p align='center'>Select an operator.</p>"));
//		workflowLc.layout();
		
	}
	private void setToolsPanel() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Label("Tools"));
		
//		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
//		removeAllButton.setIcon(Images.removeAll());
//		removeAllButton.setScale(ButtonScale.MEDIUM);
//		removeAllButton.setEnabled(false);
//		removeAllButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				emptyPanel();
//			}
//		});
//
//		toolBar.add(removeAllButton);
//		workflowLc.setTopComponent(toolBar);
	}
	private void bind() {
		EventBusProvider.getInstance().addHandler(ResubmitJobEvent.getType(), new ResubmitJobEvent.ResubmitJobHandler() {
			public void onResubmitJob(ResubmitJobEvent event) {
			//	setSelection(tabComputationPanel);
			}
		});
	}
}
