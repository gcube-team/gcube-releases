package org.gcube.portlets.user.searchportlet.client;

import org.gcube.portlets.user.searchportlet.client.widgets.SearchButtons;
import org.gcube.portlets.user.searchportlet.shared.AlertsErrorMessages;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * This Panel represents a search panel for the gCube search UI
 * The main panel supports free text search on the selected collections and it also provides an advanced interface.
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchPanel extends Composite {

	private ScrollPanel scroller = new ScrollPanel();
	private VerticalPanel mainSimplePanel = new VerticalPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel mainAdvancedPanel = new VerticalPanel();
	private HorizontalPanel hPanel = new HorizontalPanel();
	private DisclosurePanel advancedOptionsPanel = new DisclosurePanel("Advanced Search Options");
	private HTML errorMsg = new HTML("<span style=\"color: darkred\">Simple search is currently unavailable.</span>");
	public HTML infoMsg = new HTML("<span style=\"color: darkred\">" +
			"There is no collection selected. Please select collections to enable the search functionality</span>");
	public HTML noFTSMsg = new HTML("<span style=\"color: darkred\">" +
			"Simple search is currently unavailable for the selected collections.</span>");

	private SearchButtons searchButtons;
	private TextBox searchBox = new TextBox();
	private CheckBox semanticSearchCb = new CheckBox("Semantic Enrichment");
	// Selected means the user wants to search per collection, false that he wants to merge the collections
	protected CheckBox searchPerCollectionCheckBox = new CheckBox(SearchConstantsStrings.SEARCH_PER_COLLECTION);
	private CheckBox rankingCb = new CheckBox("Rank Results");
	private AdvancedSearchPanel advancedSearch;

	private boolean isAdvancedOpen = false;

	public SearchPanel() {
		scroller.setWidth("100%");
		scroller.add(mainPanel);
		mainSimplePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		mainSimplePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainSimplePanel.setSpacing(SearchConstants.SPACING);
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hPanel.setSpacing(15);
		advancedOptionsPanel.setAnimationEnabled(true);

		/* create the search buttons */
		searchButtons = new SearchButtons();

		/* set the properties for the search text box */
		searchBox.setFocus(true);
		searchBox.setName("fts");
		searchBox.setWidth("300px");
		
		AsyncCallback<Boolean> isSemanticSelectedCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable arg0) {
				semanticSearchCb.setValue(false);
				
			}

			@Override
			public void onSuccess(Boolean arg0) {
				if (arg0 != null && arg0)
					semanticSearchCb.setValue(arg0);
				else
					semanticSearchCb.setValue(false);
					
				
			}
		};SearchPortletG.searchService.isSemanticSelected(isSemanticSelectedCallback);
		
		AsyncCallback<Boolean> isRankSelectedCallback = new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable arg0) {
				rankingCb.setValue(false);
				
			}

			@Override
			public void onSuccess(Boolean arg0) {
				if (arg0 != null && arg0)
					rankingCb.setValue(arg0);
				else
					rankingCb.setValue(false);
					
				
			}
		};SearchPortletG.searchService.isRankSelected(isRankSelectedCallback);

		searchBox.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchButtons.getSearchButton().click();				
			}
		});

		searchBox.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchButtons.getSearchButton().click();				
			}
		});

		searchBox.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
					searchButtons.getSearchButton().click();				
			}
		});

		searchBox.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				setSearchTerm(searchBox.getText().trim());
			}
		});

		AsyncCallback<SearchAvailabilityType> callback = new AsyncCallback<SearchAvailabilityType>()
				{
			public void onFailure(Throwable caught)
			{
				advancedSearch = new AdvancedSearchPanel(null);
				searchButtons.setVisible(false);
				hPanel.clear();
				hPanel.add(errorMsg);
				
				getAdvancedPanelStatus();

				mainSimplePanel.add(hPanel);
				mainSimplePanel.add(searchButtons);

				mainAdvancedPanel.add(advancedSearch);
				// this is not visible when it is initialized
				mainAdvancedPanel.setVisible(false);

				advancedOptionsPanel.add(mainAdvancedPanel);
				mainPanel.add(mainSimplePanel);
				// Add the disclosure panel at the end of the simple search widgets
				mainPanel.add(advancedOptionsPanel);
			}

			public void onSuccess(SearchAvailabilityType result)
			{
				advancedSearch = new AdvancedSearchPanel(result);
				hPanel.clear();
				if(result == SearchAvailabilityType.FTS_GEO_AVAILABLE || result == SearchAvailabilityType.FTS_NOGEO_AVAILABLE)
				{
					searchButtons.enableSubmitButton(true);
					searchButtons.setVisible(true);
					hPanel.add(searchBox);
					hPanel.add(rankingCb);
					//TODO new code here
					AsyncCallback<Boolean> isSemanticAvailableCallback = new AsyncCallback<Boolean>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(Boolean result) {
							if (result) {
								hPanel.add(semanticSearchCb);
							}
						}
					};SearchPortletG.searchService.isSemanticAvailableForCurrentScope(isSemanticAvailableCallback);
					advancedOptionsPanel.setVisible(true);
				}
				else if(result == SearchAvailabilityType.NO_COLLECTION_SELECTED)  {
					searchButtons.setVisible(false);
					hPanel.add(infoMsg);						
				}

				else if (result == SearchAvailabilityType.NOFTS_NOGEO_AVAILABLE || result == SearchAvailabilityType.GEO_NOFTS_AVAILABLE)
				{
					searchButtons.setVisible(false);
					hPanel.add(noFTSMsg);
					advancedOptionsPanel.setVisible(true);
				}
				else {
					searchButtons.setVisible(false);
					hPanel.add(errorMsg);
					advancedOptionsPanel.setVisible(true);
				}

				getSearchTerm();

				getAdvancedPanelStatus();

				mainSimplePanel.add(hPanel);
				mainSimplePanel.add(new HTML("<br>", true));
				mainSimplePanel.add(searchPerCollectionCheckBox);
				mainSimplePanel.add(searchButtons);

				mainAdvancedPanel.add(advancedSearch);
				// this is not visible when it is initialized
				mainAdvancedPanel.setVisible(false);

				advancedOptionsPanel.add(mainAdvancedPanel);
				mainPanel.add(mainSimplePanel);
				// Add the disclosure panel at the end of the simple search widgets
				mainPanel.add(advancedOptionsPanel);
			}
				};
				SearchPortletG.searchService.getSearchStatus(callback);



				initWidget(scroller);

				/* Construct the click listeners for the two buttons */
				searchButtons.setSubmitClickListener(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						
						if (searchBox.getText().trim().equals("")) {
							Window.alert("There is no search term to search for. Please provide a term.");
						}
						else {

							AsyncCallback<Void> simpleCallback = new AsyncCallback<Void>()
									{
								public void onFailure(Throwable caught)
								{
									SearchPortletG.hideLoading();
									searchButtons.enableSubmitButton(true);
									searchButtons.enableResetButton(true);
									SearchPortletG.displayErrorWindow(AlertsErrorMessages.SimpleQuerySubmissionFailure, caught);

								}

								public void onSuccess(Void result)
								{

									if (semanticSearchCb.getParent().equals(hPanel) && semanticSearchCb.getValue())
										SearchPortletG.goToResults(false);
									else
										SearchPortletG.goToResults(true);
								}
									};
									SearchPortletG.showLoading();
									if (semanticSearchCb.getParent().equals(hPanel))
										SearchPortletG.searchService.submitSimpleQuery(searchBox.getText(), rankingCb.getValue(), semanticSearchCb.getValue(), searchPerCollectionCheckBox.getValue(), simpleCallback);
									else
										SearchPortletG.searchService.submitSimpleQuery(searchBox.getText(), rankingCb.getValue(), false, searchPerCollectionCheckBox.getValue(), simpleCallback);
									searchButtons.enableSubmitButton(false);
									searchButtons.enableResetButton(false);
						}
					}
				});

				searchButtons.setResetClickListener(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						searchBox.setText("");
						semanticSearchCb.setValue(false);
						searchButtons.enableSubmitButton(true);
						searchButtons.enableResetButton(true);
					}	
				});	


				advancedOptionsPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {

					public void onOpen(OpenEvent<DisclosurePanel> event) {
						// hide the simple search menu
						mainSimplePanel.setVisible(false);

						// show the advanced menu here	
						mainAdvancedPanel.setVisible(true);
						isAdvancedOpen = true;
						setAdvancedPanelStatus(isAdvancedOpen);
					}
				});

				advancedOptionsPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {

					public void onClose(CloseEvent<DisclosurePanel> event) {
						// hide the advanced menu
						mainAdvancedPanel.setVisible(false);

						// show the simple menu
						mainSimplePanel.setVisible(true);
						isAdvancedOpen = false;
						setAdvancedPanelStatus(isAdvancedOpen);
					}
				});

	}

	private void setAdvancedPanelStatus(boolean status) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Void result) {

			}
		};SearchPortletG.searchService.setAdvancedPanelStatus(status, callback);
	}


	private void getAdvancedPanelStatus() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Boolean result) {
				isAdvancedOpen = result.booleanValue();
				advancedOptionsPanel.setOpen(isAdvancedOpen);
			}
		};SearchPortletG.searchService.isAdvancedOpen(callback);
	}

	private void getSearchTerm() {
		AsyncCallback<String> stCallback = new  AsyncCallback<String>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(String result) {				
				searchBox.setText(result);
			}
		};SearchPortletG.searchService.getSimpleSearchTerm(stCallback);
	}

	private void setSearchTerm(String term) {
		AsyncCallback<Void> c = new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Void result) {

			}
		};SearchPortletG.searchService.setSimpleSearchTerm(term, c);
	}
}
