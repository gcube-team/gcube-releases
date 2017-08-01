package org.gcube.portlets.d4sreporting.common.client.uicomponents.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {
	@Source("close.gif")
	ImageResource close();
	
	@Source("close_darker.gif")
	ImageResource close_darker();
	
	@Source("lock_delete.png")
	ImageResource locked();
	@Source("lock_darker_delete.png")
	ImageResource locked_darker();
	@Source("lock_add.png")
	ImageResource unlocked();
	@Source("lock_darker_add.png")
	ImageResource unlocked_darker();
}
