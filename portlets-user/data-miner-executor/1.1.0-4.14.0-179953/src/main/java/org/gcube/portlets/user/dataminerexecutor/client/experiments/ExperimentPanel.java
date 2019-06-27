package org.gcube.portlets.user.dataminerexecutor.client.experiments;

import org.gcube.portlets.user.dataminerexecutor.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminerexecutor.client.events.InvocationModelEvent;
//import org.gcube.portlets.user.dataminerexecutor.client.events.InvocationModelRequestEvent;
import org.gcube.portlets.user.dataminerexecutor.shared.process.InvocationModel;

import com.allen_sauer.gwt.log.client.Log;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExperimentPanel extends SimpleContainer {

	private WorkflowPanel workflowPanel;
	//private OperatorsPanel operatorsPanel;
	//private OperatorPanel lastOperatorSelected = null;

	/**
	 * 
	 */
	public ExperimentPanel() {
		super();
		init();
		create();
		bind();
	
	}
	
	private void init() {
	}

	
	private void bind(){
		EventBusProvider.INSTANCE.addHandler(InvocationModelEvent.TYPE,
				new InvocationModelEvent.InvocationModelEventHandler() {

					@Override
					public void onInvocation(InvocationModelEvent event) {
						Log.debug("Catch InvocationModelEvent: " + event);
						addInvocation(event.getInvocationModel());
						
					}

				});
	}

	
	private void create() {
		//operatorsPanel = new OperatorsPanel(this);
		workflowPanel = new WorkflowPanel();

		//
		BorderLayoutContainer experimentPanelLayout = new BorderLayoutContainer();

		experimentPanelLayout.setBorders(false);
		experimentPanelLayout.setResize(true);
		experimentPanelLayout.getElement().getStyle()
				.setBackgroundColor("white");

		// Center
		MarginData mainData = new MarginData(new Margins(0));
		experimentPanelLayout.setCenterWidget(workflowPanel, mainData);		
		add(experimentPanelLayout, new MarginData(0));
		
	}

	/**
	 * 
	 */
	
	private void addInvocation(InvocationModel invocationModel) {
		//if (lastOperatorSelected != null
		//		&& lastOperatorSelected != operatorPanel)
		//	lastOperatorSelected.toggleSelected(false);
		//if (lastOperatorSelected != operatorPanel)
		//	operatorPanel.toggleSelected(true);
		//lastOperatorSelected = operatorPanel;
		workflowPanel.addInvocation(invocationModel);
	}
}
