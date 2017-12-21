package org.gcube.portlets.user.newsfeed.client;

import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.ClientFeed.ClientFeedJsonizer;
import org.gcube.portlets.user.newsfeed.client.event.PageBusEvents;
import org.gcube.portlets.user.newsfeed.client.panels.NewsFeedPanel;
import org.jsonmaker.gwt.client.Jsonizer;
import org.jsonmaker.gwt.client.base.Defaults;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NewsFeed implements EntryPoint {

	private final String UNIQUE_DIV = "newsfeedDIV";
	final public static PageBusAdapter pageBusAdapter = new PageBusAdapter();
	NewsFeedPanel mainPanel;


	public void onModuleLoad() {	
		init();
	}

	public void init() {		

		mainPanel = new NewsFeedPanel();
	
		RootPanel.get(UNIQUE_DIV).add(mainPanel);

		ClientFeed notification = new ClientFeed();
		//Subscribe to message and associate subsequent receptions with custom subscriber data
		try	{
			pageBusAdapter.PageBusSubscribe(PageBusEvents.newPostCreated, null, null, notification, (Jsonizer)GWT.create(ClientFeedJsonizer.class));
		}
		catch (PageBusAdapterException e1)	{
			e1.printStackTrace();
		}

		pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener() {
			@Override
			public void onPageBusSubscriptionCallback(PageBusEvent event) {

				if(event.getSubject().equals(this.getName())){
					// translate JavaScript message contents and subscriber data to their Java equivalents
					try {
						ClientFeed feed = (ClientFeed)event.getMessage((Jsonizer)GWT.create(ClientFeedJsonizer.class));
						mainPanel.addJustAddedFeed(feed);

						// alert the User statistics portlet to increment the number of user's posts
						pageBusAdapter.PageBusPublish(PageBusEvents.postIncrement, "", Defaults.STRING_JSONIZER);

					} catch (PageBusAdapterException e) {
						e.printStackTrace();
					}
				}
			}
			@Override
			public String getName() {
				return PageBusEvents.newPostCreated;
			}
		});	
	}
}
