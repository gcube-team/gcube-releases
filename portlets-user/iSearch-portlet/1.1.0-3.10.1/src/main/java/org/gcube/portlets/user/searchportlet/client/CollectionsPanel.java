package org.gcube.portlets.user.searchportlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.gcube.portlets.user.searchportlet.client.widgets.CollectionCheckBox;
import org.gcube.portlets.user.searchportlet.shared.CollectionBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree.Resources;

public class CollectionsPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private ScrollPanel scrollPanel = new ScrollPanel();
	private Tree collectionsTree = new Tree((Resources) GWT.create(CollectionsTreeImageResources.class), true);
	private TreeItem treeRoot;
	private CollectionCheckBox rootCb;
	private Image loadingImage = new Image(GWT.getModuleBaseURL() + "../images/loading.gif");
	private HashSet<String> currentSelectedCollections = new HashSet<String>();
	private int numberOfAllAvailableCollections = 0;

	public CollectionsPanel() {
		scrollPanel.setWidth("100%");
		mainPanel.setWidth("100%");
		mainPanel.setSpacing(5);
		scrollPanel.add(mainPanel);

		CollectionBean fakeRootCol = new CollectionBean("all_collections", "All Collections", "", "", "", "", false, true, false);
		rootCb = new CollectionCheckBox(fakeRootCol, null);
		treeRoot = new TreeItem(rootCb);
		collectionsTree.addItem(treeRoot);
		
		rootCb.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				manageClickOnRootItem(((CheckBox) event.getSource()).getValue());
			}
		});

		mainPanel.add(loadingImage);
		initializeWithData();

		initWidget(scrollPanel);
	}

	private void initializeWithData(){

		AsyncCallback<HashSet<String>> getSelectedCollectionsFromSessionCallback = new AsyncCallback<HashSet<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				createTree();	
			}

			@Override
			public void onSuccess(HashSet<String> result) {
				if (result != null)
					CollectionsPanel.this.currentSelectedCollections = result;
				createTree();

			}
		};SearchPortlet.searchService.getSelectedCollectionsFromSession(getSelectedCollectionsFromSessionCallback);


	}

	private void createTree() {
		AsyncCallback<HashMap<CollectionBean, ArrayList<CollectionBean>>> collectionsCallback = new AsyncCallback<HashMap<CollectionBean, ArrayList<CollectionBean>>>() {

			public void onFailure(Throwable caught) {
				mainPanel.clear();
				mainPanel.add(new HTML("<span style=\"color: darkblue\">There are no collections available.</span>"));
			}

			public void onSuccess(final HashMap<CollectionBean, ArrayList<CollectionBean>> collections) {
				if (collections != null && !collections.isEmpty()) {
					mainPanel.clear();
					mainPanel.add(collectionsTree);
					Iterator<Entry<CollectionBean, ArrayList<CollectionBean>>> it = collections.entrySet().iterator();
					int numOfCheckedGroups = 0;
					while (it.hasNext()) {
						Entry<CollectionBean, ArrayList<CollectionBean>> group = it.next();

						final CollectionBean collectionGroup = group.getKey();
						CollectionCheckBox groupCb = new CollectionCheckBox(collectionGroup, rootCb);
						TreeItem groupTreeItem = new TreeItem(groupCb);
						treeRoot.addItem(groupTreeItem);
						rootCb.addChildCheckBox(groupCb);

						groupCb.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								//SearchPortlet.logger.log(Level.SEVERE, "Collection Group is clicked add/remove its children to the list");
								ArrayList<CollectionBean> collectionsList = collections.get(collectionGroup);
								for (CollectionBean col : collectionsList) {
									if (((CheckBox) event.getSource()).getValue()) {
										currentSelectedCollections.add(col.getCollectionID());
										//SearchPortlet.logger.log(Level.SEVERE, "Adding to selected collections the -> " + col.getCollectionID());
									}
									else {
										currentSelectedCollections.remove(col.getCollectionID());
										//SearchPortlet.logger.log(Level.SEVERE, "Removing from the selected collections the -> " + col.getCollectionID());
									}
								}

							}
						});

						ArrayList<CollectionBean> collectionsList = collections.get(collectionGroup);
						
						//TODO: Here we count the number of collections, excluding groups
						numberOfAllAvailableCollections += collectionsList.size();
						
						int numOfCheckedCollections = 0;
						// for each collection of the current group
						for (final CollectionBean col : collectionsList) {
							CollectionCheckBox colCb = new CollectionCheckBox(col, groupCb);
							TreeItem colTreeItem = new TreeItem(colCb);
							groupTreeItem.addItem(colTreeItem);
							groupCb.addChildCheckBox(colCb);
							if (currentSelectedCollections.contains(col.getCollectionID())) {
								colCb.setValue(true);
								numOfCheckedCollections++;
							}
							colCb.addClickHandler(new ClickHandler() {

								@Override
								public void onClick(ClickEvent event) {
									//SearchPortlet.logger.log(Level.SEVERE, "Collection is clicked add/remove it to the current selected");
									if (((CheckBox) event.getSource()).getValue()) {
										currentSelectedCollections.add(col.getCollectionID());
									}
									else
										currentSelectedCollections.remove(col.getCollectionID());

								}
							});
						}
						if (numOfCheckedCollections == collectionsList.size()) {
							groupCb.setValue(true);
							numOfCheckedGroups++;
						}
						groupTreeItem.setState(true);
					}
					if (numOfCheckedGroups == collections.size())
						rootCb.setValue(true);
					treeRoot.setState(true);
				}
				else {
					mainPanel.clear();
					mainPanel.add(new HTML("<span style=\"color: darkblue\">There are no collections available.</span>"));
				}
			}
		};SearchPortlet.searchService.getAvailableCollections(collectionsCallback);
	}
	
	public int getNumberOfAvailableCollections() {
		return numberOfAllAvailableCollections;
	}

	public HashSet<String> getSelectedCollections() {
		//SearchPortlet.logger.log(Level.SEVERE, "Returning the current selected cols. Num is -> " + currentSelectedCollections.size());
		return currentSelectedCollections;
	}

	private void manageClickOnRootItem(boolean value) {
		for (int i=0; i<treeRoot.getChildCount(); i++) {
			// Group
			TreeItem groupItem = treeRoot.getChild(i);
			for (int j=0; j<groupItem.getChildCount(); j++) {
				TreeItem colItem = groupItem.getChild(j);
				CollectionBean refCol = ((CollectionCheckBox)colItem.getWidget()).getReferencedCollection();
				String id = refCol.getCollectionID();
				if (value) {

					//SearchPortlet.logger.log(Level.SEVERE, "Adding to selected collections the -> " + id);
					currentSelectedCollections.add(id);
				}
				else {

					//SearchPortlet.logger.log(Level.SEVERE, "Removing from selected collections the -> " + id);
					currentSelectedCollections.remove(id);
				}
			}
		}
	}
}
