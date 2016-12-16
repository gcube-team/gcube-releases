package org.gcube.portlets.widgets.applicationnews.client.bundles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface CssAndImages extends ClientBundle {

	public static final CssAndImages INSTANCE = GWT.create(CssAndImages.class);

	@Source("ApplicationNews_Widget.css")
	public CssResource css();
	
	@Source("feeds-loader.gif")
	ImageResource spinner();

	@Source("yes.png")
	ImageResource ok();

	@ImageOptions(repeatStyle = RepeatStyle.None)
	@Source("post-news.png")
	ImageResource buttonImage();
	
	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("feed-preview-border.png")
	ImageResource feedPreviewBorderImage();
	
	
}
