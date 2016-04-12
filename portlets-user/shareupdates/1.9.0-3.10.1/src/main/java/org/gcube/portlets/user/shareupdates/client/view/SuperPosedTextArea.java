/**
 * 
 */
package org.gcube.portlets.user.shareupdates.client.view;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.portlets.user.shareupdates.client.ShareUpdateService;
import org.gcube.portlets.user.shareupdates.client.ShareUpdateServiceAsync;
import org.gcube.portlets.widgets.pickitem.client.dialog.PickItemsDialog;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEvent;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEventHandler;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author massi
 *
 */
public class SuperPosedTextArea extends TextArea {
	private final ShareUpdateServiceAsync shareupdateService = GWT
			.create(ShareUpdateService.class);
	private final HandlerManager eventBus = new HandlerManager(null);

	PickItemsDialog pickUserDlg;
	PickItemsDialog pickHashtagDlg;
	public final static int ARROW_UP = 38; 
	public final static int ARROW_DOWN = 40; 

	private HashSet<String> mentionedUsers = new HashSet<String>();
	private ArrayList<String> hashtags = new ArrayList<String>();

	/**
	 * 
	 */
	public SuperPosedTextArea() {
		sinkEvents(Event.ONPASTE);
		//sinkEvents(Event.ONKEYPRESS);
		sinkEvents(Event.ONCONTEXTMENU);
		sinkEvents(Event.ONKEYDOWN);
		sinkEvents(Event.ONKEYUP);
		//get the users
		shareupdateService.getPortalUsers(new AsyncCallback<ArrayList<ItemBean>>() {
			@Override
			public void onSuccess(ArrayList<ItemBean> users) {
				pickUserDlg = new PickItemsDialog('@', users, eventBus, 525);	
				pickUserDlg.withPhoto();
			}
			@Override
			public void onFailure(Throwable caught) {				
			}
		});
		//get the hashtags in this group
		shareupdateService.getHashtags(new AsyncCallback<ArrayList<ItemBean>>() {
			@Override
			public void onSuccess(ArrayList<ItemBean> hashtags) {
				pickHashtagDlg = new PickItemsDialog('#', hashtags, eventBus, 525);	
				pickHashtagDlg.withTriggerCharIncluded();
			}
			@Override
			public void onFailure(Throwable caught) {				
			}
		});

		getElement().setAttribute("id", "postTextArea"); 
		bind();

		this.addKeyPressHandler(new KeyPressHandler() {			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				pickUserDlg.onKeyPress(getCursorPos(), event.getUnicodeCharCode(), getAbsoluteLeft(), getAbsoluteTop()+getOffsetHeight(), getText());	
				pickHashtagDlg.onKeyPress(getCursorPos(), event.getUnicodeCharCode(), getAbsoluteLeft(), getAbsoluteTop()+getOffsetHeight(), getText());	

			}
		});
	}

	/**
	 * @param element
	 */
	public SuperPosedTextArea(Element element) {
		super(element);
	}

	/**
	 * paste event overridden
	 */
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		switch (event.getTypeInt()) {
		case Event.ONPASTE: {
			final String before = getText();
			GWT.log("BEFORE:" + before);
			Timer t = new Timer() {
				@Override
				public void run() {
					String toCheck = getText().replaceAll(before, "");
					ShareUpdateForm.get().checkLink(toCheck);
				}
			};
			t.schedule(100);
			break;
		}
		case Event.ONKEYUP: {
			injectInDiv(getText());
			pickUserDlg.onKeyUp(event.getKeyCode());
			pickHashtagDlg.onKeyUp(event.getKeyCode());
			break;
		}
		case Event.ONCONTEXTMENU: {
			removeSampleText();
			break;
		}
		case Event.ONKEYDOWN: {
			if (pickUserDlg.isShowing()) {
				//avoid the arrow up to move the cursor at the beginning of the textbox and the TAB to move around inputs and enter to go newline
				if (event.getKeyCode() == ARROW_UP || event.getKeyCode() == KeyCodes.KEY_TAB || event.getKeyCode() ==  KeyCodes.KEY_ENTER) {
					DOM.eventCancelBubble(event, true);
					event.preventDefault();
					return;
				}
			}
			if (pickHashtagDlg.isShowing()) {
				//avoid the arrow up to move the cursor at the beginning of the textbox and the TAB to move around inputs and enter to go newline
				if (event.getKeyCode() == ARROW_UP || event.getKeyCode() == KeyCodes.KEY_TAB || event.getKeyCode() ==  KeyCodes.KEY_ENTER) {
					DOM.eventCancelBubble(event, true);
					event.preventDefault();
					return;
				}
			}
			break;
		}
		}
	}
	protected void removeSampleText() {
		if (getText().equals(ShareUpdateForm.SHARE_UPDATE_TEXT) || getText().equals(ShareUpdateForm.ERROR_UPDATE_TEXT) ) {
			setText("");
			addStyleName("darker-color");
			removeStyleName("error");
		}
	}
	protected void cleanHighlighterDiv() {
		DOM.getElementById("highlighter").setInnerHTML("");
	}
	private void injectInDiv(String textAreaText) {
		//GWT.log("# injectInDiv: "+textAreaText);
		String text;
		// parse the text:
		// replace all the line breaks by <br/>, and all the double spaces by the html version &nbsp;
		text = textAreaText.replaceAll("(\r\n|\n)","<br />");
		text = text.replaceAll("\\s\\s","&nbsp;&nbsp;");

		for (String mentionedUser : mentionedUsers) {
			text = text.replaceAll(mentionedUser,"<span class=\"highlightedUser\">"+mentionedUser+"</span>");
		}

		for (String hashtag : hashtags) {
			text = text.replaceAll(hashtag,"<span>"+hashtag+"</span>");
		}

		// re-inject the processed text into the div
		DOM.getElementById("highlighter").setInnerHTML(text);
	}

	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(PickedItemEvent.TYPE, new PickedItemEventHandler() {
			@Override
			public void onSelectedItem(PickedItemEvent event) {

				char triggerChar = event.getTriggerChar();
				String toAdd = event.getSelectedItem().getAlternativeName();
				if (triggerChar == '#') { //has to be treated differently becase the # char remain present in the text unlike the @ 
					hashtags.add(toAdd);

					String preceedingPart = getText().substring(0, event.getItemCursorIndexStart());
					String afterPart = getText().substring(event.getItemCursorIndexEnd()+1);
					//GWT.log("# BEFORE: "+preceedingPart);
					setText(preceedingPart + toAdd + " " + afterPart);
					injectInDiv(getText());
				}
				if (triggerChar == '@') {
					GWT.log("GOT @ EVENT! "+ event.getSelectedItem().getAlternativeName());
					mentionedUsers.add(toAdd);

					String preceedingPart = getText().substring(0, event.getItemCursorIndexStart());
					String afterPart = getText().substring(event.getItemCursorIndexEnd()+1);

					setText(preceedingPart + toAdd + " " + afterPart);
					injectInDiv(getText());

				}				
			}			
		});  
	}

	public ArrayList<String> getMentionedUsers() {
		ArrayList<String> toReturn = new ArrayList<String>();
		for (String mentionedUser : mentionedUsers) {
			if (getText().contains(mentionedUser))
				toReturn.add(mentionedUser);
		}
		GWT.log(toReturn.toString());
		return toReturn;
	}

	public ArrayList<String> getHashtags() {
		ArrayList<String> toReturn = new ArrayList<String>();
		for (String hashtag : hashtags) {
			if (getText().contains(hashtag))
				toReturn.add(hashtag);
		}
		GWT.log(toReturn.toString());
		return toReturn;
	}
}
