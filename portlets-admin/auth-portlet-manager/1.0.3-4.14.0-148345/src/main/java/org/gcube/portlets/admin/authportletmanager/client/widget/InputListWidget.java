package org.gcube.portlets.admin.authportletmanager.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.Entities;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
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

public class InputListWidget extends Composite {

	List<String> itemsSelected = new ArrayList<String>();
	public List<Caller> callerSelected = new ArrayList<Caller>();

	public final BulletList list;
	/*
	 * Disable list for entry caller
	 */
	public void disableList(){
		list.clear();
	}
	/*
	 * Clear list for caller and insert a input box
	 */
	public void clearList(){
		list.clear();
		callerSelected.clear();
		enabledList();
	}
	public InputListWidget() {
		FlowPanel panel = new FlowPanel();
		initWidget(panel);
		//final BulletList list = new BulletList();
		list = new BulletList();
		list.setStyleName("input-list-caller");
		final ListItem item = new ListItem();
		item.setStyleName("token-input-input-token-facebook");
		final TextBox itemBox = new TextBox();
		itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");
		final SuggestBox box = new SuggestBox(getSuggestions(), itemBox);
		box.setLimit(200);
		box.getElement().setId("suggestion_box");
		item.add(box);
		list.add(item);

		// this needs to be on the itemBox rather than box, or backspace will get executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					GWT.log("key enter");
					
					// only allow manual entries with @ signs (assumed email addresses)
					//if (itemBox.getValue().contains("@"))
					//    deselectItem(itemBox, list);
				}
				// handle backspace
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
					if ("".equals(itemBox.getValue().trim())) {
						ListItem li = (ListItem) list.getWidget(list.getWidgetCount() - 2);
						Paragraph p = (Paragraph) li.getWidget(0);
						if (itemsSelected.contains(p.getText())) {
							itemsSelected.remove(p.getText());
							GWT.log("Removing selected item '" + p.getText() + "'", null);
							GWT.log("Remaining: " + itemsSelected, null);
						}
						list.remove(li);
						itemBox.setFocus(true);
					}
				}
			}
		});

		
		
		//box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
		box.addSelectionHandler(new SelectionHandler<MultiWordSuggestOracle.Suggestion>() {			
			
			public void onSelection(SelectionEvent selectionEvent) {
				GWT.log("addSelectionHandler on Selection");
				addItem(itemBox);
			}
		});

		panel.add(list);

		panel.getElement().setAttribute("onclick", "document.getElementById('suggestion_box').focus()");
		box.setFocus(true);

	}



	/**
	 * addCaller
	 * add a caller to list 
	 * @param caller
	 * @param list
	 */	
	public void addCaller(Caller caller){
		GWT.log("aggiunto caller"+caller.getCallerName());
		String prefix=null;
		switch (caller.getTypecaller()) {
		case user:
			prefix=ConstantsSharing.TagCaller;
			break;
		case role:
			prefix=ConstantsSharing.TagRole;
			break;			
		case service:
			prefix=ConstantsSharing.TagService;
			break;

		default: 
			prefix ="";
			break;
		}

		if (!callerSelected.contains(caller))
			callerSelected.add(caller);
		//reset error
		//r_loader_space.clear();
		final TextBox itemBox=new TextBox();

		itemBox.setValue(prefix+caller.getCallerName());
		itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");             
		final ListItem displayItem = new ListItem();
		displayItem.setStyleName("token-input-token-facebook");
		Paragraph p = new Paragraph(itemBox.getValue());

		Span span = new Span("x");
		span.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent clickEvent) {
				removeListItem(displayItem, list);
			}
		});
		displayItem.add(p);

		//	displayItem.add(span);
		list.clear();
		list.insert(displayItem, 0);



	}


	/**
	 * 
	 * @param listCaller list caller for add
	 * @param list Caller used
	 * @param append Boolean used for add an input text 
	 */

	public void addListCaller(List<Caller> listCaller, boolean append){
		if(!append){
			list.clear();
		}
		for (Caller caller:listCaller){
			String prefix=null;
			switch (caller.getTypecaller()) {
			case user:
				prefix=ConstantsSharing.TagCaller;
				break;
			case role:
				prefix=ConstantsSharing.TagRole;
				break;
			case service:
				prefix=ConstantsSharing.TagService;
				break;
			default: 
				prefix ="";
				break;
			}
			//reset error
			final TextBox itemBox=new TextBox();
			itemBox.setValue(prefix+caller.getCallerName());
			itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");             
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("token-input-token-facebook");
			Paragraph p = new Paragraph(itemBox.getValue());

			Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeListItem(displayItem, list);
				}
			});
			if (!callerSelected.contains(caller)){			
				callerSelected.add(caller);
				displayItem.add(p);
				if(append)
					displayItem.add(span);
				list.insert(displayItem, 0);
			}
		}
	}



	/*
	 * Replace a list of caller selected
	 */
	public void replaceListCaller(List<Caller> listCaller){
		//remove all caller
		clearList();
		for (Caller caller:listCaller){
			String prefix=null;
			switch (caller.getTypecaller()) {
			case user:
				prefix=ConstantsSharing.TagCaller;
				break;
			case role:
				prefix=ConstantsSharing.TagRole;
				break;
			case service:
				prefix=ConstantsSharing.TagService;
				break;
			default: 
				prefix ="";
				break;
			}
			final TextBox itemBox=new TextBox();
			itemBox.setValue(prefix+caller.getCallerName());
			itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");             
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("token-input-token-facebook");
			Paragraph p = new Paragraph(itemBox.getValue());

			Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeListItem(displayItem, list);
				}
			});

			callerSelected.add(caller);
			displayItem.add(p);
			displayItem.add(span);
			list.insert(displayItem, 0);

		}
	}

	private void addItem(final TextBox itemBox) {

		if (itemBox.getValue() != null && !"".equals(itemBox.getValue().trim())) {

			String callerName= itemBox.getValue().substring(1);
			if (!callerSelected.contains(Entities.getCallerByName(callerName))){
				final ListItem displayItem = new ListItem();
				displayItem.setStyleName("token-input-token-facebook");
				Paragraph p = new Paragraph(itemBox.getValue());
				displayItem.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent clickEvent) {
						displayItem.addStyleName("token-input-selected-token-facebook");
					}
				});
				Span span = new Span("x");
				span.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent clickEvent) {
						removeListItem(displayItem, list);
					}
				});
				displayItem.add(p);
				displayItem.add(span);
				// hold the original value of the item selected and add at arraylist
				itemsSelected.add(itemBox.getValue());
				//String callerName= itemBox.getValue().substring(1);
				GWT.log("AuthManager - Insert Caller: " + callerName, null);
				callerSelected.add(Entities.getCallerByName(callerName));
				list.insert(displayItem, list.getWidgetCount() - 1);
			}
			itemBox.setValue("");
			itemBox.setFocus(true);

		}
	}
	/**
	 * 
	 * @param displayItem
	 * @param list
	 */
	private void removeListItem(ListItem displayItem, BulletList list) {
		GWT.log("Removing: " + displayItem.getWidget(0).getElement().getInnerHTML(), null);
		itemsSelected.remove(displayItem.getWidget(0).getElement().getInnerHTML());
		String callerName=displayItem.getWidget(0).getElement().getInnerHTML().substring(1);
		callerSelected.remove(Entities.getCallerByName(callerName));
		list.remove(displayItem);
	}
	/**
	 * 
	 * Enabling list caller and add an input box for input 
	 */
	public void enabledList() {
		// TODO Auto-generated method stub
		list.setStyleName("input-list-caller");
		final ListItem item = new ListItem();
		item.setStyleName("token-input-input-token-facebook");
		final TextBox itemBox = new TextBox();
		itemBox.getElement().setAttribute("style", "outline-color: -moz-use-text-color; outline-style: none; outline-width: medium;");
		final SuggestBox box = new SuggestBox(getSuggestions(), itemBox);
		box.getElement().setId("suggestion_box");
		item.add(box);
		list.add(item);

		// this needs to be on the itemBox rather than box, or backspace will get executed twice
		itemBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// only allow manual entries with @ signs (assumed email addresses)
					//if (itemBox.getValue().contains("@"))
					//    deselectItem(itemBox, list);
				}
				// handle backspace
				if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
					if ("".equals(itemBox.getValue().trim())) {
						ListItem li = (ListItem) list.getWidget(list.getWidgetCount() - 2);
						Paragraph p = (Paragraph) li.getWidget(0);
						if (itemsSelected.contains(p.getText())) {
							itemsSelected.remove(p.getText());
							GWT.log("Removing selected item '" + p.getText() + "'", null);
							GWT.log("Remaining: " + itemsSelected, null);
						}
						list.remove(li);
						itemBox.setFocus(true);
					}
				}
			}
		});
		box.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			public void onSelection(SelectionEvent selectionEvent) {
				addItem(itemBox);
			}
		});
	}
	//
	private MultiWordSuggestOracle getSuggestions() {
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle(".:");

		String prefix=null;
		for (Caller suggestion:Entities.getCallers()){ 
			switch (suggestion.getTypecaller()) {
			case user:
				prefix=ConstantsSharing.TagCaller;
				break;
			case role:
				prefix=ConstantsSharing.TagRole;
				break;
			case service:
				prefix=ConstantsSharing.TagService;
				break;
			default: 
				prefix ="";
				break;
			}
			oracle.add(prefix+suggestion.getCallerName());
			
		}
		return oracle;
	}




}