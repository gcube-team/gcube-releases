package org.gcube.portlets.widgets.applicationnews.client.templates;



import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class LinkPreviewer extends Composite {

	private static LinkPreviewUiBinder uiBinder = GWT
			.create(LinkPreviewUiBinder.class);

	interface LinkPreviewUiBinder extends UiBinder<Widget, LinkPreviewer> {
	}
	
	public LinkPreviewer(String title, String titleDesc, String host, String linkThumbUrl, String url) {
		initWidget(uiBinder.createAndBindUi(this));
		titleArea.setHTML("<a class=\"link\">"+title+"</a>");
		String desc = titleDesc;
		descText.setHTML((desc.length() > 256) ? desc.substring(0, 256)+"..." : desc);		
		image.setUrl(linkThumbUrl);
		image.setWidth("80px");
	}
	@UiField
	HTML titleArea;
	@UiField
	HTML descText;	
	@UiField
	Image image;	
}
