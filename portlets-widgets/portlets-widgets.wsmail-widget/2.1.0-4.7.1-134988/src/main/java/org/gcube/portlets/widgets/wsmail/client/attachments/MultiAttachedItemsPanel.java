package org.gcube.portlets.widgets.wsmail.client.attachments;

import java.util.ArrayList;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;
import org.gcube.portlets.widgets.wsmail.client.multisuggests.BulletList;
import org.gcube.portlets.widgets.wsmail.client.multisuggests.ListItem;
import org.gcube.portlets.widgets.wsmail.client.multisuggests.Paragraph;
import org.gcube.portlets.widgets.wsmail.client.multisuggests.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 Sep 2012
 * 
 */
public class MultiAttachedItemsPanel extends Composite {

	private ArrayList<String> addedWpItems = new ArrayList<String>();
	private ArrayList<Item> currItems = new ArrayList<Item>();
	
	private BulletList list = new BulletList();
	/**
	 * 
	 */
	public MultiAttachedItemsPanel() {
		FlowPanel panel = new FlowPanel();
		initWidget(panel);
		panel.setWidth("100%");	
		list.setStyleName("attach-panel-list");
		panel.add(list);
		list.setVisible(false);;
	}
	public void show() {
		list.setVisible(true);
	}
	/**
	 * insert the attachment item view in the flow panel
	 */
	public void addAttachment(Item selectedWpItem) {
		show();
		if (selectedWpItem != null) {
			TextBox itemBox = new TextBox();
			itemBox.setText(selectedWpItem.getName());
			itemBox.setValue(selectedWpItem.getId());
			currItems.add(selectedWpItem);
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("attach-panel-token");
			Paragraph p = new Paragraph(selectedWpItem.getName());

			Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeListItem(displayItem);
				}
			});

			displayItem.add(p);
			displayItem.add(span);

			GWT.log("Adding selected wp item '" + itemBox.getValue());
			addedWpItems.add(itemBox.getValue());
			GWT.log("Total: " + addedWpItems);

			list.insert(displayItem, list.getWidgetCount());
			itemBox.setValue("");
			itemBox.setFocus(true);
		}
	}
	/**
	 * remove the item
	 * @param displayItem
	 */
	private void removeListItem(ListItem displayItem) {
		GWT.log("Removing: " + displayItem.getWidget(0).getElement().getInnerHTML(), null);
		addedWpItems.remove(getItemIdFromName(displayItem.getWidget(0).getElement().getInnerHTML()));
		list.remove(displayItem);
		GWT.log("Total: " + addedWpItems);
	}
	private String getItemIdFromName(String itemName) {
		for (Item item : currItems) {
			if (itemName.compareTo(item.getName())==0)
				return item.getId();
		}
		return "";
	}
	/**
	 * 
	 * @return the ids od the attachments
	 */
	public ArrayList<String> getAddedWpItems() {
		return addedWpItems;
	}
}
