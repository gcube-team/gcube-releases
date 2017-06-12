package org.gcube.portlets.user.questions.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {
	@Source("avatarLoader.gif")
	ImageResource avatarLoader();
	
	@Source("Avatar_default.png")
	ImageResource avatarDefaultImage();
	
	@Source("members-loader.gif")
	ImageResource membersLoader();
	
	@Source("post-to.png")
	ImageResource postToIcon();
}
