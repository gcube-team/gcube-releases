package org.gcube.portets.user.message_conversations.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portets.user.message_conversations.client.MessageServiceAsync;
import org.gcube.portets.user.message_conversations.client.Utils;
import org.gcube.portets.user.message_conversations.client.autocomplete.MaterialAutoComplete;
import org.gcube.portets.user.message_conversations.client.oracle.UserOracle;
import org.gcube.portets.user.message_conversations.client.oracle.UserSuggestion;
import org.gcube.portets.user.message_conversations.shared.ConvMessage;
import org.gcube.portets.user.message_conversations.shared.MessageUserModel;
import org.gcube.portets.user.message_conversations.shared.WSUser;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSelectNotification.WorskpaceExplorerSelectNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.select.WorkspaceExplorerSelectDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.FilterCriteria;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsexplorer.shared.ItemType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialChip;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialPreLoader;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
/**
 * @author Massimiliano Assante, CNR-ISTI
 */
public class WriteMessage extends Composite {

	private static MessageWindowUiBinder uiBinder = GWT.create(MessageWindowUiBinder.class);

	interface MessageWindowUiBinder extends UiBinder<Widget, WriteMessage> {
	}

	@UiField MaterialPanel mainPanel;
	@UiField MaterialTextBox txtBoxSubject;
	@UiField MaterialAutoComplete acModal;
	@UiField MaterialButton btnSendModal, btnCloseModal;
	@UiField MaterialPanel modalContent, sendingLoader;
	@UiField MaterialRow  attachmentsRow;
	@UiField MaterialLink attachButton;
	@UiField MaterialLabel sendingFeedback;
	@UiField MaterialPreLoader sendingSpinner;
	@UiField MaterialTextArea txtArea;

	private ApplicationView ap;
	private MessageServiceAsync convService;

	public WriteMessage(MessageServiceAsync convService, ApplicationView ap) {
		initWidget(uiBinder.createAndBindUi(this));
		this.convService = convService;
		this.ap = ap;
		UserOracle oracle = new UserOracle();
		oracle.addContacts(getAllUsers());
		acModal.setSuggestions(oracle);
		btnSendModal.getElement().getStyle().setBackgroundImage("none");
	}

	MaterialPanel getMainPanel() {
		return mainPanel;
	}
	
	public void setNewMessageForSendTo(String[] usernames) {
		GWT.log("her add the users");
		convService.getUsersInfo(usernames, new AsyncCallback<ArrayList<WSUser>>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(ArrayList<WSUser> result) {
				for (WSUser user : result) {
					acModal.addItem(new UserSuggestion(user));			
				}				
			}
		});
	}

	public void setIsReply(ConvMessage msg) {
		acModal.addItem(new UserSuggestion(
				new WSUser(
						msg.getOwner().getUsername(), 
						msg.getOwner().getUsername(), 
						msg.getOwner().getFullName(), 
						msg.getOwner().getEmail()
						)
				));			
		String subject = getReplySubject(msg.getSubject());

		txtBoxSubject.setText(subject);
		txtBoxSubject.setReadOnly(true);

		txtArea.setText(getReplyHeaderMessage(msg));
		focusOnBody();
	}
	public void setIsReplyAll(ConvMessage msg) {
		setIsReply(msg);
		long currUserId = 0;
		try {
			currUserId = Long.parseLong(GCubeClientContext.getCurrentUserId());
		} catch (Exception e) {
			log("Could not read userId client context");
			return;
		}
		for (MessageUserModel u : msg.getRecipients()) {
			if (u.getUserId() != currUserId)
				acModal.addItem(new UserSuggestion(
						new WSUser(
								u.getUserId()+"", 
								u.getUsername(), 
								u.getFullName(), 
								u.getEmail()
								)
						));		
		}
		focusOnBody();
	}

	public void setIsForward(ConvMessage msg) {
		String subject = "Fwd: " + msg.getSubject();
		txtBoxSubject.setText(subject);
		txtBoxSubject.setReadOnly(true);		
		txtArea.setText(getForwardHeaderMessage(msg));
	}

	//need to defer this
	private void focusOnBody() {
		Timer t = new Timer() {			
			@Override
			public void run() {
				if (! Utils.isMobile()) {
					txtArea.setCursorPos(0);
					txtArea.setFocus(true);
					txtArea.setActive(true);
				}				
			}
		};
		t.schedule(1000);
	}

	private String getReplySubject(String subject) {
		if (subject != null) {
			return subject.startsWith("Re:") ? subject : "Re: " + subject;
		} else
			return "No subject";
	}

	private String getReplyHeaderMessage(ConvMessage msg) {
		String toReturn =  "\n\n---\n on " + msg.getDate() +  " " + msg.getOwner().getFullName() + " wrote:";
		toReturn += "\n\n"+msg.getContent();
		return toReturn;
	}

	private static native void log(String msg) /*-{
	  $wnd.console.log(msg);
	}-*/;

	private String getForwardHeaderMessage(ConvMessage msg) {
		String toReturn =  "\n\n---\nBegin forwarded message:";
		toReturn += "\nFrom: " + msg.getOwner().getFullName();
		toReturn += "\nDate: " + msg.getDate();
		toReturn += "\nTo: ";
		if (msg.getRecipients() != null) {
			for (MessageUserModel recipient : msg.getRecipients()) {
				toReturn += recipient.getFullName() + " ";
			}
		}
		toReturn += "\nSubject: " +msg.getSubject();
		toReturn += "\n\n"+msg.getContent();
		return toReturn;
	}

	public WriteMessage setFocusOnUsersInput() {
		if (!Utils.isMobile())
			acModal.setFocus(true);
		return this;
	}



	//just used in devlopment
	private List<WSUser> getAllUsers() {
		List<WSUser> toReturn = new ArrayList<>();
		toReturn.add(new WSUser("testing", "username testing", "Andrea testing", "@gmail.com"));
		return toReturn;
	}
	@UiHandler("attachButton")
	void onAttach(ClickEvent e) {
		List<ItemType> types = new ArrayList<>();
		ItemType[] theTypes = ItemType.values();
		for (int i = 0; i < theTypes.length; i++) {
			if (theTypes[i] != ItemType.FOLDER)
				types.add(theTypes[i]);
		}
		FilterCriteria criteria = null;
		final WorkspaceExplorerSelectDialog wpTreepopup = new WorkspaceExplorerSelectDialog("Select an item", criteria, types);
		wpTreepopup.setId(Utils.ID_MODALBOOTSTRAP);
		wpTreepopup.getElement().getStyle().setLeft(50, Unit.PCT);
		wpTreepopup.setZIndex(10010);
		WorskpaceExplorerSelectNotificationListener  listener = new WorskpaceExplorerSelectNotificationListener() {
			@Override
			public void onSelectedItem(Item item) {
				attachmentsRow.add(getChip(item.getId(), item.getName(), item.isFolder(), true));
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

	//for the attachments we distinguish between folder and files
	private MaterialChip getChip(String itemId, String itemName, boolean isFolder, boolean deletable) {
		MaterialChip chip = new MaterialChip(itemName);
		if (deletable)
			chip.setIconType(IconType.CLOSE);
		chip.setMargin(5);
		chip.setId(itemId);
		if (isFolder) {
			chip.setLetter("F");
			chip.setLetterBackgroundColor(Color.AMBER);
			chip.setBackgroundColor(Color.YELLOW);
			chip.setTextColor(Color.GREY_DARKEN_2);
		} else {
			chip.setLetter("D");
			chip.setLetterBackgroundColor(Color.RED);
			chip.setTextColor(Color.GREY_DARKEN_2);
		}
		return chip;
	}



	private ArrayList<String> getSelectedFilesAndFoldersId() {
		ArrayList<String> toReturn = new ArrayList<>();
		int n = attachmentsRow.getWidgetCount();
		for (int i = 0; i < n; i++) {
			Widget w = attachmentsRow.getWidget(i);
			if (w instanceof MaterialChip) {
				toReturn.add(((MaterialChip) w).getId());
			}
		}
		return toReturn;
	}


	@UiHandler("btnSendModal")
	void onSendMessage(ClickEvent e) {
		if (getSelectedUsers().isEmpty()) {
			acModal.setError("Look empty to me");
			return;
		} else {
			acModal.reset();
		}
		if (txtBoxSubject.getText().isEmpty()) {
			txtBoxSubject.setError("Subject is mandatory");
			return;
		}
		else {
			txtBoxSubject.reset();
		}
		if (txtArea.getText().isEmpty()) {
			txtArea.setError("The body of the message is mandatory");
			return;
		}
		else {
			txtArea.reset();
		}
		GWT.log(getSelectedUsers()+"");
		GWT.log(getSelectedFilesAndFoldersId()+"");
		ArrayList<String> recipientIds = new ArrayList<>();
		for (WSUser u : getSelectedUsers()) {
			recipientIds.add(u.getScreenname());
		}
		modalContent.setVisible(false);
		sendingLoader.setVisible(true);

		btnSendModal.setEnabled(false);
		ApplicationView.WRITING_MESSAGE = false;
		convService.sendToById(recipientIds, getSelectedFilesAndFoldersId(), txtBoxSubject.getText(), txtArea.getText(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				sendingSpinner.removeFromParent();
				sendingFeedback.setText("We're sorry an error occurred! Please try again");
				modalContent.setVisible(true);
				btnSendModal.setEnabled(true);
			}
			@Override
			public void onSuccess(Boolean result) {
				sendingSpinner.removeFromParent();
				if (result) {
					sendingFeedback.setText("Your message has been sent");
					MaterialIcon okIcon = new MaterialIcon(IconType.DONE, Color.GREEN, Color.WHITE);
					okIcon.setIconSize(IconSize.MEDIUM);
					sendingLoader.add(okIcon);
					btnCloseModal.setText("Close");
				}
				else
					sendingFeedback.setText("We're sorry an error occurred in the server! Please try again");
			}
		});
	}

	@UiHandler("btnCloseModal")
	void onClose(ClickEvent e) {
		if (Utils.isMobile())
			ap.showSidePanel();
		mainPanel.clear();
		ApplicationView.WRITING_MESSAGE = false;
	}

	public List<WSUser> getSelectedUsers() {
		List<? extends SuggestOracle.Suggestion> values = acModal.getValue();
		List<WSUser> users = new ArrayList<>(values.size());
		for(SuggestOracle.Suggestion value : values){
			if(value instanceof UserSuggestion){
				UserSuggestion us = (UserSuggestion) value;
				WSUser user = us.getUser();
				users.add(user);
			} 
		}
		return users;
	}


}
