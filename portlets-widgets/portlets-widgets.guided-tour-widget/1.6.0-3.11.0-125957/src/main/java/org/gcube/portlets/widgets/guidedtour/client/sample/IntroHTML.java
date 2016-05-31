package org.gcube.portlets.widgets.guidedtour.client.sample;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;


public class IntroHTML extends HTML {
	
	interface IntroHTMLUiBinder extends UiBinder<Element, IntroHTML> {
	}
	
	private static IntroHTMLUiBinder uiBinder = GWT.create(IntroHTMLUiBinder.class);

	public IntroHTML() {
		setHTML(uiBinder.createAndBindUi(this).getInnerHTML());		
	}
}
