package org.gcube.portlets.user.newsfeed.client;

import org.gcube.portal.databook.shared.ClientPost;
import org.gcube.portal.databook.shared.JSON;
import org.gcube.portlets.user.newsfeed.client.panels.NewsFeedPanel;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 * 
 *  This class uses Liferay's Client-side+Inter-Portlet-Communication for displaying post created in the Share Updates portlet
 *  @see https://web.liferay.com/community/wiki/-/wiki/Main/Client-side+Inter-Portlet-Communication+%28IPC%29%20using+Java+Script
 *
 */
public class NewsFeed implements EntryPoint {

	private final String UNIQUE_DIV = "newsfeedDIV";

	private NewsFeedPanel mainPanel;

	private static NewsFeedPanel instance;
	
	public static NewsFeedPanel getInstance() {
        if (instance == null) {
        	instance = new NewsFeedPanel();
        }
        return instance;
    }
	
	public void onModuleLoad() {	
		injectLiferayIPCEventReceiver();
		exportReceiveEventJavascriptFunction();
		mainPanel = getInstance();
		RootPanel.get(UNIQUE_DIV).add(mainPanel);
	}
	/**
	 * this is a JSNI method that injects the Liferay Javascript function listening for events from ShareUpdates
	 */
	public static native void injectLiferayIPCEventReceiver() /*-{
	  	try {
		  	$wnd.Liferay.on('newPostCreated',function(event) {
	 			$wnd.handleReceiveEvent(event.payload);
			});
	  	} catch(err) {
	  		$wnd.console.log('error subscribing to newPostCreated events, acceptable in dev');
	  	}
	}-*/;
	/**
	 * this is a JSNI method mapping the Javascript function handleReceiveEvent to the Java method handleReceiveEvent
	 */
	public static native void exportReceiveEventJavascriptFunction()/*-{
	  	$wnd.handleReceiveEvent = @org.gcube.portlets.user.newsfeed.client.NewsFeed::handleReceiveEvent(*);
	}-*/;
	/**
	 * the Java method handleReceiveEvent
	 * @param jsonizedClientPostInstance the jsonized {@link ClientPost} sent by ShareUpdates
	 */
	public static void handleReceiveEvent(String jsonizedClientPostInstance) {
		ClientPost cp = (ClientPost) JSON.parse(jsonizedClientPostInstance);
		getInstance().addJustAddedFeed(cp);
	}		
}
