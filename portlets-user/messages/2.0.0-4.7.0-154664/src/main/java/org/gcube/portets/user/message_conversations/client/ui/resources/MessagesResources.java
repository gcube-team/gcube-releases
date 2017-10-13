package org.gcube.portets.user.message_conversations.client.ui.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface MessagesResources extends ClientBundle {
	public static final String WELCOME_MESSAGE = "Welcome to your Messages.\nMessages lets you stay connected, organized, and productive at work, at home, and everywhere in between. "+
			"Also, you can easily attach very large files to your messages from Workspace." +
			"\n\nThis message will automatically be deleted as soon you'll receive another one.";

	public static final String ERROR_MESSAGE = "We are very sorry, it is impossible to read your messages at the moment.\n\nThis could be either a networking problem or an error on the server."
			+ " Please try to reload this page, if the error persists report the issue.";

	@Source("group.png")
	ImageResource group();

	@Source("user.png")
	ImageResource user();

	@Source("d4science.png")
	ImageResource d4scienceTeam();
}
