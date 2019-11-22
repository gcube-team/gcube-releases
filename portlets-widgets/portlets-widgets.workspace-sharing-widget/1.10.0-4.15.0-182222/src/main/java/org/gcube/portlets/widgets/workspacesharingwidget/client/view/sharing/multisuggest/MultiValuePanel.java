package org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.multisuggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.ContactFetcher;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The Class MultiValuePanel.
 *
 * @author Francesco Mangiacrapa Feb 25, 2014
 */
public class MultiValuePanel extends Composite {

	// private final WsMailServiceAsync mailingService =
	// GWT.create(WsMailService.class);
	// private List<String> itemsSelected = new ArrayList<>();
	FlowPanel panel = new FlowPanel();
	// private HandlerManager eventBus;
	private HashMap<String, InfoContactModel> users = new HashMap<String, InfoContactModel>(); // AN
																								// HAST
																								// FULLNAME
																								// -
																								// INFOCONTACTMODEL
	private BulletList listBullet = new BulletList();
	private SuggestBox box;
	// private ContactFetcher userFetch;
	private ServerMultiWordSuggestOracle oracle = new ServerMultiWordSuggestOracle(); // ORACLE

	/**
	 * Instantiates a new multi value panel.
	 *
	 * @param userFetch
	 *            the ContactFetcher
	 * @param readGroupsFromHL
	 *            the read groups from hl
	 * @param readGroupsFromPortal
	 *            the read groups from portal
	 */
	public MultiValuePanel(ContactFetcher userFetch) {
		// this.userFetch = userFetch;
		initWidget(panel);
		panel.setWidth("100%");
		listBullet.setStyleName("multivalue-panel-suggest");
		final ListItem item = new ListItem();
		final TextBox itemBox = new TextBox();
		itemBox.getElement().setAttribute("style",
				"outline-color: -moz-use-text-color; outline-style: none; outline-width: medium; border: none;");
		box = new SuggestBox(oracle, itemBox);

		item.add(box);
		listBullet.add(item);

		// needed to be set on the itemBox instead of the box, otherwise
		// backspace gets executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				// handle backspace
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
					if ("".equals(itemBox.getValue().trim())) {

						if (listBullet.getWidgetCount() > 2) {
							ListItem li = (ListItem) listBullet.getWidget(listBullet.getWidgetCount() - 2);

							if (li.getWidget(0) instanceof Paragraph) {
								Paragraph p = (Paragraph) li.getWidget(0);
								GWT.log("p " + p.getText() + " is removable : " + p.isRemovable());
								if (users.containsKey(p.getText()) && p.isRemovable() == true) {
									users.remove(p.getText());
									GWT.log("Removing selected item: " + p.getText() + "'");
									listBullet.remove(li);
								}

								itemBox.setFocus(true);
							}
						}
					}
				}
			}
		});

		box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {

			public void onSelection(SelectionEvent<SuggestOracle.Suggestion> selectionEvent) {
				InfoContactModelSuggestion suggest = (InfoContactModelSuggestion) selectionEvent.getSelectedItem();
				chosenContactItem(suggest, itemBox, listBullet);
			}
		});

		panel.add(listBullet);
		box.getElement().setId("suggestion_box"); // needed for the focus on
													// click
		panel.getElement().setAttribute("onclick", "document.getElementById('suggestion_box').focus()");
	}

	/**
	 * Box set focus.
	 */
	public void boxSetFocus() {
		box.getElement().focus();
		box.setFocus(true);
	}

	/**
	 * Gets the flow panel.
	 *
	 * @return the flow panel
	 */
	public FlowPanel getFlowPanel() {
		return panel;
	}

	/**
	 * actually insert the contact in the flow panel.
	 *
	 * @param itemBox
	 *            the item box
	 * @param list
	 *            the list
	 */
	private void chosenContactItem(InfoContactModelSuggestion suggest, final TextBox itemBox, final BulletList list) {
		if (itemBox.getValue() != null && !itemBox.getValue().trim().isEmpty()) {
			if (users.containsKey(suggest.getInfoContactModel().getName())) {
				GWT.log("User already selected: " + suggest.getInfoContactModel().getName());
				MessageBox.alert("Attention", "The user selected is already present in the list.", null);
				itemBox.setValue("");
			} else {
				GWT.log("Adding selected user: " + suggest.getInfoContactModel().getName());
				users.put(suggest.getInfoContactModel().getName(), suggest.getInfoContactModel());

				final ListItem displayItem = new ListItem();
				displayItem.setStyleName("multivalue-panel-token-ws");
				Paragraph p = new Paragraph(suggest.getInfoContactModel().getName());

				Span span = new Span("x");
				span.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent clickEvent) {
						removeListItem(displayItem, list);
					}
				});

				displayItem.add(p);
				displayItem.add(span);

				list.insert(displayItem, list.getWidgetCount() - 1);
				itemBox.setValue("");
				itemBox.setFocus(true);
			}
		}
	}

	/**
	 * Gets the selected user.
	 *
	 * @return the selected contacts (user logins e.g. pino.pini)
	 */
	public List<InfoContactModel> getSelectedUser() {
		if (users == null)
			return new ArrayList<InfoContactModel>();
		else {
			List<InfoContactModel> toReturn = new ArrayList<InfoContactModel>();
			// GWT.log("Selected User: "+itemsSelected);
			// GWT.log("users: "+users);
			for (String fullName : users.keySet()) {
				InfoContactModel wsuser = users.get(fullName);
				if (wsuser != null && !toReturn.contains(wsuser))
					toReturn.add(wsuser);
			}
			return toReturn;
		}
	}

	/**
	 * Removes the list item.
	 *
	 * @param displayItem
	 *            the display item
	 * @param list
	 *            the list
	 */
	private void removeListItem(ListItem displayItem, BulletList list) {
		GWT.log("Removing: " + displayItem.getWidget(0).getElement().getInnerHTML(), null);
		// itemsSelected.remove(displayItem.getWidget(0).getElement().getInnerHTML());
		users.remove(displayItem.getWidget(0).getElement().getInnerHTML());
		list.remove(displayItem);
	}

	/**
	 * Gets the suggestions.
	 *
	 * @return names of possible contacts
	 */
	private MultiWordSuggestOracle getSuggestions() {
		// userFetch.getListContact(contacts, false);
		return oracle;
	}

	/*
	 * /** Update suggestions.
	 *
	 * @param result the result
	 * 
	 * public void updateSuggestions(List<InfoContactModel> result) {
	 * 
	 * oracle.clear();
	 * 
	 * for (InfoContactModel wsUser : result) { oracle.add(wsUser.getName());
	 * 
	 * } }
	 * 
	 * 
	 * private AsyncCallback<List<InfoContactModel>> contacts = new
	 * AsyncCallback<List<InfoContactModel>>() {
	 * 
	 * @Override public void onFailure(Throwable caught) {
	 * GWT.log("Error on loading contacts"); MessageBox.alert("Error",
	 * caught.getMessage(), null); }
	 * 
	 * @Override public void onSuccess(List<InfoContactModel> result) {
	 * users.clear(); for (InfoContactModel wsUser : result) { String fullName =
	 * wsUser.getName(); if(users.containsKey(fullName)){ //case of homonimy
	 * fullName = fullName+"_"; users.put(fullName, wsUser); }else
	 * users.put(fullName, wsUser);
	 * 
	 * oracle.add(fullName); } } };
	 */

	/**
	 * Reset item selected.
	 */
	public void resetItemSelected() {

		listBullet.clear();
		// itemsSelected.clear();
		users.clear();
		ListItem item = new ListItem();
		item.add(box);
		listBullet.add(item);
	}

	/**
	 * Adds the recipient.
	 *
	 * @param fullName
	 *            the full name
	 * @param displayRemoveItem
	 *            the display remove item
	 */
	public void addRecipient(InfoContactModel infoContactModel, boolean displayRemoveItem) {

		if (infoContactModel != null) {

			TextBox itemBox = new TextBox();
			itemBox.setText(infoContactModel.getName());
			itemBox.setValue(infoContactModel.getName());
			final ListItem displayItem = new ListItem();

			Paragraph p = new Paragraph(infoContactModel.getName());
			displayItem.add(p);

			if (displayRemoveItem) {
				displayItem.setStyleName("multivalue-panel-token-ws");
				p.setRemovable(true);
				Span span = new Span("x");
				span.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent clickEvent) {
						removeListItem(displayItem, listBullet);
					}
				});

				displayItem.add(span);
			} else {
				displayItem.setStyleName("multivalue-panel-token-ws-notselectable");
				p.setRemovable(false);
				p.getElement().getStyle().setMargin(0, Unit.PX);
			}

			GWT.log("Adding selected wp item '" + itemBox.getValue());
			// itemsSelected.add(itemBox.getValue());
			// GWT.log("Total: " + itemsSelected);
			users.put(infoContactModel.getName(), infoContactModel);

			listBullet.insert(displayItem, listBullet.getWidgetCount() - 1);
			itemBox.setValue("");
			itemBox.setFocus(true);
		}

	}

	/**
	 * Clear list.
	 */
	public void clearList() {
		listBullet.clear();
	}

	/**
	 * Gets the box.
	 *
	 * @return the box
	 */
	public SuggestBox getBox() {
		return box;

	}
}
