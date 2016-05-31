package org.gcube.portlets.user.searchportlet.client;

import java.util.ArrayList;

import org.gcube.portlets.user.searchportlet.client.widgets.SearchButtons;
import org.gcube.portlets.user.searchportlet.shared.AlertsErrorMessages;
import org.gcube.portlets.user.searchportlet.shared.SearchAvailabilityType;
import org.gcube.portlets.user.searchportlet.shared.SearchableFieldBean;
import org.jsonmaker.gwt.client.Jsonizer;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.PersonJsonizer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

/**
 * This panel provides the functionality to search across multiple collections without the need of metadata schema selection
 * 
 */
public class AdvancedSearchPanel extends Composite
{
	VerticalPanel verticalPanel = new VerticalPanel();

	protected SearchButtons searchButtons;

	private HTML title = new HTML("<br>" + SearchConstantsStrings.FIELD_TITLE, true);
	private HTML errorMsg = new HTML("<span style=\"color: darkred\">" +
	"Advanced search is currently unavailable.</span>");
	private HTML geoMsg = new HTML("<span style=\"color: darkred\">" +
	"Advanced search is not available. You can only submit geospatial queries</span>");


	// Selected means the user wants to search per collection, false that he wants to merge the collections
	protected CheckBox searchPerCollectionCheckBox = new CheckBox(SearchConstantsStrings.SEARCH_PER_COLLECTION);

	// It holds the current condition type (OR / AND)
	protected String cType = "OR";
	// Define the AND - OR conditions
	public RadioButton and = new RadioButton("conditionType", SearchConstantsStrings.AND);
	public RadioButton or = new RadioButton("conditionType", SearchConstantsStrings.OR);
	// This panel will hold the AND - OR conditions
	public FlowPanel fPanel = new FlowPanel();
	final HorizontalPanel langPanel = new HorizontalPanel();

	// This Panel wraps the SearchField Class allowing multiple search rows
	public VerticalPanel searchFieldVerticalPanel = new VerticalPanel();

	private ListBox availLangs = new ListBox();

	// Holds the available search Fields, that a user can search through
	public SearchableFieldBean[] searchFields = null; // for all classes
	
	private SearchAvailabilityType searchStatus = null;

	private CheckBox semanticSearchCb = new CheckBox("Semantic Enrichment");
	
	// For the notification mechanism
	final PageBusAdapter pageBusAdapter = new PageBusAdapter();

	public static AdvancedSearchPanel singleton = null;

	public static AdvancedSearchPanel get()
	{
		return singleton;
	}

	protected int searchFieldNo;
	protected int searchFieldActiveNo;

	public AdvancedSearchPanel(SearchAvailabilityType searchStatus)
	{
		//this.enableSemanticSearch = enableSemanticSearch;
		this.searchStatus = searchStatus;
		searchButtons = new SearchButtons();
		//if (searchStatus == SearchAvailabilityType.FTS_GEO_AVAILABLE || searchStatus == SearchAvailabilityType.FTS_NOGEO_AVAILABLE) {
		AsyncCallback<ArrayList<SearchableFieldBean>> searchFieldscallback = new AsyncCallback<ArrayList<SearchableFieldBean>>()
		{
			public void onFailure(Throwable caught)
			{
				SearchPortletG.displayErrorWindow("Failed to retrieve the available searchable fields. Please refresh the page and try again", caught);
			}

			public void onSuccess(ArrayList<SearchableFieldBean> result)
			{
				//TODO
				verticalPanel.clear();
				if (result == null || result.size() <= 0)
				{
					searchButtons.setVisible(false);
					verticalPanel.add(errorMsg);
				}
				else
				{

					searchFields = new SearchableFieldBean[result.size()];
					for(int i=0, k=0; i< result.size(); i++)
					{
						searchFields[k] = result.get(k);
						k++;
					}
					verticalPanel.add(title);


					AsyncCallback<ArrayList<String>> getAvailableLangscallback = new AsyncCallback<ArrayList<String>>() {

						public void onFailure(Throwable caught) {

						}

						public void onSuccess(ArrayList<String> result) {
							// languages are available
							if (result != null && result.size() > 0) {
								final ArrayList<String> availableLangs = result;
								AsyncCallback<String> getSelectedLangCallback = new AsyncCallback<String>() {

									public void onFailure(Throwable caught) {
									}

									public void onSuccess(String result) {
										String selectedLang = result;
										availLangs.clear();
										int i = 0;
										for (String lang : availableLangs) {
											availLangs.addItem(lang);
											if (lang.equals(selectedLang))
												availLangs.setSelectedIndex(i);
											i++;
										}
										
										langPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
										langPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
										langPanel.add(new HTML("Select Language: &nbsp;&nbsp;&nbsp;"));
										langPanel.add(availLangs);
										langPanel.setSpacing(12);
										//TODO new code here
										AsyncCallback<Boolean> isSemanticAvailableCallback = new AsyncCallback<Boolean>() {

											public void onFailure(Throwable caught) {

											}

											public void onSuccess(Boolean result) {
												if (result) {
													langPanel.add(semanticSearchCb);
												}
											}
										};SearchPortletG.searchService.isSemanticAvailableForCurrentScope(isSemanticAvailableCallback);
										
											
										verticalPanel.insert(langPanel, 0);
										verticalPanel.remove(errorMsg);
									}
								};
								SearchPortletG.searchService.getSelectedLanguage(getSelectedLangCallback);
							}
						}
					};
					SearchPortletG.searchService.getAvailableLanguages(getAvailableLangscallback);
				}
				init();
			}
		};
		SearchPortletG.searchService.getSearchFields(searchFieldscallback);
		initWidget(verticalPanel);
		if (singleton == null)
			singleton = this;
	}

	public void init()
	{
		// Select the OR condition as default the first time only.
		AsyncCallback<String> cl = new AsyncCallback<String>()
		{
			public void onFailure(Throwable caught)
			{
				and.setValue(false);
				or.setValue(true);
			}

			public void onSuccess(String result)
			{
				if(result == null)
				{
					and.setValue(false);
					or.setValue(true);
				}
				else
				{
					String type = result;
					if (type.equals(SearchConstantsStrings.CONDITIONTYPE_AND)) {
						and.setValue(true);
						or.setValue(false);
						cType = "AND";
					}
					else {
						and.setValue(false);
						or.setValue(true);
						cType = "OR";
					}
				}
			}
		};
		SearchPortletG.searchService.getSelectedConditionType(cl);


		fPanel.add(and);
		fPanel.add(or);

		if (searchFieldNo <= 1 || searchFieldActiveNo <= 1)
			fPanel.setVisible(false);
		else
			fPanel.setVisible(true);

		verticalPanel.add(fPanel);
		verticalPanel.add(searchFieldVerticalPanel);
		searchFieldVerticalPanel.setSpacing(SearchConstants.SPACING);
		verticalPanel.add(new HTML("<br>", true));
		
		//TODO reenabled when ASL is tested
		verticalPanel.add(searchPerCollectionCheckBox);
		
		verticalPanel.add(new HTML("<br>", true));
		// Add the search buttons
		verticalPanel.add(searchButtons);

		/* No searchable fields to search for. Show the error message */
		if (searchFields == null &&  (searchStatus == SearchAvailabilityType.FTS_NOGEO_AVAILABLE || searchStatus == null || searchStatus == SearchAvailabilityType.NO_COLLECTION_SELECTED || searchStatus == SearchAvailabilityType.NOFTS_NOGEO_AVAILABLE))
		{
			searchButtons.setVisible(false);
			searchFieldVerticalPanel.setVisible(false);
			searchPerCollectionCheckBox.setVisible(false);
			fPanel.setVisible(false);
			verticalPanel.insert(errorMsg, 0);
		}
		else if (searchFields == null && (searchStatus == SearchAvailabilityType.FTS_GEO_AVAILABLE || searchStatus == SearchAvailabilityType.GEO_NOFTS_AVAILABLE)) {
			searchButtons.setVisible(true);
			searchFieldVerticalPanel.setVisible(false);
			searchPerCollectionCheckBox.setVisible(false);
			fPanel.setVisible(false);
			verticalPanel.remove(errorMsg);
			verticalPanel.insert(geoMsg, 0);
		}
		else
		{
			// Counts the active search fields
			searchFieldNo = 0;
			searchFieldActiveNo = 0;

			// Asynchronous call in order to retrieve the selected fields.
			// How many criteria we have and their values
			AsyncCallback<ArrayList<SearchableFieldBean>> callback = new AsyncCallback<ArrayList<SearchableFieldBean>>()
			{
				public void onFailure(Throwable caught)
				{
					SearchPortletG.displayErrorWindow("Failed to get the selected fields for the current search. Please reselect your fields", caught);
				}

				public void onSuccess(ArrayList<SearchableFieldBean> result)
				{
					ArrayList<SearchableFieldBean> selectedFields = result;
					if (selectedFields != null)
					{
						// n is the number of the criteria retrieved from server
						int n = selectedFields.size();
						for (int i=0; i<n; i++)
						{
							// k is the number of the searchable fields
							int k = searchFields.length;
							int j;
							for (j=0; j<k; j++)
							{
								// If the name of the search field is the same
								if (searchFields[j].equals(selectedFields.get(i)))
								{
									SearchField searchFieldOne = new SearchField(searchFieldNo);
									searchFieldOne.fieldListBox.setItemSelected(j, true);
									searchFieldOne.textBox.setText(selectedFields.get(i).getValue());
									searchFieldVerticalPanel.add(searchFieldOne);
									break;
								}
							}
							if(j==k)
								searchFieldNo++;	
						}
						if(searchFieldActiveNo == 0)
							addSearchField();

						if (n>1)
							fPanel.setVisible(true);
						else 
							fPanel.setVisible(false);

						// For all Criteria except the last one hide the add condition button
						for (int l=0; l<searchFieldVerticalPanel.getWidgetCount()-1; l++) {
							((SearchField)searchFieldVerticalPanel.getWidget(l)).addConditionButton.setEnabled(false);
							((SearchField)searchFieldVerticalPanel.getWidget(l)).addConditionButton.setVisible(false);
						}
					}
					else
					{
						addSearchField();
					}
				}
			};
			SearchPortletG.searchService.getSelectedFields(callback);


			and.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event) {
					if (and.getValue())
					{
						cType = "AND";
						setConditionType(SearchConstantsStrings.CONDITIONTYPE_AND);
					}

				}
			});

			or.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
				{
					if (or.getValue())
					{
						cType = "OR";
						setConditionType(SearchConstantsStrings.CONDITIONTYPE_OR);
					}
				}
			});
		}

		searchButtons.setResetClickListener(new ClickHandler()
		{
			public void onClick(ClickEvent event) {

				if (searchFields == null && (searchStatus == SearchAvailabilityType.FTS_GEO_AVAILABLE || searchStatus == SearchAvailabilityType.GEO_NOFTS_AVAILABLE)) {

				}
				else {
					AsyncCallback<Void> callback = new AsyncCallback<Void>()
					{
						public void onFailure(Throwable caught)
						{
						}

						public void onSuccess(Void result)
						{
							and.setValue(false);
							or.setValue(true);
							semanticSearchCb.setValue(false);
							verticalPanel.remove(searchFieldVerticalPanel);
							searchFieldVerticalPanel = new VerticalPanel();
							// Place the SearchFieldVertical panel before the condition button and the sort panel, including all breaks.
							// After the select language panel IT WAS 4 and BECAME 3
							verticalPanel.insert(searchFieldVerticalPanel, verticalPanel.getWidgetCount()-3);
							searchFieldVerticalPanel.setSpacing(SearchConstants.SPACING);
							searchFieldNo = 0;
							searchFieldActiveNo = 0;
							addSearchField();
							fPanel.setVisible(false);
						}
					};
					SearchPortletG.searchService.resetFields(false, callback);

				}
			}

		});

		availLangs.addChangeHandler( new ChangeHandler()
		{
			public void onChange(ChangeEvent event) {
				if(event.getSource() instanceof ListBox)
				{
					String selectedLang = ((ListBox)event.getSource()).getItemText(((ListBox)event.getSource()).getSelectedIndex());
					AsyncCallback<Boolean> callback2 = new AsyncCallback<Boolean>()
					{
						public void onFailure(Throwable caught)
						{
						}

						public void onSuccess(Boolean result)
						{
							Boolean ret = result;

							//Notify the search portlet that the selected collections have been changed
							Person searchNotification = new Person();
							searchNotification.setName(SearchConstantsStrings.COLLECTIONS_CHANGED);


							// publish a message with Person bean data
							try {
								pageBusAdapter.PageBusPublish("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", searchNotification, (Jsonizer)GWT.create(PersonJsonizer.class));
							} catch (PageBusAdapterException e) {
								e.printStackTrace();
							}

							Person geoNotification = new Person();
							if (ret.booleanValue()==true) {
								geoNotification.setName("Enable Geospatial");
							}
							else {
								geoNotification.setName("Disable Geospatial");
							}

							// publish a message with Person bean data
							try {
								pageBusAdapter.PageBusPublish("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", geoNotification, (Jsonizer)GWT.create(PersonJsonizer.class));
							} catch (PageBusAdapterException e) {
								e.printStackTrace();
							}
						}
					};
					SearchPortletG.searchService.setSelectedLanguage(selectedLang, callback2);

				}

			}

		});


		searchButtons.setSubmitClickListener(new ClickHandler()
		{
			public void onClick(ClickEvent event) {
				int n = searchFieldActiveNo;
				boolean isSearchEnabled = true;
				for (int i = 0; i < searchFieldNo; i++)
				{
					SearchField temp = (SearchField) getSearchFieldPanel().getWidget(i);
					String textValue = temp.getTextBox().getText();

					if (textValue.trim().equals("")) {
						Window.alert("Some of the given criteria do not contain any search term. Please provide a term or remove the empty criteria.");
						isSearchEnabled = false;
						break;
					}
				}
				
				if (isSearchEnabled) {
					SearchField theFirst = null;
					if (getSearchFieldPanel().getWidgetCount() > 0)
						theFirst = (SearchField) getSearchFieldPanel().getWidget(0);
					if (searchFields == null && (searchStatus == SearchAvailabilityType.FTS_GEO_AVAILABLE || searchStatus == SearchAvailabilityType.GEO_NOFTS_AVAILABLE)) {
						AsyncCallback<Void> callback = new AsyncCallback<Void>()
						{
							public void onFailure(Throwable caught)
							{
								SearchPortletG.hideLoading();
								searchButtons.enableSubmitButton(true);
								searchButtons.enableResetButton(true);
								SearchPortletG.displayErrorWindow(AlertsErrorMessages.GeospatialQuerySubmissionFailure, caught);
							}

							public void onSuccess(Void result)
							{
								SearchPortletG.goToResults(true);
							}
						};

						SearchPortletG.showLoading();
						SearchPortletG.searchService.submitAdvancedQuery(null, null, null, false, semanticSearchCb.getValue(), callback);
						searchButtons.enableSubmitButton(false);
						searchButtons.enableResetButton(false);
					}



					else if (n == 1 && theFirst != null && theFirst.getTextBox().getText().compareTo("") == 0)
					{

						AsyncCallback<Void> callback = new AsyncCallback<Void>()
						{
							public void onFailure(Throwable caught)
							{
								SearchPortletG.hideLoading();
								searchButtons.enableSubmitButton(true);
								searchButtons.enableResetButton(true);
								SearchPortletG.displayErrorWindow(AlertsErrorMessages.GeospatialQuerySubmissionFailure, caught);
							}

							public void onSuccess(Void result)
							{
								SearchPortletG.goToResults(true);
							}
						};

						SearchPortletG.showLoading();
						SearchPortletG.searchService.submitAdvancedQuery(null, null, null, searchPerCollectionCheckBox.getValue(), semanticSearchCb.getValue(), callback);
						searchButtons.enableSubmitButton(false);
						searchButtons.enableResetButton(false);
					}
					else
					{
						// loads the SearchField widgets
						SearchField[] vSF = new SearchField[n];
						for (int i = 0; i < n; i++)
							vSF[i] = (SearchField) getSearchFieldPanel().getWidget(i);

						// get the new fieldNo (after the blank deletion);
						searchFieldNo = getSearchFieldNo();

						ArrayList<SearchableFieldBean> selFields = new ArrayList<SearchableFieldBean>();
						// Get the values and builds the query
						for (int i = 0; i < searchFieldNo; i++)
						{
							SearchField temp = (SearchField) getSearchFieldPanel().getWidget(i);
							String listValue = temp.getListItem().getItemText(temp.getListItem().getSelectedIndex());
							String id = temp.getListItem().getValue(temp.getListItem().getSelectedIndex());
							String textValue = temp.getTextBox().getText();

							selFields.add(new SearchableFieldBean(id, listValue, textValue));
						}
						AsyncCallback<Void> callback = new AsyncCallback<Void>()
						{
							public void onFailure(Throwable caught)
							{
								SearchPortletG.hideLoading();
								searchButtons.enableSubmitButton(true);
								searchButtons.enableResetButton(true);
								SearchPortletG.displayErrorWindow(AlertsErrorMessages.AbstractQuerySubmissionFailure, caught);
							}

							public void onSuccess(Void result)
							{
								if ((semanticSearchCb.getParent().equals(langPanel)) && semanticSearchCb.getValue())
									SearchPortletG.goToResults(false);
								else
									SearchPortletG.goToResults(true);
							}
						};

						SearchPortletG.showLoading();
						SearchPortletG.searchService.submitAdvancedQuery(selFields, null, null, searchPerCollectionCheckBox.getValue(), semanticSearchCb.getValue(), callback);
						searchButtons.enableSubmitButton(false);
						searchButtons.enableResetButton(false);
					}

				}
			}
		});
	}

	/**
	 * Adds a new searchField to the SearchField panel and update the information to the server.
	 * The first time we create a new SearchField with index 0.
	 * 
	 */
	public void addSearchField()
	{
		SearchField searchFieldOne = new SearchField(searchFieldNo);

		searchFieldVerticalPanel.add(searchFieldOne);

		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			public void onFailure(Throwable caught)
			{
				SearchPortletG.displayErrorWindow("Failed to add new field. Please reset fields and try again", caught);
			}

			public void onSuccess(Void result)
			{
			}
		};
		SearchPortletG.searchService.addSearchField(searchFields[0], callback);
	}

	/**
	 * Removes the search field with 'id'.
	 * 
	 * @param id The index of the search field to delete
	 */
	public void removeSearchField(int id)
	{

		for (int i=id+1; i<searchFieldNo; i++) {
			((SearchField) searchFieldVerticalPanel.getWidget(i)).setId(i-1); // i-1  => id
		}
		searchFieldVerticalPanel.remove(id);
		this.searchFieldNo--;
		this.searchFieldActiveNo--;
		// If only 1 search field remove the OR/AND choice
		if (searchFieldNo == 1 || searchFieldActiveNo == 1)
			fPanel.setVisible(false);

		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			public void onFailure(Throwable caught)
			{

			}

			public void onSuccess(Void result)
			{
			}
		};
		SearchPortletG.searchService.removeSearchField(id, callback);

	}


	private void setConditionType(String type) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			public void onFailure(Throwable caught)
			{
			}

			public void onSuccess(Void result)
			{
			}
		};
		SearchPortletG.searchService.storeConditionType(type, callback);
	}

	// Needs to be used by WhatPanel class
	protected VerticalPanel getSearchFieldPanel()
	{
		return this.searchFieldVerticalPanel;
	}

	public void adjustSize(int width, int height)
	{
		verticalPanel.setPixelSize(width, height);
	}



	/**
	 * This class wraps the three fields for a row in the Abstract Search ListBox
	 * for the Type to search TextBox for what to search ListBox 
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	protected class SearchField extends Composite
	{

		private HorizontalPanel horizontalPanel = new HorizontalPanel();

		/**
		 * The list box with the available search fields
		 */
		private ListBox fieldListBox = new ListBox();

		/**
		 * What to search for
		 */
		private TextBox textBox = new TextBox();

		private Button deleteConditionButton = new Button();

		private Button addConditionButton = new Button();

		private int id;

		// access methods
		public ListBox getListItem()
		{
			return this.fieldListBox;
		}

		public TextBox getTextBox()
		{
			return this.textBox;
		}

		/**
		 * The constructor of the class
		 * 
		 * @param id : the number of the search field (starting from zero)
		 */
		public SearchField(int id)
		{
			this.id = id;
			textBox.setWidth("125");
			textBox.setText("");
			textBox.addKeyPressHandler(new KeyPressHandler()
			{
				public void onKeyPress(KeyPressEvent event) {
					if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode())
						searchButtons.getSearchButton().click();
				}
			});
			horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			horizontalPanel.add(fieldListBox);
			horizontalPanel.add(textBox);
			horizontalPanel.add(deleteConditionButton);
			horizontalPanel.add(addConditionButton);
			addConditionButton.setStyleName("addConditionButton");
			deleteConditionButton.setStyleName("deleteButton");

			deleteConditionButton.addClickHandler(new ClickHandler()
			{

				public void onClick(ClickEvent event) {
					if (searchFieldNo == 1)
						Window.alert(AlertsErrorMessages.OneConditionNeeded);
					else {
						int idToRemove = ((SearchField)((Button)event.getSource()).getParent().getParent()).getId();

						// If the user selects to remove the last one. Then the add condition button should be enabled at the previous search field
						if ((((SearchField)((Button)event.getSource()).getParent().getParent()).getId()) == (searchFieldVerticalPanel.getWidgetCount()-1)) {
							SearchField previous = (SearchField) searchFieldVerticalPanel.getWidget(idToRemove-1);
							previous.addConditionButton.setEnabled(true);
							previous.addConditionButton.setVisible(true);
						}
						removeSearchField(idToRemove);
					}
				}
			}
			);

			addConditionButton.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event) {
					SearchField searchFieldToAdd = new SearchField(searchFieldNo);
					searchFieldToAdd.fieldListBox.setSelectedIndex(0);
					// shows the AND - OR Condition
					if (!fPanel.isVisible())
						fPanel.setVisible(true);

					searchFieldVerticalPanel.add(searchFieldToAdd);

					//hide the previous condition button
					((SearchField)searchFieldVerticalPanel.getWidget(searchFieldNo-2)).addConditionButton.setEnabled(false);
					((SearchField)searchFieldVerticalPanel.getWidget(searchFieldNo-2)).addConditionButton.setVisible(false);
					AsyncCallback<Void> callback = new AsyncCallback<Void>()
					{
						public void onFailure(Throwable caught)
						{
						}

						public void onSuccess(Void result)
						{
						}
					};
					SearchPortletG.searchService.addSearchField(searchFields[0], callback);

				}
			}
			);


			horizontalPanel.setSpacing(SearchConstants.SPACING);

			initWidget(horizontalPanel);

			/* The constructor is using the index of the field and then the searchFieldNo is increased
			 * and counts the real number of criteria that are active*/
			searchFieldNo++;
			searchFieldActiveNo++;

			if (searchFields != null && searchFields.length > 0)
			{
				searchButtons.enableSubmitButton(true);
				searchButtons.enableResetButton(true);
				// adds the new search fields in the field list box
				for (int i = 0; i < searchFields.length; i++)
					fieldListBox.addItem(searchFields[i].getName(), searchFields[i].getId());
			}

			//track the changes on selected search field
			// change the selected type of field (Criterion name)
			fieldListBox.addChangeHandler(new ChangeHandler()
			{
				public void onChange(ChangeEvent event) {
					AsyncCallback<Void> callback = new AsyncCallback<Void>()
					{
						public void onFailure(Throwable caught)
						{
							SearchPortletG.displayErrorWindow("Failed to update the criterion. Please remove this criterion and add it again", caught);
						}

						public void onSuccess(Void result)
						{
						}
					};
					SearchPortletG.searchService.updateCriterionName(((SearchField)((ListBox)event.getSource()).getParent().getParent()).getId(),((ListBox)event.getSource()).getValue(((ListBox)event.getSource()).getSelectedIndex()), ((ListBox)event.getSource()).getItemText(((ListBox)event.getSource()).getSelectedIndex()), callback);

				}
			}
			);

			//track the changes on value field (Criterion value)
			textBox.addChangeHandler(new ChangeHandler()
			{
				public void onChange(ChangeEvent event) {
					AsyncCallback<Void> callback = new AsyncCallback<Void>()
					{
						public void onFailure(Throwable caught)
						{
							SearchPortletG.displayErrorWindow("Failed to update the criterion. Please remove this criterion and add it again", caught);
						}

						public void onSuccess(Void result)
						{
						}
					};
					SearchPortletG.searchService.updateCriterionValue(((SearchField)((TextBox)event.getSource()).getParent().getParent()).getId(), ((TextBox)event.getSource()).getText() , callback);
				}
			}
			);
		}

		public int getId()
		{
			return id;
		}

		public void setId(int id)
		{
			this.id = id;
		}
	}

	public int getSearchFieldNo()
	{
		return searchFieldNo;
	}

	public void setSearchFieldNo(int searchFieldNo)
	{
		this.searchFieldNo = searchFieldNo;
	}
}
