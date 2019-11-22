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
import org.gcube.portlets.widgets.dataminermanagerwidget.client.info.ServiceInfoPanel;

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
	private static final String OPERATOR = "Operator";
	private static final String SERVICE_INFO = "Service Profile";
	
	//private ComputationExecutionPanel computationExecutionPanel;
	private ComputationPanel computationPanel;
	private ServiceInfoPanel serviceInfoPanel;
	

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

		TabItemConfig tabWorkFlowLcItemConf = new TabItemConfig(OPERATOR,
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

	
		TabItemConfig tabServiceInfoPanelItemConf = new TabItemConfig(
				SERVICE_INFO, false);
		tabServiceInfoPanelItemConf.setIcon(DataMinerManagerPanel.resources
				.folderExplore());
		serviceInfoPanel = new ServiceInfoPanel();
		add(serviceInfoPanel, tabServiceInfoPanelItemConf);
		
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

	}

	public void addOperator(Operator op) {
		setActiveWidget(computationPanel);
		computationPanel.addOperator(op);

	}

}
