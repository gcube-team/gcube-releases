package org.gcube.portlet.user.userstatisticsportlet.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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

	public StatisticWidget(boolean isRoot) {
		initWidget(uiBinder.createAndBindUi(this));
		
		//Set the right margin of this widget according to where the portlet is currently deployed.
		if(isRoot)
			container.getElement().getStyle().setMarginRight(10.0, Unit.PX); 
		else
			container.getElement().getStyle().setMarginRight(5.0, Unit.PX);
	}

	public void setHeader(String header) {
		this.header.setText(header);
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
		this.setTitle(tooltip);
	}
}
