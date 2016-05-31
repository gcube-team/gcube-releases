package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gcube.portlets.user.collectionsnavigatorportlet.shared.CollectionInfoModel;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * 
 *
 */
public class TreeListItems {

	public TreeItem treeRoot = null;
	protected FormPanel form;

	/**
	 * A helper method to simplify adding tree items of text.
	 * {@link #addTextItem(TreeItem, String) code}
	 * 
	 * @param root the tree item to which the new item will be added.
	 * @param title the text associated with this item.
	 * @param name the Collection ID
	 */
	private TreeItem addTextItem(TreeItem root, String title,String name, String altText, String checked, String recNo, String creationDate, boolean isGroup) {
		TreeItem item = null;

		StackPanel itemContent = new StackPanel();

		Hidden hidden = new Hidden();
		hidden.setName("hidden_collections[]");
		hidden.setValue(name+"_0");

		MyCheckBox checkBox = new MyCheckBox(title, ((MyCompositeCheckBox)((StackPanel)root.getWidget()).getWidget(0)).getCheckBox());
		checkBox.setName("collection_name_" + name);
		checkBox.setForm(form);
		DOM.sinkEvents(checkBox.getElement(),Event.ONMOUSEOVER | Event.ONMOUSEOUT | DOM.getEventsSunk(checkBox.getElement()));

		MyCompositeCheckBox comp = new MyCompositeCheckBox(checkBox, altText, isGroup);
		itemContent.add(comp);

		// set the alt text to be shown when the more link is clicked. also set the recNo and the creationDate
		checkBox.setAltText(altText);
		if (isGroup) {
			checkBox.setCreationDate(null);
			checkBox.setRecNo(null);
		}
		else {
			checkBox.setCreationDate(creationDate);
			checkBox.setRecNo(recNo);
		}

		itemContent.add(hidden);
		item = new TreeItem(itemContent);
		comp.setTreeItem(item);


		Element elItem = item.getElement();
		elItem = DOM.getFirstChild(elItem);	// Get the "table" element
		elItem = DOM.getFirstChild(elItem);	// Get the "tbody" element
		elItem = DOM.getFirstChild(elItem);	// Get the "tr" element
		elItem = DOM.getFirstChild(elItem);	// Get the "td" element
		DOM.setStyleAttribute(elItem, "verticalAlign", "top");

		root.addItem(item);
		item.setState(false, true);
		if(checked.trim().equals("selected"))
		{
			checkBox.setValue(true);
		}
		//item.setStyleName("unknown");
		try
		{
			((MyCompositeCheckBox)((StackPanel)root.getWidget()).getWidget(0)).getCheckBox().addCheckbox(checkBox);
		}
		catch (Exception e) {
			Window.alert(e.toString());
		}
		return item;
	}	

	/**
	 * This method creates the tree with the available Collections
	 * 
	 */
	private void createTree(){

		AsyncCallback<HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>>> collectionsCallback = new AsyncCallback<HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>>>() {

			public void onFailure(Throwable caught) {
				//Do Something here
				CollectionsNavigatorPortletG.hideLoading();
				Window.alert("Failed to retrieve the collections. Click on the refresh button to try again");
			}
			
			public void onSuccess(HashMap<CollectionInfoModel, ArrayList<CollectionInfoModel>> collections) {
				CollectionsNavigatorPortletG.hideLoading();
				if (collections != null && !collections.isEmpty()) {
					Iterator<Entry<CollectionInfoModel, ArrayList<CollectionInfoModel>>> it = collections.entrySet().iterator();
					while (it.hasNext()) {
						Entry<CollectionInfoModel, ArrayList<CollectionInfoModel>> group = it.next();

						CollectionInfoModel collectionGroup = group.getKey();
						String selectedLabel = "selected";
						if (!collectionGroup.isCollectionSelected())
							selectedLabel = "not selected";
						TreeItem groupTreeItem = addTextItem(treeRoot, collectionGroup.getCollectionName(), collectionGroup.getCollectionID(), collectionGroup.getCollecionDescription(), selectedLabel, collectionGroup.getRecordNumber(), collectionGroup.getCreationDate(), true);


						ArrayList<CollectionInfoModel> collectionsList = collections.get(collectionGroup);
						// for each collection of the current group
						for (CollectionInfoModel col : collectionsList) {
							selectedLabel = "selected";
							if (!col.isCollectionSelected())
								selectedLabel = "not selected";
							addTextItem(groupTreeItem, col.getCollectionName(), col.getCollectionID(), col.getCollecionDescription(), selectedLabel, col.getRecordNumber(), col.getCreationDate(), false);
						}
						
						if (collectionGroup.isCollectionOpen()) {
							//groupTreeItem.setState(true, true);
							((Hidden)((StackPanel) groupTreeItem.getWidget()).getWidget(1)).setValue(collectionGroup.getCollectionID() + "_1");
						}
						//TODO new entry here
						else
							((Hidden)((StackPanel) groupTreeItem.getWidget()).getWidget(1)).setValue(collectionGroup.getCollectionID() + "_0");
						groupTreeItem.setState(true);
					}
					treeRoot.setState(true);
				}
			}
		};CollectionsNavigatorPortletG.collectionsService.getAvailableCollections(collectionsCallback);
		CollectionsNavigatorPortletG.showLoading();
	}

	public TreeListItems(TreeItem tree, FormPanel formPanel){
		form = formPanel;
		treeRoot = tree;
		createTree();
	}
}
