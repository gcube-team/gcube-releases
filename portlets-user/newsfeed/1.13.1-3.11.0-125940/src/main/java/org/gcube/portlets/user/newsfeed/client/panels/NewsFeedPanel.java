package org.gcube.portlets.user.newsfeed.client.panels;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.ClientFeed;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.Like;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.newsfeed.client.FilterType;
import org.gcube.portlets.user.newsfeed.client.NewsFeed;
import org.gcube.portlets.user.newsfeed.client.NewsService;
import org.gcube.portlets.user.newsfeed.client.NewsServiceAsync;
import org.gcube.portlets.user.newsfeed.client.event.AddCommentEvent;
import org.gcube.portlets.user.newsfeed.client.event.AddCommentEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.AddLikeEvent;
import org.gcube.portlets.user.newsfeed.client.event.AddLikeEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.DeleteCommentEvent;
import org.gcube.portlets.user.newsfeed.client.event.DeleteCommentEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.DeleteFeedEvent;
import org.gcube.portlets.user.newsfeed.client.event.DeleteFeedEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.EditCommentEvent;
import org.gcube.portlets.user.newsfeed.client.event.EditCommentEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.OpenFeedEvent;
import org.gcube.portlets.user.newsfeed.client.event.OpenFeedEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.PageBusEvents;
import org.gcube.portlets.user.newsfeed.client.event.SeeCommentsEvent;
import org.gcube.portlets.user.newsfeed.client.event.SeeCommentsEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.SeeLikesEvent;
import org.gcube.portlets.user.newsfeed.client.event.SeeLikesEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.ShowMoreUpdatesEvent;
import org.gcube.portlets.user.newsfeed.client.event.ShowMoreUpdatesEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.ShowNewUpdatesEvent;
import org.gcube.portlets.user.newsfeed.client.event.ShowNewUpdatesEventHandler;
import org.gcube.portlets.user.newsfeed.client.event.UnLikeEvent;
import org.gcube.portlets.user.newsfeed.client.event.UnLikeEventHandler;
import org.gcube.portlets.user.newsfeed.client.ui.FilterPanel;
import org.gcube.portlets.user.newsfeed.client.ui.LoadingText;
import org.gcube.portlets.user.newsfeed.client.ui.NewFeedsAvailable;
import org.gcube.portlets.user.newsfeed.client.ui.ResultsFor;
import org.gcube.portlets.user.newsfeed.client.ui.ShowMoreFeeds;
import org.gcube.portlets.user.newsfeed.client.ui.SingleComment;
import org.gcube.portlets.user.newsfeed.client.ui.TweetTemplate;
import org.gcube.portlets.user.newsfeed.shared.MoreFeedsBean;
import org.gcube.portlets.user.newsfeed.shared.NewsConstants;
import org.gcube.portlets.user.newsfeed.shared.OperationResult;
import org.gcube.portlets.user.newsfeed.shared.UserSettings;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;
import org.gcube.portlets.widgets.userselection.client.UserSelectionDialog;
import org.gcube.portlets.widgets.userselection.client.events.SelectedUserEvent;
import org.gcube.portlets.widgets.userselection.client.events.SelectedUserEventHandler;
import org.gcube.portlets.widgets.userselection.client.events.UsersFetchedEvent;
import org.gcube.portlets.widgets.userselection.shared.ItemSelectableBean;
import org.jsonmaker.gwt.client.base.Defaults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
public class NewsFeedPanel extends Composite {
	/**
	 * the current scope on the client can be static
	 */
	private static String currentScope;
	/**
	 * Create a remote service proxy to talk to the server-side News service.
	 */
	private final NewsServiceAsync newsService = GWT.create(NewsService.class);
	private final HandlerManager eventBus = new HandlerManager(null);

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel filterPanelWrapper = new HorizontalPanel();
	private FilterPanel filterPanel;
	private SimplePanel newUpdatesPanel = new SimplePanel();
	private VerticalPanel showMoreUpdatesPanel = new VerticalPanel();
	private VerticalPanel newsPanel = new VerticalPanel();

	private NewFeedsAvailable newsFeedAlert;

	private static final String warning = GWT.getModuleBaseURL() + "../images/warning_blue.png";
	private static final String spacer = GWT.getModuleBaseURL() + "../images/feeds-spacer.gif";
	public static final String loading = GWT.getModuleBaseURL() + "../images/feeds-loader.gif";

	public static final String GET_OID_PARAMETER = "oid";

	public static final String LIKE_LABEL = "Favorite";
	public static final String LIKED_LABEL = "Favorited";
	public static final String COMMENT_LABEL = "Reply";
	public static final String SHARE_FWD_LABEL =  "Share";
	private static final int SEARCHED_FEEDS_TO_SHOW = 10;

	private String vreLabel;

	private boolean showFeedTimelineSource = false;
	private boolean isInfrastructure = false;

	private int delayMillis = 300000; //5 minutes by default (is read from a configuration file in the first async callback)

	private int currNewUpdatesNo = 0;

	private boolean isFirstTweet = false;

	// is user searching for something?
	private boolean isSearch = false;

	// the current query (if isSearch is true)
	protected String currentQuery;

	private LoadingText loadingIcon = new LoadingText();
	private Image loadingImage;
	private UserInfo myUserInfo;
	private FilterType currentFilter;
	private Timer feedsTimer;
	private ShowMoreFeeds showMoreWidget;
	//needed to know the next range start
	private Integer fromStartingPoint;

	private ArrayList<EnhancedFeed> allUpdates = new ArrayList<EnhancedFeed>();

	private ArrayList<EnhancedFeed> tempCacheNewUpdates = new ArrayList<EnhancedFeed>();

	/**
	 * events binder
	 */
	private void bind() {
		eventBus.addHandler(ShowMoreUpdatesEvent.TYPE, new ShowMoreUpdatesEventHandler() {
			@Override
			public void onShowMoreUpdatesClick(ShowMoreUpdatesEvent event) {
				doShowMoreUpdates();
			}
		});  

		eventBus.addHandler(ShowNewUpdatesEvent.TYPE, new ShowNewUpdatesEventHandler() {
			@Override
			public void onShowNewUpdatesClick(ShowNewUpdatesEvent event) {
				doShowCachedNewUpdates();
			}
		});  


		eventBus.addHandler(AddLikeEvent.TYPE, new AddLikeEventHandler() {
			@Override
			public void onAddLike(AddLikeEvent event) {
				doAddLike( event.getOwner(), event.getFeedId());
			}
		});  

		eventBus.addHandler(UnLikeEvent.TYPE, new UnLikeEventHandler() {
			@Override
			public void onUnLike(UnLikeEvent event) {
				doUnLike(event.getOwner(), event.getFeedId());
			}
		});  

		eventBus.addHandler(AddCommentEvent.TYPE, new AddCommentEventHandler() {
			@Override
			public void onAddComment(AddCommentEvent event) {
				doAddComment(event.getOwner(), event.getText(), event.getMentionedUsers());
			}
		});  

		eventBus.addHandler(EditCommentEvent.TYPE, new EditCommentEventHandler() {
			@Override
			public void onEditComment(EditCommentEvent event) {
				doEditComment(event.getOwner(), event.getCommentInstance(), event.getCommentPanel());
			}
		});  


		eventBus.addHandler(SelectedUserEvent.TYPE, new SelectedUserEventHandler() {
			@Override
			public void onSelectedUser(SelectedUserEvent event) {
				GWT.log("event...");
				Location.assign(GCubeSocialNetworking.USER_PROFILE_LINK+"?"+
						Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
						Encoder.encode(event.getSelectedUser().getId()));
			}
		});  

		eventBus.addHandler(SeeLikesEvent.TYPE, new SeeLikesEventHandler() {
			@Override
			public void onSeeLikes(SeeLikesEvent event) {
				doShowLikes(event.getFeedId());
			}
		});  

		eventBus.addHandler(SeeCommentsEvent.TYPE, new SeeCommentsEventHandler() {
			@Override
			public void onSeeComments(SeeCommentsEvent event) {
				doShowComments(event.getOwner(), event.isCommentForm2Add());	
			}
		});  

		eventBus.addHandler(DeleteCommentEvent.TYPE, new DeleteCommentEventHandler() {
			@Override
			public void onDeleteComment(DeleteCommentEvent event) {
				doDeleteComment(event.getOwner(), event.getCommentId());
			}
		});  

		eventBus.addHandler(DeleteFeedEvent.TYPE, new DeleteFeedEventHandler() {
			@Override
			public void onDeleteFeed(DeleteFeedEvent event) {
				doDeleteFeed(event.getToDelete());
			}			
		});  

		eventBus.addHandler(OpenFeedEvent.TYPE, new OpenFeedEventHandler() {
			@Override
			public void onOpenFeed(OpenFeedEvent event) {
				doShowFeed(event.getToShow());
			}			
		});  
	}

	/**
	 * 
	 */
	public NewsFeedPanel() {
		bind();
		mainPanel.setWidth("600px");
		mainPanel.add(filterPanelWrapper);
		filterPanelWrapper.setVisible(false);
		mainPanel.add(newUpdatesPanel);
		mainPanel.add(newsPanel);
		filterPanel = new FilterPanel(this, newsService);
		filterPanelWrapper.add(filterPanel);
		initWidget(mainPanel);
		newsPanel.clear();
		newsPanel.setWidth("100%");
		showMoreUpdatesPanel.setWidth("100%");
		newsPanel.setHeight("300px");
		newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		loadingImage = new Image(loading);
		newsPanel.add(loadingIcon);
		CheckSession.getInstance().startPolling();
		isSearch = false;

		newsService.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) {		
			}

			@Override
			public void onSuccess(UserSettings result) {
				myUserInfo = result.getUserInfo();	
				delayMillis = result.getRefreshingTimeInMillis();	
				vreLabel = result.getVreLabel();
				currentScope = result.getCurrentScope();
				if (result.getUserInfo().getUsername().equals(NewsConstants.TEST_USER)) {
					doStopFeedsTimer();
					doShowSessionExpired();			
				} 
				else {
					GWT.log("checking params ");
					if (getFeedToShowId() != null) {
						String feedKey = getFeedToShowId();
						showSingleFeed(feedKey);
						filterPanel.removeFilterSelected();
					} 
					else if (getHashtagParam() != null) {
						String hashtag = "";
						try {
							hashtag = Encoder.decode(getHashtagParam());
						} catch (Exception e) {
							newsPanel.clear();
							newsPanel.add(new HTML("<div class=\"nofeed-message\"><div style=\"padding-top: 90px;\">" +
									"We're sorry, it seems you used an invalid character, please check the hashtag</div>"));
							return;
						}
						showFeedsByHashtag(hashtag);
						filterPanel.removeFilterSelected();
					}
					else if (getSearchParam() != null) {
						String query = "";
						try {
							query = Encoder.decode(getSearchParam());
							currentQuery =  query;
						} catch (Exception e) {
							newsPanel.clear();
							newsPanel.add(new HTML("<div class=\"nofeed-message\"><div style=\"padding-top: 90px;\">" +
									"We're sorry, it seems you used an invalid character, please check the query</div>"));
							return;
						}
						// show 
						isSearch = true;
						showFeedsSearch(query, 0, SEARCHED_FEEDS_TO_SHOW);
						filterPanel.removeFilterSelected();
					} 
					else {
						showAllUpdatesFeeds();
					}
					currentFilter = FilterType.ALL_UPDATES;
				}
				//adjustments in the UI Depending on the scope
				if (result.isInfrastructure()) {
					filterPanelWrapper.setVisible(getFeedToShowId() == null);
					showFeedTimelineSource = result.isShowTimelineSourceLabel();
					isInfrastructure = true;
				}
				else 
					mainPanel.addStyleName("framed");	

			}		
		});

		feedsTimer = new Timer() {
			@Override
			public void run() {
				checkForNewUpdates();
			}
		};
		feedsTimer.scheduleRepeating(delayMillis);

		//this is for the automatic scroll of feeds
		Window.addWindowScrollHandler(new ScrollHandler() {
			@Override
			public void onWindowScroll(ScrollEvent event) {
				boolean isInView = isScrolledIntoView(showMoreWidget);
				if (isInView) {
					eventBus.fireEvent(new ShowMoreUpdatesEvent());
				}
			}
		});
	}

	/**
	 * stop the feeds timer (when session expires)
	 */
	private void doStopFeedsTimer() {
		feedsTimer.cancel();		
	}
	/**
	 * 
	 */
	private void checkForNewUpdates() {
		switch (currentFilter) {
		case ALL_UPDATES:
			checkAllUpdatesFeeds();
			break;
		case CONNECTIONS:
			showOnlyConnectionsFeeds();
			break;
		case MINE:
			showOnlyMyFeeds();
			break;
		default:
			break;
		}
	}
	/**
	 * check if it has to show just one feed
	 * @return
	 */
	private String getFeedToShowId() {
		return Window.Location.getParameter(GET_OID_PARAMETER);
	}
	/**
	 * check if it has to show the feeds given an hashtag
	 * @return
	 */
	private String getHashtagParam() {
		return Window.Location.getParameter(Encoder.encode(GCubeSocialNetworking.HASHTAG_OID));
	}

	/**
	 * check if it has to show the feeds given a query
	 * @return
	 */
	private String getSearchParam() {
		return Window.Location.getParameter(Encoder.encode(GCubeSocialNetworking.SEARCH_OID));
	}

	/**
	 * used from notification referrals (see this Post)
	 * @param feedKey
	 */
	private void showSingleFeed(String feedKey) {
		newsPanel.clear();
		newsService.getSingleFeed(feedKey, new AsyncCallback<EnhancedFeed>() {
			@Override
			public void onSuccess(EnhancedFeed result) {
				if (result.getFeed().getType() == FeedType.DISABLED) {

					String usrLink = "<a class=\"link\" href=\""+GCubeSocialNetworking.USER_PROFILE_LINK+"?"+
							Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
							Encoder.encode(result.getFeed().getEntityId())+"\">"+result.getFeed().getFullName()+
							"</a> ";
					newsPanel.add(new HTML("<div class=\"nofeed-message\"><div style=\"padding-top: 90px;\">" +
							"We're sorry, "+ usrLink +" removed the post in the meantime!</div></div>"));
				} else {
					newsPanel.add(new ResultsFor("selected post", ""));
					newsPanel.setHeight("");
					newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
					newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
					newsPanel.add(new TweetTemplate(true, true, myUserInfo, result, eventBus));
				}
				newsPanel.add(new Image(spacer));
			}
			@Override
			public void onFailure(Throwable caught) {
				showProblems();							
			}

		});
	}
	/**
	 * check for updates
	 */
	private void checkAllUpdatesFeeds() {		
		newsService.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) { 
				doStopFeedsTimer(); 
			}
			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals(NewsConstants.TEST_USER)) {
					doStopFeedsTimer();
					doShowSessionExpired();				
				} 
				/**
				 * this check avoids the 2 tabs open in 2 different scope, if the previous tab was open at VRE Level and then antoher
				 * is open at infra level the first tab stops checking for updates
				 */
				if (result.getCurrentScope().compareTo(currentScope) == 0) { 
					newsService.getAllUpdateUserFeeds(NewsConstants.FEEDS_NO_PER_CATEGORY, new AsyncCallback<ArrayList<EnhancedFeed>>() {
						@Override
						public void onSuccess(ArrayList<EnhancedFeed> feeds) {
							if (feeds != null && allUpdates.size() > 0) {

								Date myLastUpdateTime = allUpdates.get(0).getFeed().getTime();	//this is the last update in the View
								GWT.log("Last Mine: "+allUpdates.get(0).getFeed().getDescription());

								GWT.log("Last Retr.: "+feeds.get(0).getFeed().getDescription());


								tempCacheNewUpdates = new ArrayList<EnhancedFeed>(); //need to clear it everytime i check (in case someone deleted the updated in the meanwhile)

								//check if there are new updates (enter the while) and put them in a temporary cache for displaying on user click

								int i = 0;
								while (i < feeds.size() && feeds.get(i).getFeed().getTime().after(myLastUpdateTime)) {
									tempCacheNewUpdates.add(feeds.get(i));
									i++;
								} 

								/* currNewUpdatesNo keeps the number of updates to be added on user clicks, 
								 * i keeps the total number as it arrives, 
								 * if they differ you got to refresh the updates to show the new number
								 */
								if (currNewUpdatesNo < i) {
									//add the current "show new updates" alert panel if not present
									if (newsFeedAlert == null) {
										newsFeedAlert = new NewFeedsAvailable(i, eventBus);
										newUpdatesPanel.add(newsFeedAlert);
									}
									else //update it otherwise
										newsFeedAlert.updateNewUpdatesNo(i);

									currNewUpdatesNo = i;
								}
							}
						}
						@Override
						public void onFailure(Throwable caught) {}
					});
				}				
			}
		});	
	}
	/**
	 * get the hashtagged feeds
	 * @param hashtag to look for
	 */
	private void showFeedsByHashtag(final String hashtag) {
		showLoader();
		newsService.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) { 
				doStopFeedsTimer(); 
			}
			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals(NewsConstants.TEST_USER)) {
					doStopFeedsTimer();
					doShowSessionExpired();				
				}  else {
					/**
					 * this check avoids the 2 tabs open in 2 different scope, if the previous tab was open at VRE Level and then antoher
					 * is open at infra level the first tab stops checking for updates
					 */
					if (result.getCurrentScope().compareTo(currentScope) == 0) { 
						newsService.getFeedsByHashtag(hashtag, new AsyncCallback<ArrayList<EnhancedFeed>>() {
							@Override
							public void onSuccess(ArrayList<EnhancedFeed> feeds) {
								filterPanelWrapper.setVisible(false);
								newsPanel.clear();
								if (feeds != null) {
									if (feeds.size() == 0) { 
										newsPanel.add(new ResultsFor("results for", hashtag));
										newsPanel.add(new HTML("<div class=\"nofeed-message\" style=\"height: 200px;\">" +
												"Sorry, looks like we found no updates with topic: " + hashtag +"</div>"));
										isFirstTweet = true;
									}
									else {
										newsPanel.setHeight("");
										newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
										newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
										newsPanel.add(new ResultsFor("results for", hashtag));
										for (EnhancedFeed feed : feeds) {
											newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus)); //in the view
										}
										if (feeds.size() < 5) {
											newsPanel.add(new Image(spacer));
										}									
										isFirstTweet = false;
									}
								} else {
									showProblems();
								}
							}
							@Override
							public void onFailure(Throwable caught) {
								showProblems();
							}
						});
					}				
				}
			}
		});	
	}

	/**
	 * Called when a user search something
	 */
	private void showFeedsSearch(final String query, final int from, final int to) {

		// show loader while waiting
		showLoader();

		// stop asking for feeds
		doStopFeedsTimer();
		
		newsService.getUserSettings(new AsyncCallback<UserSettings>() {
			@Override
			public void onFailure(Throwable caught) { 
			}
			@Override
			public void onSuccess(UserSettings result) {
				if (result.getUserInfo().getUsername().equals(NewsConstants.TEST_USER)) {
					doShowSessionExpired();				
				}  else {
					/**
					 * this check avoids the 2 tabs open in 2 different scope, if the previous tab was open at VRE Level and then antoher
					 * is open at infra level the first tab stops checking for updates
					 */
					if (result.getCurrentScope().compareTo(currentScope) == 0) { 
						newsService.getFeedsByQuery(query, from, to, new AsyncCallback<ArrayList<EnhancedFeed>>() {
							@Override
							public void onSuccess(ArrayList<EnhancedFeed> feeds) {
								filterPanelWrapper.setVisible(false);
								newsPanel.clear();
								if (feeds != null) {

									GWT.log("Retrieved " + feeds.size() + " hits for search.");
									if (feeds.size() == 0) { 
										newsPanel.add(new ResultsFor("results for", query));
										newsPanel.add(new HTML("<div class=\"nofeed-message\" style=\"height: 200px;\">" +
												"Sorry, looks like we found no match for: " + query +"</div>"));
									}
									else {
										newsPanel.setHeight("");
										newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
										newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
										newsPanel.add(new ResultsFor("results for", query));
										for (EnhancedFeed feed : feeds) {
											newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus)); //in the view
											// save them (they will be used when asking more feeds)
											allUpdates.add(feed);
										}
										if (feeds.size() < 5) {
											newsPanel.add(new Image(spacer));
										}			

										// add widget to lookup more feeds: if the size of the returned data is less
										// than the required disable this feature.
										if(feeds.size() == SEARCHED_FEEDS_TO_SHOW){
											showMoreUpdatesPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
											showMoreWidget = new ShowMoreFeeds(eventBus);
											showMoreUpdatesPanel.add(showMoreWidget);
											newsPanel.add(showMoreUpdatesPanel);
										}
									}
								} else {
									showProblems();
								}
							}
							@Override
							public void onFailure(Throwable caught) {
								showProblems();
							}
						});
					}				
				}
			}
		});
	}	

	/**
	 * called when a user click on the are new updates	
	 */
	protected void doShowCachedNewUpdates() {		
		newUpdatesPanel.clear(); //remove the alert panel
		newsFeedAlert = null; //reset the alert panel and other needed vars
		currNewUpdatesNo = 0;

		//need to put them in reverse order;
		for (int i = tempCacheNewUpdates.size(); i > 0; i--) {
			EnhancedFeed feed = tempCacheNewUpdates.get(i-1);
			final TweetTemplate tt = new TweetTemplate(myUserInfo, feed, eventBus, true);
			newsPanel.insert(tt, 0);  //insert in the view
			allUpdates.add(0, feed); //insert in the model

			//timer for the transition
			Timer t = new Timer() {
				@Override
				public void run() {
					tt.setcontentAreaStyle("visible");

					// alert the user-statistics portlet to update statistics in case
					// one or more feed belongs to user himself
					if(tt.isUser()){
						try{

							NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.postIncrement, "", Defaults.STRING_JSONIZER);

							int numComments = tt.numberOfComments();
							int numLikes = tt.numberOfLikes();

							for(int i = 0; i < numComments; i++)
								NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.commentsIncrement, "", Defaults.STRING_JSONIZER);

							for(int i = 0; i < numLikes; i++)
								NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.likesIncrement, "", Defaults.STRING_JSONIZER);

						}catch (PageBusAdapterException ex) {
							GWT.log(ex.toString());
						}
					} 
				}
			};
			t.schedule(100);
		}	
		//after that I remove the ($updatesNo) from Window Title
		String currTitle = Document.get().getTitle();
		Document.get().setTitle(currTitle.startsWith("(") ? currTitle.substring(4) : currTitle);	
	}

	/**
	 * used when adding directly a feed from the UI (IPC)
	 * @param userid
	 * @param fullName
	 * @param thumbURL
	 * @param description
	 */
	public void addJustAddedFeed(ClientFeed cFeed) {

		// build up the feed
		Feed feed = new Feed(
				cFeed.getKey(), 
				FeedType.SHARE, 
				cFeed.getUserid(), 
				cFeed.getTime(), 
				"", 
				cFeed.getUri(), 
				cFeed.getLinkUrlThumbnail(), 
				cFeed.getDescription(), 
				PrivacyLevel.CONNECTION, 
				cFeed.getFullName(),
				cFeed.getEmail(), 
				cFeed.getThumbnailURL(), 
				cFeed.getLinkTitle(), 
				cFeed.getLinkDescription(), 
				cFeed.getLinkHost());

		// set multi-attachments property
		boolean multiAttachments = cFeed.getAttachments() != null;
		feed.setMultiFileUpload(multiAttachments);

		//false because he could not have liked this yet and true because is the current user's 
		EnhancedFeed toAdd = new EnhancedFeed(feed, false, true); 

		// be careful when converting from List<> to ArrayList<> ...
		ArrayList<Attachment> attachments = multiAttachments ?  new ArrayList<Attachment>(cFeed.getAttachments()) : null;
		toAdd.setAttachments(attachments); 

		// build up the post template
		final TweetTemplate tt = new TweetTemplate(myUserInfo, toAdd, eventBus, true);
		if (isFirstTweet) {
			newsPanel.clear();
			newsPanel.add(new Image(spacer));
			isFirstTweet = false;
		}
		newsPanel.insert(tt, 0);

		Timer t = new Timer() {

			@Override
			public void run() {
				tt.setcontentAreaStyle("visible");

			}
		};

		// show after half a second
		t.schedule(500);	

		//insert it also in the model so that the user who created it do not get notified about this new update
		allUpdates.add(0, toAdd);	 

	}

	/**
	 * All Updates
	 */
	public void showAllUpdatesFeeds() {
		showLoader();
		newsService.getAllUpdateUserFeeds(NewsConstants.FEEDS_NO_PER_CATEGORY, new AsyncCallback<ArrayList<EnhancedFeed>>() {
			@Override
			public void onSuccess(ArrayList<EnhancedFeed> feeds) {
				newsPanel.clear();
				if (feeds != null) {
					if (feeds.size() == 0) { 
						if (!isInfrastructure) {
							newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
									"Sorry, looks like nobody shared anything yet. <br> " +
									"You may begin by sharing a news!</div>"));
						} else {
							newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
									"Sorry, looks like we've got nothing for you at the moment. <br> " +
									"You may begin by <a class=\"vrelink\" href=\"/group/data-e-infrastructure-gateway/join-new/\"><span class=\"important\">joining</span></a> some of the available " +
									"<br>"+vreLabel+"s.</div>"));
						}
						isFirstTweet = true;
					}
					else {
						newsPanel.setHeight("");
						newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
						newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
						for (EnhancedFeed feed : feeds) {
							newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus)); //in the view
							allUpdates.add(feed); //in the model
						}
						if (feeds.size() < 5) {
							newsPanel.add(new Image(spacer));
						} 
						//if you are showing more than feedsMaxPerCategory-1 feeds there is probably more
						if (feeds.size() >= NewsConstants.FEEDS_MAX_PER_CATEGORY-1 && (!isInfrastructure)) {
							GWT.log("Show MORE " + NewsConstants.FEEDS_NO_PER_CATEGORY);
							showMoreUpdatesPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
							showMoreWidget = new ShowMoreFeeds(eventBus);
							showMoreUpdatesPanel.add(showMoreWidget);
							newsPanel.add(showMoreUpdatesPanel);
						} 	
						isFirstTweet = false;
					}
				} else {
					showProblems();
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				newsPanel.clear();
				newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
						"Ops! There were problems while retrieving your feeds!. <br> " +
						"Please try again in a short while.</div>"));
			}
		});
	}
	/**
	 * called when a user scroll down the page to the bottom	
	 */
	protected void doShowMoreUpdates() {	

		showMoreUpdatesPanel.remove(0);
		loadingImage.getElement().getStyle().setMargin(10, Unit.PX);
		showMoreUpdatesPanel.add(loadingIcon);

		if(isSearch){

			GWT.log("Going to request more feeds for this search");

			// start position
			int start = allUpdates.size();

			GWT.log("StartingPoint = " + start);
			newsService.getFeedsByQuery(currentQuery, start, SEARCHED_FEEDS_TO_SHOW , new AsyncCallback<ArrayList<EnhancedFeed>>() {
				@Override
				public void onSuccess(ArrayList<EnhancedFeed> feeds){
					newsPanel.remove(showMoreUpdatesPanel);
					if (feeds != null) {
						GWT.log("There are " + feeds.size() + " more feeds");

						for (EnhancedFeed feed : feeds) {
							// avoid to insert same data
							if(!isFeedPresent(feed)){
								newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus)); //in the view
								allUpdates.add(feed);
							}
						}

						// clear panel
						showMoreUpdatesPanel.clear();
						
						// check if we can ask for other data
						if(feeds.size() == SEARCHED_FEEDS_TO_SHOW){
							GWT.log("It seems there are no more feeds for this query. Stop asking further");
							showMoreWidget = new ShowMoreFeeds(eventBus);
							showMoreUpdatesPanel.add(showMoreWidget);
							newsPanel.add(showMoreUpdatesPanel);
						}else{
							showMoreWidget = null;
						}
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					showMoreUpdatesPanel.clear();
					newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
							"Ops! There were problems while retrieving your feeds!. <br> " +
							"Please try again in a short while.</div>"));				
				}
			});
		}
		else{

			int from = (fromStartingPoint == null) ? allUpdates.size()+1 : fromStartingPoint;

			final int quantity = 10;
			GWT.log("StartingPoint = " + from);
			newsService.getMoreFeeds(from, quantity, new AsyncCallback<MoreFeedsBean>() {
				@Override
				public void onSuccess(MoreFeedsBean rangeFeeds) {
					newsPanel.remove(showMoreUpdatesPanel);
					if (rangeFeeds.getFeeds() != null) {
						fromStartingPoint = rangeFeeds.getLastReturnedFeedTimelineIndex();
						int c = 1;
						for (EnhancedFeed feed : rangeFeeds.getFeeds()) {
							if (!isFeedPresent(feed)) { //avoid possible duplicates
								newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus)); //in the view
								allUpdates.add(feed); //in the model
							}
							c++;
						}
						if (c >= quantity) { //there could be more feeds
							GWT.log("there could be more feeds");
							showMoreUpdatesPanel.clear();
							showMoreWidget = new ShowMoreFeeds(eventBus);
							showMoreUpdatesPanel.add(showMoreWidget);
							newsPanel.add(showMoreUpdatesPanel);
						}
					}
				}
				@Override
				public void onFailure(Throwable caught) {
					showMoreUpdatesPanel.clear();
					newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
							"Ops! There were problems while retrieving your feeds!. <br> " +
							"Please try again in a short while.</div>"));				
				}
			});	
		}
	}
	/**
	 * @param widget the widget to check
	 * @returnn true if the widget is in the visible part of the page
	 */
	private boolean isScrolledIntoView(Widget widget) {
		if (widget != null) {
			int docViewTop = Window.getScrollTop();
			int docViewBottom = docViewTop + Window.getClientHeight();
			int elemTop = widget.getAbsoluteTop();
			int elemBottom = elemTop + widget.getOffsetHeight();
			return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
		}		
		return false;
	}

	private boolean isFeedPresent(EnhancedFeed toCheck) {
		for (EnhancedFeed feed : allUpdates) {
			if (feed.getFeed().getKey().compareTo(toCheck.getFeed().getKey()) == 0)
				return true;
		}
		return false;
	}
	/**
	 * Only User Connections
	 */
	public void showOnlyConnectionsFeeds() {
		showLoader();
		newsService.getOnlyConnectionsUserFeeds(new AsyncCallback<ArrayList<EnhancedFeed>>() {
			@Override
			public void onSuccess(ArrayList<EnhancedFeed> feeds) {
				if (feeds != null) {
					newsPanel.clear();
					if (feeds.size() == 0) { 
						//TODO: provide actual link for Making some friends
						newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
								"Looks like we've got nothing for you at the moment. <br> " +
								"You may begin by <strong>adding</strong> some friend!</div>"));
						isFirstTweet = true;
					}
					else {
						newsPanel.setHeight("");
						newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
						newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
						for (EnhancedFeed feed : feeds) 
							newsPanel.add(new TweetTemplate(false, false, myUserInfo, feed, eventBus));
						if (feeds.size() < 5) {
							newsPanel.add(new Image(spacer));
						}
						isFirstTweet = false;
					}
				} else
					showProblems();				

			}

			@Override
			public void onFailure(Throwable caught) {
				loadingImage.setUrl(warning);				
				newsPanel.add(new HTML("Ops! There were problems while retrieving your feeds! Please try again in a short while"));
			}
		});
	}
	/**
	 * Only User Feeds
	 */
	public void showOnlyMyFeeds() {
		showLoader();
		newsService.getOnlyMyUserFeeds(new AsyncCallback<ArrayList<EnhancedFeed>>() {
			@Override
			public void onSuccess(ArrayList<EnhancedFeed> feeds) {
				if (feeds != null) {
					newsPanel.clear();
					if (feeds.size() == 0) { 
						newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
								"Looks like we've got nothing for you at the moment. <br> " +
								"You may begin by <strong>sharing</strong> an update!</div>"));
						isFirstTweet = true;
					}
					else {
						newsPanel.setHeight("");
						newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
						newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
						for (EnhancedFeed feed : feeds) 
							newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus));
						if (feeds.size() < 5) {
							newsPanel.add(new Image(spacer));
						}
						isFirstTweet = false;
					}
				} else
					showProblems();		

			}

			@Override
			public void onFailure(Throwable caught) {
				loadingImage.setUrl(warning);				
				newsPanel.add(new HTML("Ops! There were problems while retrieving your feeds! Please try again in a short while"));
			}
		});
	}
	/**
	 * Only User Liked Feeds
	 */
	public void showOnlyLikedFeeds() {
		showLoader();
		newsService.getOnlyLikedFeeds(new AsyncCallback<ArrayList<EnhancedFeed>>() {
			@Override
			public void onSuccess(ArrayList<EnhancedFeed> feeds) {
				if (feeds != null) {
					newsPanel.clear();
					if (feeds.size() == 0) { 
						newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
								"Looks like we've got nothing for you at the moment. <br> " +
								"Set an update as your <strong>favorite</strong> to see it here</div>"));
						isFirstTweet = true;
					}
					else {
						newsPanel.setHeight("");
						newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
						newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
						for (EnhancedFeed feed : feeds) 
							newsPanel.add(new TweetTemplate(false, showFeedTimelineSource, myUserInfo, feed, eventBus));
						if (feeds.size() < 5) {
							newsPanel.add(new Image(spacer));
						}
						isFirstTweet = false;
					}
				} else
					showProblems();		

			}

			@Override
			public void onFailure(Throwable caught) {
				loadingImage.setUrl(warning);				
				newsPanel.add(new HTML("Ops! There were problems while retrieving your feeds! Please try again in a short while"));
			}
		});
	}



	private void showLoader() {
		newsPanel.clear();
		newsPanel.setWidth("100%");
		newsPanel.setHeight("300px");
		newsPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		newsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		newsPanel.add(new Image(loading));
	}

	private void showProblems() {
		newsPanel.clear();
		newsPanel.add(new HTML("<div class=\"nofeed-message\">" +
				"Ops! There were problems while retrieving your feeds!. <br> " +
				"Looks like we are not able to communicate with the infrastructure,<br> (or your session expired)<br> please try again in a short while or refresh the page.</div>"));
	}

	private void doAddLike(final TweetTemplate owner, final String feedId) {
		newsService.like(feedId, owner.getMyFeedText(), owner.getMyFeedUserId(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Boolean result) {
				if (!result) {
					doShowSessionExpired();
				} else{

					// alert the User statistics portlet to increment the number of likes got
					if(owner.isUser()){
						try {
							NewsFeed.pageBusAdapter.PageBusPublish(
									PageBusEvents.likesIncrement
									, ""
									, Defaults.STRING_JSONIZER);
						} catch (PageBusAdapterException ex) {
							GWT.log(ex.toString());
						}
					}

				}
			}
		});		
	}

	protected void doUnLike(final TweetTemplate owner, String feedId) {
		newsService.unlike(feedId, owner.getMyFeedText(), owner.getMyFeedUserId(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {}
			@Override
			public void onSuccess(Boolean result) {	
				if (!result) {
					doShowSessionExpired();
				}else{

					// alert the User statistics portlet to decrement the number of likes got
					if(owner.isUser()){
						try {
							NewsFeed.pageBusAdapter.PageBusPublish(
									PageBusEvents.likesDecrement
									, ""
									, Defaults.STRING_JSONIZER);
						} catch (PageBusAdapterException ex) {
							GWT.log(ex.toString());
						}
					}

				}
			}
		});		

	}

	private void doShowSessionExpired() {
		GWT.runAsync(UserSelectionDialog.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				CheckSession.showLogoutDialog();
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}   
		});
	}

	private void doShowLikes(final String feedId) {
		GWT.runAsync(UserSelectionDialog.class, new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				final UserSelectionDialog dlg = new UserSelectionDialog("People who set this as Favorite", eventBus);
				dlg.center();
				dlg.show();		

				newsService.getAllLikesByFeed(feedId, new AsyncCallback<ArrayList<Like>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("People who liked this could not be retrieved: " + caught.getMessage());
					}

					@Override
					public void onSuccess(ArrayList<Like> result) {
						ArrayList<ItemSelectableBean> toShow = new ArrayList<ItemSelectableBean>();
						for (Like like : result) {
							toShow.add(new ItemSelectableBean(like.getUserid(), like.getFullName(), like.getThumbnailURL()));
						}
						eventBus.fireEvent(new UsersFetchedEvent(toShow));
					}
				});
			}
			public void onFailure(Throwable reason) {
				Window.alert("Could not load this component: " + reason.getMessage());
			}   
		});

	}

	private void doAddComment(final TweetTemplate owner, String text, HashSet<String> mentionedUsers) {
		newsService.comment(owner.getFeedKey(), text, mentionedUsers, owner.getMyFeedUserId(), owner.isAppFeed(), new AsyncCallback<OperationResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not deliver this comment: " + caught.getMessage());
			}
			@Override
			public void onSuccess(OperationResult result) {
				if (result != null) {
					if (!result.isSuccess()) {
						CheckSession.showLogoutDialog();
					}
					else {
						Comment comment = (Comment) result.getObject();
						owner.addComment(new SingleComment(comment, owner, (comment.getUserid().equals(myUserInfo.getUsername()))));
						owner.setCommentingDisabled(false);
						owner.updateCommentsNumberCount();
						owner.showAddCommentForm(false);

						if(owner.isUser()){
							// alert the User statistics portlet to increment the number of comments got 
							try {
								NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.commentsIncrement, "", Defaults.STRING_JSONIZER);
							} catch (PageBusAdapterException e) {
								GWT.log(e.toString());
							}
						}
					}
				} 
				else {
					Window.alert("Could not deliver this comment. Please try again in a short while.");
				}
			}
		});
	}

	private void doEditComment(final TweetTemplate owner, Comment edited, final HTMLPanel commentPanel) {
		newsService.editComment(edited, new AsyncCallback<OperationResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not edit this comment: " + caught.getMessage());
			}

			@Override
			public void onSuccess(OperationResult result) {
				if (result != null) {
					if (!result.isSuccess()) {
						CheckSession.showLogoutDialog();
					}
					else {
						Comment comment = (Comment) result.getObject();
						owner.updateSingleComment(comment, commentPanel);
						owner.setCommentingDisabled(false);
					}
				}
				else {
					Window.alert("Could not deliver this comment. Please try again in a short while.");
				}	
			}
		});


	}

	private void doShowComments(final TweetTemplate owner, final boolean commentForm2Add) {
		owner.showLoadingComments();
		newsService.getAllCommentsByFeed(owner.getFeedKey(), new AsyncCallback<ArrayList<Comment>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Comments could not be retrieved: " + caught.getMessage());				
			}

			@Override
			public void onSuccess(ArrayList<Comment> comments) {
				owner.clearComments();
				for (Comment comment :comments) 
					owner.addComment(new SingleComment(comment, owner,(comment.getUserid().equals(myUserInfo.getUsername()))));					
				owner.setCommentsFetched(true);
				if (commentForm2Add)
					owner.showAddCommentForm(false);
				owner.updateCommentsNumberCount();
				owner.showAddCommentForm(false);
			}
		});

	}


	protected void doDeleteComment(final TweetTemplate owner, String commentId) {
		newsService.deleteComment(commentId, owner.getFeedKey(), new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Comment could not be deleted: " + caught.getMessage());	

			}
			@Override
			public void onSuccess(Boolean result) {				
				if (result) {
					doShowComments(owner, false);
					owner.updateCommentsNumberCount();

					if(owner.isUser()){
						// alert the User statistics portlet to decrement the number of comments got 
						try {
							NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.commentsDecrement, "", Defaults.STRING_JSONIZER);
						} catch (PageBusAdapterException ex) {
							GWT.log(ex.toString());
						}
					}
				} else
					Window.alert("Comment could not be deleted, please try again in a short while.");	
			}
		});

	}

	private void doDeleteFeed(final TweetTemplate toDelete) {
		if (Window.confirm("Are you sure you want to delete this post?")) {
			newsService.deleteFeed(toDelete.getFeedKey(), new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Feed could not be deleted: " + caught.getMessage());					
				}

				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						toDelete.removeFromParent();

						if(toDelete.isUser()){
							try{
								// alert the User statistics portlet to decrement the number of user's posts
								NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.postDecrement, "", Defaults.STRING_JSONIZER);

								// alert the same portlet to decrement the number of likes/replies, if any
								int numComments = toDelete.numberOfComments();
								int numLikes = toDelete.numberOfLikes();

								for(int i = 0; i < numComments; i++)
									NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.commentsDecrement, "", Defaults.STRING_JSONIZER);

								for(int i = 0; i < numLikes; i++)
									NewsFeed.pageBusAdapter.PageBusPublish(PageBusEvents.likesDecrement, "", Defaults.STRING_JSONIZER);

							}catch (PageBusAdapterException ex) {
								GWT.log(ex.toString());
							}
						}
					} else
						Window.alert("Feed could not be deleted, please try again in a short while.");	
				}
			});
		}		
	}

	private void doShowFeed(final TweetTemplate toShow) {
		String feedKey = toShow.getFeedKey();
		Window.Location.assign(Window.Location.getHref() + ((Window.Location.getHref().contains("?")) ? "&oid="+feedKey : "?oid="+feedKey));
	}

	/**
	 * set the filter type status for automatic reloading of tweets
	 * @param currentFilter
	 */
	public void setCurrentFilter(FilterType currentFilter) {
		this.currentFilter = currentFilter;
	}
	/**
	 * 
	 * @return the current scope on the client
	 */
	public static String getCurrentScope() {
		return currentScope;
	}
}
