package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portlets.user.newsfeed.client.FilterType;
import org.gcube.portlets.user.newsfeed.client.NewsServiceAsync;
import org.gcube.portlets.user.newsfeed.client.panels.NewsFeedPanel;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class FilterPanel extends Composite {
	
	private static FilterPanelUiBinder uiBinder = GWT
			.create(FilterPanelUiBinder.class);

	interface FilterPanelUiBinder extends UiBinder<Widget, FilterPanel> {
	}

	protected static final String ERROR_MESSAGE = "Ops! we encountered some problems, server is not responding, please try again in a short while.";
	protected static final String SESSION_EXPIRED = "Your session has expired, please log out and login again";
	
	NewsFeedPanel caller;
	NewsServiceAsync service;

	public FilterPanel(NewsFeedPanel caller, NewsServiceAsync newsService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.caller = caller;
		this.service = newsService;
		//connectionsLink.setHTML("<a>Connections</a>");
		allUpdatesLink.setHTML("<a>All Updates</a>");
		favoritesLink.setHTML("<a>Favorites</a>");
		onlyme.setHTML("<a>Your Posts</a>");
		//connectionsLink.getElement().getStyle().setCursor(Cursor.POINTER);
		allUpdatesLink.getElement().getStyle().setCursor(Cursor.POINTER);
		favoritesLink.getElement().getStyle().setCursor(Cursor.POINTER);
		onlyme.getElement().getStyle().setCursor(Cursor.POINTER);
		
		allUpdatesLink.setStyleName("filter-selected");
	}
	
//	@UiField
//	HTML connectionsLink;
	@UiField
	HTML allUpdatesLink;
	@UiField
	HTML favoritesLink;
	@UiField
	HTML onlyme;
	
	
//	@UiHandler("connectionsLink")
//	void onConnectionsClick(ClickEvent e) {
//		allUpdatesLink.removeStyleName("filter-selected");
//		onlyme.removeStyleName("filter-selected");
//		connectionsLink.setStyleName("filter-selected");
//		caller.setCurrentFilter(FilterType.CONNECTIONS);
//		service.getUserInfo(new AsyncCallback<UserInfo>() {
//			@Override
//			public void onFailure(Throwable caught) {	
//				Window.alert("Ops! we encountered some problems delivering your message, server is not responding, please try again in a short while.");
//			}
//
//			@Override
//			public void onSuccess(UserInfo result) {
//				if (result.getUsername().equals("test.user")) {
//					Window.alert("Your session has expired, please log out and login again");
//					} 
//				else
//					caller.showOnlyConnectionsFeeds();
//			}
//		});		
//	}
//	
	public void removeFilterSelected() {
		allUpdatesLink.removeStyleName("filter-selected");
		onlyme.removeStyleName("filter-selected");
		favoritesLink.removeStyleName("filter-selected");
	}
	
	@UiHandler("favoritesLink")
	void onFavoritesClick(ClickEvent e) {
		allUpdatesLink.removeStyleName("filter-selected");
		onlyme.removeStyleName("filter-selected");
		favoritesLink.setStyleName("filter-selected");
		caller.setCurrentFilter(FilterType.LIKEDFEEDS);
		service.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) {	
				Window.alert(ERROR_MESSAGE);
			}

			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals("test.user")) {
					Window.alert(SESSION_EXPIRED);
					} 
				else
					caller.showOnlyLikedFeeds();
			}
		});		
	}
	
	@UiHandler("allUpdatesLink")
	void onAllUpdatesClick(ClickEvent e) {
		onlyme.removeStyleName("filter-selected");
		favoritesLink.removeStyleName("filter-selected");
		allUpdatesLink.setStyleName("filter-selected");
		caller.setCurrentFilter(FilterType.ALL_UPDATES);
		service.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) {	
				Window.alert(ERROR_MESSAGE);
			}

			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals("test.user")) {
					Window.alert(SESSION_EXPIRED);
					} 
				else
					caller.showAllUpdatesFeeds();
			}
		});	
	}
	
	@UiHandler("onlyme")
	void onlyme(ClickEvent e) {
		allUpdatesLink.removeStyleName("filter-selected");
		favoritesLink.removeStyleName("filter-selected");
		onlyme.setStyleName("filter-selected");
		caller.setCurrentFilter(FilterType.MINE);
		service.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) {	
				Window.alert(ERROR_MESSAGE);
			}

			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals("test.user")) {
					Window.alert(SESSION_EXPIRED);
					} 
				else
					caller.showOnlyMyFeeds();
			}
		});			
	}
}
