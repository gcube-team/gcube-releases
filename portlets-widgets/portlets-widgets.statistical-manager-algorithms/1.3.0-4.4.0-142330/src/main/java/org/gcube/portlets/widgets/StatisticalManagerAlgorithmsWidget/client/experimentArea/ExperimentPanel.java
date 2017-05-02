/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;

import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;




/**
 * @author ceras
 *
 */
public class ExperimentPanel extends LayoutContainer implements OperatorsPanelHandler {

	protected Logger logger = Logger.getLogger("logger");
	
	private WorkflowPanel workflowPanel;
	//private ContentPanel operatorsPanel = new ContentPanel();
	private OperatorsPanel operatorsPanel;
	private OperatorPanel lastOperatorSelected = null;

	/**
	 * 
	 */
	public ExperimentPanel() {		
		super();

		operatorsPanel = new OperatorsPanel(this);
		this.workflowPanel = new WorkflowPanel();
		this.setLayout(new BorderLayout());
		this.setStyleAttribute("background-color", "#FFFFFF");

		// WEST: OPERATORS
		BorderLayoutData westPanelData = new BorderLayoutData(LayoutRegion.WEST, 300);
		westPanelData.setSplit(true);
		westPanelData.setCollapsible(true);
		westPanelData.setFloatable(true);
		westPanelData.setMargins(new Margins(1, 1, 1, 1));		
		this.add(operatorsPanel, westPanelData);

		// CENTER
		BorderLayoutData centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(1));
		this.add(workflowPanel, centerPanelData);
		
		
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		operatorsPanel.setHeight(height-20);
		workflowPanel.setHeight(height-20);
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.experimentArea.OperatorsPanelHandler#addOperator(org.gcube.portlets.user.statisticalmanager.client.experimentArea.OperatorPanel, org.gcube.portlets.user.statisticalmanager.client.bean.Operator)
	 */
	@Override
	public void addOperator(OperatorPanel operatorPanel, Operator operator) {
		if (lastOperatorSelected!=null && lastOperatorSelected!=operatorPanel)
			lastOperatorSelected.toggleSelected(false);
		if (lastOperatorSelected!=operatorPanel)
			operatorPanel.toggleSelected(true);
		lastOperatorSelected = operatorPanel;
		workflowPanel.addOperator(operator);		
	}
}
