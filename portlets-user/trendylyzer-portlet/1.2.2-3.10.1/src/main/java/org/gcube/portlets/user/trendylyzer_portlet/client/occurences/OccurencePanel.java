package org.gcube.portlets.user.trendylyzer_portlet.client.occurences;





import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.Algorithm;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmsPanel;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.AlgorithmsPanelHandler;
import org.gcube.portlets.user.trendylyzer_portlet.client.algorithms.WorkflowPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class OccurencePanel extends LayoutContainer implements AlgorithmsPanelHandler{
	private WorkflowPanel workflowPanel;
	private AlgorithmsPanel algorithmSPanel;
	private AlgorithmPanel lastalgorithmSelected = null;

	/**
	 * 
	 */
	public OccurencePanel() {		
		super();
		
		algorithmSPanel = new AlgorithmsPanel(this);
		
		this.workflowPanel = new WorkflowPanel();
		this.setLayout(new BorderLayout());
		this.setStyleAttribute("background-color", "#FFFFFF");

		BorderLayoutData westPanelData = new BorderLayoutData(LayoutRegion.WEST, 300);
		westPanelData.setSplit(true);
		westPanelData.setCollapsible(true);
		westPanelData.setFloatable(true);
		westPanelData.setMargins(new Margins(0, 5, 0, 0));		
		this.add(algorithmSPanel, westPanelData);

		// CENTER
		BorderLayoutData centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		this.add(workflowPanel, centerPanelData);
		
		this.setStyleAttribute("margin-right", "20px");
		//this.addStyleName("layoutContainerArea");

		
	}

	@Override
	public void addAlgorithm(AlgorithmPanel algorithmPanel, Algorithm algorithm) {
		if (lastalgorithmSelected!=null && lastalgorithmSelected!=algorithmPanel)
			lastalgorithmSelected.toggleSelected(false);
		if (lastalgorithmSelected!=algorithmPanel)
			algorithmPanel.toggleSelected(true);
		lastalgorithmSelected = algorithmPanel;
		workflowPanel.addAlgorithm(algorithm);		
	}
}
