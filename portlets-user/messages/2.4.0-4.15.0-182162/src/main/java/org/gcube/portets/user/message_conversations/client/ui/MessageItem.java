package org.gcube.portets.user.message_conversations.client.ui;

import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.client.ui.resources.MessagesResources;
import org.gcube.portets.user.message_conversations.shared.ConvMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialCollection;
import gwt.material.design.client.ui.MaterialCollectionItem;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialToast;

public class MessageItem extends Composite  {

	private static MessageItemUiBinder uiBinder = GWT.create(MessageItemUiBinder.class);

	interface MessageItemUiBinder extends UiBinder<Widget, MessageItem> {
	}

	private final static Color HOVER_MESSAGE_COLOR = Color.GREY_LIGHTEN_4;

	private boolean deleteClicked = true;

	private ApplicationView ap;
	private MaterialCollection parentCollection;
	private ConvMessage myMessage;
	private boolean sent;


	@UiField MaterialCollectionItem item;
	@UiField MaterialImage avatarImage;
	@UiField MaterialLabel senderNameLabel;
	@UiField MaterialLabel subjectLabel;
	@UiField MaterialLabel previewLabel;
	@UiField MaterialLabel timeLabel;
	@UiField MaterialIcon attachmentsIcon;
	@UiField MaterialIcon messageActionIcon;
	@UiField MaterialLink setUnreadButton;
	@UiField MaterialLink deleteButton;
	@UiField MaterialDropDown dd;

	MessagesResources images = GWT.create(MessagesResources.class);


	public MessageItem(final ConvMessage m, MaterialCollection parentCollection, ApplicationView ap, boolean sent) {
		initWidget(uiBinder.createAndBindUi(this));
		item.getElement().getStyle().setCursor(Cursor.POINTER);
		this.sent = sent;
		this.myMessage = m;
		this.ap = ap;
		this.parentCollection = parentCollection;
		if (m.hasAttachments())
			attachmentsIcon.setVisibility(Visibility.VISIBLE);
		if (!sent && !m.isRead())
			item.addStyleName("unread-message");
		if (sent || !myMessage.isRead()) {
			dd.remove(0); //remove the option to set unread if the message is sent	
		}

		if (m.getOwner().getAvatarURL() == null || m.getOwner().getAvatarURL().compareTo("")== 0) {			
			if (sent) 
				avatarImage.setResource(
						m.getRecipients().size() > 1 ? images.group() : images.user());
			else {
				avatarImage.setResource(images.user());
			}
		}
		else {
			if (sent && m.getRecipients().size() > 1) 
				avatarImage.setResource(images.group());
			else 
				avatarImage.setUrl(m.getOwner().getAvatarURL());
		}
		String fullName =  (m.getOwner().getFullName() == null) ? m.getOwner().getUsername() : m.getOwner().getFullName();
		senderNameLabel.setText(fullName);
		subjectLabel.setText(m.getSubject().length()  > 40 ? m.getSubject().substring(0, 37)+" ...": m.getSubject());
		previewLabel.setText(m.getContent());
		timeLabel.setText(Utils.getFormatteDate(m.getDate()));
		if (sent)
			item.setBackgroundColor(Color.WHITE);

		//because on tablet and on mobile cause problems
		if (!Utils.isMobile()) {
			item.addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					messageActionIcon.setVisibility(Visibility.VISIBLE);
					item.setBackgroundColor(HOVER_MESSAGE_COLOR);
				}
			});
			item.addMouseOutHandler(new MouseOutHandler() {			
				@Override
				public void onMouseOut(MouseOutEvent event) {
					messageActionIcon.setVisibility(Visibility.HIDDEN);
					item.setBackgroundColor(Color.WHITE);
				}
			});
		}
		else {//on mobile
			messageActionIcon.setVisibility(Visibility.VISIBLE);
			dd.setHoverable(false);
		}
		messageActionIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
			}
		});

		deleteButton.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				event.stopPropagation();
				MaterialLink link = new MaterialLink("UNDO");
				link.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						deleteClicked = false;
						MaterialToast.fireToast("UNDO DONE");
						item.setVisible(true);
					}
				});
				new MaterialToast(()->{doDelete(m.getSubject());}, link).toast("Deleting Message ("+m.getSubject()+")");
				item.setVisible(false);
			}
		});
		dd.getElement().getStyle().setWidth(300, Unit.PX);
		String activator = "activate"+Random.nextInt();
		dd.setActivator(activator);
		messageActionIcon.setActivates(activator);
	}


	@UiHandler("setUnreadButton")
	void onSetUnread(ClickEvent e) {
		e.stopPropagation();
		if (myMessage.isRead()) {
			ap.setMessageUnread(myMessage, this.sent);
			myMessage.setRead(false);
			item.addStyleName("unread-message");
		} else {
			Window.alert("Message is marked as unread already.");
		}
	}

	private void doDelete(String subject) {
		if (deleteClicked) {
			ap.deleteMessage(myMessage, this.sent);
		}
		else
			deleteClicked = true;
	}

	public void setSelected(boolean active) {
		item.setBackgroundColor(Color.WHITE);
		item.removeStyleName("unread-message");
		item.setActive(active);	
		myMessage.setRead(true);
	}

	@UiHandler("item")
	void onClickedMessage(ClickEvent e) {
		checkHideSideBarOnMobile();
		parentCollection.clearActive();
		ap.readUserMessage(myMessage.getId(), this.sent);
		setSelected(true);
		myMessage.setRead(true);
	}

	private void checkHideSideBarOnMobile() {
		if (Utils.isMobile())
			ap.hideSidePanel();
	}

	public void hideMessageMenu() {
		messageActionIcon.setVisible(false);
	}

}
