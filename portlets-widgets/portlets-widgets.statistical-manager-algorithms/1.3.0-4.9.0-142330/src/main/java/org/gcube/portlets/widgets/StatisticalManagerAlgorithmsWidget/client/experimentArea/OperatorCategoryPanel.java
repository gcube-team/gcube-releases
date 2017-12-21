/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.Operator;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorCategory;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;



/**
 * @author ceras
 *
 */
public class OperatorCategoryPanel extends VerticalPanel {

	private List<OperatorPanel> operatorPanelsList = null;
	private OperatorsPanelHandler handler;
	private static final int TOOLTIP_WIDTH = 300;
	private boolean isCollapsed = true;
	private OperatorCategory category;
	private Html categoryHtml;
//	private LayoutContainer collapsedPanel = new LayoutContainer();
	

	/**
	 * @param cat
	 */
	public OperatorCategoryPanel(OperatorsPanelHandler handler, OperatorCategory cat) {
		super();		
		this.handler = handler;
		this.category = cat;
		this.setTableWidth("100%");
		
		// set category info
		categoryHtml = new Html(category.getName() + " <span class='counter'>("+category.getOperators().size()+")</span>");
		categoryHtml.addStyleName("categoryItem");
		categoryHtml.addStyleName("categoryItem-Collapsed");
		ToolTipConfig tooltipConfig = createToolTip(category);
		categoryHtml.setToolTip(tooltipConfig);
		
		categoryHtml.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				switchOpenClose();
			}
		});
		
		this.add(categoryHtml);
	}

	/**
	 * @return
	 */
	private ToolTipConfig createToolTip(OperatorCategory cat) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setText(cat.getBriefDescription());  
		tooltipConfig.setTitle("<br>&nbsp;&nbsp;"+cat.getName());  
		tooltipConfig.setMouseOffset(new int[] {0, 0});  
		tooltipConfig.setAnchor("left");
		tooltipConfig.setDismissDelay(5000);
		String imageUri=Images.categoryImagesMap.get(cat.hasImage()?cat.getId():"DEFAULT_IMAGE").getSafeUri().asString();
		tooltipConfig.setTemplate(new Template(
				getTooltipTemplate(imageUri, cat.getDescription())
				));  
		//		config.setCloseable(true);  
		tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
		return tooltipConfig;
	}

	private native String getTooltipTemplate(String imgSrc, String description) /*-{ 
    	var html = [ 
			"<div class='categoryItemTooltip'>",
				"<img src='"+imgSrc+"' >",
				description,
			"</div>" 
			//    '<div><ul style="list-style: disc; margin: 0px 0px 5px 15px">', 
			//    '<li>5 bedrooms</li>', 
			//    '<li>2 baths</li>', 
			//    '<li>Large backyard</li>', 
			//    '<li>Close to metro</li>', 
			//    '</ul>', 
			//    '</div>' 
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

		for (OperatorPanel op: this.operatorPanelsList)
			this.remove(op);
		this.layout();
		this.isCollapsed = true;
	}

	/**
	 * 
	 */
	private void expand() {
		if (operatorPanelsList==null) {
			// init operator panels
			operatorPanelsList = new ArrayList<OperatorPanel>();
			for (Operator op : category.getOperators()) {
				OperatorPanel operatorPanel = new OperatorPanel(op, handler);
				operatorPanelsList.add(operatorPanel);
			}
		}
		
		categoryHtml.removeStyleName("categoryItem-Collapsed");
		categoryHtml.addStyleName("categoryItem-Expanded");
		for (OperatorPanel op: this.operatorPanelsList)
			this.add(op);
		this.layout();
		this.isCollapsed = false;
	}
}
