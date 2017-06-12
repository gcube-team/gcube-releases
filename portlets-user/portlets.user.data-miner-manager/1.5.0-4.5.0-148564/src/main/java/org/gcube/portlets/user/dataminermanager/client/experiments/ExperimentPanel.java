package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.portlets.user.dataminermanager.shared.process.Operator;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ExperimentPanel extends SimpleContainer implements
		OperatorsPanelHandler {

	private WorkflowPanel workflowPanel;
	private OperatorsPanel operatorsPanel;
	private OperatorPanel lastOperatorSelected = null;

	/**
	 * 
	 */
	public ExperimentPanel() {
		super();
		init();
		create();

	}

	private void init() {
	}

	private void create() {
		operatorsPanel = new OperatorsPanel(this);
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

		// West

		BorderLayoutData westData = new BorderLayoutData(320);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setFloatable(false);
		westData.setCollapseMini(false);
		westData.setMargins(new Margins(0, 5, 0, 0));
		westData.setCollapseHidden(false);

		experimentPanelLayout.setWestWidget(operatorsPanel, westData);

		add(experimentPanelLayout, new MarginData(0));

	}

	/**
	 * 
	 */
	@Override
	public void addOperator(OperatorPanel operatorPanel, Operator operator) {
		if (lastOperatorSelected != null
				&& lastOperatorSelected != operatorPanel)
			lastOperatorSelected.toggleSelected(false);
		if (lastOperatorSelected != operatorPanel)
			operatorPanel.toggleSelected(true);
		lastOperatorSelected = operatorPanel;
		workflowPanel.addOperator(operator);
	}
}
