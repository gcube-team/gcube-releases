package org.gcube.portlets.user.workspace.client.view.sharing.multisuggest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.client.view.sharing.ContactFetcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The Class MultiValuePanel.
 *
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.2 Gen 2013 modified by Francesco Mangiacrapa
 * Facebook style autocompleter
 */
public class MultiValuePanel extends Composite {

//	private final WsMailServiceAsync mailingService = GWT.create(WsMailService.class);
	private List<String> itemsSelected = new ArrayList<String>();
	FlowPanel panel = new FlowPanel();
//	private HandlerManager eventBus;
	private HashMap<String, InfoContactModel> users = new HashMap<String, InfoContactModel>();
	private BulletList listBullet = new BulletList();
	private SuggestBox box;
	private ContactFetcher userFetch;
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

	/**
	 * Instantiates a new multi value panel.
	 *
	 * @param userFetch the ContactFetcher
	 */
	public MultiValuePanel(ContactFetcher userFetch) {
		this.userFetch = userFetch;
		initWidget(panel);
		panel.setWidth("100%");
		listBullet.setStyleName("multivalue-panel-suggest");
		final ListItem item = new ListItem();
		final TextBox itemBox = new TextBox();
		itemBox.getElement().setAttribute("autocomplete", "off");
		itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium; border: none;");

		box = new SuggestBox(getSuggestions(), itemBox);
		item.add(box);
		listBullet.add(item);

		// needed to be set on the itemBox instead of the box, otherwise backspace gets executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				// handle backspace
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
					if ("".equals(itemBox.getValue().trim())) {

						if(listBullet.getWidgetCount()>2){
							ListItem li = (ListItem) listBullet.getWidget(listBullet.getWidgetCount() - 2);

							if(li.getWidget(0) instanceof Paragraph){
								Paragraph p = (Paragraph) li.getWidget(0);
//								GWT.log("p "+p.getText() +" is removable : " + p.isRemovable());
								if (itemsSelected.contains(p.getText()) && p.isRemovable()==true) {
									itemsSelected.remove(p.getText());
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
				chosenContactItem(itemBox, listBullet);
			}
		});

		panel.add(listBullet);
		box.getElement().setId("suggestion_box");  //needed for the focus on click
		panel.getElement().setAttribute("onclick", "document.getElementById('suggestion_box').focus()");
	}

	/**
	 * Box set focus.
	 */
	public void boxSetFocus(){
		box.getElement().focus();
		box.setFocus(true);
	}

	/**
	 * Gets the flow panel.
	 *
	 * @return the flow panel
	 */
	public FlowPanel getFlowPanel(){
		return panel;
	}

	/**
	 * actually insert the contact in the flow panel.
	 *
	 * @param itemBox the item box
	 * @param list the list
	 */
	private void chosenContactItem(final TextBox itemBox, final BulletList list) {
		if (itemBox.getValue() != null && !"".equals(itemBox.getValue().trim())) {
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("multivalue-panel-token-ws");
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
			GWT.log("Total: " + itemsSelected);

			list.insert(displayItem, list.getWidgetCount() - 1);
			itemBox.setValue("");
			itemBox.setFocus(true);
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

			for (String login : itemsSelected) {
				InfoContactModel wsuser = users.get(login);
				if(wsuser!= null && !toReturn.contains(wsuser)){
					toReturn.add(wsuser);
				}
			}
			return toReturn;
		}
	}

	/**
	 * Removes the list item.
	 *
	 * @param displayItem the display item
	 * @param list the list
	 */
	private void removeListItem(ListItem displayItem, BulletList list) {
		GWT.log("Removing: " + displayItem.getWidget(0).getElement().getInnerHTML(), null);
		itemsSelected.remove(displayItem.getWidget(0).getElement().getInnerHTML());
		list.remove(displayItem);
	}

	/**
	 * Gets the suggestions.
	 *
	 * @return names of possible contacts
	 */
	private MultiWordSuggestOracle getSuggestions() {

		userFetch.getListContact(contacts, false);
		return oracle;
	}


	/**
	 * Update suggestions.
	 *
	 * @param result the result
	 */
	public void updateSuggestions(List<InfoContactModel> result) {

		oracle.clear();
		for (InfoContactModel wsUser : result) {
			oracle.add(wsUser.getName());
		}
	}

	private AsyncCallback<List<InfoContactModel>> contacts = new AsyncCallback<List<InfoContactModel>>() {


		@Override
		public void onFailure(Throwable caught) {

		}

		@Override
		public void onSuccess(List<InfoContactModel> result) {

			users.clear();

			for (InfoContactModel wsUser : result) {
				oracle.add(wsUser.getName());
				users.put(wsUser.getName(), wsUser);

			}
		}
	};

	/**
	 * Reset item selected.
	 */
	public void resetItemSelected(){

		listBullet.clear();
		itemsSelected.clear();
		ListItem item = new ListItem();
		item.add(box);
		listBullet.add(item);
	}


	/**
	 * Adds the recipient.
	 *
	 * @param fullName the full name
	 * @param displayRemoveItem the display remove item
	 */
	public void addRecipient(String fullName, boolean displayRemoveItem) {

		if (fullName != null) {

			TextBox itemBox = new TextBox();
			itemBox.setText(fullName);
			itemBox.setValue(fullName);
			final ListItem displayItem = new ListItem();

			Paragraph p = new Paragraph(fullName);
			displayItem.add(p);

			if(displayRemoveItem){
				displayItem.setStyleName("multivalue-panel-token-ws");
				p.setRemovable(true);
				Span span = new Span("x");
				span.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent clickEvent) {
						removeListItem(displayItem, listBullet);
					}
				});

				displayItem.add(span);
			}
			else{
				displayItem.setStyleName("multivalue-panel-token-ws-notselectable");
				p.setRemovable(false);
			}



			GWT.log("Adding selected wp item '" + itemBox.getValue());
			itemsSelected.add(itemBox.getValue());
			GWT.log("Total: " + itemsSelected);

			listBullet.insert(displayItem, listBullet.getWidgetCount()-1);
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
