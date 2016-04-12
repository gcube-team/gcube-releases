package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import java.util.ArrayList;
import java.util.List;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;



import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;

public class AlgorithmCategoryPanel extends VerticalPanel{

	private List<AlgorithmPanel> algorithmPanelsList = null;
	private AlgorithmsPanelHandler handler;
	private static final int TOOLTIP_WIDTH = 300;
	private boolean isCollapsed = true;
	private AlgorithmCategory category;
	private Html categoryHtml;
//	private LayoutContainer collapsedPanel = new LayoutContainer();
	

	/**
	 * @param cat
	 */
	public AlgorithmCategoryPanel(AlgorithmsPanelHandler handler, AlgorithmCategory cat) {
		super();		
		this.handler = handler;
		this.category = cat;
		this.setTableWidth("100%");
		
		// set category info
		categoryHtml = new Html(category.getName() + " <span class='counter'>("+category.getAlgorithms().size()+")</span>");
		categoryHtml.addStyleName("categoryItem");
		categoryHtml.addStyleName("categoryItem-Collapsed");
		ToolTipConfig tooltipConfig = createToolTip(category);
		categoryHtml.setToolTip(tooltipConfig);
		
		categoryHtml.addListener(Events.OnClick, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				switchOpenClose();
			}
		});
		
		this.add(categoryHtml);
	}

	/**
	 * @return
	 */
	private ToolTipConfig createToolTip(AlgorithmCategory cat) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setText(cat.getBriefDescription());  
		tooltipConfig.setTitle(cat.getName());  
		tooltipConfig.setMouseOffset(new int[] {0, 0});  
		tooltipConfig.setAnchor("left");
		tooltipConfig.setDismissDelay(5000);
		tooltipConfig.setTemplate(new Template(
				getTooltipTemplate(GWT.getModuleBaseURL(), cat.getId(), cat.hasImage(), cat.getDescription())
				));  
		tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
		return tooltipConfig;
	}

	private native String getTooltipTemplate(String base, String id, boolean hasImage, String description) /*-{ 
    	var html = [ 
			"<div class='categoryItemTooltip'>",
				"<img src='" + base + "../images/categories/"+(hasImage ? id : "DEFAULT_IMAGE")+".png' >",
				description,
			"</div>" 
			
		]; 
		return html.join(""); 
	}-*/;

	public void switchOpenClose() {
		if (isCollapsed)
			expand();
		else
			collapse();
	}

	/**
	 * 
	 */
	private void collapse() {
		categoryHtml.removeStyleName("categoryItem-Expanded");
		categoryHtml.addStyleName("categoryItem-Collapsed");

		for (AlgorithmPanel alg: this.algorithmPanelsList)
			this.remove(alg);
		this.layout();
		this.isCollapsed = true;
	}

	/**
	 * 
	 */
	private void expand() {
		if (algorithmPanelsList==null) {
			// init operator panels
			algorithmPanelsList = new ArrayList<AlgorithmPanel>();
			for (Algorithm alg : category.getAlgorithms()) {
				AlgorithmPanel operatorPanel = new AlgorithmPanel(alg, handler);
				algorithmPanelsList.add(operatorPanel);
			}
		}
		
		categoryHtml.removeStyleName("categoryItem-Collapsed");
		categoryHtml.addStyleName("categoryItem-Expanded");
		for (AlgorithmPanel alg: this.algorithmPanelsList)
			this.add(alg);
		this.layout();
		this.isCollapsed = false;
	}
}
