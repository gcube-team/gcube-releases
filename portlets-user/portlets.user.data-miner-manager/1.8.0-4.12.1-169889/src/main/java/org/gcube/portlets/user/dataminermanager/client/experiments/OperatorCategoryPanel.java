/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.experiments;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.gcube.data.analysis.dataminermanagercl.shared.process.OperatorCategory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class OperatorCategoryPanel extends SimpleContainer {

	private List<OperatorPanel> operatorPanelsList = null;
	private OperatorsPanelHandler handler;
	private static final int TOOLTIP_WIDTH = 300;
	private boolean isCollapsed = true;
	private OperatorCategory category;
	private HtmlLayoutContainer categoryHtml;
	private VerticalLayoutContainer v;

	/**
	 * 
	 * @param handler
	 *            operator panel handler
	 * @param operatorCategory
	 *            operator category
	 */
	public OperatorCategoryPanel(OperatorsPanelHandler handler, OperatorCategory operatorCategory) {
		super();
		this.handler = handler;
		this.category = operatorCategory;
		create();
	}

	public OperatorCategory getCategory() {
		return category;
	}

	private void create() {
		v = new VerticalLayoutContainer();
		add(v);
		// set category info
		categoryHtml = new HtmlLayoutContainer(
				category.getName() + " <span class='counter'>(" + category.getOperators().size() + ")</span>");
		categoryHtml.addStyleName("categoryItem");
		categoryHtml.addStyleName("categoryItem-Collapsed");
		ToolTipConfig tooltipConfig = createToolTip(category);
		categoryHtml.setToolTipConfig(tooltipConfig);

		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				switchOpenClose();

			}

		}, ClickEvent.getType());

		v.add(categoryHtml);
	}

	/**
	 * 
	 * @param cat
	 * @return
	 */
	private ToolTipConfig createToolTip(OperatorCategory cat) {
		ToolTipConfig tooltipConfig = new ToolTipConfig();
		tooltipConfig.setTitleHtml("<br>&nbsp;&nbsp;" + cat.getName());
		tooltipConfig.setMouseOffsetX(0);
		tooltipConfig.setMouseOffsetY(0);
		tooltipConfig.setAnchor(Side.LEFT);
		tooltipConfig.setDismissDelay(5000);
		tooltipConfig.setBodyHtml(
				getTooltipTemplate(GWT.getModuleBaseURL(), cat.getId(), cat.hasImage(), cat.getDescription()));
		tooltipConfig.setMaxWidth(TOOLTIP_WIDTH);
		return tooltipConfig;
	}

	/**
	 * 
	 * @param base
	 * @param id
	 * @param hasImage
	 * @param description
	 * @return
	 */
	private String getTooltipTemplate(String base, String id, boolean hasImage, String description) {
		String html = "<div class='categoryItemTooltip'>" + "<img src='" + base + "../images/categories/"
				+ (hasImage ? id : "DEFAULT_IMAGE") + ".png' >" + Format.ellipse(description, 100) + "</div>";
		return html;
	};

	/**
	 * 
	 */
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

		for (OperatorPanel op : this.operatorPanelsList)
			v.remove(op);
		isCollapsed = true;
		forceLayout();
	}

	/**
	 * 
	 */
	private void expand() {
		if (operatorPanelsList == null) {
			// init operator panels
			operatorPanelsList = new ArrayList<OperatorPanel>();
			for (Operator op : category.getOperators()) {
				OperatorPanel operatorPanel = new OperatorPanel(op, handler);
				operatorPanelsList.add(operatorPanel);
			}
		}

		categoryHtml.removeStyleName("categoryItem-Collapsed");
		categoryHtml.addStyleName("categoryItem-Expanded");
		for (OperatorPanel op : this.operatorPanelsList)
			v.add(op);
		isCollapsed = false;
		forceLayout();
	}

	public void setDefaultOperator() {
		expand();
	}

	public void setOperatorDefault(Operator operatorDefault) {
		expand();
		for (OperatorPanel opPanel : operatorPanelsList) {
			if (opPanel.getOperator().compareTo(operatorDefault) == 0) {
				opPanel.setAsTheDefaultOperator();
				break;
			}
		}

	}
}
