package org.gcube.portlets.user.templates.client.components.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;



/**
 * This {@link ImageBundle} is used for all the button icons. Using an image
 * bundle allows all of these images to be packed into a single image, which
 * saves a lot of HTTP requests, drastically improving startup time.
 */
public interface Images extends ClientBundle {
	@Source("title.png")
	ImageResource title();
	
	@Source("heading_1.png")
	ImageResource heading_1();
	@Source("heading_2.png")
	ImageResource heading_2();
	@Source("heading_3.png")
	ImageResource heading_3();
	@Source("heading_4.png")
	ImageResource heading_4();
	@Source("heading_5.png")
	ImageResource heading_5();
	
	@Source("image.png")
	ImageResource image();
		
	@Source("text.png")
	ImageResource text();
	
	@Source("text-table-image.png")
	ImageResource textTableImage();
	
	@Source("simple_text.png")
	ImageResource simple_text();	
	
	@Source("table.png")
	ImageResource table();
	
	@Source("comment_area.png")
	ImageResource comment_area();
	
	@Source("attr_multi.png")
	ImageResource attr_multi();
	
	@Source("attr_unique.png")
	ImageResource attr_unique();
	
	@Source("instruction_area.png")
	ImageResource instruction_area();
	
	@Source("page_break.png")
	ImageResource page_break();
	
	@Source("text_text.png")
	ImageResource text_text();
	
	@Source("text_image.png")
	ImageResource text_image();
	
	@Source("repetitive.png")
	ImageResource repetitive();
	
	
	
}
