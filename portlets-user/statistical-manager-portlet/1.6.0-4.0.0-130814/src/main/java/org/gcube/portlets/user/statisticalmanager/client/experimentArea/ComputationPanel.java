/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.JobItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.events.JobsGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.ResubmitJobEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class ComputationPanel extends ContentPanel {
	
	
	private List<ComputationStatusPanel> computationStatusPanels = new ArrayList<ComputationStatusPanel>();

	private static final String DELETE_ALL_BUTTON_TOOLTIP = "Remove Computations Log";
	private Button removeAllButton;
    Logger logger = Logger.getLogger("");
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
	
	public void startNewComputation(final Operator operator, String computationTitle, String computationDescription) {
		logger.log(Level.SEVERE, "Computation Panel: start new computation ");
		final ComputationStatusPanel statusPanel = new ComputationStatusPanel(operator);
		computationStatusPanels.add(statusPanel);
		logger.log(Level.SEVERE, "Added status bar ");
		this.insert(statusPanel, 0);
		this.layout();
		removeAllButton.setEnabled(true);
		
		StatisticalManager.getService().startComputation(operator, computationTitle, computationDescription,
				new AsyncCallback<String>() {
			@Override
			public void onSuccess(String id) {
				if (id==null)
					MessageBox.alert("ERROR", "Failed to start computation "+operator.getName()+", the computation id is null (1)", null);
				else {
					statusPanel.computationStarted(id);
					EventBusProvider.getInstance().fireEvent(new JobsGridGotDirtyEvent());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("ERROR", "Failed to start computation "+operator.getName()+" (2)<br/>Cause:"+caught.getCause()+"<br/>Message:"+caught.getMessage(), null);
			}
		});
		
		this.layout();
		
	}
	
	private void bind() {
		EventBusProvider.getInstance().addHandler(ResubmitJobEvent.getType(), new ResubmitJobEvent.ResubmitJobHandler() {
			@Override
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
		
		StatisticalManager.getService().resubmit(jobItem,
				new AsyncCallback<String>() {
			@Override
			public void onSuccess(String id) {
				if (id==null)
					MessageBox.alert("ERROR", "Failed to resubmit computation "+jobItem.getName()+", the computation id is null (1)", null);
				else {
					statusPanel.computationStarted(id);
					EventBusProvider.getInstance().fireEvent(new JobsGridGotDirtyEvent());
				}
			}

			@Override
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
