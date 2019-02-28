package org.gcube.portlets.user.dataminermanager.client.experiments;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class OperatorPanel extends SimpleContainer {

	private static final int TOOLTIP_DESCRIPTION_LENGHT = 435;
	private static final int OPERATOR_BRIEF_DESCRIPTION_LENGHT = 160;
	private static final int TOOLTIP_WIDTH = 500;
	private VerticalLayoutContainer vert;

	private Operator operator;
	private OperatorsPanelHandler handler;
	private HTML titleHtml;

	/**
	 * 
	 * @param operator
	 *            operator
	 * @param handler
	 *            operator panel handler
	 */
	public OperatorPanel(Operator operator, OperatorsPanelHandler handler) {
		super();
		this.operator = operator;
		this.handler = handler;
		init();

	}

	private void init() {
		addStyleName("operatorPanel");
		addStyleName("opePanel");

		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				handler.addOperator(OperatorPanel.this, operator);

			}

		}, ClickEvent.getType());

		titleHtml = new HTML(operator.getName());
		titleHtml.addStyleName("operatorPanel-title");

		HTML descriptionHtml = new HTML(
				Format.ellipse(operator.getBriefDescription(), OPERATOR_BRIEF_DESCRIPTION_LENGHT));
		descriptionHtml.addStyleName("operatorPanel-briefDescription");

		vert = new VerticalLayoutContainer();

		vert.add(titleHtml, new VerticalLayoutData(1, -1, new Margins(0)));
		vert.add(descriptionHtml, new VerticalLayoutData(1, -1, new Margins(0)));
		add(vert);

		ToolTipConfig tooltipConfig = createToolTip(operator);
		setToolTipConfig(tooltipConfig);

	}

	/**
	 * 
	 * @return operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @return tooltip config
	 */
	private ToolTipConfig createToolTip(Operator op) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setTitleHtml("<br>&nbsp;&nbsp;" + op.getName());
		tooltipConfig.setMouseOffsetX(0);
		tooltipConfig.setMouseOffsetY(0);
		tooltipConfig.setAnchor(Side.LEFT);
		tooltipConfig.setDismissDelay(5000);
		tooltipConfig.setBodyHtml(
				getTooltipTemplate(GWT.getModuleBaseURL(), op.getId(), op.hasImage(), op.getDescription()));
		// config.setCloseable(true);
		tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
		return tooltipConfig;
	}

	private String getTooltipTemplate(String base, String id, boolean hasImage, String description) {
		String template = "<div class='categoryItemTooltip'>" + "<img src='" + base + "../images/operators/"
				+ (hasImage ? id : "DEFAULT_IMAGE") + ".png' >"
				+ Format.ellipse(description, TOOLTIP_DESCRIPTION_LENGHT) + "</div>";

		return template;
	};

	public void toggleSelected(boolean isSelect) {
		if (isSelect)
			this.addStyleName("operatorPanel-selected");
		// titleHtml.addStyleName("operatorPanel-title-selected");
		else
			this.removeStyleName("operatorPanel-selected");
		// titleHtml.removeStyleName("operatorPanel-title-selected");
	}

	/**
	 * Set as the default operator
	 */
	public void setAsTheDefaultOperator() {
		handler.addOperator(OperatorPanel.this, operator);
	}

}
