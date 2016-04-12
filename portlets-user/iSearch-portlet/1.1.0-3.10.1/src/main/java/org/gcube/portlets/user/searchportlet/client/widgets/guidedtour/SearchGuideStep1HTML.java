package org.gcube.portlets.user.searchportlet.client.widgets.guidedtour;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;

public class SearchGuideStep1HTML extends HTML {
	 
	interface SearchGuideStep1HTMLUiBinder extends UiBinder<Element, SearchGuideStep1HTML> {
	}

	private static SearchGuideStep1HTMLUiBinder uiBinder = GWT.create(SearchGuideStep1HTMLUiBinder.class);

	public SearchGuideStep1HTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());
	}
}
