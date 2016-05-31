/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.experimentArea;

import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;

import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * @author ceras
 *
 */
public class OperatorPanel extends LayoutContainer {

	private Operator operator;
	private OperatorsPanelHandler handler;
	private static final int TOOLTIP_WIDTH = 500;
	private Html titleHtml;

	/**
	 * @param handler 
	 * @param op
	 */
	public OperatorPanel(Operator operator, OperatorsPanelHandler handler) {
		super();
		this.operator = operator;
		this.handler = handler;
		this.addStyleName("operatorPanel");
		this.setHeight(50);
	}

	/* (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
	 */
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
				this.addStyleName("opePanel");
				this.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {

					handler.addOperator(OperatorPanel.this, operator);
			}
		});
//		Image addOperatorImage = new Image(StatisticalManager.resources.addOperator());
//		addOperatorImage.setTitle("Add this Operator to the Workflow");
//		addOperatorImage.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				handler.addOperator(OperatorPanel.this, operator);
//			}
//		});
//		this.add(addOperatorImage);
		
//		Button addOperatorButton = new Button();
//		addOperatorButton.setIcon(Images.addOperator());
//		addOperatorButton.setTitle();
//		addOperatorButton.addStyleName("operatorPanel-button");
//		addOperatorButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				handler.addOperator(operator);
//			}
//		});
//		this.add(addOperatorButton);

		titleHtml = new Html(operator.getName());
		titleHtml.addStyleName("operatorPanel-title");

		Html descriptionHtml = new Html(operator.getBriefDescription());
		descriptionHtml.addStyleName("operatorPanel-briefDescription");

		this.add(titleHtml);
		this.add(descriptionHtml);

		ToolTipConfig tooltipConfig = createToolTip(operator);
		this.setToolTip(tooltipConfig);
		
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @return
	 */
	private ToolTipConfig createToolTip(Operator op) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setText(op.getBriefDescription());  
		tooltipConfig.setTitle("<br>&nbsp;&nbsp;"+op.getName());  
		tooltipConfig.setMouseOffset(new int[] {0, 0});  
		tooltipConfig.setAnchor("left");
		tooltipConfig.setDismissDelay(5000);
		tooltipConfig.setTemplate(new Template(
				getTooltipTemplate(GWT.getModuleBaseURL(), op.getId(), op.hasImage(), op.getDescription())
				));  
		//		config.setCloseable(true);  
		tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
		return tooltipConfig;
	}

	/**
	 * @param hostPageBaseURL
	 * @param id
	 * @param hasImage
	 * @param description
	 * @return
	 */
	private native String getTooltipTemplate(String base, String id, boolean hasImage, String description) /*-{ 
	    var html = [ 
		    "<div class='categoryItemTooltip'>",
		    	"<img src='" + base + "../images/operators/"+(hasImage ? id : "DEFAULT_IMAGE")+".png' >",
		    	description,
		    "</div>" 
	    ]; 
	    return html.join(""); 
  	}-*/; 
	
	public void toggleSelected(boolean isSelect) {
		if (isSelect)
			this.addStyleName("operatorPanel-selected");
//			titleHtml.addStyleName("operatorPanel-title-selected");
		else
			this.removeStyleName("operatorPanel-selected");
//			titleHtml.removeStyleName("operatorPanel-title-selected");
	}
}
