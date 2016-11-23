package org.gcube.portlets.widgets.wsmail.client.multisuggests;

import java.util.ArrayList;


import org.gcube.portlets.widgets.wsmail.client.WsMailService;
import org.gcube.portlets.widgets.wsmail.client.WsMailServiceAsync;
import org.gcube.portlets.widgets.wsmail.client.events.RenderForm;
import org.gcube.portlets.widgets.wsmail.server.WsMailServiceImpl;
import org.gcube.portlets.widgets.wsmail.shared.CurrUserAndPortalUsersWrapper;
import org.gcube.portlets.widgets.wsmail.shared.WSUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
/**
 * @author Massimiliano Assante, ISTI-CNR
 * Facebook style autocompleter
 * 
 */
public class MultiValuePanel extends Composite {

	private final WsMailServiceAsync mailingService = GWT.create(WsMailService.class);
	private WSUser currentUser;
	private ArrayList<String> itemsSelected = new ArrayList<String>();
	FlowPanel panel = new FlowPanel();
	private HandlerManager eventBus;
	private ArrayList<WSUser> users;
	private BulletList list = new BulletList();
	private SuggestBox box; 

	/**
	 * 
	 * @param eventBus the eventbus to fire events
	 */
	public MultiValuePanel(HandlerManager eventBus) {
		this.eventBus = eventBus;	
		initWidget(panel);
		panel.setWidth("100%");	
		list.setStyleName("multivalue-panel-list");
		final ListItem item = new ListItem();
		final TextBox itemBox = new TextBox();
		itemBox.getElement().setAttribute("autocomplete", "off");
		itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium; border: none;");
		box = new SuggestBox(getSuggestions(), itemBox);

		item.add(box);
		list.add(item);

		// needed to be set on the itemBox instead of the box, otherwise backspace gets executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				// handle backspace
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
					if ("".equals(itemBox.getValue().trim())) {
						ListItem li = (ListItem) list.getWidget(list.getWidgetCount() - 2);
						Paragraph p = (Paragraph) li.getWidget(0);
						if (itemsSelected.contains(p.getText())) {
							itemsSelected.remove(p.getText());
							GWT.log("Removing selected item: " + p.getText() + "'");
						}
						list.remove(li);
						itemBox.setFocus(true);
					}
				}
			}
		});

		box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			public void onSelection(SelectionEvent<SuggestOracle.Suggestion> selectionEvent) {
				chosenContactItem(itemBox, list);
			}
		});

		panel.add(list);
		box.getElement().setId("suggestion_box");  //needed for the focus on click
		panel.getElement().setAttribute("onclick", "document.getElementById('suggestion_box').focus()");
		box.setFocus(true);
	}
	/**
	 * actually insert the contact in the flow panel
	 * @param itemBox
	 * @param list
	 */
	private void chosenContactItem(final TextBox itemBox, final BulletList list) {
		if (itemBox.getValue() != null && !"".equals(itemBox.getValue().trim())) {
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("multivalue-panel-token");
			Paragraph p = new Paragraph(itemBox.getValue());

			Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeListItem(displayItem, list);
				}
			});

			displayItem.add(p);
			displayItem.add(span);

			//original value of the item selected
			GWT.log("Adding selected item '" + itemBox.getValue());
			itemsSelected.add(itemBox.getValue());
			//GWT.log("Total: " + itemsSelected + " count="+list.getWidgetCount());

			int insertionPoint = (list.getWidgetCount() < 1) ? 0 : list.getWidgetCount()-1;
			
			list.insert(displayItem, insertionPoint);
			itemBox.setValue("");
			itemBox.setFocus(true);			
		}
	}
	/**
	 * insert the recipient view in the flow panel for simple Reply
	 */
	public void addRecipient(String fullName) {
		if (fullName != null) {
			final ListItem item = new ListItem();
			final TextBox itemBox = new TextBox();
			itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium; border: none;");
			box = new SuggestBox(getSuggestions(), itemBox);

			item.add(box);
			list.add(item);

			itemBox.setText(fullName);
			itemBox.setValue(fullName);
			chosenContactItem(itemBox, list);

			box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
				public void onSelection(SelectionEvent<SuggestOracle.Suggestion> selectionEvent) {
					chosenContactItem(itemBox, list);
				}
			});
		}
	}

	/**
	 * insert the recipients view in the flow panel for Reply all
	 */
	public void addRecipients(ArrayList<String> fullNames) {
		for (int i = 0; i < fullNames.size(); i++) {
			String fullName = fullNames.get(i);
			if (fullName != null) {
				final TextBox itemBox = new TextBox();
				itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium; border: none;");

				itemBox.setText(fullName);
				itemBox.setValue(fullName);
				chosenContactItem(itemBox, list);
			}
		}

	}

	public void setFocusOn() {
		Timer t = new Timer() {

			@Override
			public void run() {
				box.setFocus(true);			
			}
		};
		t.schedule(300);

	}
	/**
	 * 
	 * @return the screennames of the addressee (user logins e.g. pino.pini)
	 */
	public ArrayList<String> getSelectedUserIds() {
		if (users == null) 
			return new ArrayList<String>();
		else {
			ArrayList<String> toReturn = new ArrayList<String>();
			for (String fullName : itemsSelected) 
				for (WSUser wsuser : users) 
					if (wsuser.getFullName().compareTo(fullName) == 0) {
						toReturn.add(wsuser.getScreenname());
						break;
					}
			return toReturn;
		}
	}

	private void removeListItem(ListItem displayItem, BulletList list) {
		GWT.log("Removing: " + displayItem.getWidget(0).getElement().getInnerHTML(), null);
		itemsSelected.remove(displayItem.getWidget(0).getElement().getInnerHTML());
		list.remove(displayItem);
	}


	/**
	 *
	 * @return names of possible contacts
	 */
	private MultiWordSuggestOracle getSuggestions() {
		final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		mailingService.getWorkspaceUsers(new AsyncCallback<CurrUserAndPortalUsersWrapper>() {

			@Override
			public void onSuccess(CurrUserAndPortalUsersWrapper result) {				
				if (result.getCurrentUser().getScreenname().compareTo(WsMailServiceImpl.TEST_USER) == 0) {
					doShowSessionExpired();
					eventBus.fireEvent(new RenderForm(false));
				} else{
					currentUser = result.getCurrentUser(); 
					for (WSUser wsUser : result.getUsers()) {
						GWT.log(wsUser.getFullName());
						oracle.add(wsUser.getFullName());
					}				
					users = result.getUsers();
					GWT.log("doh"+(eventBus==null));
					eventBus.fireEvent(new RenderForm(true));
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new RenderForm(false));
			}
		});

		return oracle;
	}

	public ArrayList<WSUser> getAllUsers() {
		return users;
	}

	public WSUser getCurrentUser() {
		return currentUser;
	}

	public void clearList() {
		list.clear();
	}

	private void doShowSessionExpired() {
		Window.alert("Session Expired, logout please");
//		GWT.runAsync(new RunAsyncCallback() {
//			@Override
//			public void onSuccess() {
//				CheckSession.showLogoutDialog();
//			}
//			public void onFailure(Throwable reason) {
//				Window.alert("Could not load this component: " + reason.getMessage());
//			}   
//		});
	}
}
