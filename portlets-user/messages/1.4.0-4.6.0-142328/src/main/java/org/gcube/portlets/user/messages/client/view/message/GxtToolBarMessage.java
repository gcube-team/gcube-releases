package org.gcube.portlets.user.messages.client.view.message;

import java.util.List;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.MessagesApplicationController;
import org.gcube.portlets.user.messages.client.event.DeleteMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent.MarkType;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent.OpenType;
import org.gcube.portlets.user.messages.client.event.SendMessageEvent;
import org.gcube.portlets.user.messages.client.resources.Resources;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.extjs.gxt.ui.client.Style.ButtonArrowAlign;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.HandlerManager;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtToolBarMessage {

	private Button btnCreateMessage = null;
//	private Button btnDownloadMessages = null;
	private Button btnForwardMessage = null;
	private Button btnDeleteMessage = null;
	private Button btnReplyMessage = null;
//	private GxtGridFilterGroupPanel gridGroupViewContainer;
	private HandlerManager eventBus = MessagesApplicationController.getEventBus();
	
	private ToolBar toolBar = new ToolBar();
	private GxtGridMessagesFilterPanel messagesPanelContainer;
	private Button btnReplyAllMessage;

	public GxtToolBarMessage() {
		initToolBar();
		addSelectionListenersOnToolBar();
	}

	public GxtToolBarMessage(GxtGridMessagesFilterPanel messagesPanelContainer) {
		this();
		this.messagesPanelContainer = messagesPanelContainer;
	}

	private void initToolBar() {

		btnCreateMessage = new Button(ConstantsPortletMessages.MESSAGE_CREATE_NEW_MESSAGE, Resources.getIconNewMail());
		btnCreateMessage.setScale(ButtonScale.MEDIUM);
		btnCreateMessage.setIconAlign(IconAlign.TOP);
		btnCreateMessage.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnCreateMessage);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new SeparatorToolItem());

//		btnDownloadMessages = new Button(ConstantsPortletMessages.MESSAGE_GET_ALL_NEW_MESSAGES, Resources.getIconDownloadEmails());
//		btnDownloadMessages.setScale(ButtonScale.SMALL);
//		btnDownloadMessages.setIconAlign(IconAlign.TOP);
//		btnDownloadMessages.setArrowAlign(ButtonArrowAlign.BOTTOM);
//		toolBar.add(btnDownloadMessages);
//		toolBar.add(new SeparatorToolItem());
//		toolBar.add(new SeparatorToolItem());
		
		//modified for Massi
		btnReplyMessage = new Button(ConstantsPortletMessages.MESSAGE_REPLY, Resources.getIconReplyMail());
		btnReplyMessage.setScale(ButtonScale.MEDIUM);
		btnReplyMessage.setIconAlign(IconAlign.TOP);
		btnReplyMessage.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnReplyMessage);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new SeparatorToolItem());
		
		//modified for Massi
		btnReplyAllMessage = new Button(ConstantsPortletMessages.MESSAGE_REPLY_ALL, Resources.getIconReplyAllMail());
		btnReplyAllMessage.setScale(ButtonScale.MEDIUM);
		btnReplyAllMessage.setIconAlign(IconAlign.TOP);
		btnReplyAllMessage.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnReplyAllMessage);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new SeparatorToolItem());
		
		btnForwardMessage = new Button(ConstantsPortletMessages.MESSAGE_FORWARD_MESSAGE,Resources.getIconEmailForward());
		btnForwardMessage.setScale(ButtonScale.MEDIUM);
		btnForwardMessage.setIconAlign(IconAlign.TOP);
		btnForwardMessage.setArrowAlign(ButtonArrowAlign.BOTTOM);
		toolBar.add(btnForwardMessage);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new SeparatorToolItem());
		
		btnDeleteMessage = new Button(ConstantsPortletMessages.MESSAGE_DELETE_MESSAGE, Resources.getIconDeleteMessage());
		btnDeleteMessage.setScale(ButtonScale.MEDIUM);
		btnDeleteMessage.setIconAlign(IconAlign.TOP);
		btnDeleteMessage.setArrowAlign(ButtonArrowAlign.BOTTOM);
		
		toolBar.add(btnDeleteMessage);
		
		this.activeButtonsOnSelect(false);
	}

	private void addSelectionListenersOnToolBar() {

		btnCreateMessage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				eventBus.fireEvent(new SendMessageEvent(null));
			}
		        	
		});
		
		
//		btnDownloadMessages.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				
////				if(messagesPanelContainer.getCurrentMessageType().equals(GXTCategoryItemInterface.MS_SENT)){
////					eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_SENT, false));
////				}else if(messagesPanelContainer.getCurrentMessageType().equals(GXTCategoryItemInterface.MS_RECEIVED)){
////						eventBus.fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_RECEIVED, false));
////					}
//				
//				eventBus.fireEvent(new GetAllNewMessagesEvent());
//			}
//			
//		});
		
		//modified for Massi
		btnReplyMessage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
	
        	MessageModel selected = messagesPanelContainer.getGridMessages().getSelectionModel().getSelectedItem();  

	        	if(selected!=null){
	        		eventBus.fireEvent(new OpenMessageEvent(selected.getId(), OpenType.REPLY, selected.getMessageType()));
	        		if(selected.getIsRead().equals("false"))
	        			eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.BOTH));
				}
			}
		});
		
		
		//modified for Massi
		btnReplyAllMessage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
	
        	MessageModel selected = messagesPanelContainer.getGridMessages().getSelectionModel().getSelectedItem();  

	        	if(selected!=null){
	        		eventBus.fireEvent(new OpenMessageEvent(selected.getId(), OpenType.REPLYALL, selected.getMessageType()));
	        		
	        		if(selected.getIsRead().equals("false"))
		        		  eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.BOTH));
	        	}

			}
		});
		
		
		btnForwardMessage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
	
        	MessageModel selected = messagesPanelContainer.getGridMessages().getSelectionModel().getSelectedItem();  

	        	if(selected!=null){
	        		eventBus.fireEvent(new OpenMessageEvent(selected.getId(), OpenType.FORWARD, selected.getMessageType()));
	        		if(selected.getIsRead().equals("false"))
	        			eventBus.fireEvent(new MarkMessageEvent(selected, true, MarkType.BOTH));
	        	}
        	}
		});

		

		btnDeleteMessage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				   final List<MessageModel> listMessageModel = messagesPanelContainer.getGridMessages().getSelectionModel().getSelectedItems();  
		   
				   if(listMessageModel==null || listMessageModel.size()==0)
					   return;
				   
				   if(listMessageModel.size()==1){
					   final MessageModel message = listMessageModel.get(0);
				
					   final MessageBox confirm = MessageBox.confirm("Delete confirm?", "Do you want delete: \""+message.getSubject()+"\"?", null);
			        	 
					   confirm.addCallback(new Listener<MessageBoxEvent>() {
							
							@Override
							public void handleEvent(MessageBoxEvent be) {
								//IF NOT CANCELLED
								String clickedButton = be.getButtonClicked().getItemId();
								if(clickedButton.equals(Dialog.YES)){
									 eventBus.fireEvent(new DeleteMessageEvent(message));
									 confirm.close();
								}
								
								if(clickedButton.equals(Dialog.NO)){
									confirm.close();
								}
							}
						});
			        	 
			        	confirm.show();
			        	
				   }else if(listMessageModel.size()>1){
					   
					   final MessageBox confirm = MessageBox.confirm("Delete confirm?", "Do you want delete "+listMessageModel.size()+" messages?", null);
			        	 
					   confirm.addCallback(new Listener<MessageBoxEvent>() {
							
							@Override
							public void handleEvent(MessageBoxEvent be) {
								//IF NOT CANCELLED
								String clickedButton = be.getButtonClicked().getItemId();
								if(clickedButton.equals(Dialog.YES)){
									for(MessageModel message : listMessageModel)
						   	        	  eventBus.fireEvent(new DeleteMessageEvent(message));
						 
									 confirm.close();
								}
								
								if(clickedButton.equals(Dialog.NO)){
									confirm.close();
								}
							}
						});
			        	 
			        	confirm.show();
				   }
			}
		});
		

	}

	public void activeButtonsOnSelect(boolean active) {

		if (!active) {
			this.btnDeleteMessage.disable();
			this.btnForwardMessage.disable();
			this.btnReplyMessage.disable();
			this.btnReplyAllMessage.disable();
		} else {
			this.btnDeleteMessage.enable();
			this.btnForwardMessage.enable();
			this.btnReplyMessage.enable();
			this.btnReplyAllMessage.enable();
		}
	}

	public ToolBar getToolBar() {
		return this.toolBar;
	}

}
