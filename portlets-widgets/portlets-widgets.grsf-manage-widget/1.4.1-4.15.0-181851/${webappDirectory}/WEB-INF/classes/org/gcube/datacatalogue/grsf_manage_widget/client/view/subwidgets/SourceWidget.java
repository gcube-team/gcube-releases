package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.List;

import org.gcube.datacatalogue.grsf_manage_widget.shared.SourceRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SourceWidget extends Composite {

	private static SourceWidgetUiBinder uiBinder = GWT
			.create(SourceWidgetUiBinder.class);

	interface SourceWidgetUiBinder extends UiBinder<Widget, SourceWidget> {
	}

	@UiField
	VerticalPanel sourcesInnerPanel;

	public SourceWidget(List<SourceRecord> availableSources) {
		initWidget(uiBinder.createAndBindUi(this));

		sourcesInnerPanel.getElement().getStyle().setMarginLeft(15, Unit.PX);
		
		for (SourceRecord sourceRecord : availableSources) {

			VerticalPanel subPanel = new VerticalPanel();
			subPanel.setWidth("90%");
			String name = sourceRecord.getName();
			Anchor url = new Anchor();
			url.getElement().getStyle().setMarginLeft(10, Unit.PX);
			url.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			url.setHref(sourceRecord.getUrl());
			url.setText(name); 
			url.setTitle("Click to view the source record");
			url.setTarget("_blank");
			subPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
			subPanel.add(url);
			subPanel.getElement().getStyle().setMarginBottom(15, Unit.PX);
			sourcesInnerPanel.add(subPanel);

		}

	}

}
