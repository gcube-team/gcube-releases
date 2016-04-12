package org.gcube.portlets.user.searchportlet.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;

public class SearchGuideStep2HTML extends HTML {
	 
	interface SearchGuideStep2HTMLUiBinder extends UiBinder<Element, SearchGuideStep2HTML> {
	}

	private static SearchGuideStep2HTMLUiBinder uiBinder = GWT.create(SearchGuideStep2HTMLUiBinder.class);

	public SearchGuideStep2HTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());
	}
}
