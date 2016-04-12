package org.gcube.portlets.user.newsfeed.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResultsFor extends Composite {

	private static ResultsForUiBinder uiBinder = GWT
			.create(ResultsForUiBinder.class);

	interface ResultsForUiBinder extends UiBinder<Widget, ResultsFor> {
	}

	@UiField HTML allUpdatesLink;
	@UiField HTML resultForDiv;
	public ResultsFor(String prefix, String hashtag) {
		initWidget(uiBinder.createAndBindUi(this));
		allUpdatesLink.setHTML("<a>All Updates</a>");
		allUpdatesLink.getElement().getStyle().setCursor(Cursor.POINTER);
		resultForDiv.setHTML(prefix + " <a>" + hashtag+"</a>");
		resultForDiv.setStyleName("filter-selected");
	}	
	
	@UiHandler("allUpdatesLink")
	void onAllUpdatesClick(ClickEvent e) {
		String href = Location.getHref();
		if (href.contains("?"))
			href = href.substring(0, href.indexOf("?"));
		Location.assign(href);
	}

}
