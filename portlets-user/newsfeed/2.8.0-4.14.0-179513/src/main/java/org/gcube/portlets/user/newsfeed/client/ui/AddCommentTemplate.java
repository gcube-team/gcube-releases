package org.gcube.portlets.user.newsfeed.client.ui;


import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.gcubewidgets.client.elements.Div;
import org.gcube.portlets.user.newsfeed.client.event.AddCommentEvent;
import org.gcube.portlets.user.newsfeed.client.event.EditCommentEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AddCommentTemplate extends Composite {

	interface CommentTemplateUiBinder extends UiBinder<Widget, AddCommentTemplate> {
	}

	private static CommentTemplateUiBinder uiBinder = GWT.create(CommentTemplateUiBinder.class);


	public final static String COMMENT_TEXT = "Write a comment, use @ to mention someone";
	public final static String ERROR_UPDATE_TEXT = "Looks like empty to me!";
	public static final String avatar_default = GWT.getModuleBaseURL() + "../images/Avatar_default.png";

	private TweetTemplate owner;
	private HandlerManager eventBus;
	private boolean isEditing = false;
	private HTMLPanel commentPanel;
	private Comment toEdit;
	private ClosingHandler closingHandler;
	private HandlerRegistration handlerRegistration;

	@UiField HTMLPanel mainPanel;
	@UiField Image avatarImage;
	@UiField SuperPosedTextArea commentTextArea;
	@UiField Div highlighterDIV;
	@UiField Button submitButton;
	@UiField Button cancelButton; //obsolete


	/**
	 * called on add comment
	 * @param caller
	 * @param myUserInfo
	 * @param eventBus
	 */
	public AddCommentTemplate(TweetTemplate caller, UserInfo myUserInfo, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		owner = caller;
		avatarImage.setPixelSize(30, 30);
		avatarImage.setUrl(myUserInfo.getAvatarId());	
		submitButton.setVisible(false);
		cancelButton.setVisible(false);
		commentTextArea.setHeight("30px");
		commentTextArea.setContext(owner.getVREContext());
	}
	/**
	 * called on edit comment
	 * @param caller
	 * @param editText
	 */
	public AddCommentTemplate(TweetTemplate caller, Comment toEdit, HTMLPanel commentPanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = caller.getEventBus();		
		this.commentPanel = commentPanel;
		isEditing = true;
		this.toEdit = toEdit;

		String commentText = new HTML(toEdit.getText()).getText();
		//replace the < & and >
		commentText = commentText.replaceAll("&lt;","<").replaceAll("&gt;",">");
		commentText = commentText.replaceAll("&amp;","&");

		owner = caller;
		commentTextArea.setContext(owner.getVREContext());
		avatarImage.setPixelSize(30, 30);
		avatarImage.setUrl(caller.getMyUserInfo().getAvatarId());	
		commentTextArea.setText(commentText);
		mainPanel.removeStyleName("comment-hidden");
		mainPanel.setStyleName("single-comment");
		commentTextArea.addStyleName("comment-dark-color");
		submitButton.setText("Edit");
		closingHandler = new Window.ClosingHandler() {
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				String currentText = commentTextArea.getText();
				if(!currentText.isEmpty()){
					closingEvent.setMessage("Do you really want to leave the page?");
				}
			}
		};
		handlerRegistration = Window.addWindowClosingHandler(closingHandler);
		
	}
	
	

	/** Used by AddCommentTemplate to instantiate SuperPosedTextArea */
	@UiFactory SuperPosedTextArea build() { 
		return new SuperPosedTextArea(highlighterDIV);
	}

	public void setFocus() {
		commentTextArea.setFocus(true);
		submitButton.setVisible(true);
		cancelButton.setVisible(true);
		//it needs a timer otherwise it won't work
		Timer t = new Timer() {
			@Override
			public void run() {
				setCaretPositionToBegin(commentTextArea.getAreaId());	
				closingHandler = new Window.ClosingHandler() {
					public void onWindowClosing(Window.ClosingEvent closingEvent) {
						String currentText = commentTextArea.getText();
						if(!currentText.isEmpty()){
							closingEvent.setMessage("Do you really want to leave the page?");
						}
					}
				};
				handlerRegistration = Window.addWindowClosingHandler(closingHandler);
			}
		};
		t.schedule(200);

	}


	@UiHandler("submitButton")
	void onSubmitClick(ClickEvent e) {
		String userComment = commentTextArea.getText().trim();
		if (userComment.equals(COMMENT_TEXT) || userComment.equals(ERROR_UPDATE_TEXT) || userComment.equals("")) {
			commentTextArea.addStyleName("nwfeed-error");
			commentTextArea.setText(ERROR_UPDATE_TEXT);
			return;
		}
		if (isEditing) {
			toEdit.setText(escapeHtml(commentTextArea.getText()));
			eventBus.fireEvent(new EditCommentEvent(owner, toEdit, commentPanel));
		}
		else { //it is ok to add this comment
			eventBus.fireEvent(new AddCommentEvent(owner, escapeHtml(commentTextArea.getText()), commentTextArea.getMentionedUsers()));			
		}
		this.getWidget().setVisible(false);
		owner.setCommentingDisabled(false);
		handlerRegistration.removeHandler();
		GWT.log("	handlerRegistration.removeHandler();");
	}

	/**
	 * called when pasting. it tries to avoid pasting long non spaced strings
	 * @param linkToCheck
	 */
	private boolean checkTextLength(String textToCheck) {

		String [] parts = textToCheck.split("\\s");
		// check the length of tokens   
		for( String item : parts ) {
			if (!item.startsWith("http") && !item.startsWith("ftp")) { //url are accepted as they can be trunked
				if (item.length() > 50) {
					return false;
				}
			}
		}
		return true;
	}

	@UiHandler("cancelButton")
	void onCancelClick(ClickEvent e) {
		this.getWidget().setVisible(false);
		owner.setCommentingDisabled(false);
		if (isEditing) {
			commentPanel.clear();
			SingleComment sc = new SingleComment(toEdit, owner, true);
			commentPanel.add(sc);
		}
		handlerRegistration.removeHandler();
	}


	@UiHandler("commentTextArea")
	void onCommentClick(ClickEvent e) {
		if (commentTextArea.getText().equals(COMMENT_TEXT) || commentTextArea.getText().equals(ERROR_UPDATE_TEXT) ) {
			commentTextArea.setText("");
			commentTextArea.addStyleName("comment-dark-color");
			commentTextArea.removeStyleName("nwfeed-error");
		}
		submitButton.setVisible(true);
		cancelButton.setVisible(true);
		closingHandler = new Window.ClosingHandler() {
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				String currentText = commentTextArea.getText();
				if(!currentText.isEmpty()){
					closingEvent.setMessage("Do you really want to leave the page?");
				}
			}
		};
		handlerRegistration = Window.addWindowClosingHandler(closingHandler);
	}

	@UiHandler("commentTextArea")
	void onCommentKeyPress(KeyPressEvent e) {
		if (commentTextArea.getText().equals(COMMENT_TEXT) || commentTextArea.getText().equals(ERROR_UPDATE_TEXT) ) {
			commentTextArea.setText("");
			commentTextArea.addStyleName("comment-dark-color");
			commentTextArea.removeStyleName("nwfeed-error");
		}
	}
	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
	/**
	 * this position the caret at the begin in a TextArea
	 * @param myAreaId the unique identifier of the textarea
	 */
	public static native void setCaretPositionToBegin(String myAreaId) /*-{
	    var elem = $doc.getElementById(myAreaId);
	    if(elem != null) {
	        if(elem.createTextRange) {
	            var range = elem.createTextRange();
	            range.move('character', 0);
	            range.select();
	        }
	        else {
	            if(elem.selectionStart) {
	                elem.focus();
	                elem.setSelectionRange(0, 0);
	            }
	            else
	                elem.focus();
	        }
	    }		
	}-*/;
}
