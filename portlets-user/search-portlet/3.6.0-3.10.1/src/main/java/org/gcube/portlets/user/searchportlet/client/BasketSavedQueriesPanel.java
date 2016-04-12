package org.gcube.portlets.user.searchportlet.client;

import org.gcube.portlets.user.searchportlet.client.widgets.SearchButtons;
import org.gcube.portlets.user.searchportlet.shared.SavedBasketQueriesInfo;
import org.gcube.portlets.widgets.lighttree.client.Item;
import org.gcube.portlets.widgets.lighttree.client.ItemType;
import org.gcube.portlets.widgets.lighttree.client.WorkspaceLightTreePanel;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionEvent;
import org.gcube.portlets.widgets.lighttree.client.event.ItemSelectionHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BasketSavedQueriesPanel extends Composite{

	/* UI Elements - Panels */
	private VerticalPanel hostPanel = new VerticalPanel();
	protected WorkspaceLightTreePanel loadPanel;
//	private GCubeDialog infoMsg = new GCubeDialog(true);
	/* UI Elements - Buttons */
	private SearchButtons searchButtons = new SearchButtons("Submit", "Cancel");


	/* Holds the information of the current selected query from the basket */
	private String currentSelectedQueryID;
	private String currentQueryDescription;
	private String currentQueryType;

	public BasketSavedQueriesPanel() {

		loadPanel = new WorkspaceLightTreePanel();
		loadPanel.setShowEmptyFolders(false);
		loadPanel.setShowableTypes(ItemType.QUERY);
		loadPanel.setSelectableTypes(ItemType.QUERY);
		loadPanel.setHeight("200px");
		loadPanel.setWidth("100%");	
		loadPanel.loadTree();
		hostPanel.setSpacing(4);
		hostPanel.add(loadPanel);
		hostPanel.add(searchButtons);
		searchButtons.enableSubmitButton(false);
		searchButtons.enableResetButton(false);

		ItemSelectionHandler selectionHandler = new ItemSelectionHandler() {

			public void onSelection(ItemSelectionEvent event) {
				if (event.isSelectable()) {
					Item item = event.getSelectedItem();
					// If the user selects a FOLDER or the ROOT display an informational message
					if ((item.getType() == ItemType.FOLDER) || item.getType() == ItemType.ROOT) {
					//	infoMsg.setSize("250px", "150px");
					//	infoMsg.add(new HTML("A query should be selected."));
					//	infoMsg.show();
					}
					else {
						currentSelectedQueryID = item.getId();

						AsyncCallback<SavedBasketQueriesInfo> callback = new AsyncCallback<SavedBasketQueriesInfo>()
						{
							public void onFailure(Throwable caught)
							{
								SearchPortletG.displayErrorWindow("Failed to retrieve the saved queries from the basket. Please try again", caught);
							}

							public void onSuccess(SavedBasketQueriesInfo result)
							{
								if (result != null) {
									if (result.getQueryDescription() != null)
										currentQueryDescription = result.getQueryDescription();
									if (result.getQueryType() != null)
										currentQueryType = result.getQueryType();
									searchButtons.enableSubmitButton(true);
									searchButtons.enableResetButton(true);
								}
								else
									Window.alert("The selected query was not properly saved. Please select a different query.");
							}
						};SearchPortletG.searchService.getQueryFromBasket(currentSelectedQueryID, callback);
					}
				}

			}

		};

		loadPanel.addSelectionHandler(selectionHandler);


		searchButtons.setSubmitClickListener(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					public void onFailure(Throwable caught)
					{
						SearchPortletG.displayErrorWindow("Failed to submit the query. Please try again", caught);
						SearchPortletG.hideLoading();
					}

					public void onSuccess(Void result)
					{
						SearchPortletG.goToResults(true);

					}
				};
				SearchPortletG.showLoading();
				SearchPortletG.searchService.submitGenericQuery(currentQueryDescription, currentQueryType, callback);
				searchButtons.enableSubmitButton(false);
				searchButtons.enableResetButton(false);
			}

		});

		searchButtons.setResetClickListener(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				loadPanel.refreshTree();
				searchButtons.enableResetButton(false);
				searchButtons.enableSubmitButton(false);
			}

		});


		initWidget(hostPanel);
	}
}
