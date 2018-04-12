package org.gcube.portlets.widgets.pickitem.client.dialog;

import java.util.ArrayList;

import org.gcube.portlets.widgets.pickitem.client.bundle.CssAndImages;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEvent;
import org.gcube.portlets.widgets.pickitem.client.events.PickedItemEventHandler;
import org.gcube.portlets.widgets.pickitem.client.uibinder.NoPhotoTemplate;
import org.gcube.portlets.widgets.pickitem.client.uibinder.SelectableItem;
import org.gcube.portlets.widgets.pickitem.client.uibinder.WithPhotoTemplate;
import org.gcube.portlets.widgets.pickitem.shared.ItemBean;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * Use this widget to display a  a dropdown user list you can attach to a textbox to make select portal users typing @
 * 
 * To get to know which user was selected listen for the {@link PickedItemEvent} on the {@link HandlerManager} instance you pass to this widget.
 *
 */
public class PickItemsDialog extends PopupPanel {

	public final static int ARROW_UP = 38; 
	public final static int ARROW_DOWN = 40; 

	public final static int DELETE = KeyCodes.KEY_DELETE; 
	public final static int ENTER = KeyCodes.KEY_ENTER; 
	public final static int ESCAPE = KeyCodes.KEY_ESCAPE; 
	public final static int TAB = KeyCodes.KEY_TAB; 

	private HandlerManager eventBus;

	private int limit = 10;

	private int itemCursorIndexEnd;

	private int itemCursorIndexStart = -1;
	boolean handleNonCharKeys = false;

	private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

	private int displayIndexSelected;

	private FocusPanel focusPanel = new FocusPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private char triggerChar;
	private ArrayList<ItemBean> beans;

	//needed because is selected when it popups 
	private Widget first;
	private boolean hasPhoto;
	private boolean includeTriggerChar;
	//to explain
	private boolean stopListening = true;
	
	static {
		CssAndImages.INSTANCE.css().ensureInjected();
	}

	/**
	 * @param triggerChar the 'single char' used to trigger the items list show, e.g. '@', '#' ....
	 * @param the list of user to pick
	 * @param eventBus the event bus on where the widget will fire the selected user event
	 * @param widthInPixel the desired width (grater than 199 pixel)
	 * @param hasPhoto tell of you have want to show photo for the item or not
	 * @param includeTriggerChar true if your suggestions start with the trigger char (e.g. #anHashTag triggered by #) false otherwise
	 */
	public PickItemsDialog(char triggerChar, ArrayList<ItemBean> beans, final HandlerManager eventBus, int widthInPixel) {
		super(true, false);
		if (widthInPixel < 200) {
			throw new IllegalArgumentException("width must be greater than 199");
		}
		this.eventBus = eventBus;
		this.triggerChar = triggerChar;
		this.includeTriggerChar = false;
		this.hasPhoto = false;
		this.beans = beans;
		focusPanel.setWidth(widthInPixel+"px");
		mainPanel.setWidth(widthInPixel+"px");
		setWidth(widthInPixel+"px");
		focusPanel.add(mainPanel);
		setWidget(focusPanel);
		setStyleName("pickDialog");		

		//add the user fill names to the oracle
		for (ItemBean bean : beans) {
			oracle.add(bean.getAlternativeName());
			
			// if it is a team, set the avatar
			if(bean.isItemGroup())
				bean.setThumbnailURL(CssAndImages.INSTANCE.iconTeam().getURL());
				
		}	

		//remove the first selected when hovering
		focusPanel.addMouseOverHandler(new MouseOverHandler() {			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				String styleSelected = hasPhoto ? "pickperson-selected" : "pickitem-selected";
				if (first != null)
					first.removeStyleName(styleSelected);				
			}
		});

		focusPanel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				select(displayIndexSelected);		
			}
		});	

		focusPanel.addMouseDownHandler(new MouseDownHandler() {			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				handleMouseDown();
			}
		});
		
		eventBus.addHandler(PickedItemEvent.TYPE, new PickedItemEventHandler() {
			@Override
			public void onSelectedItem(PickedItemEvent event) {
				GWT.log("GOT @ CAZZEVENT! "+ event.getSelectedItem().getAlternativeName());
				stopListening = true;
			}
		});
	}
	/**
	 * use if you have want to show a photo for the item or not, remember to provide it in {@link ItemBean} instances
	 */
	public void withPhoto() {
		hasPhoto = true;
	}
	/**
	 * use to include the trigger char in search if your suggestions start with the trigger char (e.g. #anHashTag triggered by #)
	 */
	public void withTriggerCharIncluded() {
		includeTriggerChar = true;
	}

	private void handleMouseDown() {
		SelectableItem ut = (SelectableItem) mainPanel.getWidget(displayIndexSelected);
		ItemBean itemBean = new ItemBean(ut.getItemId(), "username", ut.getItemName(), "thumb");
		itemBean.setItemGroup(ut.isGroup());
		eventBus.fireEvent(new PickedItemEvent(itemBean, this.triggerChar, itemCursorIndexStart, itemCursorIndexEnd));
		hide();
		select(0); //RESET
	}

	/**
	 * called for each onKeyPress event from the user
	 * @param keyCode the event keycode
	 * @param x
	 * @param y
	 * @param currText
	 */
	public void onKeyPress(int cursorPos, int keyCode, int x, int y, String currText) {
		char ch = (char) keyCode;
		if (ch == triggerChar) { 
			setPopupPosition(x, y);
			hide();
			handleNonCharKeys = false;
			stopListening = false;
			this.itemCursorIndexStart = cursorPos;
		}	
		else {
			
			itemCursorIndexEnd = cursorPos;
			currText = currText.substring(itemCursorIndexStart, cursorPos)+ch;
					
			if (currText.contains(""+triggerChar) && currText.length() > 1 && !stopListening) {
				if (pickingUser(currText.substring(1))) {
					handleNonCharKeys = true;
				} 
			} else if (!currText.contains(""+triggerChar) || stopListening) {
				hide();
				handleNonCharKeys = false;
				GWT.log("stopListening =" +stopListening);
			}
		}
	}

	/**
	 * called for each onKeyUp event from the user
	 * @param keyCode the event keycode
	 */
	public void onKeyUp(int keyCode) {
		if (handleNonCharKeys) {
			handleNonCharKeys(keyCode);
		}
		if (keyCode == ENTER) {
			stopListening = true;
			handleNonCharKeys = false;
		} 
	}

	/**
	 * split the text and keeps listening for user keyboard events
	 * @param currText the text being typed
	 */
	private boolean pickingUser(String currText) {
		if (currText.trim().length() > 0) {
			if (includeTriggerChar)
				showSuggestions(triggerChar+currText);
			else
				showSuggestions(currText);
			return true;
		}
		hide();
		return false;
	}
	/**
	 * handles the nonchar events (arrows, esc, enter etc)
	 * @param event
	 */
	private void handleNonCharKeys(int keyCode) {
		switch (keyCode) {
		case ARROW_UP:
			if (displayIndexSelected > 0)
				select(--displayIndexSelected);
			break;
		case ARROW_DOWN:
		case TAB:			
			if (displayIndexSelected+1 < mainPanel.getWidgetCount()) 
				select(displayIndexSelected+1);		
			break;
		case ESCAPE:
		case DELETE:
			hide();
			break;
		case ENTER: //selectd with keyboard
			GWT.log("Enter selcted");
			SelectableItem ut = null;
			if (mainPanel.getWidgetCount() > 0) {
				if (displayIndexSelected < 0 || displayIndexSelected >= mainPanel.getWidgetCount()) //when there's only one left sometimes here i get -sth, no time to see why :)
					ut = (SelectableItem) mainPanel.getWidget(0);
				else 
					ut = (SelectableItem) mainPanel.getWidget(displayIndexSelected);
				ItemBean itemBean = new ItemBean(ut.getItemId(), "username", ut.getItemName(), "thumb");
				itemBean.setItemGroup(ut.isGroup());
				eventBus.fireEvent(new PickedItemEvent(itemBean, this.triggerChar, itemCursorIndexStart, itemCursorIndexEnd));
				hide();
				select(0); //RESET
			}
			else {
				GWT.log("mainPanel.getWidgetCount() non ci entra");
				hide();
				select(0); //RESET
			}
			break;
		default:
			break;
		}
	}

	public void showSuggestions(String query) {
		if (query.length() > 0) {
			oracle.requestSuggestions(new Request(query, limit), new Callback() {
				public void onSuggestionsReady(Request request, Response response) {
					mainPanel.clear();
					int i = 0;
					for (Suggestion s : response.getSuggestions()) {
						if (i == 0) {
							first = getUserTemplate(getUserModelBySuggestion(s), i, hasPhoto);
							String styleSelected = hasPhoto ? "pickperson-selected" : "pickitem-selected";
							first.addStyleName(styleSelected);
							mainPanel.add(first);
						}
						else
							mainPanel.add(getUserTemplate(getUserModelBySuggestion(s), i, hasPhoto));
						i++;
					}
					if (i > 0) {
						show();
					}
				}
			});
		}
	}
	
	private ItemBean getUserModelBySuggestion(Suggestion suggestion) {
		for (ItemBean bean : beans) {
			if (suggestion.getReplacementString().compareTo(bean.getAlternativeName()) ==0) 
				return bean;			
		}
		return new ItemBean("no-match","no-match","no-match","no-match");
	}

	private Widget getUserTemplate(ItemBean user, int displayIndex, boolean hasPhoto) {
		if (hasPhoto)
			return new WithPhotoTemplate(this, user, displayIndex);
		return new NoPhotoTemplate(this, user, displayIndex);
	}

	/**
	 * select the user in the model and in the view
	 * @param displayIndex
	 */
	public void select(int displayIndex) {
		String styleSelected = hasPhoto ? "pickperson-selected" : "pickitem-selected";
		for (int i = 0; i < mainPanel.getWidgetCount(); i++) {
			Widget ut = (Widget) mainPanel.getWidget(i);
			if (i == displayIndex) {
				ut.addStyleName(styleSelected);
				displayIndexSelected = i;
			}
			else
				ut.removeStyleName(styleSelected);
		}
	}
}
