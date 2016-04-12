/*package org.gcube.portlets.user.searchportlet.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class GeospatialSearch extends Composite
{

	private ScrollPanel scrollPanel = new ScrollPanel();
	private VerticalPanel verticalPanel = new VerticalPanel();
	private HorizontalPanel horPanel = new HorizontalPanel();
	private Button searchButton = new Button("Search");

	public String[] searchFields = null; // for all classes
	public boolean found = false; // for all classes

	public HTML errorMsg = new HTML(
			"<span style=\"color: darkred\">Geospatial search is currently unavailable either because you haven't selected any collections, or the selected collections do not contain any geospatial index.</span>");
	

	public GeospatialSearch()
	{

		AsyncCallback<String[]> callback = new AsyncCallback<String[]>()
		{
			public void onFailure(Throwable caught)
			{
				verticalPanel.add(errorMsg);
			}

			public void onSuccess(String[] result)
			{
				searchFields = (String[]) result;
				if (searchFields != null)
				{
					for(int i=0; i < searchFields.length; i++)
					{
						if(searchFields[i].startsWith("Geospatial"))
						{
							found = true;
							break;
						}
					}
				}
				if(found)
				{
					verticalPanel.setSpacing(SearchConstants.SPACING);
					verticalPanel.setWidth("100%");
					verticalPanel.add(new HTML("<span style=\"color: darkblue\">Select an area on the map and click Search button to perform search.</span>", true));
					verticalPanel.add(new HTML("<br>", true));
					verticalPanel.add(new HTML("<br>", true));
					verticalPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
					horPanel.setWidth("100%");
					horPanel.add(searchButton);
					horPanel.setCellWidth(searchButton, "100%");
					horPanel.setCellHorizontalAlignment(searchButton,HorizontalPanel.ALIGN_CENTER);
					verticalPanel.add(horPanel);
				}
				else
				{
					verticalPanel.add(errorMsg);
				}
			}
		};
		SearchPortletG.searchService.getSearchFields(callback);	
		initWidget(scrollPanel);
		scrollPanel.add(verticalPanel);


		searchButton.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				AsyncCallback<Void> callback = new AsyncCallback<Void>()
				{
					public void onFailure(Throwable caught)
					{
						Window.alert("Failed to submit the geospatial query");
						SearchPortletG.hideLoading();
						searchButton.setEnabled(true);
					}

					public void onSuccess(Void result)
					{
						SearchPortletG.goToResults();
					}
				};
				SearchPortletG.showLoading(sender.getAbsoluteLeft() + 50, sender.getAbsoluteTop() + 50);
				SearchPortletG.searchService.submitAdvancedQuery(null, null, null, null, null, false, SearchConstantsStrings.ADVANCED_SEARCH, callback);
				searchButton.setEnabled(false);
			}
		});
	}
}
*/