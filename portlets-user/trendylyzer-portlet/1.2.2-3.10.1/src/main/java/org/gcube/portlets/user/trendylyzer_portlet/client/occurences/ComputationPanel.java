/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.occurences;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.ComputationStatusPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.JobsGridGotDirtyEvent;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.JobItem;
import org.gcube.portlets.user.trendylyzer_portlet.client.results.ResubmitJobEvent;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ComputationPanel extends ContentPanel {
	
	
	private List<ComputationStatusPanel> computationStatusPanels = new ArrayList<ComputationStatusPanel>();

	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove Results";
	private Button removeAllButton;
	Logger log = Logger.getLogger("");
	
	/**
	 * 
	 */
	public ComputationPanel() {
		super();
		
		bind();
		
//		this.setHeading(".: Computation Execution");
		this.setHeaderVisible(false);
		this.addStyleName("computationExcecutionPanel");
		this.setScrollMode(Scroll.AUTO);
		setToolsPanel();
	}
	
	public void startNewComputation(final Algorithm operator, String computationTitle, String computationDescription) {
		final ComputationStatusPanel statusPanel = new ComputationStatusPanel(operator);
//		List<Parameter>parameters= new ArrayList<Parameter>();
//		parameters=operator.getAlgorithmParameters();
//		log.log(Level.SEVERE, "inside startNewComputation");
//		for(Parameter p: parameters)
//		{
//			
//			String param=p.getName()+":&nbsp"+p.getValue()+"<BR>";
//			
//			log.log(Level.SEVERE, param);
//		}
		computationStatusPanels.add(statusPanel);
		this.insert(statusPanel, 0);
		this.layout();
		removeAllButton.setEnabled(true);
		
		TrendyLyzer_portlet.getService().startComputation(operator, computationTitle, computationDescription,
				new AsyncCallback<String>() {
			public void onSuccess(String id) {
				if (id==null)
					MessageBox.alert("ERROR", "Failed to start computation "+operator.getName()+", the computation id is null (1)", null);
				else {
					statusPanel.computationStarted(id);
					EventBusProvider.getInstance().fireEvent(new JobsGridGotDirtyEvent());
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.alert("ERROR", "Failed to start computation "+operator.getName()+" (2)<br/>Cause:"+caught.getCause()+"<br/>Message:"+caught.getMessage(), null);
			}
		});
		
		this.layout();
		
	}
	
	private void bind() {
		EventBusProvider.getInstance().addHandler(ResubmitJobEvent.getType(), new ResubmitJobEvent.ResubmitJobHandler() {
			public void onResubmitJob(ResubmitJobEvent event) {
				resubmitComputation(event.getJobItem());
			}
		});
	}

	public void resubmitComputation(final JobItem jobItem) {
		final ComputationStatusPanel statusPanel = new ComputationStatusPanel(jobItem.getOperator());
		computationStatusPanels.add(statusPanel);
		this.insert(statusPanel, 0);
		this.layout();
		removeAllButton.setEnabled(true);
		
		TrendyLyzer_portlet.getService().resubmit(jobItem,
				new AsyncCallback<String>() {
			public void onSuccess(String id) {
				if (id==null)
					MessageBox.alert("ERROR", "Failed to resubmit computation "+jobItem.getName()+", the computation id is null (1)", null);
				else {
					statusPanel.computationStarted(id);
					EventBusProvider.getInstance().fireEvent(new JobsGridGotDirtyEvent());
				}
			}

			public void onFailure(Throwable caught) {
				MessageBox.alert("ERROR", "Failed to start computation "+jobItem.getName()+" (2)<br/>Cause:"+caught.getCause()+"<br/>Message:"+caught.getMessage(), null);
			}
		});
		
		this.layout();
		
	}


	/**
	 * 
	 */
	private void emptyPanel() {
		// stop timers
		for (ComputationStatusPanel statusPanel : computationStatusPanels)
			statusPanel.stopTimer();
		
		removeAllButton.setEnabled(false);

		this.computationStatusPanels.clear();
		
		this.removeAll();
		this.layout();
	}
	
	private void setToolsPanel() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Label("Tools"));

		removeAllButton = new Button(DELETE_ALL_BUTTON_TOOLTIP, Images.removeAll(), new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				emptyPanel();
			}
		});
		
		removeAllButton.setToolTip(DELETE_ALL_BUTTON_TOOLTIP);
		removeAllButton.setScale(ButtonScale.MEDIUM);
		removeAllButton.setEnabled(false);

		toolBar.add(removeAllButton);
		this.setTopComponent(toolBar);
	}
}
