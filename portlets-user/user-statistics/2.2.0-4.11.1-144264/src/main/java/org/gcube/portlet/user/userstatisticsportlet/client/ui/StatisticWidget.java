package org.gcube.portlet.user.userstatisticsportlet.client.ui;

import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Container for couple Statistic Header/Value
 * @author Costantino Perciante at ISTI-CNR
 *
 */
public class StatisticWidget extends Composite{

	private static StatisticWidgetUiBinder uiBinder = GWT
			.create(StatisticWidgetUiBinder.class);

	interface StatisticWidgetUiBinder extends UiBinder<Widget, StatisticWidget> {
	}

	@UiField
	HTMLPanel container; 

	@UiField
	Label header; //i.e., FEEDS, LIKES ..

	@UiField
	FlowPanel containerValues;

	@UiField
	Popover popover;

	private String headerTitle;

	public StatisticWidget(boolean isRoot) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setHeader(String header) {
		this.header.setText(header);
		headerTitle = header;
	}

	public void appendToPanel(Widget w) {
		this.containerValues.add(w);
	}

	public void removeFromPanel(Widget w){
		this.containerValues.remove(w);
	}

	public void clearPanelValues(){
		this.containerValues.clear();
	}

	public void setToolTip(String tooltip){

		// change popover text content
		popover.setText(tooltip);

		HTML headerHtml = new HTML(
				"<span style=\"font-size:14px; font-weight:bold\">"+headerTitle+"</span>"
				);

		popover.setPlacement(Placement.TOP);

		// change popover text header
		popover.setHeading(headerHtml.getHTML());

		// change popover text content
		popover.setText(tooltip);

		// set html
		popover.setHtml(true);

		popover.reconfigure();
	}
}
