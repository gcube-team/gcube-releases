package org.gcube.portlets.user.messages.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.gcube.portlets.user.messages.client.alert.MessageBoxAlert;
import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;
import org.gcube.portlets.user.messages.shared.SessionExpiredException;
import org.gcube.portlets.widgets.wsmail.client.forms.MailForm;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class MessagesApplicationSubscriber.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 6, 2015
 */
public class MessagesApplicationSubscriber implements SubscriberInterface{
	
	
	private MessagesApplicationController msgController;
	private static boolean notifyMessageDatabook = false;

	/**
	 * Instantiates a new messages application subscriber.
	 *
	 * @param msgController the msg controller
	 */
	public MessagesApplicationSubscriber(MessagesApplicationController msgController) {
		this.msgController = msgController;
		this.msgController.subscribe(this, new EventsTypeEnum[] {
				EventsTypeEnum.DELETED_MESSAGE,
				EventsTypeEnum.MARK_MESSAGE_AS_READ,
				EventsTypeEnum.SELECTED_MESSAGE,
				EventsTypeEnum.CREATE_NEW_MESSAGE,
				EventsTypeEnum.REPLY_FORWARD_MESSAGE,
				EventsTypeEnum.LOAD_MESSAGES_EVENT
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#rootLoaded(org.gcube.portlets.user.messages.shared.FileModel)
	 */
	@Override
	public void rootLoaded(FileModel root) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#loadSentMessages()
	 */
	@Override
	public void loadSentMessages() {

		//the interface is update if and only if it's view received messages

		msgController.getMessagesPanelContainer().mask(ConstantsPortletMessages.LOADING,ConstantsPortletMessages.LOADINGSTYLE);
		msgController.getMessagesPanelContainer().setBorderAsOnSearch(false);

		MessagesApplicationController.rpcMessagesManagementService.getAllMessagesSent(new AsyncCallback<List<MessageModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				
				if(caught instanceof SessionExpiredException){
					ConstantsPortletMessages.messagesLogger.log(Level.INFO, "Session expired");
					msgController.viewSessionExpiredPanel();
					return;
				}
				
				new MessageBoxAlert("Error", "Sorry, an error occurred on retrieving sent messages, try again later", null);
				
//				new MessageBoxAlert("Error", "get all messages sent", null);
				msgController.getMessagesPanelContainer().setMessagesType(null);
				msgController.getMessagesPanelContainer().unmask();

			}

			@Override
			public void onSuccess(List<MessageModel> result) {

				if(result!=null){
					ConstantsPortletMessages.messagesLogger.log(Level.INFO, "list messages size is: " +result.size());
					
					/*for(MessageModel mess: result){
						System.out.println("Mess: " + mess.getSubject() + " " + mess.getFromLogin() + " " + mess.getDate() + " " + mess.getNumAttchments() + " "+ mess.getAttachmentsNamesView());
					}*/

					msgController.getMessagesPanelContainer().updateStore(result);	
				}

				msgController.getMessagesPanelContainer().setMessagesType(GXTCategoryItemInterface.MS_SENT); //Set current view messages as sent
				msgController.getMessagesPanelContainer().unmask();
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#loadReceivedMessages()
	 */
	@Override
	public void loadReceivedMessages() {

		msgController.getMessagesPanelContainer().mask(ConstantsPortletMessages.LOADING,ConstantsPortletMessages.LOADINGSTYLE);
		msgController.getMessagesPanelContainer().setBorderAsOnSearch(false);

		MessagesApplicationController.rpcMessagesManagementService.getAllMessagesReceived(new AsyncCallback<List<MessageModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				
				if(caught instanceof SessionExpiredException){
					ConstantsPortletMessages.messagesLogger.log(Level.INFO, "Session expired");
					msgController.viewSessionExpiredPanel();
					return;
				}
				
				new MessageBoxAlert("Error", "Sorry, an error occurred on retrieving received messages, try again later", null);
//				new MessageBoxAlert("Error", "get all messages receveid", null);
				msgController.getMessagesPanelContainer().setMessagesType(null);
				msgController.getMessagesPanelContainer().unmask();

			}

			@Override
			public void onSuccess(List<MessageModel> result) {

				if(result!=null){
					ConstantsPortletMessages.messagesLogger.log(Level.INFO, "list messages size is: " +result.size() );
					
					/*for(MessageModel mess: result){
						System.out.println("Mess: " + mess.getSubject() + " " + mess.getFromLogin() + " " + mess.getDate() + " " + mess.getNumAttchments() + " "+ mess.getAttachmentsNamesView());
					}*/

					msgController.getMessagesPanelContainer().updateStore(result);
					
					if(!notifyMessageDatabook){
						//notify user message notification as read
						msgController.rpcMessagesManagementService.setAllUserMessageNotificationsRead(new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(Boolean result) {
								// TODO Auto-generated method stub
								
							}
						});
						notifyMessageDatabook = true;
					}
					
				}

				msgController.getMessagesPanelContainer().setMessagesType(GXTCategoryItemInterface.MS_RECEIVED); //Set current view messages as received
				msgController.getMessagesPanelContainer().unmask();

				
			}

		});

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#deleteMessage(java.lang.String)
	 */
	@Override
	public void deleteMessage(String id) {
		msgController.getMessagesPanelContainer().deleteMessage(id);

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#markAsReadMessage(java.lang.String, boolean)
	 */
	@Override
	public void markAsReadMessage(String id, boolean isRead) {
		msgController.getMessagesPanelContainer().markMessageAsRead(id, isRead);

	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#updatePrevieMessage(org.gcube.portlets.user.messages.shared.MessageModel)
	 */
	public void updatePrevieMessage(MessageModel message) {

		msgController.getMessagesPanelContainer().setBodyValue(message.getSubject(), message.getTextMessage());
		msgController.getMessagesPanelContainer().setAttachs(message.getListAttachments());

		if(message.getMessageType().equals(GXTCategoryItemInterface.MS_RECEIVED)){
			String nameFrom = message.getFullName()!=null?message.getFullName():message.getFromLogin();

			msgController.getMessagesPanelContainer().setFromTitle(nameFrom, ""+message.getDate(), message.getListFullNameToContacts());
		}
		else
			msgController.getMessagesPanelContainer().setToTitle(message.getListFullNameToContacts(), ""+message.getDate());

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#createNewMessage(java.util.HashMap)
	 */
	public void createNewMessage(final HashMap<String, String> hashAttachs) {
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				if (hashAttachs.size() == 0) { //no attachments
					new MailForm();
				} else {
					new MailForm(hashAttachs);
				}
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}   
		});
	}

	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#forwardMessage(java.lang.String, java.lang.String, java.util.List, java.util.Date, java.util.HashMap, java.lang.String)
	 */
	public void forwardMessage(final String fromLogin, final String subject, final List<String> listToLogin, final Date date, final HashMap<String, String> hashAttachs, final String textMessage) {
		ConstantsPortletMessages.messagesLogger.log(Level.INFO, "To:  " +listToLogin);
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				new MailForm(fromLogin, subject, listToLogin, date, hashAttachs, textMessage, false); 
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface#replyMessage(java.lang.String, java.lang.String, java.util.List, java.util.Date, java.lang.String)
	 */
	public void replyMessage(final String fromLogin, final String subject, final List<String> listContactsLogin, final Date date, final String textMessage) {
		ConstantsPortletMessages.messagesLogger.log(Level.INFO, "To: " +listContactsLogin);
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				new MailForm(fromLogin, subject, listContactsLogin, date, new HashMap<String, String>(), textMessage, true); 
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}
		});
		
	}
}
