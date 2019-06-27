package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portlets.user.newsfeed.client.FilterType;
import org.gcube.portlets.user.newsfeed.client.NewsServiceAsync;
import org.gcube.portlets.user.newsfeed.client.panels.NewsFeedPanel;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
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
	@UiField NavLink allUpdatesLink;
	@UiField NavLink recentCommentsLink;
	@UiField Dropdown sortByDD;

	public FilterPanel(NewsFeedPanel caller, NewsServiceAsync newsService) {
		initWidget(uiBinder.createAndBindUi(this));
		this.caller = caller;
		this.service = newsService;
	}


	public void removeFilterSelected() {
		allUpdatesLink.setActive(false);
		recentCommentsLink.setActive(false);
	}
	
	@UiHandler("recentCommentsLink")
	void onRecentCommentsLinkClick(ClickEvent e) {
		allUpdatesLink.setDisabled(false);
		recentCommentsLink.setDisabled(true);
		sortByDD.setText("newest Comment");
		caller.setCurrentFilter(FilterType.RECENT_COMMENTS);
		int loadedPostsInView = caller.getAllUpdatesSize() + 1;
		int quantity = loadedPostsInView < 100 ? 100 - loadedPostsInView : loadedPostsInView;
		caller.loadMorePosts(quantity, true);
	}
	
	@UiHandler("allUpdatesLink")
	void onAllUpdatesClick(ClickEvent e) {
		allUpdatesLink.setDisabled(true);
		recentCommentsLink.setDisabled(false);	
		sortByDD.setText("newest 	Post");
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

}
