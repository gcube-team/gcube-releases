package org.gcube.portlets.user.messages.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.messages.client.alert.InfoDisplay;
import org.gcube.portlets.user.messages.client.alert.MessageBoxAlert;
import org.gcube.portlets.user.messages.client.event.DeleteMessageEvent;
import org.gcube.portlets.user.messages.client.event.DeleteMessageEventHandler;
import org.gcube.portlets.user.messages.client.event.FileDownloadEvent;
import org.gcube.portlets.user.messages.client.event.FileDownloadEvent.DownloadType;
import org.gcube.portlets.user.messages.client.event.FileDownloadEventHandler;
import org.gcube.portlets.user.messages.client.event.GetAllNewMessagesEvent;
import org.gcube.portlets.user.messages.client.event.GetAllNewMessagesEventHandler;
import org.gcube.portlets.user.messages.client.event.GridMessageSelectedEvent;
import org.gcube.portlets.user.messages.client.event.GridMessageSelectedEventHandler;
import org.gcube.portlets.user.messages.client.event.GridMessageUnSelectedEvent;
import org.gcube.portlets.user.messages.client.event.GridMessageUnSelectedEventHandler;
import org.gcube.portlets.user.messages.client.event.LoadMessagesEvent;
import org.gcube.portlets.user.messages.client.event.LoadMessagesEventHandler;
import org.gcube.portlets.user.messages.client.event.MarkMessageEvent;
import org.gcube.portlets.user.messages.client.event.MarkMessageEventHandler;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent;
import org.gcube.portlets.user.messages.client.event.OpenMessageEvent.OpenType;
import org.gcube.portlets.user.messages.client.event.OpenMessageEventHandler;
import org.gcube.portlets.user.messages.client.event.PreviewMessageEvent;
import org.gcube.portlets.user.messages.client.event.PreviewMessageEventHandler;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentAndOpenEvent;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentAndOpenEventHandler;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentsEvent;
import org.gcube.portlets.user.messages.client.event.SaveAttachmentsEventHandler;
import org.gcube.portlets.user.messages.client.event.SendMessageEvent;
import org.gcube.portlets.user.messages.client.event.SendMessageEventHandler;
import org.gcube.portlets.user.messages.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.messages.client.interfaces.GuiEventInterface;
import org.gcube.portlets.user.messages.client.interfaces.SubscriberInterface;
import org.gcube.portlets.user.messages.client.rpc.MessagesService;
import org.gcube.portlets.user.messages.client.rpc.MessagesServiceAsync;
import org.gcube.portlets.user.messages.client.view.GxtBorderLayoutMainPanel;
import org.gcube.portlets.user.messages.client.view.message.GxtGridMessagesFilterPanel;
import org.gcube.portlets.user.messages.shared.FileModel;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;
import org.gcube.portlets.user.messages.shared.SessionExpiredException;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * The Class MessagesApplicationController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 */
public class MessagesApplicationController{
	/**
	 *
	 */

	private final static HandlerManager eventBus = new HandlerManager(null);
	private MessagesMainPanel mainPanel;
	private HashMap<EventsTypeEnum, ArrayList<SubscriberInterface>> subscribers = null;
	private MessagesApplicationSubscriber messagesSubscriber;
	private HasWidgets rootPanel;
	private String myLogin = null;


	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	public final static MessagesServiceAsync rpcMessagesManagementService = GWT.create(MessagesService.class);
	protected static final int DELAY = 10000;

	/**
	 * Instantiates a new messages application controller.
	 */
	public MessagesApplicationController() {
		subscribers = new HashMap<EventsTypeEnum, ArrayList<SubscriberInterface>>();

		rpcMessagesManagementService.getMyLogin(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable arg0) {
				ConstantsPortletMessages.messagesLogger.log(Level.INFO,"An error occurred on recovering my login");

			}

			@Override
			public void onSuccess(String login) {
				myLogin = login;
				ConstantsPortletMessages.messagesLogger.log(Level.INFO,"My login is: "+login);

			}

		});
		bind();
	}

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static HandlerManager getEventBus() {
		return eventBus;
	}

	/**
	 * Gets the messages panel container.
	 *
	 * @return the messages panel container
	 */
	public GxtGridMessagesFilterPanel getMessagesPanelContainer() {
		return mainPanel.getMessagesPanelContainer();
	}

	/**
	 * Bind.
	 */
	private void bind() {

		eventBus.addHandler(GetAllNewMessagesEvent.TYPE, new GetAllNewMessagesEventHandler() {

			@Override
			public void onNewMessagesEvent(GetAllNewMessagesEvent newMessagesEvent) {

				FileModel currentSelection = mainPanel.getMessagesPanel().getCurrentSelection();

				if(currentSelection!=null){
					if(currentSelection.getIdentifier().equals(GXTCategoryItemInterface.MS_MESSAGES) || currentSelection.getIdentifier().equals(GXTCategoryItemInterface.MS_RECEIVED))
						mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_RECEIVED);
					else if(currentSelection.getIdentifier().equals(GXTCategoryItemInterface.MS_SENT))
						mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_SENT);
				}
				else
					mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_RECEIVED); //DEFAULT SELECTION RECEIVED MESSAGES
			}
		});

		eventBus.addHandler(GridMessageSelectedEvent.TYPE, new GridMessageSelectedEventHandler() {

			@Override
			public void onGridMessageSelected(GridMessageSelectedEvent gridMessageSelectedEvent) {
				mainPanel.getToolBarMessage().activeButtonsOnSelect(true);

			}
		});

		eventBus.addHandler(GridMessageUnSelectedEvent.TYPE, new GridMessageUnSelectedEventHandler() {

			@Override
			public void onGridMessageUnSelected(GridMessageUnSelectedEvent gridMessageUnSelectedEvent) {
				mainPanel.getToolBarMessage().activeButtonsOnSelect(false);

			}
		});

		eventBus.addHandler(MarkMessageEvent.TYPE, new MarkMessageEventHandler() {

			@Override
			public void onMark(MarkMessageEvent markAsReadMessageEvent) {
				doMarkAsRead(markAsReadMessageEvent);

			}

			private void doMarkAsRead(final MarkMessageEvent markAsReadMessageEvent) {

				rpcMessagesManagementService.markMessage(markAsReadMessageEvent.getMessageTarget().getId(), markAsReadMessageEvent.getMessageTarget().getMessageType(), markAsReadMessageEvent.getBoolMark(), markAsReadMessageEvent.getMarkTypeToString(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry, an error occurred on marking message, try again later", null);
						//						new MessageBoxAlert("Error", "Sorry - mark message error - " + caught.getMessage(), null);

					}

					@Override
					public void onSuccess(Boolean result) {
						if(result)
							notifySubscriber(markAsReadMessageEvent);

					}
				});

			}
		});

		eventBus.addHandler(DeleteMessageEvent.TYPE, new DeleteMessageEventHandler() {

			@Override
			public void onDeleteMessage(DeleteMessageEvent deleteMessageEvent) {
				doDeleteMessage(deleteMessageEvent);

			}

			private void doDeleteMessage(final DeleteMessageEvent deleteMessageEvent) {

				rpcMessagesManagementService.deleteMessage(deleteMessageEvent.getMessageTarget().getId(), deleteMessageEvent.getMessageTarget().getMessageType(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry, an error occurred on deleting message, try again later", null);
						//						new MessageBoxAlert("Error", "Sorry - delete message error - " + caught.getMessage(), null);

					}

					@Override
					public void onSuccess(Boolean result) {
						if(result)
							notifySubscriber(deleteMessageEvent);

					}
				});


			}
		});

		eventBus.addHandler(FileDownloadEvent.TYPE, new FileDownloadEventHandler() {

			@Override
			public void onFileDownloadEvent(FileDownloadEvent fileDownloadEvent) {

				if(fileDownloadEvent.getItemIdentifier()!=null){
					String currentContextId = GCubeClientContext.getCurrentContextId();
					String currentUserId = GCubeClientContext.getCurrentUserId();
					if(fileDownloadEvent.getDownloadType().equals(DownloadType.SHOW)){
						if(fileDownloadEvent.getItemName()!= null)
							com.google.gwt.user.client.Window.open(ConstantsPortletMessages.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileDownloadEvent.getItemIdentifier()+"&viewContent=true&redirectonerror=true&contextID="+currentContextId+"&currUserId="+currentUserId, "_blank", "");
					}
					else
						com.google.gwt.user.client.Window.open(ConstantsPortletMessages.DOWNLOAD_WORKSPACE_SERVICE+"?id="+fileDownloadEvent.getItemIdentifier()+"&redirectonerror=true&contextID="+currentContextId+"&currUserId="+currentUserId, "_self", "");
				}
			}
		});

		eventBus.addHandler(SaveAttachmentsEvent.TYPE, new SaveAttachmentsEventHandler() {

			@Override
			public void onSaveAttachments(SaveAttachmentsEvent saveAttachmentsEvent) {

				final InfoDisplay saving = new InfoDisplay("Info","saving in progress...");

				rpcMessagesManagementService.saveAttachments(saveAttachmentsEvent.getMessageIdentifier(), saveAttachmentsEvent.getMessageType(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry, an error occurred on saving attachment/s, try again later", null);
						//						new MessageBoxAlert("Error", "Sorry - save attachments error - " + caught.getMessage(), null);

					}

					@Override
					public void onSuccess(Boolean result) {

						if(saving.isAttached())
							saving.hide();

						if(result){
							new InfoDisplay("Info","Message attachments has been saved");
							//							explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
						}
					}
				});
			}
		});



		eventBus.addHandler(SaveAttachmentAndOpenEvent.TYPE, new SaveAttachmentAndOpenEventHandler() {

			@Override
			public void onSaveAttachmentsAndOpen(final SaveAttachmentAndOpenEvent saveAttachAndOpenEvent) {

				final InfoDisplay saving = new InfoDisplay("Info","saving in progress...");

				rpcMessagesManagementService.saveAttachment(saveAttachAndOpenEvent.getMessageIdentifier(), saveAttachAndOpenEvent.getAttachmentId(), saveAttachAndOpenEvent.getMessageType(), new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry, an error occurred on saving attachment/s, try again later", null);

					}

					@Override
					public void onSuccess(String oid) {

						if(saving.isAttached())
							saving.hide();

						if(oid!=null){
							new InfoDisplay("Info","Attachment has been saved.. now opening");
							saveAttachAndOpenEvent.getAttachOpenListner().onSavingComplete(oid);
							//							explorerPanel.getAsycTreePanel().removeAllAndRecoveryRoot();
						}else
							new InfoDisplay("Error","Sorry an error occurred when saving attach, please try again");
					}
				});

			}
		});



		eventBus.addHandler(PreviewMessageEvent.TYPE, new PreviewMessageEventHandler() {

			@Override
			public void onPreviewMessage(PreviewMessageEvent previewMessageEvent) {
				doPreviewMessage(previewMessageEvent);

			}

			private void doPreviewMessage(final PreviewMessageEvent previewMessageEvent) {

				rpcMessagesManagementService.getMessageById(previewMessageEvent.getMessageIdentifier(), previewMessageEvent.getMessageType(), new AsyncCallback<MessageModel>() {

					@Override
					public void onFailure(Throwable caught) {

						if(caught instanceof SessionExpiredException){
							ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Session expired");
							viewSessionExpiredPanel();
							return;
						}

						new MessageBoxAlert("Error", "Sorry - an error occurred when opening the message, try again", null);

					}

					@Override
					public void onSuccess(MessageModel message) {

						//						String headerTitle = ConstantsExplorer.MESSAGE_SENT_IN_DATE  +": " +message.getDate() + " by "+ message.getFromLogin();
						//						new SendMessage(result.getId(), headerTitle, result.getSubject(), result.getTextMessage(), result.getListAttachments(), result.getListContactsToString());

						previewMessageEvent.setMessage(message);
						notifySubscriber(previewMessageEvent);

					}
				});

			}
		});

		eventBus.addHandler(OpenMessageEvent.TYPE, new OpenMessageEventHandler() {

			@Override
			public void onOpenMessage(OpenMessageEvent openMessageEvent) {
				doOpenMessage(openMessageEvent);

			}

			private void doOpenMessage(final OpenMessageEvent openMessageEvent) {

				rpcMessagesManagementService.getMessageById(openMessageEvent.getMessageIdentifier(), openMessageEvent.getMessageType(), new AsyncCallback<MessageModel>() {

					@Override
					public void onFailure(Throwable caught) {
						new MessageBoxAlert("Error", "Sorry, an error occurred on opening message, try again later", null);
					}

					@Override
					public void onSuccess(MessageModel result) {
						openMessageEvent.setMessage(result); //This fill item text and list contact
						notifySubscriber(openMessageEvent);
					}
				});
			}
		});


		eventBus.addHandler(LoadMessagesEvent.TYPE, new LoadMessagesEventHandler() {

			@Override
			public void onLoadMessages(LoadMessagesEvent loadMessagesEvent) {
				doGetMessages(loadMessagesEvent);

			}

			private void doGetMessages(LoadMessagesEvent loadMessagesEvent) {

				FileModel currentSelection = mainPanel.getMessagesPanel().getCurrentSelection();

				//Control on polling event
				if(loadMessagesEvent.isPolling()){
					if(currentSelection!=null) {
						if(currentSelection.getIdentifier().equals(GXTCategoryItemInterface.MS_MESSAGES) || currentSelection.getIdentifier().equals(GXTCategoryItemInterface.MS_RECEIVED))
							mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_RECEIVED); //notify subscriber after select menu item
					}
					else
						mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_RECEIVED); //notify subscriber after select menu item

					return; //not notify subscriber (notify subscriber after select item of context menu inbox)
				}


				//update counter messages
				rpcMessagesManagementService.getNewMessagesReceived(new AsyncCallback<List<MessageModel>>() {

					@Override
					public void onSuccess(List<MessageModel> result) {

						PollingWorkspace.setCounterNotOpenMessages(result.size()); //update current counter

					}

					@Override
					public void onFailure(Throwable caught) {

						if(caught instanceof SessionExpiredException){
							ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Session expired");
							viewSessionExpiredPanel();
							return;
						}
						System.out.println("Failure rpc get new message/s");

					}
				});

				notifySubscriber(loadMessagesEvent);

			}
		});

		eventBus.addHandler(SendMessageEvent.TYPE, new SendMessageEventHandler() {

			@Override
			public void onSendMessage(SendMessageEvent sendMessageEvent) {
				notifySubscriber(sendMessageEvent);
			}
		});

	}


	/**
	 * View session expired panel.
	 */
	public void viewSessionExpiredPanel() {

		/*if(this.rootPanel!=null){
			rootPanel.clear();

			LayoutContainer errorPanel = new LayoutContainer();
			errorPanel.setLayout(new FitLayout());
			errorPanel.setHeight(350);

			errorPanel.add(new HTML(ConstantsPortletMessages.SESSION_EXPIRED_DIV));

			rootPanel.add(errorPanel);
		}*/

		ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Session Expired, showing CheckSession LogoutDialog!");



		if(this.rootPanel!=null){
			rootPanel.clear();
			LayoutContainer errorPanel = new LayoutContainer();
			errorPanel.setLayout(new FitLayout());
			errorPanel.setHeight(350);
			errorPanel.add(new HTML(ConstantsPortletMessages.SESSION_EXPIRED_DIV));
			rootPanel.add(errorPanel);
		}

	}



	//Method Notify Subscriber
	/**
	 * Notify subscriber.
	 *
	 * @param event the event
	 */
	public void notifySubscriber(GuiEventInterface event){
		if (subscribers.containsKey(event.getKey()))
			for (SubscriberInterface sub : subscribers.get(event.getKey())){

				if(event instanceof LoadMessagesEvent){

					LoadMessagesEvent loadMessagesEvent = (LoadMessagesEvent) event;

					if(loadMessagesEvent.getTypeMessages().equals(GXTCategoryItemInterface.MS_SENT)){
						sub.loadSentMessages();
					}else{
						sub.loadReceivedMessages();
					}

				}else if(event instanceof DeleteMessageEvent){

					DeleteMessageEvent deleteMessage = (DeleteMessageEvent) event;

					sub.deleteMessage(deleteMessage.getMessageTarget().getId());

				}else if(event instanceof MarkMessageEvent){

					MarkMessageEvent mark = (MarkMessageEvent) event;

					sub.markAsReadMessage(mark.getMessageTarget().getId(), mark.getBoolMark());

				}else if(event instanceof PreviewMessageEvent){

					PreviewMessageEvent messageEvent = (PreviewMessageEvent) event;

					//            		sub.updatePrevieMessage(messageEvent.getMessage().getFromLogin(), messageEvent.getMessage().getSubject(), messageEvent.getMessage().getDate(), messageEvent.getMessage().getTextMessage(), messageEvent.getMessage().getListAttachments());

					if(messageEvent.getMessageType().equals(GXTCategoryItemInterface.MS_SENT))
						sub.updatePrevieMessage(messageEvent.getMessage());
					else
						sub.updatePrevieMessage(messageEvent.getMessage());

				}else if(event instanceof SendMessageEvent){

					SendMessageEvent messageEvent = (SendMessageEvent) event;

					//            		sub.updatePrevieMessage(messageEvent.getMessage().getFromLogin(), messageEvent.getMessage().getSubject(), messageEvent.getMessage().getDate(), messageEvent.getMessage().getTextMessage(), messageEvent.getMessage().getListAttachments());

					HashMap<String, String> hashFiles = new HashMap<String, String>();

					if(messageEvent.getListFileModelSelected()!=null){
						for (FileModel fileModel : messageEvent.getListFileModelSelected()) {
							hashFiles.put(fileModel.getIdentifier(), fileModel.getName());
						}
					}

					sub.createNewMessage(hashFiles);
				}else if(event instanceof OpenMessageEvent){

					OpenMessageEvent messageEvent = (OpenMessageEvent) event;
					MessageModel message = messageEvent.getMessage();

					if(messageEvent.getOpenType().equals(OpenType.FORWARD)){

						HashMap<String, String> hashAttachs = new HashMap<String, String>();

						if(message.getListAttachments()!=null){
							for (FileModel fileModel : message.getListAttachments()) {
								hashAttachs.put(fileModel.getIdentifier(), fileModel.getName());
							}
						}

						List<String> contactsFullName = getFullNameWithoutMyLogin(message, false);
						ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Forward To, From Login: "+message.getFromLogin());

						sub.forwardMessage(message.getFromLogin(), 	handlePrefixHeaderSubject(OpenType.FORWARD, message.getSubject()), contactsFullName, message.getDate(), hashAttachs, message.getTextMessage());

					}else if(messageEvent.getOpenType().equals(OpenType.REPLY)){

						ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Reply To, From Login: "+message.getFromLogin());
						sub.replyMessage(message.getFromLogin(), handlePrefixHeaderSubject(OpenType.REPLY, message.getSubject()), new ArrayList<String>(), message.getDate(), message.getTextMessage());

					}else if(messageEvent.getOpenType().equals(OpenType.REPLYALL)){

						List<String> contactsFullName = getFullNameWithoutMyLogin(message, true);
						ConstantsPortletMessages.messagesLogger.log(Level.INFO,"Reply All, From Login: "+message.getFromLogin());

						sub.replyMessage(message.getFromLogin(), handlePrefixHeaderSubject(OpenType.REPLYALL, message.getSubject()), contactsFullName, message.getDate(), message.getTextMessage());
					}

				}


			}

	}


	/**
	 * Handle prefix header subject. The subject return a unique Re: or Fwd: like prefix
	 *
	 * @param type the type
	 * @param subject the subject
	 * @return the string
	 */
	private String handlePrefixHeaderSubject(OpenType type, String subject){

		if(subject==null || subject.isEmpty())
			return subject;

		switch (type) {

		case FORWARD: {

			if (subject.startsWith("Fwd: "))
				return subject;
			else
				return "Fwd: " + subject;
		}
		case REPLYALL:
		case REPLY: {

			if (subject.startsWith("Re: "))
				return subject;
			else
				return "Re: " + subject;
		}

		default:
			return subject;
		}

	}

	/**
	 * Gets the full name without my login.
	 *
	 * @param message the message
	 * @param withoutMyLogin the without my login
	 * @return the full name without my login
	 */
	private List<String> getFullNameWithoutMyLogin(MessageModel message, boolean withoutMyLogin){

		Map<String, String> mapContacts = new HashMap<String, String>(message.getMapContacts());
		if(withoutMyLogin && myLogin!=null && !myLogin.isEmpty())
			mapContacts.remove(myLogin);

		List<String> fullNames = new ArrayList<String>(mapContacts.size());
		for (String login : mapContacts.keySet()) {
			fullNames.add(mapContacts.get(login));
		}

		return fullNames;
	}

	/**
	 * Prints the contact.
	 *
	 * @param contacts the contacts
	 */
	private void printContact(List<String> contacts){
		if(contacts==null)
			return;

		for (String string : contacts) {
			ConstantsPortletMessages.messagesLogger.log(Level.INFO,string);
		}
	}

	/**
	 * init method.
	 *
	 * @param rPanel the r panel
	 */
	public void go(final HasWidgets rPanel) {

		this.mainPanel = new MessagesMainPanel();
		rPanel.add(this.mainPanel.getBorderLayoutContainer());
		//		this.jobManager = JobManager.getInstance();
		this.rootPanel = rPanel;

		initApplication();

	}


	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public GxtBorderLayoutMainPanel getMainPanel(){
		return this.mainPanel.getBorderLayoutContainer();
	}

	/**
	 * Inits the application.
	 */
	private void initApplication(){

		//		PollingWorkspace.pollReceivedMessages(-1, 20000); //the parameter -1 force (first) synchronization with HL

		this.messagesSubscriber = new MessagesApplicationSubscriber(this);

		//DEFAULT SELECT
		mainPanel.getMessagesPanel().setSelect(GXTCategoryItemInterface.MS_RECEIVED);

	}

	/**
	 * Subscribe.
	 *
	 * @param subscriber the subscriber
	 * @param keys the keys
	 */
	public void subscribe(SubscriberInterface subscriber, EventsTypeEnum[] keys)
	{
		for (EventsTypeEnum m : keys)
			subscribe(subscriber, m);
	}

	/**
	 * Subscribe.
	 *
	 * @param subscriber the subscriber
	 * @param key the key
	 */
	public void subscribe(SubscriberInterface subscriber, EventsTypeEnum key)
	{
		if (subscribers.containsKey(key))
			subscribers.get(key).add(subscriber);
		else
		{
			ArrayList<SubscriberInterface> subs = new ArrayList<SubscriberInterface>();
			subs.add(subscriber);
			subscribers.put(key, subs);
		}
	}

	/**
	 * Unsubscribe.
	 *
	 * @param subscriber the subscriber
	 * @param key the key
	 */
	public void unsubscribe(SubscriberInterface subscriber, EventsTypeEnum key)
	{
		if (subscribers.containsKey(key))
			subscribers.get(key).remove(subscriber);
	}

}
