package org.gcube.portlets.user.searchportlet.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;

public class SearchGuideStep4HTML extends HTML {
	 
	interface SearchGuideStep4HTMLUiBinder extends UiBinder<Element, SearchGuideStep4HTML> {
	}

	private static SearchGuideStep4HTMLUiBinder uiBinder = GWT.create(SearchGuideStep4HTMLUiBinder.class);

	public SearchGuideStep4HTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());
	}
}
