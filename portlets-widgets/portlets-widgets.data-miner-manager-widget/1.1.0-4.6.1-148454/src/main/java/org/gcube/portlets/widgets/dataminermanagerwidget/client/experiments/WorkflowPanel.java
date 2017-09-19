/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.common.EventBusProvider;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ComputationReadyEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.ResubmitComputationExecutionEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.StartComputationExecutionRequestEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WorkflowPanel extends TabPanel {

	public static final String DEFAULT_OPERATOR = "AQUAMAPS_SUITABLE";

	//private ComputationExecutionPanel computationExecutionPanel;
	private ComputationPanel computationPanel;

	/**
	 * 
	 */
	public WorkflowPanel() {
		super();
		init();
		create();
		bind();

	}

	private void init() {
		setBodyBorder(false);
	}

	private void create() {

		TabItemConfig tabWorkFlowLcItemConf = new TabItemConfig("Operator",
				false);
		tabWorkFlowLcItemConf.setIcon(DataMinerManagerPanel.resources
				.folderExplore());
		computationPanel = new ComputationPanel();
		computationPanel
				.addComputationReadyEventHandler(new ComputationReadyEvent.ComputationReadyEventHandler() {

					@Override
					public void onReady(ComputationReadyEvent event) {
						Log.debug("StartComputationEvent Received:" + event);
						startComputation(event.getOperator());

					}
				});
		add(computationPanel, tabWorkFlowLcItemConf);

		/*
		TabItemConfig tabComputationPanelItemConf = new TabItemConfig(
				"Computations Execution", false);
		tabComputationPanelItemConf.setIcon(DataMinerManagerPanel.resources
				.folderExplore());
		computationExecutionPanel = new ComputationExecutionPanel();
		add(computationExecutionPanel, tabComputationPanelItemConf);
		*/
		
		setActiveWidget(computationPanel);
	}

	/**
	 * 
	 */
	private void bind() {
		EventBusProvider.INSTANCE
				.addHandler(
						ResubmitComputationExecutionEvent.getType(),
						new ResubmitComputationExecutionEvent.ResubmitComputationExecutionEventHandler() {
							@Override
							public void onResubmit(
									ResubmitComputationExecutionEvent event) {
								resubmitComputation();
							}
						});
	}

	/**
	 * 
	 */
	private void resubmitComputation() {
		//setActiveWidget(computationExecutionPanel);
	}

	/**
	 * @param computationTitle
	 * @param operator
	 * 
	 */
	private void startComputation(Operator op) {
		StartComputationExecutionRequestEvent event = new StartComputationExecutionRequestEvent(
				op, 1);
		EventBusProvider.INSTANCE.fireEvent(event);
		/*
		setActiveWidget(computationExecutionPanel);
		computationExecutionPanel.startNewComputation(op);*/

	}

	public void addOperator(Operator op) {
		setActiveWidget(computationPanel);
		computationPanel.addOperator(op);

	}

}
