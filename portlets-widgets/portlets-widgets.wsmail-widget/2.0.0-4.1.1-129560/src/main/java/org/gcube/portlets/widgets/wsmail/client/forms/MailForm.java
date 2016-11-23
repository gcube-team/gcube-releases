package org.gcube.portlets.widgets.wsmail.client.forms;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;
import org.gcube.portlets.widgets.wsmail.client.WsMailService;
import org.gcube.portlets.widgets.wsmail.client.WsMailServiceAsync;
import org.gcube.portlets.widgets.wsmail.client.attachments.MultiAttachedItemsPanel;
import org.gcube.portlets.widgets.wsmail.client.events.RenderForm;
import org.gcube.portlets.widgets.wsmail.client.events.RenderFormEventHandler;
import org.gcube.portlets.widgets.wsmail.client.multisuggests.MultiValuePanel;
import org.gcube.portlets.widgets.wsmail.shared.WSUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 */
public class MailForm extends Composite  {
	public static final String loading = GWT.getModuleBaseURL() + "images/feeds-loader.gif";
	public static final String attachImageUrl = GWT.getModuleBaseURL() + "images/attach.png";
	public static final String mailSentOK = GWT.getModuleBaseURL() + "images/yes.png";
	public static final String mailSentNOK = GWT.getModuleBaseURL() + "images/warning_blue.png";
	private static final String ERROR_UPDATE_TEXT = "Looks like empty to me!";

	private final WsMailServiceAsync mailingService = GWT.create(WsMailService.class);

	private static MailFormUiBinder uiBinder = GWT.create(MailFormUiBinder.class);

	interface MailFormUiBinder extends UiBinder<Widget, MailForm> {
	}

	private final HandlerManager eventBus = new HandlerManager(null);
	private Image loadingImage;
	private VerticalPanel mainPanel = new VerticalPanel();

	private boolean isReply = false;
	private boolean isForward = false;
	private boolean isRecipientSet = false;
	private String fromLogin; 
	private List<String> listToLogin;
	private Date date; 
	private String mSubject;
	private String textMessage;

	GCubeDialog myDialog = new GCubeDialog(false, false);

	@UiField MultiValuePanel myInput;
	@UiField TextBox subject;
	@UiField TextArea messageTextArea;
	@UiField Button send;
	@UiField Button cancel;
	@UiField Image attach;
	@UiField HTML attachHandler;
	@UiField MultiAttachedItemsPanel myAttachPanel;

	// Add a UI Factory method for the sub-widget & pass the eventbus
	@UiFactory
	MultiValuePanel init() {
		return new MultiValuePanel(eventBus);
	}

	@UiFactory
	Image setImage() {
		return new Image(attachImageUrl);
	}

	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(RenderForm.TYPE, new RenderFormEventHandler() {
			@Override
			public void onRenderForm(RenderForm event) {
				showMe(event.isSuccess());
			}
		});  
	}

	/**
	 *  Bind the xml and the events
	 */
	public MailForm() {	
		initWidget(uiBinder.createAndBindUi(this));		
		bind();
		showLoading("Loading, please wait ...");
		mainPanel.setWidth("600px");
		mainPanel.setHeight("350px");
		myDialog.center();
	}

	/**
	 * constructor to be used  for send to
	 */
	public MailForm(HashMap<String, String> attachedFiles) {
		this();
		for (String key : attachedFiles.keySet()) {
			Item toAttach = new Item(null, key, attachedFiles.get(key), ItemType.EXTERNAL_FILE, "", "", false, false);
			attachHandler.setHTML("<a class=\"attach-file-link\">add another item</a>");
			myAttachPanel.addAttachment(toAttach);
		}		
	}

	/**
	 * constructor to be used for sending message to a predefined user or users
	 */
	public MailForm(List<String> listToLogin) {
		this();
		isRecipientSet = true;
		this.listToLogin = listToLogin;
	}
	/**
	 *  constructor to be used when replying or forwarding
	 */
	public MailForm(String fromLogin, String mSubject, List<String> listToLogin, Date date, HashMap<String, String> attachedFiles, String textMessage, boolean isReply) {
		this(attachedFiles);
		this.isReply = isReply; //need to set this to complete the from with actual user full names that are fetched right after this
		this.isForward = !isReply;
		this.fromLogin = fromLogin;
		this.listToLogin = listToLogin;
		this.date = date;
		this.textMessage = textMessage;
		this.mSubject = mSubject;
	}
	/**
	 * show the initial loading gif
	 */
	private void showLoading(String text) {
		myDialog.clear();
		mainPanel.clear();
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		myDialog.setText(text);
		loadingImage = new Image(loading);	
		mainPanel.add(loadingImage);
		myDialog.add(mainPanel);			
	}

	/**
	 * show the delivery mail result
	 */
	private void showDeliveryResult(boolean success) {
		myDialog.clear();
		mainPanel.clear();
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (success) {
			myDialog.setText("Your message has been sent");
			loadingImage = new Image(mailSentOK);	
		}
		else {
			myDialog.setText("Your message has not been sent");
			loadingImage = new Image(mailSentNOK);	
			mainPanel.add(new HTML("There were problems contacting the server, or your session expired. "
					+ "Please save your data before refreshing the page and try again."));
			Button close = new Button("Close");
			close.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					myDialog.hide();					
				}
			});
			mainPanel.add(close);
		}

		mainPanel.add(loadingImage);
		Button close = new Button("Close Window");
	
		close.addClickHandler(new ClickHandler() {		
			@Override
			public void onClick(ClickEvent event) {
				myDialog.hide();				
			}
		});
		mainPanel.add(close);
		myDialog.add(mainPanel);			
	}

	/**
	 * triggered after users are fetched from the server
	 * @param success true if the server has answered correctly
	 */
	private void showMe(boolean success) {
		if (success) {
			if (isReply || isForward) {  //reply or forward
				if (isReply) {
					messageTextArea.setText(getReplyHeaderMessage(fromLogin, listToLogin, date, mSubject, textMessage));
					myInput.clearList();
					if (listToLogin != null && listToLogin.size() > 0) { //reply all
						myInput.addRecipient(getFullName(myInput.getAllUsers(), fromLogin));
						for (String dest : listToLogin) {
							if (dest.compareTo(myInput.getCurrentUser().getScreenname()) != 0)  {//the listToContains the sender also, need to be removed, this skips it
								ArrayList<String> fullNames = new ArrayList<String>();
								fullNames.add(getFullName(myInput.getAllUsers(), dest));
								myInput.addRecipients(fullNames);
							}
						}						
					} else  { //simple replyreply
						GWT.log("simple reply: " + fromLogin);
						myInput.addRecipient(getFullName(myInput.getAllUsers(), fromLogin));
					}
				}
				else
					messageTextArea.setText(getForwardHeaderMessage(fromLogin, listToLogin, date, mSubject, textMessage));
				isReply = false;
				isForward = false;
				myInput.setFocusOn();
			}
			else if (isRecipientSet) {  //for sending message to a predefined user or users
				myInput.clearList();
				
				ArrayList<String> fullNames = new ArrayList<String>();
				for (String dest : listToLogin) {
					fullNames.add(getFullName(myInput.getAllUsers(), dest));
				}
				myInput.addRecipients(fullNames);
//				for (String dest : listToLogin) {
//					myInput.addRecipient(getFullName(myInput.getAllUsers(), dest));
//				}
				GWT.log("subject.setFocus(true);");
				setFocusOnSubject();				
				isRecipientSet = false;
			} else {
				myInput.setFocusOn();
			}
			//Create new Message
			GWT.log("simple Send");
			myDialog.setText("Compose Message");
			myDialog.clear();
			myDialog.add(this);	
		} else {
			myDialog.setText("Server Error");
			myDialog.clear();
			VerticalPanel vp = new VerticalPanel();
			vp.add(new HTML("There were problems contacting the server, please try again in a short while.."));
			Button close = new Button("Close");
			close.addClickHandler(new ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					myDialog.hide();
				}
			});
			vp.add(close);
			myDialog.add(vp);
		}
	}

	private void setFocusOnSubject() {
		Timer t = new Timer() {
			
			@Override
			public void run() {
				subject.setFocus(true);				
			}
		};
		t.schedule(600);
	}

	private String getForwardHeaderMessage(String fromLogin, List<String> toLogins, Date date, String mSubject, String message) {
		subject.setText(mSubject);	
		ArrayList<WSUser> users = myInput.getAllUsers();
		String toReturn =  "\n\n---\nBegin forwarded messageTextArea:";
		toReturn += "\nFrom: " + getFullName(users, fromLogin);
		toReturn += "\nDate: " + date;
		toReturn += "\nTo:";
		if (toLogins != null) {
			for (String login : toLogins) {
				toReturn += getFullName(users, login) + " ";
			}
		}
		toReturn += "\nSubject: " +mSubject;
		toReturn += "\n\n"+message;
		return toReturn;
	}

	private String getReplyHeaderMessage(String fromLogin,  List<String> toLogins, Date date, String mSubject, String message) {
		subject.setText(mSubject);	
		ArrayList<WSUser> users = myInput.getAllUsers();
		String toReturn =  "\n\n---\n on " + date +  " " + getFullName(users, fromLogin) + " wrote:";
		toReturn += "\n\n"+message;
		return toReturn;
	}
	/**
	 * iterate the array containing all users full info
	 * @param users
	 * @param loginToLookFor
	 * @return the fullName of the user given its login
	 */
	private String getFullName(ArrayList<WSUser> users, String loginToLookFor) {
		if (loginToLookFor == null)
			loginToLookFor = "test.user";
		if (getUserByLogin(users, loginToLookFor) != null)
			return getUserByLogin(users, loginToLookFor).getFullName();
		return loginToLookFor;
	}

	private WSUser getUserByLogin(ArrayList<WSUser> users, String loginToLookFor) {
		for (WSUser wsUser : users) {
			if (wsUser.getScreenname().compareTo(loginToLookFor) == 0)
				return wsUser;
		}
		return null;
	}
	/// HANDLERS

	@UiHandler("send")
	void onClick(ClickEvent e) {
		boolean allFilled = true;
		if (myInput.getSelectedUserIds().isEmpty()) {
			Window.alert("Please enter at least one addressee.");
			allFilled = false;
		}
		if (subject.getText().equals("")) {
			subject.removeStyleName("dark-color");
			subject.addStyleName("ws-mail-error");
			subject.setText(ERROR_UPDATE_TEXT);
			allFilled = false;
		}
		if (messageTextArea.getText().equals("")) {
			messageTextArea.removeStyleName("dark-color");
			messageTextArea.addStyleName("ws-mail-error");
			messageTextArea.setText(ERROR_UPDATE_TEXT);
			allFilled = false;
		}
		if (! allFilled) {
			return;
		} else {
			showLoading("Sending your message, please wait ...");
			mailingService.sendToById(myInput.getSelectedUserIds(), myAttachPanel.getAddedWpItems(), subject.getText(), messageTextArea.getText(), new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					showDeliveryResult(result);				
				}

				@Override
				public void onFailure(Throwable caught) {
					showDeliveryResult(false);		
				}
			});
		}
	}

	@UiHandler("attachHandler")
	void onAttachHandlerClick(ClickEvent e) {
		
		final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select an item", false);
		wpTreepopup.setZIndex(10010);
		WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {
			@Override
			public void onSelectedItem(Item item) {
				GWT.log("Selected Item");
				myAttachPanel.addAttachment(item);
				GWT.log("addAttachment Item");
				attachHandler.setHTML("<a class=\"attach-file-link\">add another item</a>");	
				wpTreepopup.hide();
			}
			@Override
			public void onFailed(Throwable throwable) {
				Window.alert("There are networks problem, please check your connection.");            
			}				 
			@Override
			public void onAborted() {}
			@Override
			public void onNotValidSelection() {				
			}
		};
		
		wpTreepopup.addWorkspaceExplorerSelectNotificationListener(listener);
		wpTreepopup.show();						
	}

	@UiHandler("cancel")
	void onCancelClick(ClickEvent e) {
		myDialog.hide();
	}

	@UiHandler("subject")
	void onSubjectClick(ClickEvent e) {
		if (subject.getText().equals(ERROR_UPDATE_TEXT) ) {
			subject.setText("");
			subject.addStyleName("dark-color");
			subject.removeStyleName("ws-mail-error");
		}
	}
	@UiHandler("messageTextArea")
	void onBodyClick(ClickEvent e) {
		if (messageTextArea.getText().equals(ERROR_UPDATE_TEXT) ) {
			messageTextArea.setText("");
			messageTextArea.addStyleName("dark-color");
			messageTextArea.removeStyleName("ws-mail-error");
		}
	}
}
