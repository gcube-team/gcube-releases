package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import java.util.List;

import org.gcube.datacatalogue.grsf_manage_widget.shared.SourceRecord;

import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

		int counter = 1;
		sourcesInnerPanel.setWidth("90%");
		for (SourceRecord sourceRecord : availableSources) {
			
			VerticalPanel subPanel = new VerticalPanel();
			String sourceNumber = "Source " + counter + ":";
			String name = sourceRecord.getName();
			Anchor url = new Anchor();
			url.setHref(sourceRecord.getUrl());
			url.setText("View"); 
			url.setTarget("_blank");
			subPanel.add(new Paragraph(sourceNumber));
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.add(new Paragraph(name  + " - "));
			hPanel.add(url);
			subPanel.add(hPanel);
			subPanel.setWidth("100%");
			sourcesInnerPanel.add(subPanel);
			counter++;
			
		}

	}

}
