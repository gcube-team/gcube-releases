package org.gcube.portlets.user.searchportlet.client.widgets.guidedtour;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;

public class SearchGuideStep3HTML extends HTML {
	 
	interface SearchGuideStep3HTMLUiBinder extends UiBinder<Element, SearchGuideStep3HTML> {
	}

	private static SearchGuideStep3HTMLUiBinder uiBinder = GWT.create(SearchGuideStep3HTMLUiBinder.class);

	public SearchGuideStep3HTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());
	}
}
