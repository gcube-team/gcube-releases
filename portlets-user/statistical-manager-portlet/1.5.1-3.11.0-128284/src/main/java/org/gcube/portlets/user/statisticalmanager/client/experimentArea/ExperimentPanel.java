/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;

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
//		// NORD: HEADER
//		BorderLayoutData northPanelData = new BorderLayoutData(LayoutRegion.NORTH, 50);
//		northPanelData.setMargins(new Margins(0, 0, 5, 0));		
//		ContentPanel header = createtHeader();
//		this.add(header, northPanelData);
		
		// WEST: OPERATORS
		BorderLayoutData westPanelData = new BorderLayoutData(LayoutRegion.WEST, 300);
		westPanelData.setSplit(true);
		westPanelData.setCollapsible(true);
		westPanelData.setFloatable(true);
		westPanelData.setMargins(new Margins(0, 5, 0, 0));		
		this.add(operatorsPanel, westPanelData);

		// CENTER
		BorderLayoutData centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		this.add(workflowPanel, centerPanelData);
		
		this.setStyleAttribute("margin-right", "20px");
//		this.addStyleName("smLayoutContainer");
//		this.addStyleName("layoutContainerArea");
		
	}

//	/**
//	 * @return
//	 */
//	private ContentPanel createtHeader() {
//		ContentPanel cp = new ContentPanel();
//		cp.setHeaderVisible(false);
//		HorizontalPanel hp = new HorizontalPanel();
//		hp.add(new Html("<h2>Experiment Area</h2>"), new TableData("300x", "50px"));
//		hp.add(new Button("<--back", new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				mainPanelHandler.comeBack();
//			}
//		}));
//		cp.add(hp);
//
//		return cp;
//	}

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
