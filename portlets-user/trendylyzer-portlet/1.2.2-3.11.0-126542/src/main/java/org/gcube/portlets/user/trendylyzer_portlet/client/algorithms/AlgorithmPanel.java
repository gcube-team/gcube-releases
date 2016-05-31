package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;




import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class AlgorithmPanel extends LayoutContainer {
	Algorithm algorithm;
	AlgorithmsPanelHandler handler;
	private static final int TOOLTIP_WIDTH = 300;
	private Html titleHtml;
	Logger log = Logger.getLogger("");
	
	public AlgorithmPanel(Algorithm algorithm ,AlgorithmsPanelHandler handler)
	{
		super();
		this.algorithm=algorithm;
		this.handler=handler;
		this.addStyleName("operatorPanel");
		this.setHeight(50);
	}
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	Image addOperatorImage = new Image(TrendyLyzer_portlet.resources.addOperator());
	addOperatorImage.setTitle("Add this Algorithm to the Workflow");
	addOperatorImage.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			handler.addAlgorithm(AlgorithmPanel.this, algorithm);
		}
	});
	this.add(addOperatorImage);
	

	titleHtml = new Html(algorithm.getName());
	titleHtml.addStyleName("operatorPanel-title");

	Html descriptionHtml = new Html(algorithm.getDescription());
	descriptionHtml.addStyleName("operatorPanel-briefDescription");

	this.add(titleHtml);
	this.add(descriptionHtml);

	ToolTipConfig tooltipConfig = createToolTip(algorithm);
	this.setToolTip(tooltipConfig);
	
}

/**
 * @return the operator
 */
public Algorithm getOperator() {
	return algorithm;
}

/**
 * @return
 */
private ToolTipConfig createToolTip(Algorithm alg) {
	ToolTipConfig tooltipConfig = new ToolTipConfig();
	tooltipConfig.setText(alg.getDescription());  
	tooltipConfig.setTitle(alg.getName());  
	tooltipConfig.setMouseOffset(new int[] {0, 0});  
	tooltipConfig.setAnchor("left");
	tooltipConfig.setDismissDelay(5000);
	Image img= new Image(TrendyLyzer_portlet.resources.defaultAlg());
	String url=img.getUrl();
	log.log(Level.SEVERE, "***********"+ alg.getDescription()+"******");
	tooltipConfig.setTemplate(new Template(
			getTooltipTemplate(url, alg.getId(), alg.hasImage(), alg.getDescription())
			));  
	tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
	return tooltipConfig;
}
//<img src='" + base + "../images/operators/DEFAULT_IMAGE.png' >",
/**
 * @param hostPageBaseURL
 * @param id
 * @param hasImage
 * @param description
 * @return
 */
private native String getTooltipTemplate(String base, String id, boolean hasImage, String description) 
/*-{ 
    var html = [ 
	    "<div class='categoryItemTooltip'>",
	    	"<img src='"+ base + "' >",
	    	description,
	    "</div>" 
    ]; 
    return html.join(""); 
	}-*/; 

public void toggleSelected(boolean isSelect) {
	if (isSelect)
		this.addStyleName("operatorPanel-selected");
	else
		this.removeStyleName("operatorPanel-selected");
}

}
