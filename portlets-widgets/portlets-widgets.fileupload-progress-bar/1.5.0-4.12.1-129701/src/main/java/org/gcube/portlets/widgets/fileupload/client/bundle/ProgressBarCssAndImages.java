package org.gcube.portlets.widgets.fileupload.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface ProgressBarCssAndImages extends ClientBundle {

	public static final ProgressBarCssAndImages INSTANCE = GWT.create(ProgressBarCssAndImages.class);

	@Source("FileUpload.css")
	public CssResource css();
	
	@Source("PanelFileUpload.css")
	public CssResource panelCss();

	@Source("spinning.gif")
	ImageResource spinner();

	@Source("error.png")
	ImageResource error();

	@Source("ok.png")
	ImageResource ok();

	@ImageOptions(repeatStyle = RepeatStyle.Horizontal)
	@Source("progress.png")
	ImageResource progressTexture();

	interface MyCssResource extends CssResource {
		String myBackground();
	}
}
