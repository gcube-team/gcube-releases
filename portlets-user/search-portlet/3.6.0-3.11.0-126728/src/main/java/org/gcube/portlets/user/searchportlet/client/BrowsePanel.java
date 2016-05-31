package org.gcube.portlets.user.searchportlet.client;

import java.util.ArrayList;

import org.gcube.portlets.user.searchportlet.client.SearchPreviousResultsPanel.DescPopUp;
import org.gcube.portlets.user.searchportlet.shared.AlertsErrorMessages;
import org.gcube.portlets.user.searchportlet.shared.BrowsableFieldBean;

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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This class is one of the Panel that stands inside the StackPanel
 * 
 */
public class BrowsePanel extends Composite
{
	// Set the 'Ascending' order as the default order.
	private String sortOrder = SearchConstantsStrings.ORDERTYPE_ASC;
	private static final int maxResultsNumberPerPage = 50;
	private static final String defaultResultsNumberPerPage = "10";

	private ScrollPanel scroller = new ScrollPanel();
	private VerticalPanel mainPanel = new VerticalPanel();
	private HTML filteredByText = new HTML("<b>Browse by</b> ", true);
	private HTML resultsNo = new HTML(SearchConstantsStrings.BROWSE_SEARCH_RESULTS_NO, true);
	private HTML sortTitle = new HTML(" <b>order</b>", true);

	// creating the listbox Widget
	public ListBox fieldListBox = new ListBox();

	public RadioButton asc = new RadioButton("orderType", SearchConstantsStrings.ASCENDING);
	public RadioButton desc = new RadioButton("orderType", SearchConstantsStrings.DESCENDING);
	public TextBox resultsNoTextbox = new TextBox();

	public boolean disableSubmit;

	private Button infoBrowseButton = new Button();
	private String infoBrowseDesc = "Click on the <b>'Browse Collection'</b> button to browse the contents of a collection " +
			"from the perspective of a specific field's values or click on the <b>'Browse Field Values'</b> button to project" +
			" all the distinct values contained in the corresponding field.";
	private static DescPopUp infoBrowsePopup = null;
	private HorizontalPanel browseFieldButtonPanel = new HorizontalPanel();
	private Button browseFieldButton = new Button("Browse Field Values");
	private Button browseCollectionButton = new Button("Browse Collection(s)");
	private HorizontalPanel filterByPanel = new HorizontalPanel();
	private HorizontalPanel sortOrderPanel = new HorizontalPanel();
	private HorizontalPanel resultsNoPanel = new HorizontalPanel();

	private HTML errorMsg = new HTML("<span style=\"color: darkred\">Browse is currently unavailable.</span>");
	private HTML noColSelectedMsg = new HTML("<span style=\"color: darkred\">A collection should be selected to enable browse functionality</span>");

	public BrowsePanel()
	{
		mainPanel.setSpacing(SearchConstants.SPACING);
		browseFieldButtonPanel.setSpacing(15);
		browseFieldButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		disableSubmit = false;
		filterByPanel.setSpacing(15);
		filterByPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		filterByPanel.add(filteredByText);

		AsyncCallback<ArrayList<BrowsableFieldBean>> callback = new AsyncCallback<ArrayList<BrowsableFieldBean>>()
		{
			public void onFailure(Throwable caught)
			{
				mainPanel.clear();
				mainPanel.add(errorMsg);
				browseCollectionButton.setVisible(false);
				browseFieldButton.setVisible(false);
				infoBrowseButton.setVisible(false);
			}
			
			public void onSuccess(ArrayList<BrowsableFieldBean> result)
			{
				ArrayList<BrowsableFieldBean> browsableFields = result;
				if (browsableFields != null && browsableFields.size() > 0)
				{
					for (int i = 0; i < browsableFields.size(); i++)
						fieldListBox.addItem(browsableFields.get(i).getName(), browsableFields.get(i).getId());
					fieldListBox.setItemSelected(0, true);

					fieldListBox.setName("sort");
					filterByPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					filterByPanel.add(fieldListBox);

					// setting size for the textbox
					resultsNoTextbox.setMaxLength(2);
					resultsNoTextbox.setPixelSize(40, 18);


					AsyncCallback<Integer> resultsNumberCallback = new AsyncCallback<Integer>()
					{

						public void onFailure(Throwable caught)
						{
							resultsNoTextbox.setText(defaultResultsNumberPerPage);
						}

						public void onSuccess(Integer result)
						{
							if (result == null)
								resultsNoTextbox.setText(defaultResultsNumberPerPage);
							else
								resultsNoTextbox.setText(result.toString());

						}

					};
					SearchPortletG.searchService.getResultsNumberPerPage(resultsNumberCallback);

					resultsNoTextbox.setEnabled(true);
					resultsNoTextbox.setName("resNo");
					resultsNoTextbox.setTitle(SearchConstantsStrings.RESULTS_PER_PAGE_INFO);

					resultsNoPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					resultsNoPanel.add(resultsNo);
					resultsNoPanel.add(resultsNoTextbox);

					// Ascending order is the default sorting order
					asc.setValue(true);
					desc.setValue(false);

					sortOrderPanel.setSpacing(15);
					sortOrderPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
					sortOrderPanel.add(new HTML("<b>in</b> "));
					sortOrderPanel.add(asc);
					sortOrderPanel.add(desc);
					sortOrderPanel.add(sortTitle);
					filterByPanel.add(sortOrderPanel);
					mainPanel.add(filterByPanel);
					mainPanel.add(resultsNoPanel);
					browseCollectionButton.setEnabled(true);
					browseFieldButton.setEnabled(true);
					infoBrowseButton.setEnabled(true);
				}
				else
				{
					mainPanel.clear();
					browseCollectionButton.setVisible(false);
					browseFieldButton.setVisible(false);
					infoBrowseButton.setVisible(false);
					AsyncCallback<Integer> getNumOfSelColsCallback = new AsyncCallback<Integer>() {

						public void onFailure(Throwable caught) {
							
							mainPanel.add(errorMsg);
						}

						public void onSuccess(Integer result) {
							if (result.intValue() == 0)
								mainPanel.add(noColSelectedMsg);
							else 
								mainPanel.add(errorMsg);
						}
						
					};SearchPortletG.searchService.getNumberOfSelectedCollections(getNumOfSelColsCallback);
				}
				browseFieldButtonPanel.add(browseCollectionButton);
				browseFieldButtonPanel.add(browseFieldButton);
				browseFieldButtonPanel.add(infoBrowseButton);
				infoBrowseButton.setStyleName("info-button");
				mainPanel.add(browseFieldButtonPanel);
			}
		};
		SearchPortletG.searchService.getBrowsableFields(callback);
		scroller.add(mainPanel);
		scroller.setWidth("100%");
		
		browseCollectionButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				BrowsableFieldBean bf = new BrowsableFieldBean(fieldListBox.getValue(fieldListBox.getSelectedIndex()), fieldListBox.getItemText(fieldListBox.getSelectedIndex()));
				AsyncCallback<Boolean> browseCallback = new AsyncCallback<Boolean>()
				{
					public void onFailure(Throwable caught)
					{
						SearchPortletG.hideLoading();
						browseCollectionButton.setEnabled(true);
						browseFieldButton.setEnabled(true);
						SearchPortletG.displayErrorWindow(AlertsErrorMessages.BrowseQuerySubmissionFailure, caught);
					}

					public void onSuccess(Boolean result)
					{
						SearchPortletG.goToResults(true);
					}
				};
				SearchPortletG.showLoading();
				SearchPortletG.searchService.submitBrowseQuery(bf, sortOrder, SearchConstantsStrings.BROWSE_COLLECTION, Integer.parseInt(resultsNoTextbox.getText().trim()), browseCallback);
				browseCollectionButton.setEnabled(false);
				browseFieldButton.setEnabled(false);
				
			}
			
		});

		// Let's disallow non-numeric entry in the normal text box.
		resultsNoTextbox.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				int keyCode = event.getNativeEvent().getKeyCode();
				if ((!Character.isDigit(event.getCharCode()) && (keyCode != KeyCodes.KEY_TAB)
						&& (keyCode != KeyCodes.KEY_BACKSPACE)
						&& (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) 
						&& (keyCode != KeyCodes.KEY_HOME) && (keyCode != KeyCodes.KEY_END)
						&& (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
						&& (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN))) {
					((TextBox)event.getSource()).cancelKey();
				}
			}
		});

		resultsNoTextbox.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event)
			{
				if (Integer.parseInt(resultsNoTextbox.getText().trim()) > maxResultsNumberPerPage) {
					Window.alert("The results number per page cannot be more than 50. Please specify a lower value, otherwise the default value will be used.");
					resultsNoTextbox.setText(defaultResultsNumberPerPage);
				}
			}

		});

		browseFieldButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				BrowsableFieldBean bf = new BrowsableFieldBean(fieldListBox.getValue(fieldListBox.getSelectedIndex()), fieldListBox.getItemText(fieldListBox.getSelectedIndex()));
				AsyncCallback<Boolean> browseCallback = new AsyncCallback<Boolean>()
				{
					public void onFailure(Throwable caught)
					{
						SearchPortletG.hideLoading();
						browseCollectionButton.setEnabled(true);
						browseFieldButton.setEnabled(true);
						SearchPortletG.displayErrorWindow(AlertsErrorMessages.BrowseFieldFailed, caught);
					}

					public void onSuccess(Boolean result)
					{
						SearchPortletG.goToResults(true);
					}
				};
				SearchPortletG.showLoading();
				SearchPortletG.searchService.submitBrowseQuery(bf, sortOrder, SearchConstantsStrings.BROWSE_FIELD, Integer.parseInt(resultsNoTextbox.getText().trim()), browseCallback);
				browseCollectionButton.setEnabled(false);
				browseFieldButton.setEnabled(false);
			}
		}); 

		infoBrowseButton.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				int left = ((UIObject) event.getSource()).getAbsoluteLeft() + ((UIObject) event.getSource()).getOffsetWidth() + 3;
				int top =  ((UIObject) event.getSource()).getAbsoluteTop();

				if (infoBrowsePopup == null)
					infoBrowsePopup = new DescPopUp("Browse", true);
				infoBrowsePopup.setWidth("300px");
				infoBrowsePopup.setPopupPosition(left, top);
				infoBrowsePopup.clear();
				infoBrowsePopup.addDock(infoBrowseDesc);
				infoBrowsePopup.show();
			}
		});

		asc.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if (asc.getValue())
				{
					sortOrder = SearchConstantsStrings.ORDERTYPE_ASC;

				}
			}
		});

		desc.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if (desc.getValue())
				{
					sortOrder = SearchConstantsStrings.ORDERTYPE_DESC;
				}
			}
		});
		
		initWidget(scroller);
	}

}
