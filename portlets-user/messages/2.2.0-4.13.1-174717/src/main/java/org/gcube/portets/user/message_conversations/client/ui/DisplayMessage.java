package org.gcube.portets.user.message_conversations.client.ui;

import org.gcube.portets.user.message_conversations.client.MessageServiceAsync;
import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.shared.ConvMessage;
import org.gcube.portets.user.message_conversations.shared.FileModel;
import org.gcube.portets.user.message_conversations.shared.MessageUserModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTitle;

public class DisplayMessage extends Composite {

	private static DisplayMessageUiBinder uiBinder = GWT.create(DisplayMessageUiBinder.class);

	interface DisplayMessageUiBinder extends UiBinder<Widget, DisplayMessage> {
	}
	@UiField MaterialPanel mainPanel;

	@UiField MaterialRow  messageAttachmentsBody;
	@UiField MaterialButton showMessages;
	@UiField MaterialTextArea messageContent;
	@UiField MaterialTitle messageSender, messageSubject, messageRecipients;
	private ApplicationView ap;
	private MessageServiceAsync convService;
	
	public DisplayMessage(MessageServiceAsync convService, ApplicationView ap) {
		initWidget(uiBinder.createAndBindUi(this));
		this.convService = convService;
		this.ap = ap;
	}

	/**
	 * Display the message in the main panel
	 * @param m message to display
	 */
	public void showMessage(ConvMessage m) {
		String recipientsLabel = "";

		for (MessageUserModel r : m.getRecipients()) {
			String fullName =  (r.getFullName() == null) ? r.getUsername() : r.getFullName(); 
			recipientsLabel+= fullName+"; ";
		}
		String fullName =  (m.getOwner().getFullName() == null) ? m.getOwner().getUsername() : m.getOwner().getFullName();
		messageSender.setTitle(fullName + ", " + Utils.getFormatteDate(m.getDate()));
		messageSubject.setTitle(m.getSubject());
		messageRecipients.setTitle("To: "+recipientsLabel);
		messageContent.setText(m.getContent());
		messageRecipients.setVisible(true);
		messageContent.setVisible(true);

		messageAttachmentsBody.clear();
		messageAttachmentsBody.setVisible(m.hasAttachments());
		messageAttachmentsBody.add(new MaterialLabel("Attachments: "));
		int i = 0;
		for (FileModel item : m.getAttachments()) {
			String activator = "item"+i;
			String attachmentName = (item.getName().length() > 25) ? item.getName().substring(0, 20) + "..." : item.getName();
			MaterialButton toAdd = new MaterialButton(attachmentName, IconType.ARROW_DROP_DOWN);	
			toAdd.setTitle(item.getName());

			AttachmentMenu dd = new AttachmentMenu(convService, toAdd, item);
			dd.setActivator(activator);
			dd.setSeparator(true);

			toAdd.setMargin(5);
			toAdd.setPaddingRight(5);
			toAdd.setPaddingLeft(5);
			toAdd.setWaves(WavesType.DEFAULT);
			toAdd.setActivates(activator);
			toAdd.setIconPosition(IconPosition.RIGHT);
			toAdd.setBackgroundColor(Color.RED_DARKEN_1);
			toAdd.getElement().getStyle().setBackgroundImage("none");	
			toAdd.getElement().getStyle().setTextTransform(TextTransform.NONE);	

			messageAttachmentsBody.add(toAdd);
			messageAttachmentsBody.add(dd);
			i = i+1;
		}
	}
	
	@UiHandler("showMessages")
	void onShowMessages(ClickEvent e) {
		ap.showSidePanel();
	}	
	
	MaterialPanel getMainPanel() {
		return mainPanel;
	}
}
