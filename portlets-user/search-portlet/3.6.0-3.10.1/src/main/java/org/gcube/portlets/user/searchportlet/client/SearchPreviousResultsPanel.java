package org.gcube.portlets.user.searchportlet.client;

import java.util.ArrayList;
import java.util.LinkedList;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.searchportlet.client.widgets.SearchButtons;
import org.gcube.portlets.user.searchportlet.shared.AlertsErrorMessages;
import org.gcube.portlets.user.searchportlet.shared.PreviousResultsInfo;
import org.gcube.portlets.user.searchportlet.shared.SearchableFieldBean;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchPreviousResultsPanel extends Composite
{
	//This array holds the previous queries. Each item is an object of type 'PreviousResultsInfo'
	private LinkedList<PreviousResultsInfo> prResults = null;
	
	// The index of the previous query that is currently selected
	private int currentIndex;
	
	// The description of the query that is currently selected
	private String currentQuery = null;
	
	// List box with the previous queries
	public ListBox oldResults = new ListBox();
	private HTML chooseRStitle = new HTML(SearchConstantsStrings.RS_FIELD_TITLE, true);
	
	//public HTML seeFullQuery = new HTML("&nbsp;&nbsp;<a href=\"Javascript:\" style=\"font-style: italic; font-size: smaller\">See query", true);
	public HTML seeFullQuery = new HTML("&nbsp;&nbsp;<a style=\"font-style: italic; font-size: smaller\">See query", true);
	
	// This is for displaying a pop-up with the query of the current selected RSepr
	private static DescPopUp queryDescPopup = null;
	private HorizontalPanel browseHorPanel = new HorizontalPanel();
	
	//public HTML seeResults = new HTML("&nbsp;&nbsp;<a href=\"Javascript:\" style=\"font-style: italic; font-size: smaller\">See results", true);
	public HTML seeResults = new HTML("&nbsp;&nbsp;<a style=\"font-style: italic; font-size: smaller\">See results", true);
	
	// this panel will hold the list with the RSeprs
	private HorizontalPanel RSeprPanel = new HorizontalPanel();

	VerticalPanel verticalPanel = new VerticalPanel();

	protected SearchButtons searchButtons;

	// It holds the current condition type (OR / AND)
	String cType = "OR";
	
	private HTML searchTitle = new HTML("<br>" + SearchConstantsStrings.FIELD_TITLE, true);
	public HTML errorMsg = new HTML("<span style=\"color: darkred\">Search In Previous results is currently unavailable " +
			"because you have not performed any search yet or the refinement is not allowed on the performed queries.</span>");

	// Define the AND - OR conditions
	public RadioButton and = new RadioButton("conditionType", SearchConstantsStrings.AND);
	public RadioButton or = new RadioButton("conditionType", SearchConstantsStrings.OR);
	// This panel will hold the AND - OR conditions
	public FlowPanel fPanel = new FlowPanel();

	// This Panel wraps the SearchField Class allowing multiple search rows
	public VerticalPanel searchFieldVerticalPanel = new VerticalPanel();
	// The searchable fields for the current selected RSepr
	public SearchableFieldBean[] searchFields = null;
	// The current fields (textboxes)
	public String[] textFields = null;
	
	public static SearchPreviousResultsPanel singleton = null;

	public static SearchPreviousResultsPanel get()
	{
		return singleton;
	}

	protected int searchFieldNo;
	protected int searchFieldActiveNo;

	public SearchPreviousResultsPanel()
	{
		searchButtons = new SearchButtons("Refine", "Reset");
		verticalPanel.setSpacing(4);
		AsyncCallback<LinkedList<PreviousResultsInfo>> callback = new AsyncCallback<LinkedList<PreviousResultsInfo>>()
		{
			public void onFailure(Throwable caught)
			{
				//SearchPortletG.displayErrorWindow("Failed to retrieve the already submitted queries. Please try again", caught);
			}

			public void onSuccess(LinkedList<PreviousResultsInfo> result)
			{
				prResults = result;
				if (prResults == null || prResults.size() == 0)
				{
					searchButtons.setVisible(false);
					verticalPanel.clear();
					verticalPanel.add(errorMsg);
				}
				else
				{
					if (prResults.size() > 0) {
						int last = -1;
						for (int i=0; i<prResults.size(); i++) {
							// Add the queryDescritpion to the ListBox
							if (prResults.get(i) != null) {
								oldResults.addItem(prResults.get(i).getDisplayQuery().substring(0, 120));
								last = i;
							}
						}
						
						// Set the last query as prechecked
						// The last query is defined by the variable 'last'
						oldResults.setItemSelected(last, true);
						//currentIndex = last;
						currentIndex = prResults.get(last).getIndexOfQueryGroup();
						currentQuery = prResults.get(last).getQuery();		
						
						RSeprPanel.setSpacing(SearchConstants.SPACING);
						RSeprPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
						RSeprPanel.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
						RSeprPanel.add(chooseRStitle);
						
						browseHorPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
						browseHorPanel.add(seeResults);
						browseHorPanel.add(seeFullQuery);
						verticalPanel.clear();
						verticalPanel.add(RSeprPanel);
						verticalPanel.add(oldResults);
						verticalPanel.add(browseHorPanel);
						verticalPanel.add(searchTitle);		
											
						AsyncCallback<SearchableFieldBean[]> cl10 = new AsyncCallback<SearchableFieldBean[]>()
						{
							public void onFailure(Throwable caught)
							{
							}

							public void onSuccess(SearchableFieldBean[] result)
							{
								if (result != null) {
									searchFields = result;
									init();
									// TODO remove the comment from reset
									//resetFields();
								}
								
							}
						};
						SearchPortletG.searchService.createPreviousQuery(currentIndex, cl10);
					}
				}	
			}
		};
		SearchPortletG.searchService.getPreviousQueries(callback);
		initWidget(verticalPanel);
	
		if (singleton == null)
			singleton = this;
	}

	public void init()
	{
		oldResults.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				// Take the selected item of the ListBox and add to the SearchField ListBox the searchable fields
				// Get the index of the selected item
				//currentIndex = oldResults.getSelectedIndex();
				int selectedIndex = oldResults.getSelectedIndex();
				currentIndex = prResults.get(selectedIndex).getIndexOfQueryGroup();
				currentQuery = prResults.get(selectedIndex).getQuery();
				
				
				AsyncCallback<SearchableFieldBean[]> createPreviousQuerycallback = new AsyncCallback<SearchableFieldBean[]>()
				{
					public void onFailure(Throwable caught)
					{
					}

					public void onSuccess(SearchableFieldBean[] result)
					{
						if (result != null && result.length > 0) {
							searchFields = result;
							resetFields();
							//init();
						}
					}
				};
				SearchPortletG.searchService.createPreviousQuery(currentIndex, createPreviousQuerycallback);
			}
		});
		
		if (searchFieldNo <= 1 || searchFieldActiveNo <=1)
			fPanel.setVisible(false);
		else {
			fPanel.setVisible(true);
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
			SearchPortletG.searchService.getSelectedConditionTypeOnPreviousSearch(cl);
		}
		fPanel.add(and);
		fPanel.add(or);

		verticalPanel.add(fPanel);
		verticalPanel.add(searchFieldVerticalPanel);
		searchFieldVerticalPanel.setSpacing(SearchConstants.SPACING);
		verticalPanel.add(new HTML("<br>", true));
		verticalPanel.add(searchButtons);

		/* No previous queries have been submitted. Show the error message */
		if (prResults == null)
		{
			searchButtons.setVisible(false);
			searchFieldVerticalPanel.setVisible(false);
			fPanel.setVisible(false);
			verticalPanel.clear();
			verticalPanel.add(errorMsg);
		}
		else
		{ 
			try
			{
				searchFieldNo = 0;
				searchFieldActiveNo = 0;

				// Asynchronous call in order to retrieve the selected fields.
				AsyncCallback<ArrayList<SearchableFieldBean>> callback = new AsyncCallback<ArrayList<SearchableFieldBean>>()
				{
					public void onFailure(Throwable caught)
					{
						SearchPortletG.displayErrorWindow("Failed to retrieve the available search fields. Please click the Reset button and try again", caught);
					}

					public void onSuccess(ArrayList<SearchableFieldBean> result)
					{
						ArrayList<SearchableFieldBean> selectedFields = result;
						if (selectedFields != null)
						{
							int n = selectedFields.size();
							for (int i = 0; i < n; i++)
							{
								int k = searchFields.length;
								int j;
								for (j = 0; j < k; j++)
								{
									// in the past we were compating only the name now the ID and the name (but not the value)
									if (searchFields[j].equals(selectedFields.get(i)))
									{
										SearchField searchFieldOne = new SearchField(searchFieldNo);
										searchFieldOne.fieldListBox.setItemSelected(j, true);
										searchFieldOne.textBox.setText(selectedFields.get(i).getValue());
										searchFieldVerticalPanel.add(searchFieldOne);
										break;
									}
								}
								if(j == k)
									searchFieldNo++;
							}
							if(searchFieldActiveNo == 0)
								addSearchField();
							
							if (n > 1)
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
				SearchPortletG.searchService.getSelectedFieldsOnPreviousSearch(callback);


				and.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
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
				
				// Displays the query of the selectedRSepr
				seeFullQuery.addClickHandler(new ClickHandler()
				{
					public void onClick(ClickEvent event)
					{
						int left = seeFullQuery.getAbsoluteLeft() + seeFullQuery.getOffsetWidth();
					    int top =  seeFullQuery.getAbsoluteTop() + seeFullQuery.getOffsetHeight();
						
					    if (queryDescPopup == null)
					    	queryDescPopup = new DescPopUp("Query Description",true);
					    queryDescPopup.setWidth("300px");
					    queryDescPopup.setPopupPosition(left, top);
					    queryDescPopup.clear();
					    queryDescPopup.addDock("<b>The query is:</b><br>" + currentQuery);
					    queryDescPopup.show();
					}
				});
			}
			catch (Exception e)
			{
				Window.alert("An internal error occured...");
			}
		}

		searchButtons.setResetClickListener(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					public void onFailure(Throwable caught)
					{
					}

					public void onSuccess(Void result)
					{
						resetFields();
					}
				};
				SearchPortletG.searchService.resetFields(true, callback);

			}

		});


		searchButtons.setSubmitClickListener(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				boolean isSearchEnabled = true;
				int n = searchFieldActiveNo;
				//String[] criteria = new String[n];
				//String[] values = new String[n];

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
					
					if (textValue.trim().equals("")) {
						Window.alert("Some of the given criteria do not contain any search term. Please provide a term or remove the empty criteria.");
						isSearchEnabled = false;
						break;
					}
					SearchableFieldBean sf = new SearchableFieldBean(id, listValue, textValue);
					selFields.add(sf);	
				}
				if (isSearchEnabled) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					public void onFailure(Throwable caught)
					{
						Window.alert("Failed to submit the query. Please try again.");
						SearchPortletG.hideLoading();
						searchButtons.enableSubmitButton(true);
						searchButtons.enableResetButton(true);
						SearchPortletG.displayErrorWindow("Failed to submit the query. Please try again", caught);
					}

					public void onSuccess(Void result)
					{
						SearchPortletG.goToResults(true);
					}
				};
				SearchPortletG.showLoading();
				// TODO: the 2 null parameters must be changed and put the values: sortBy 'ASC' and 'DESC'
				SearchPortletG.searchService.submitQueryOnPreviousResult(selFields, null, null, null, callback);
				searchButtons.enableSubmitButton(false);
				searchButtons.enableResetButton(false);
			}
		}

		});
		
		seeResults.addClickHandler(new ClickHandler()
		{

			public void onClick(ClickEvent event)
			{
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					public void onFailure(Throwable caught)
					{
						SearchPortletG.hideLoading();
						searchButtons.enableSubmitButton(true);
						searchButtons.enableResetButton(true);
						SearchPortletG.displayErrorWindow("Failed to submit the browse query. Please try again", caught);
					}
					public void onSuccess(Void result)
					{
						SearchPortletG.goToResults(true);
					}
				};
				SearchPortletG.showLoading();
				SearchPortletG.searchService.submitBrowseQueryOnPreviousResult(currentIndex, callback);
				searchButtons.enableSubmitButton(false);
				searchButtons.enableResetButton(false);
			}
			
		});
	}

	public void addSearchField()
	{
		SearchField searchFieldOne = new SearchField(searchFieldNo);
		
		searchFieldVerticalPanel.add(searchFieldOne);

		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			public void onFailure(Throwable caught)
			{
				SearchPortletG.displayErrorWindow("Failed to add a new field. Please try again", caught);
			}

			public void onSuccess(Void result)
			{
			}
		};
		SearchPortletG.searchService.addSearchFieldOnPreviousQuery(searchFields[0], callback);
	}

	/**
	 * Removes the search field with 'id'.
	 * 
	 * @param id The index of the search field to delete
	 */
	public void removeSearchField(int id)
	{
		for (int i=id+1; i<searchFieldNo; i++) {
			((SearchField) searchFieldVerticalPanel.getWidget(i)).setId(id);
		}
		searchFieldVerticalPanel.remove(id);
		this.searchFieldNo--;
		this.searchFieldActiveNo--;
		// If only 1 search field remove the OR/AND choice
		if (searchFieldNo == 1)
			fPanel.setVisible(false);

		AsyncCallback<Void> callback = new AsyncCallback<Void>()
		{
			public void onFailure(Throwable caught)
			{
				SearchPortletG.displayErrorWindow("Failed to remove the selected search field. Please try again", caught);
			}

			public void onSuccess(Void result)
			{
			}
		};
		SearchPortletG.searchService.removeSearchFieldOnPreviousQuery(id, callback);

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
		SearchPortletG.searchService.storeConditionTypeOnPreviousSearch(type, callback);
	}


	// Needs to be used by WhatPanel class
	protected VerticalPanel getSearchFieldPanel()
	{
		return this.searchFieldVerticalPanel;
	}

	/*
	 * Access Methods for knowing the current Fields number
	 */
	public void adjustSize(int width, int height)
	{
		verticalPanel.setPixelSize(width, height);
	}



	/**
	 * This class wraps the three fields for a row in the Advanced Search ListBox for the Type to search TextBox for what to search ListBox for the condtion AND/OR
	 * 
	 * @author Valia, Giota
	 *
	 */
	protected class SearchField extends Composite
	{

		private HorizontalPanel horizontalPanel = new HorizontalPanel();

		// Holds the available search fields
		private ListBox fieldListBox = new ListBox();

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
		 * @param id : the number of the search field
		 */
		public SearchField(int id)
		{

			this.id = id;


			textBox.setWidth("125");
			textBox.setText("");
			
			
			textBox.addKeyPressHandler(new KeyPressHandler() {
				
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
				public void onClick(ClickEvent event)
				{
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
				public void onClick(ClickEvent event)
				{
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
					SearchPortletG.searchService.addSearchFieldOnPreviousQuery(searchFields[0], callback);
				}
			}
		);


			horizontalPanel.setSpacing(SearchConstants.SPACING);

			initWidget(horizontalPanel);


			searchFieldNo++;
			searchFieldActiveNo++;

			if (searchFields != null && searchFields.length > 0)
			{
				searchButtons.enableSubmitButton(true);
				searchButtons.enableResetButton(true);
				for (int i = 0; i < searchFields.length; i++)
					// add the new search fields in the field list box.
					// keep the name's field and its ID
					fieldListBox.addItem(searchFields[i].getName(), searchFields[i].getId());
				
			}

			//track the changes on selected search field
			// change the selected type of field
			fieldListBox.addChangeHandler(new ChangeHandler()
			{

				public void onChange(ChangeEvent event)
				{
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
					SearchPortletG.searchService.updateCriterionNameOnPreviousQuery(((SearchField)((ListBox)event.getSource()).getParent().getParent()).getId(),((ListBox)event.getSource()).getValue(((ListBox)event.getSource()).getSelectedIndex()), ((ListBox)event.getSource()).getItemText(((ListBox)event.getSource()).getSelectedIndex()), callback);
				}
			}
			);

			//track the changes on value field
			textBox.addChangeHandler(new ChangeHandler()
			{

				public void onChange(ChangeEvent event)
				{
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
					SearchPortletG.searchService.updateCriterionValueOnPreviousQuery(((SearchField)((TextBox)event.getSource()).getParent().getParent()).getId(), ((TextBox)event.getSource()).getText() , callback);
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
	
	private void resetFields() {
		and.setValue(false);
		or.setValue(true);
		verticalPanel.remove(searchFieldVerticalPanel);
		searchFieldVerticalPanel = new VerticalPanel();
		fPanel.setVisible(false);
		verticalPanel.insert(searchFieldVerticalPanel, verticalPanel.getWidgetCount()-2);
		//searchFieldVerticalPanel.
		searchFieldVerticalPanel.setSpacing(SearchConstants.SPACING);
		searchFieldNo = 0;
		searchFieldActiveNo = 0;
		addSearchField();
	}
	
	public static class DescPopUp extends GCubeDialog implements ClickHandler{
		public DescPopUp(String title, boolean autoHide) {
			super(autoHide);
			setText(title);
		}

		public void onClick(ClickEvent event) {
			hide();
		}
		
		public void addDock(String description)
		{
		      HTML msg = new HTML(description, true);
		      DockPanel dock = new DockPanel();
		      dock.setSpacing(2);
		      dock.add(msg, DockPanel.NORTH);
		      dock.setWidth("100%");
		      add(dock);
		}
	}

}
