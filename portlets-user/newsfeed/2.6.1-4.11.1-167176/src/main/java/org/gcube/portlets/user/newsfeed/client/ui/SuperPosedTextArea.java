/**
 * 
 */
package org.gcube.portlets.user.newsfeed.client.ui;

import java.util.ArrayList;
import java.util.HashSet;

import org.gcube.portlets.user.gcubewidgets.client.elements.Div;
import org.gcube.portlets.user.newsfeed.client.NewsService;
import org.gcube.portlets.user.newsfeed.client.NewsServiceAsync;
import org.gcube.portlets.user.newsfeed.shared.MentionedDTO;
import org.gcube.portlets.widgets.pickitem.client.dialog.PickItemsDialog;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEvent;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEventHandler;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author massi
 *
 */
public class SuperPosedTextArea extends TextArea {

	private final NewsServiceAsync newsService = GWT.create(NewsService.class);

	private final HandlerManager eventBus = new HandlerManager(null);
	
	private PickItemsDialog pickUserDlg = null;
	private Div highlighterDIV;
	public final static int ARROW_UP = 38; 
	public final static int ARROW_DOWN = 40; 

	private HashSet<MentionedDTO> mentionedUsers = new HashSet<MentionedDTO>();
	
	private String areaId;

	/**
	 * 
	 */
	public SuperPosedTextArea(Div highlighterDIV) {
		sinkEvents(Event.ONPASTE);
		sinkEvents(Event.ONCONTEXTMENU);
		sinkEvents(Event.ONKEYDOWN);
		sinkEvents(Event.ONKEYUP);
		setText(AddCommentTemplate.COMMENT_TEXT);
		this.highlighterDIV = highlighterDIV;
		//needed to give unique identifiers to the Area (for the jQuery plugin)
		areaId = "postTextArea"+Random.nextInt();
		getElement().setAttribute("id", areaId); 
		bind();
		Timer t = new Timer() {
			@Override
			public void run() {
				myAutoSize(areaId);	
			}
		};
		t.schedule(200);
		
		this.addKeyPressHandler(new KeyPressHandler() {			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (pickUserDlg != null) {
					int top = getAbsoluteTop();
					int offset = getOffsetHeight();
					int y =  getAbsoluteTop()+getOffsetHeight();
					GWT.log("top=" + top + " - offset = " +offset);
					pickUserDlg.onKeyPress(getCursorPos(), event.getUnicodeCharCode(), getAbsoluteLeft(), y, getText());	
				}
			}
		});
		this.addFocusHandler(new FocusHandler() {			
			@Override
			public void onFocus(FocusEvent event) {
				newsService.getOrganizationUsers(new AsyncCallback<ArrayList<ItemBean>>() {
					@Override
					public void onSuccess(ArrayList<ItemBean> users) {
						pickUserDlg = new PickItemsDialog('@', users, eventBus, 460);				
					}

					@Override
					public void onFailure(Throwable caught) {				
					}
				});				
			}
		});
		
	}
	/**
	 * This is the way to wrap jQuery plugins into GWT, wrap it in a function and call it.
	 */
	public static native void myAutoSize(String myAreaId) /*-{
		function autoSizeArea() {
		 	$wnd.jQuery('#'+myAreaId).autosize();
		}
		autoSizeArea();
	}-*/;
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
			if (getText().equals(AddCommentTemplate.COMMENT_TEXT) || getText().equals(AddCommentTemplate.ERROR_UPDATE_TEXT) ) {
				setText("");
				addStyleName("dark-color");
				removeStyleName("nwfeed-error");
			}
			break;
		}
		case Event.ONKEYUP: {
			injectInDiv(getText());
			pickUserDlg.onKeyUp(event.getKeyCode());
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
			break;
		}
		}
	}
	protected void removeSampleText() {
		if (getText().equals(AddCommentTemplate.COMMENT_TEXT) || getText().equals(AddCommentTemplate.ERROR_UPDATE_TEXT) ) {
			setText("");
			addStyleName("darker-color");
			removeStyleName("nwfeed-error");
		}
	}
	protected void cleanHighlighterDiv() {
		//DOM.getElementById("comment-highlighter").setInnerHTML("");
		highlighterDIV.getElement().setInnerHTML("");
	}
	/**
	 * copy what is being written in the text area in the underneath DIV
	 * @param textAreaText
	 */
	private void injectInDiv(String textAreaText) {
		String text;
		// parse the text:
		// replace all the line braks by <br/>, and all the double spaces by the html version &nbsp;
		text = textAreaText.replaceAll("(\r\n|\n)","<br />");
		text = text.replaceAll("\\s\\s","&nbsp;&nbsp;");
		
		for (MentionedDTO mentionedUser : mentionedUsers) {
			text = text.replaceAll(mentionedUser.value,
					"<span id=\""+ mentionedUser.id +"\" title=\""+ mentionedUser.type +"\" class=\"highlightedUser\">"+mentionedUser.value+"</span>");
		}		
		// re-inject the processed text into the div
		highlighterDIV.getElement().setInnerHTML(text);
	}

	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(PickedItemEvent.TYPE, new PickedItemEventHandler() {
			@Override
			public void onSelectedItem(PickedItemEvent event) {

				String toAdd = event.getSelectedItem().getAlternativeName();
		
				ItemBean ib = event.getSelectedItem();
				String type = ib.isItemGroup() ? "team" : "user";
				MentionedDTO mToAdd = new MentionedDTO(ib.getId(), ib.getAlternativeName(), type);
				mentionedUsers.add(mToAdd);
		
				String preceedingPart = getText().substring(0, event.getItemCursorIndexStart());
				String afterPart = getText().substring(event.getItemCursorIndexEnd()+1);
				
				setText(preceedingPart + toAdd + " " + afterPart);
				injectInDiv(getText());
			}			
		});  
	}

	public HashSet<MentionedDTO> getMentionedUsers() {
		NodeList<Element> elems = highlighterDIV.getElement().getElementsByTagName("span");
		HashSet<MentionedDTO> toReturn = new HashSet<MentionedDTO>();
		if (elems != null && elems.getLength() > 0) {
			int elemsNo = elems.getLength();
			for (int i = 0; i < elemsNo; i++) {
				Element el = elems.getItem(i);
				String id = el.getId();
				String type = el.getTitle();
				String value = el.getInnerText();
				MentionedDTO toAdd = new MentionedDTO(id, value, type);
				toReturn.add(toAdd);
				GWT.log(toAdd.toString());
			}
		}
		return toReturn;
	}
	/**
	 * return the unique identifier of this textarea, useful for getElementById JS method
	 * @return
	 */
	public String getAreaId() {
		return areaId;
	}
	
	
}
