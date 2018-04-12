package org.gcube.portets.user.message_conversations.client;

import org.gcube.portets.user.message_conversations.client.ui.ApplicationView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MessageConversations implements EntryPoint {
	public static final String DIV_CONTAINER_ID = "create-users-container";
	public static final String ARTIFACT_ID = "messages";
	
	private ApplicationView ap;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ap = new ApplicationView();
		RootPanel.get(DIV_CONTAINER_ID).add(ap);
	}
}
