package org.gcube.portlets.user.lastupdatedfiles.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

public interface IconImages extends ClientBundle {

	public static final IconImages INSTANCE = GWT.create(IconImages.class);

	@Source("default.png")
	ImageResource noType();

	@Source("avi.png")
	ImageResource avi();

	@Source("docx.png")
	ImageResource docx();

	@Source("html.png")
	ImageResource html();
	
	@Source("jpeg.png")
	ImageResource jpeg();
	
	@Source("pptx.png")
	ImageResource pptx();
	
	@Source("pdf.png")
	ImageResource pdf();
	
	@Source("rar.png")
	ImageResource rar();
	
	@Source("url.png")
	ImageResource url();
	
	@Source("zip.png")
	ImageResource zip();
	
	@Source("xls.png")
	ImageResource xls();

}
