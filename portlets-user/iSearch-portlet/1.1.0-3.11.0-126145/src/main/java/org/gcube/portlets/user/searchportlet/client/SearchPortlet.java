package org.gcube.portlets.user.searchportlet.client;

import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchService;
import org.gcube.portlets.user.searchportlet.client.interfaces.SearchServiceAsync;
import org.gcube.portlets.user.searchportlet.client.widgets.ExceptionAlertWindow;
import org.gcube.portlets.user.searchportlet.client.widgets.LoadingPopUp;

import java.util.logging.Logger;


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
public class SearchPortlet implements EntryPoint
{

	public static SearchServiceAsync searchService = (SearchServiceAsync) GWT.create(SearchService.class);
	private static ServiceDefTarget endpoint = (ServiceDefTarget) searchService;

	private VerticalPanel verticalPanel = new VerticalPanel();

	// The Main Panel 
	private static GCubePanel mainLayout = new GCubePanel( "", "https://gcube.wiki.gcube-system.org/gcube/index.php/Common_Functionality#Search_2");
	private SearchPanel mp = null;

	protected static DialogBox dlg = new LoadingPopUp(false);

	public static Logger logger = Logger.getLogger("SearchLogger");

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		endpoint.setServiceEntryPoint(GWT.getModuleBaseURL()+"SearchServlet");
		init();
	}

	void init()
	{
		mp = new SearchPanel();
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
