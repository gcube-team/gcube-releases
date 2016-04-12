package org.gcube.portlets.user.searchportlet.client;

import java.util.HashSet;

import org.gcube.portlets.user.searchportlet.client.widgets.GenericGCubePopup;
import org.gcube.portlets.user.searchportlet.shared.AlertsErrorMessages;
import org.gcube.portlets.user.searchportlet.shared.SearchTypeBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchPanel extends Composite {

	private static final String SEARCH_TYPE_GROUP = "sTypeGroup";

	/* Panels */
	private VerticalPanel mainPanel = new VerticalPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private HorizontalPanel checkBoxPanel = new HorizontalPanel();
	private CollectionsPanel collectionsPanel = new CollectionsPanel();
	private static GenericGCubePopup popup;

	/* Widgets */
	private TextBox searchTextBox = new TextBox();
	private Button searchBtn = new Button("Search");
	
	private Image logo;

	private RadioButton searchAll = new RadioButton(SEARCH_TYPE_GROUP, "Any");
	//private RadioButton searchNative = new RadioButton(SEARCH_TYPE_GROUP, "Native");
	//private RadioButton searchExternal = new RadioButton(SEARCH_TYPE_GROUP, "Externals");
	private RadioButton searchSpecificCols = new RadioButton(SEARCH_TYPE_GROUP, "Filter Collections");

	public SearchPanel () {
		popup = new GenericGCubePopup(collectionsPanel, "Select Collections", 300, 350, false);

		addLogo();

		searchSpecificCols.addStyleName("hand");
		searchTextBox.setFocus(true);
		searchTextBox.setWidth("420px");
		setPreviousSearchTermToTextbox();
		mainPanel.setWidth("100%");
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setSpacing(10);
		checkBoxPanel.setSpacing(12);
		checkBoxPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		checkBoxPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		checkBoxPanel.add(searchAll);	
		//checkBoxPanel.add(searchNative);
		//checkBoxPanel.add(searchExternal);
		checkBoxPanel.add(searchSpecificCols);

		addKeyboardSearchSupport();
		getSelectedSearchTypeFromSession();
		
		mainPanel.add(searchTextBox);
		mainPanel.add(checkBoxPanel);
		mainPanel.add(searchBtn);

		scroller.setWidth("100%");
		scroller.add(mainPanel);

		initWidget(scroller);

		searchSpecificCols.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.show();
				popup.center();
			}
		});

		searchBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final SearchTypeBean type = getSearchType();
				if (type == SearchTypeBean.GCUBESIMPLE) {
					final HashSet<String> selectedCollections  = collectionsPanel.getSelectedCollections();
					if (selectedCollections == null || selectedCollections.isEmpty()) {
						Window.alert("You have not selected any collection. Please select collections or use the other search options");
						return;
					}
					submitSearch(type, selectedCollections);
				}
				else if (type == SearchTypeBean.EXTERNAL) {
					 AsyncCallback<Boolean> areExternalAvailableCallback = new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable arg0) {
							Window.alert("External collections are not available. Please refresh the page and try again");
							return;
						}

						@Override
						public void onSuccess(Boolean arg0) {
							if (arg0.booleanValue() == false) {
								Window.alert("External collections are not available. Please refresh the page and try again");
								return;
							}
							else
								submitSearch(type, null);
							
						}
					};SearchPortlet.searchService.areExternalCollectionsAvailable(areExternalAvailableCallback);
				}
				else if (type == SearchTypeBean.NATIVE) {
					 AsyncCallback<Boolean> areNativeAvailableCallback = new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable arg0) {
							Window.alert("Native collections are not available. Please refresh the page and try again");
							return;
						}

						@Override
						public void onSuccess(Boolean arg0) {
							if (arg0.booleanValue() == false) {
								Window.alert("Native collections are not available. Please refresh the page and try again");
								return;
							}
							else
								submitSearch(type, null);
							
						}
					};SearchPortlet.searchService.areNativeCollectionsAvailable(areNativeAvailableCallback);
				}
				// Generic search
				else
					if (collectionsPanel.getNumberOfAvailableCollections() > 0)
						submitSearch(type, null);
					else {
						Window.alert("There are no collections available. Please refresh the page and try again");
						return;
					}
				

			}
		});
	}

	private void submitSearch(SearchTypeBean searchType, HashSet<String> selectedCollections) {
		if (searchTextBox.getText().trim().equals("")) {
			Window.alert("There is no search term to search for. Please provide a term.");
		}
		else {

			AsyncCallback<Void> simpleCallback = new AsyncCallback<Void>()
					{
				public void onFailure(Throwable caught)
				{
					searchBtn.setEnabled(true);
					SearchPortlet.displayErrorWindow(AlertsErrorMessages.GenericQuerySubmissionFailure, caught);

				}

				public void onSuccess(Void result)
				{

					SearchPortlet.goToResults(true);
				}
					};
					SearchPortlet.searchService.submitGenericQuery(searchTextBox.getText().trim(), searchType, selectedCollections, simpleCallback);
					searchBtn.setEnabled(false);;
		}
	}

	private void addKeyboardSearchSupport() {
		searchTextBox.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchBtn.click();				
			}
		});

		searchTextBox.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchBtn.click();				
			}
		});

		searchTextBox.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchBtn.click();					
			}
		});
	}

	private SearchTypeBean getSearchType() {
//		if (searchNative.getValue())
//			return SearchTypeBean.NATIVE;
//		else if (searchExternal.getValue())
//			return SearchTypeBean.EXTERNAL;
		if (searchSpecificCols.getValue())
			return SearchTypeBean.GCUBESIMPLE;
		return SearchTypeBean.GENERIC;

	}

	private void getSelectedSearchTypeFromSession() {
		AsyncCallback<SearchTypeBean> getSearchTypeFromSessionCallback = new AsyncCallback<SearchTypeBean>() {

			@Override
			public void onFailure(Throwable caught) {
				searchAll.setValue(true);
			}

			@Override
			public void onSuccess(SearchTypeBean result) {
				if (result != null) {
//					if (result == SearchTypeBean.EXTERNAL)
//						searchExternal.setValue(true);
//					else if (result == SearchTypeBean.NATIVE)
//						searchNative.setValue(true);
					if (result == SearchTypeBean.GCUBESIMPLE)
						searchSpecificCols.setValue(true);
					else
						searchAll.setValue(true);
				}
				else
					searchAll.setValue(true);

			}
		};SearchPortlet.searchService.getSelectedSearchType(getSearchTypeFromSessionCallback);
	}
	
	
	private void setPreviousSearchTermToTextbox() {
		AsyncCallback<String> getSearchTermFromSessionCallback = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				searchAll.setValue(true);
			}

			@Override
			public void onSuccess(String result) {
				if (result != null) 
					searchTextBox.setText(result.trim());

			}
		};SearchPortlet.searchService.getSimpleSearchTerm(getSearchTermFromSessionCallback);
	}
	
	private void addLogo() {
		AsyncCallback<String> getLogoURLCallback = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(String result) {
				if (result != null) {
					logo = new Image(result);
					mainPanel.insert(logo, 0);
				}
			}
		};SearchPortlet.searchService.getLogoURL(getLogoURLCallback);
	}
}
