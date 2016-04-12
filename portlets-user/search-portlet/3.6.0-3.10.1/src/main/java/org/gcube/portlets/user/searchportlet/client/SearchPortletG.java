package org.gcube.portlets.user.searchportlet.client;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.PersonJsonizer;

import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchService;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchServiceAsync;
import org.gcube.portlets.user.searchportlet.client.widgets.ExceptionAlertWindow;
import org.gcube.portlets.user.searchportlet.client.widgets.LoadingPopUp;
import org.gcube.portlets.user.searchportlet.client.widgets.QuickGuidedTour;
import org.jsonmaker.gwt.client.Jsonizer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SearchPortletG implements EntryPoint
{

	public static SearchServiceAsync searchService = (SearchServiceAsync) GWT.create(SearchService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) searchService;

	private VerticalPanel verticalPanel = new VerticalPanel();

	// The Main Panel 
	private static GCubePanel mainLayout = new GCubePanel( "Search", "https://technical.wiki.d4science.research-infrastructures.eu/documentation/index.php/Common_Functionality#Search");
	private MainPanel mp = null;
	
	// Use the value of this variable to avoid 2 times initialization
	private boolean initThroughNotification = false;
	
	final PageBusAdapter pageBusAdapter = new PageBusAdapter();
	protected static DialogBox dlg = new LoadingPopUp(false);
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
			endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()+"SearchServlet");
			Person searchNotification = new Person();
			searchNotification.setName(SearchConstantsStrings.COLLECTIONS_CHANGED);
			
			//Subscribe to message and associate subsequent receptions with custom subscriber data
			try
			{
				pageBusAdapter.PageBusSubscribe("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", null, null, searchNotification, (Jsonizer)GWT.create(PersonJsonizer.class));
			}
			catch (PageBusAdapterException e1)
			{
				e1.printStackTrace();
			}
			
			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener() {
				public void onPageBusSubscriptionCallback(PageBusEvent event) {
					// translate JavaScript message contents and subscriber data to their Java equivalents
					try {

						Person message = (Person)event.getMessage((Jsonizer)GWT.create(PersonJsonizer.class));
						if (message.getName().equals(SearchConstantsStrings.COLLECTIONS_CHANGED)) {
							initThroughNotification = true;
							RootPanel.get("SearchDIV").clear();	
							init();
						}
					} catch (PageBusAdapterException e) {
						e.printStackTrace();
					}
				}
				public String getName() {
					return null;
				}
			});
			
			if (initThroughNotification == false) {
				init();
			}
			QuickGuidedTour tour = new QuickGuidedTour();
			tour.showGuide();
	}
	
	void init()
	{
		mp = new MainPanel();
		mp.setSize("100%", "590px");
		verticalPanel.clear();
		verticalPanel.add(mp);
		verticalPanel.setSize("100%", "590px");
		verticalPanel.setSpacing(10);
		mainLayout.setSize("100%", "590px");
		mainLayout.add(verticalPanel);
		RootPanel.get("SearchDIV").add(mainLayout);
	}

	/*
	 * This Method creates the header for the Title of the StackPanelItem
	 */
	protected static String createHeaderHTML(String imageUrl, String caption)
	{
		return "<table class=\"my-header\" width=\"100%\"><tr>" + "<td width=\"30\"><img src='" + imageUrl
		+ "'></td><td width=\"*\" style='vertical-align:middle;text-align:left;'>" + caption + "</td></tr></table>";
	}

	
	public static void goToResults(boolean nativeResults)
	{
		if (nativeResults)
			Window.open(GWT.getHostPageBaseURL()+"results", "_self", "");
		else
			//TODO this should be changed based on the name we will decide to use
			Window.open(GWT.getHostPageBaseURL()+"semanticresults", "_self", "");
	}

	protected static void showLoading() {
		dlg.setStyleName("unknown");
		int left = RootPanel.get("SearchDIV").getAbsoluteLeft() + RootPanel.get("SearchDIV").getOffsetWidth()/2;
		int top = RootPanel.get("SearchDIV").getAbsoluteTop() + RootPanel.get("SearchDIV").getOffsetHeight()/2;
		dlg.setPopupPosition(left, top);
		dlg.show();
	}
	
	protected static void hideLoading() {
		dlg.hide();
	}
	
	
	protected static void displayErrorWindow(String userMsg, Throwable caught) {
		ExceptionAlertWindow alertWindow = new ExceptionAlertWindow(userMsg, true);
		alertWindow.addDock(caught);
		int left = com.google.gwt.user.client.Window.getClientWidth()/2;
		int top = com.google.gwt.user.client.Window.getClientHeight()/2;
		alertWindow.setPopupPosition(left, top);
		alertWindow.show();
	}
	
	
}
