package org.gcube.portlets.admin.searchmanagerportlet.gwt.client;

import java.util.ArrayList;

import org.gcube.portlets.admin.searchmanagerportlet.gwt.shared.CollectionFieldsBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Tree.Resources;

public class CollectionsFieldsPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private ScrollPanel scrollPanel = new ScrollPanel();
	private HorizontalPanel toolbar = new HorizontalPanel();
	private HTML splitLine = new HTML("<div style=\"width:100%; height:2px;  background-color:#D0E4F6\"></div>"); //#D0E4F6, #92C1F0
	private Tree collectionsTree = new Tree((Resources) GWT.create(FieldsImageResources.class), true);

	// Create the root Item of the tree
	private TreeItem treeRoot = new TreeItem("Collections");

	private Button refreshBtn = new Button();

	public CollectionsFieldsPanel() {
		scrollPanel.setWidth("100%");
		scrollPanel.setHeight("650px");
		mainPanel.setWidth("100%");
		mainPanel.setSpacing(5);
		scrollPanel.add(mainPanel);
		
		refreshBtn.setTitle("Refresh the collections' information");
		refreshBtn.setStyleName("refreshButton");
		toolbar.setSpacing(8);
		toolbar.add(refreshBtn);

		// Get the available collections and their fields
		refreshTreeInfo();

		initWidget(scrollPanel);

		refreshBtn.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				refreshTreeInfo();
			}
		});

	}

	public void refreshTreeInfo() {
		AsyncCallback<ArrayList<CollectionFieldsBean>> retrieveFieldsCallback = new AsyncCallback<ArrayList<CollectionFieldsBean>>() {

			public void onFailure(Throwable caught) {
				SearchManager.hideLoading();
				SearchManager.displayErrorWindow("Failed to retrieve the available collections and fields. Please click on the refresh button to try again.", caught);
			}

			public void onSuccess(ArrayList<CollectionFieldsBean> result) {
				SearchManager.hideLoading();
				treeRoot.removeItems();
				collectionsTree.removeItems();
				mainPanel.clear();
				mainPanel.add(toolbar);
				mainPanel.add(splitLine);
				if (result.size() > 0) {
					collectionsTree.addItem(treeRoot);
					// Add the available collections to the tree
					for (CollectionFieldsBean colBean : result) {
						TreeItem item;
						if (colBean.getSearchableFields().size() <= 0 && colBean.getPresentableFields().size() <= 0) {
							item = new TreeItem(new HTML("<span style=\"color: #bbbbbb;\">" + colBean.getName() + " (" + colBean.getID() + ")", true));
							item.addItem(new HTML("<span style=\"color: darkblue\">There are no fields associated to this collection.</span>", true));
						}
						else
							item = new TreeItem(new HTML(colBean.getName() + " (" + colBean.getID() + ")", true));
						treeRoot.addItem(item);

						//Create the subtrees for this collection
						if (colBean.getSearchableFields().size() > 0) {
							TreeItem searchablesNode = new TreeItem("Searchable Fields");
							item.addItem(searchablesNode);
							for (String sBean : colBean.getSearchableFields()) {
								searchablesNode.addItem(sBean);
							}
						}
							
						if (colBean.getPresentableFields().size() > 0) {
							TreeItem presentablesNode = new TreeItem("Presentable Fields");
							item.addItem(presentablesNode);
							for (String pBean : colBean.getPresentableFields()) {
								presentablesNode.addItem(pBean);
							}
						}
							
					}
					mainPanel.add(collectionsTree);
					// Display the tree open. SetState after items are added
					treeRoot.setState(true);
				}
			}
		};SearchManager.smService.getCollectionAndFieldsInfo(retrieveFieldsCallback);
		SearchManager.showLoading();
	}
}
