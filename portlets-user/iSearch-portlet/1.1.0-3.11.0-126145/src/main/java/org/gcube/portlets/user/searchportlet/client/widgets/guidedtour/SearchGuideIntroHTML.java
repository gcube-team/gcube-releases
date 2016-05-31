package org.gcube.portlets.user.searchportlet.client.widgets.guidedtour;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;

public class SearchGuideIntroHTML extends HTML {
	 
	interface SearchGuideIntroHTMLUiBinder extends UiBinder<Element, SearchGuideIntroHTML> {
	}
 
	private static SearchGuideIntroHTMLUiBinder uiBinder = GWT.create(SearchGuideIntroHTMLUiBinder.class);
 
	public SearchGuideIntroHTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());
	}
}
