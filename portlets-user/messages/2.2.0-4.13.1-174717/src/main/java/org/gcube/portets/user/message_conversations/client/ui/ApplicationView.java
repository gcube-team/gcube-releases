package org.gcube.portets.user.message_conversations.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gcube.portets.user.message_conversations.client.MessageService;
import org.gcube.portets.user.message_conversations.client.MessageServiceAsync;
import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.client.ui.resources.MessagesResources;
import org.gcube.portets.user.message_conversations.shared.ConvMessage;
import org.gcube.portets.user.message_conversations.shared.MessageUserModel;

import com.google.gwt.core.client.GWT;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.MaterialAnchorButton;
import gwt.material.design.client.ui.MaterialBadge;
import gwt.material.design.client.ui.MaterialCollection;
import gwt.material.design.client.ui.MaterialFAB;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialProgress;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.animate.MaterialAnimation;
import gwt.material.design.client.ui.animate.Transition;
/**
 * @author Massimiliano Assante, CNR-ISTI
 */
public class ApplicationView extends Composite {

	interface Binder extends UiBinder<Widget, ApplicationView> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	private final MessageServiceAsync convService = GWT.create(MessageService.class);


	private boolean toggle = false;
	private boolean toggleSwitch = false;
	private int unreadMessages = 0;
	private int totalMessages = 0;


	@UiField ScrollPanel scrollerPanel;
	@UiField MaterialRow rightPanel;
	@UiField HTMLPanel htmlPanel;
	@UiField MaterialFAB FAB;
	@UiField MaterialLink menu, newMessage, switcher;
	@UiField MaterialAnchorButton replyAll, reply, forward;
	@UiField MaterialCollection messagesCollection;
	@UiField MaterialProgress messageLoader, messagesLoader;
	@UiField MaterialBadge badge;

	private ConvMessage currentSelected;
	private DisplayMessage displayMessage;
	private WriteMessage newMessageDisplay;
	private boolean isSendTo = false;

	public ApplicationView(String... sendToUserNames) {
		initWidget(uiBinder.createAndBindUi(this));
		((ServiceDefTarget) convService).setServiceEntryPoint(Utils.getServiceEntryPoint());
		displayMessage = new DisplayMessage(convService, this);
		newMessageDisplay = new WriteMessage(convService, this);
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {   
				int height = event.getHeight() - 100;
				scrollerPanel.setHeight(height+"px"); 
			}
		});
		scrollerPanel.add(displayMessage);
		if (! Utils.isMobile()) {
			reply.setTooltip("Reply");
			reply.setTooltipPosition(Position.LEFT);
			replyAll.setTooltip("Reply All");
			replyAll.setTooltipPosition(Position.LEFT);
			forward.setTooltip("Forward");
			forward.setTooltipPosition(Position.LEFT);
		}
	
		String[] usernamesToSendTo = sendToUserNames;
		if (usernamesToSendTo != null && usernamesToSendTo.length > 0) {
			prepareNewMessageForSendTo(usernamesToSendTo);
			isSendTo = true;
			messageLoader.setVisible(false);
		}
		readUserMessages(false, isSendTo);

	}

	/**
	 * 
	 * @param sent
	 */
	public void readUserMessages(final boolean sent, boolean isSendTo) {
		messagesLoader.setVisible(true);
		convService.getMessages(sent, new AsyncCallback<ArrayList<ConvMessage>>() {
			@Override
			public void onFailure(Throwable arg0) {
				RootPanel.get("create-users-container").add(new HTML("ERROR "));
			}

			@Override
			public void onSuccess(ArrayList<ConvMessage> messages) {
				if (messages != null) {
					showMessages(messages, sent);
					if (!isSendTo) {
						if (messages.size() > 0)
							readUserMessage(messages.get(0).getId(), sent);
						else {
							writeWelcomeMessage();
							messageLoader.setVisible(false);
						}
					}
				} else {
					showErrorOccurred();
				}
			}
		});
	}
	private void showErrorOccurred() {
		messagesCollection.clear();
		messagesLoader.setVisible(false);
		messageLoader.setVisible(false);
		hideSidePanel();
		writeErrorMessage();
	}
	/**
	 * 
	 * @param messages
	 * @param sent
	 */
	public void showMessages(ArrayList<ConvMessage> messages, boolean sent) {
		messagesCollection.clear();
		messagesLoader.setVisible(false);
		messagesCollection.setVisible(true);
		int scrollerHeight = Window.getClientHeight() - 100;
		scrollerPanel.setHeight(scrollerHeight+"px"); 
		for (ConvMessage convMessage : messages) {
			messagesCollection.add(new MessageItem(convMessage, messagesCollection, this, sent));
			if (! (sent || convMessage.isRead()))
				unreadMessages++;
			totalMessages++;
		}
		if (messages.size() > 0) {
			MessageItem first = (MessageItem) messagesCollection.getChildrenList().get(0);
			first.setSelected(true);
		} else {

		}
		updateBadge(sent);
	}
	/**
	 * 
	 * @param sent 
	 * @param event
	 */
	public void deleteMessage(final ConvMessage toDelete, boolean sent) {
		convService.deleteMessageById(toDelete.getId(), sent, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				MaterialToast.fireToast("Message Deleted Failed for ("+toDelete.getSubject()+")");						
			}
			@Override
			public void onSuccess(Boolean result) {
				MaterialToast.fireToast("Message Deleted ("+toDelete.getSubject()+")");
				totalMessages = totalMessages - 1;
				updateBadge(sent);
			}
		});
	}

	/**
	 * 
	 * @param event
	 */
	public void setMessageUnread(ConvMessage toSet, boolean sent) {
		convService.markMessageUnread(toSet.getId(), sent,  new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {		
			}
			@Override
			public void onSuccess(Boolean result) {
				unreadMessages++;
				updateBadge(false);
			}
		});
	}


	/**
	 * 
	 * @param messageId
	 * @param sent
	 */
	public void readUserMessage(String messageId,  final boolean sent) {
		messageLoader.setColor(Utils.getRandomColor());
		messageLoader.setVisible(true);
		scrollerPanel.clear();
		convService.getMessageById(messageId, toggleSwitch, new AsyncCallback<ConvMessage>() {
			@Override
			public void onFailure(Throwable arg0) {
				RootPanel.get("create-users-container").add(new HTML("ERROR getting message "));
				messageLoader.setVisible(false);
			}

			@Override
			public void onSuccess(ConvMessage toShow) {
				messageLoader.setVisible(false);
				displayMessage.showMessage(toShow);
				scrollerPanel.add(displayMessage);				
				setCurrentSelectedMessage(toShow);
				if (!sent)
					decreaseUnreadCounter(toShow);
				FAB.setVisible(true);
			}
		});
	}
	private void updateBadge(boolean sent) {
		String badgeText = totalMessages + (sent ? " sent" : " (" + unreadMessages + " unread" + ")");
		if (unreadMessages == 0 && !sent)
			badgeText = totalMessages+" received";
		badge.setText(badgeText);
		badge.setVisible(true);
	}
	private void decreaseUnreadCounter(ConvMessage toShow) {
		if (unreadMessages > 0 && !toShow.isRead())
			unreadMessages--;
		updateBadge(false);
	}

	private void resetCounters() {
		badge.setVisible(false);
		unreadMessages = 0;
		totalMessages = 0;
	}



	@UiHandler("switcher")
	void onSwitchSentInbox(ClickEvent e) {
		resetCounters();		
		MaterialAnimation animation = new MaterialAnimation();
		animation.setDelay(0);
		animation.setDuration(1000);
		animation.transition(Transition.FLIPINX);		
		animation.animate(switcher);
		if(!toggleSwitch){
			switcher.setIconType(IconType.SEND);
		}else{
			switcher.setIconType(IconType.INBOX);
		}
		toggleSwitch = ! toggleSwitch;
		readUserMessages(toggleSwitch, false);
	}

	@UiHandler("menu")
	void onHideShowSidebar(ClickEvent e) {
		if(toggle){
			showSidePanel();
		}else{
			hideSidePanel();
		}
	}


	private void displayNewOrReplyMessage() {
		FAB.setVisible(false);
		messagesCollection.clearActive();
		scrollerPanel.clear();
		scrollerPanel.add(newMessageDisplay);
		newMessageDisplay.setFocusOnUsersInput();
		if (Utils.isMobile())
			hideSidePanel();
	}

	private void prepareNewMessageForSendTo(String[] usernamesToSendTo) {
		newMessageDisplay = new WriteMessage(convService, this);
		newMessageDisplay.setNewMessageForSendTo(usernamesToSendTo);
		displayNewOrReplyMessage();
	}

	@UiHandler("newMessage")
	void onNewMessage(ClickEvent e) {
		newMessageDisplay = new WriteMessage(convService, this);
		displayNewOrReplyMessage();
	}	

	@UiHandler("reply")
	void onReplyMessage(ClickEvent e) {	
		if (currentSelected != null) {
			newMessageDisplay = new WriteMessage(convService, this);
			ConvMessage msg = currentSelected;
			newMessageDisplay.setIsReply(msg);	
			displayNewOrReplyMessage();
		} 
		else {
			Window.alert("Cannot find which message to reply, please report the issue");
		}
	}
	@UiHandler("replyAll")
	void onReplyAllMessage(ClickEvent e) {
		if (currentSelected != null) {
			newMessageDisplay = new WriteMessage(convService, this);
			ConvMessage msg = currentSelected;
			newMessageDisplay.setIsReplyAll(msg);	
			displayNewOrReplyMessage();
		} 
		else {
			Window.alert("Cannot find which message to reply all, please report the issue");
		}
	}
	@UiHandler("forward")
	void onForwardMessage(ClickEvent e) {	
		if (currentSelected != null) {
			newMessageDisplay = new WriteMessage(convService, this);
			ConvMessage msg = currentSelected;
			newMessageDisplay.setIsForward(msg);	
			displayNewOrReplyMessage();
		} 
		else {
			Window.alert("Cannot find which message to forward, please report the issue");
		}
	}
	protected void showSidePanel() {
		displayMessage.getMainPanel().setLeft(350);
		displayMessage.getMainPanel().setGrid("l8 m12 s12");
		newMessageDisplay.getMainPanel().setLeft(350);
		newMessageDisplay.getMainPanel().setGrid("l8 m12 s12");
		rightPanel.setLeft(0);
		toggle = ! toggle;
	}

	protected void hideSidePanel() {
		displayMessage.getMainPanel().setLeft(0);
		displayMessage.getMainPanel().setGrid("l12 s12 m12");
		newMessageDisplay.getMainPanel().setLeft(0);
		newMessageDisplay.getMainPanel().setGrid("l12 s12 m12");

		rightPanel.setLeft(-350);
		toggle = ! toggle;
	}

	private void setCurrentSelectedMessage(ConvMessage msg) {
		currentSelected = msg;
	}

	private void writeWelcomeMessage() {
		MessagesResources images = GWT.create(MessagesResources.class);
		List<MessageUserModel> recipients = Arrays.asList(new MessageUserModel(0, "you", "You", ""));
		@SuppressWarnings("deprecation")
		MessageUserModel sender = new MessageUserModel(0, "jarvis", "D4Science Team", images.d4scienceTeam().getURL(), "", "");
		ConvMessage welcomeMessage = new ConvMessage("0", "Welcome to your Messages", sender, recipients , new Date(), MessagesResources.WELCOME_MESSAGE, true, false);
		FAB.setVisible(false);
		displayMessage.showMessage(welcomeMessage);
		welcomeMessage.setContent(MessagesResources.WELCOME_MESSAGE.substring(0, 102)+ " ...");
		MessageItem item = new MessageItem(welcomeMessage, messagesCollection, this, false);
		item.hideMessageMenu();
		messagesCollection.add(item);
		totalMessages++;
		updateBadge(false);
	}

	private void writeErrorMessage() {
		MessagesResources images = GWT.create(MessagesResources.class);
		List<MessageUserModel> recipients = Arrays.asList(new MessageUserModel(0, "you", "You", ""));
		@SuppressWarnings("deprecation")
		MessageUserModel sender = new MessageUserModel(0, "jarvis", "D4Science Team", images.d4scienceTeam().getURL(), "", "");
		ConvMessage welcomeMessage = new ConvMessage("0", "An error occurred!", sender, recipients , new Date(), MessagesResources.ERROR_MESSAGE, true, false);
		FAB.setVisible(false);
		displayMessage.showMessage(welcomeMessage);
		welcomeMessage.setContent(MessagesResources.ERROR_MESSAGE.substring(0, 102)+ " ...");
		MessageItem item = new MessageItem(welcomeMessage, messagesCollection, this, false);
		item.hideMessageMenu();
		messagesCollection.add(item);
		totalMessages++;
		updateBadge(false);
	}
}
