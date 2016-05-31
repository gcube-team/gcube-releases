package org.gcube.portlets.user.searchportlet.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class MainPanel extends Composite {

	private BrowsePanel b = new BrowsePanel();
	private SearchPanel s = new SearchPanel();
	private RefineSearchPanel r = new RefineSearchPanel();

	private DecoratedTabPanel mainPanel = new DecoratedTabPanel();
	private ScrollPanel scroller = new ScrollPanel();

	public MainPanel () {

		mainPanel.add(s, "Search");
		mainPanel.add(b, "Browse");
		mainPanel.add(r, "Refine Search");
		mainPanel.selectTab(0);
		mainPanel.setAnimationEnabled(true);
		mainPanel.setSize("100%", "570px");

		AsyncCallback<Integer> getSelectedTabCallback = new AsyncCallback<Integer>() {

			public void onFailure(Throwable caught) {
				mainPanel.selectTab(0);
			}

			public void onSuccess(Integer result) {
				if (result != null)
					mainPanel.selectTab(result.intValue());			
			}
		};SearchPortletG.searchService.getSelectedTab(getSelectedTabCallback);

		mainPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			public void onSelection(SelectionEvent<Integer> event) {
				AsyncCallback<Void> setSelectedTabCallback = new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {

					}

					public void onSuccess(Void result) {

					}
				};SearchPortletG.searchService.setSelectedTab(event.getSelectedItem(), setSelectedTabCallback);
			}
		});

		scroller.setWidth("100%");
		scroller.add(mainPanel);
		
		initWidget(scroller);
	}
}
