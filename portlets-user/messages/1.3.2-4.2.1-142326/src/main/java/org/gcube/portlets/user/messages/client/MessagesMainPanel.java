package org.gcube.portlets.user.messages.client;

import org.gcube.portlets.user.messages.client.view.GxtBorderLayoutMainPanel;
import org.gcube.portlets.user.messages.client.view.message.GxtGridMessagesFilterPanel;
import org.gcube.portlets.user.messages.client.view.message.GxtMessagesPanel;
import org.gcube.portlets.user.messages.client.view.message.GxtToolBarMessage;
import org.gcube.portlets.user.messages.client.view.message.MessagesTreePanel;



public class MessagesMainPanel {

	private GxtBorderLayoutMainPanel borderLayoutContainer;
	private MessagesTreePanel messagesTreePanel;
	private GxtGridMessagesFilterPanel messagesPanelContainer;
	private GxtToolBarMessage toolBarMessage;
	private GxtMessagesPanel gxtMessagesPanel;
	

	public MessagesMainPanel() {
		this.messagesTreePanel = new MessagesTreePanel();
		this.messagesPanelContainer = new GxtGridMessagesFilterPanel();
		this.toolBarMessage = new GxtToolBarMessage(messagesPanelContainer); //instance toolbar
		this.gxtMessagesPanel = new GxtMessagesPanel(messagesPanelContainer, toolBarMessage);

		
		this.borderLayoutContainer = new GxtBorderLayoutMainPanel(messagesTreePanel, gxtMessagesPanel);
	}

	public GxtBorderLayoutMainPanel getBorderLayoutContainer() {
		return borderLayoutContainer;
	}


	public MessagesTreePanel getMessagesPanel() {
		return messagesTreePanel;
	}

	public GxtGridMessagesFilterPanel getMessagesPanelContainer() {
		return messagesPanelContainer;
	}

	public GxtToolBarMessage getToolBarMessage() {
		return toolBarMessage;
	}

}
