package org.gcube.portlets.user.reportgenerator.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;

public interface FimesReportTreeStructureResources extends ClientBundle {
	public static final FimesReportTreeStructureResources INSTANCE =  GWT.create(FimesReportTreeStructureResources.class);

	@Source("report.png")
	ImageResource root();

	@Source("page.png")
	ImageResource section();

	@Source("heading_1.png")
	ImageResource heading1();

	@Source("heading_2.png")
	ImageResource heading2();

	@Source("heading_3.png")
	ImageResource heading3();

	@Source("heading_4.png")
	ImageResource heading4();

	@Source("heading_5.png")
	ImageResource heading5();

	@Source("text_dropcaps.png")
	ImageResource text();

	@Source("table.png")
	ImageResource table();

	@Source("image.png")
	ImageResource image();

	@Source("instruction.png")
	ImageResource instructions();

	@Source("comments.png")
	ImageResource comments();

	@Source("sequence.png")
	ImageResource sequence();

	@Source("checkbox.png")
	ImageResource checkbox();
	
	@Source("radio.png")
	ImageResource radio();
	
	@Source("reference.png")
	ImageResource reference();

}

