package org.gcube.portlet.user.userstatisticsportlet.client;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.ShowUserStatisticAction;
import org.gcube.portlet.user.userstatisticsportlet.client.events.PageBusEvents;
import org.gcube.portlet.user.userstatisticsportlet.client.events.ShowFeedsRelatedToUserStatisticsEvent;
import org.gcube.portlet.user.userstatisticsportlet.client.events.ShowFeedsRelatedToUserStatisticsEventHandler;
import org.gcube.portlet.user.userstatisticsportlet.client.resources.Images;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.ActivityWidget;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.StatisticWidget;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
/**
 * Panel to show received user's statistics
 * 
 * @author Costantino Perciante at ISTI-CNR
 */
public class StatisticsPanel extends Composite {

	/**
	 * Create a remote service proxy to talk to the server-side statistics service.
	 */
	private final UserStatisticsServiceAsync statisticsService = GWT.create(UserStatisticsService.class);

	/**
	 * Path of the image to be shown during loading
	 */
	public static final String imagePath = GWT.getModuleBaseURL() + "../images/statistics-loader.gif";

	/**
	 * tooltip for the above image
	 */
	private static final String ALERT_MESSAGE = "Information not available at the moment";

	/**
	 * Title of the page
	 */
	public static final String DISPLAY_NAME =  "Your Stats ";

	/**
	 * Labels
	 */
	private final static String ACTIVITY_LABEL = "Activity";
	private final static String LIKES_COMMENTS_GOT_LABEL = "Got";
	private final static String STORAGE_LABEL = "Space Used";
	private final static String PROFILE_STRENGTH_LABEL = "Profile Strength";
	private final static String SHOW_STATISTICS_OPTION_LABEL = "Show my statistics to VRE Members";

	/**
	 * Some tooltips
	 */
	private final static String TOOLTIP_ACTIVITY_ROOT_PROFILE = "Posts, likes, replies done during the last year";
	private final static String TOOLTIP_ACTIVITY_VRE = "Posts, likes, replies done in the last year in this VRE";
	private final static String TOOLTIP_GOT_ROOT_PROFILE = "Likes and post replies got during the last year";
	private final static String TOOLTIP_GOT_VRE = "Likes and post replies got during the last year in this VRE";
	private final static String TOOLTIP_INFRASTRUCTURE_SPACE = "Total amount of space used in the infrastructure";
	private final static String TOOLTIP_PROFILE_STRENGHT = "Profile strength evaluated taking into account contacts, professional summary and current position information";
	private final static String SHOW_STATISTICS_OPTION_TOOLTIP = "Show Statistics to members viewing your profile";

	/**
	 *  Specific tooltips (for the values)
	 */
	private final static String TOOLTIP_POSTS_DONE = "Posts done during the last year";
	private final static String TOOLTIP_POSTS_DONE_VRE = "Posts done during the last year in this VRE";
	private final static String TOOLTIP_LIKES_GOT = "Likes got during the last year";
	private final static String TOOLTIP_LIKES_GOT_VRE = "Likes got during the last year in this VRE";
	private final static String TOOLTIP_LIKES_DONE = "Likes done during the last year";
	private final static String TOOLTIP_LIKES_DONE_VRE = "Likes done during the last year in this VRE";
	private final static String TOOLTIP_REPLIES_DONE = "Post replies done during the last year";
	private final static String TOOLTIP_REPLIES_DONE_VRE = "Post replies done during the last year in this VRE";
	private final static String TOOLTIP_REPLIES_GOT = "Post replies got during the last year";
	private final static String TOOLTIP_REPLIES_GOT_VRE = "Post replies got during the last year in this VRE";

	/**
	 * improve profile hints
	 */
	public static String IMPROVE_PROFILE_HINT_MESSAGE_ROOT = "You can improve your profile strength by: <ul>"
			+ "<li> adding a job title, your current position or your professional summary;" 
			+ "<li> adding your contact information (facebook, linkedin, skype, google mail and so on)</ul>";

	/**
	 * improve profile hints
	 */
	public static final String IMPROVE_PROFILE_HINT_MESSAGE_PROFILE = "You can improve your profile strength by: <ul>"
			+ "<li> adding a job title, your current position or your professional summary;" 
			+ "<li> adding your contact information (facebook, linkedin, skype, google mail and so on)</ul>";

	/**
	 * profile button label
	 */
	private final static String IMPROVE_BUTTON_LABEL = "Improve";

	/**
	 * threshold for improving profile button
	 */
	private final static int profileImproveThreshold = 50;

	/**
	 * Information about the context of the running portlet
	 */
	private boolean isRoot;

	/**
	 * Image to be shown during loading
	 */
	private Image loadingImage;

	/**
	 * FlowPanel to contain the statistics
	 */
	private FlowPanel mainPanel = new FlowPanel();

	/**
	 * Since the number of likes/comments got can be manipulated, we need a class reference
	 */
	private ActivityWidget activityGot;

	/**
	 * Posts, comments, likes done
	 */
	private ActivityWidget activityDone;

	/**
	 * Number of written posts
	 */
	private long numberOfWrittenFeeds;

	/**
	 * Number of likes got
	 */
	private long numberOfLikesGot;

	/**
	 * Number of likes got
	 */
	private long numberOfCommentsGot;

	/**
	 * Pagebus to listen for events (coming from the news feed portlet)
	 */
	final public static PageBusAdapter pageBusAdapter = new PageBusAdapter();

	/**
	 * Handler Manager for internal events
	 */
	private final HandlerManager eventBus = new HandlerManager(null);

	/**
	 * Saved current user information bean
	 */
	UserInformation informationBeanRetrieved;

	public StatisticsPanel() {

		//init this object
		super();
		initWidget(mainPanel);

		// bind pagebus events/ internal events
		bind();

		// set style of the main panel
		mainPanel.setStyleName("user-stats-frame");

		//init image loader
		loadingImage = new Image(imagePath);
		loadingImage.setStyleName("loading-image-center");

		//show loader, waiting for the answer coming by the server
		showLoader();

		/* does the profile belong to someone? (when we are on a profile page of someone other, we need
		/ to retrieve the statistics of that person).. if it is null or empty, the current user's information 
		/ will be retrieved. 
		 */
		final String userid = getUserToShowId();

		// request user's information
		statisticsService.getUserSettings(userid, new AsyncCallback<UserInformation>() {

			@Override
			public void onFailure(Throwable arg0) {

				showError();

			}

			@Override
			public void onSuccess(final UserInformation information) {

				// remove loading image
				mainPanel.remove(loadingImage);

				// first of all check if the statistics can be shown to other people
				if(userid != null && !userid.equals(information.getAslSessionUsername()) && !information.isProfileShowable()){

					mainPanel.add(new HTML("Sorry but the user set statistics to private"));
					return;

				}

				// save it
				informationBeanRetrieved = information;

				// is a user profile page? or a vre/home one (this portlet can be also deployed in a vre)
				final boolean isProfilePage = isProfilePage();

				// check which kind of information we have to show
				isRoot = information.isRoot();

				if(!isRoot && !isProfilePage){

					// add the border to the panel and the VRE name (check for VRE name lenght)
					mainPanel.addStyleName("user-stats-frame-border");
					String nameToShow = DISPLAY_NAME + " in " + information.getActualVre();

					final HTML name = new HTML(nameToShow);	
					name.setTitle(DISPLAY_NAME + " in " + information.getActualVre());
					name.setStyleName("user-stats-title");
					mainPanel.add(name);

				}

				// save page landing
				if(information.getCurrentPageLanding() != null)
					IMPROVE_PROFILE_HINT_MESSAGE_ROOT += "<a href='" + information.getCurrentPageLanding() + "/profile"  + "'><b>Go to your profile</b></a>";

				// user image 
				Images image = GWT.create(Images.class);
				Image userImage = new Image(image.avatarLoader());

				// check if the user has an avatar
				if(information.getUrlAvatar() == null)
					userImage.setResource(image.avatarDefaultImage());
				else{

					userImage.setUrl(information.getUrlAvatar());

					// set the title
					if(userid != null)
						userImage.setTitle("User's avatar");
					else
						userImage.setTitle("Your current avatar");
				}

				// set the style for the user image
				userImage.setStyleName("user-image");

				// add image to mainPanel
				mainPanel.add(userImage);

				// feeds, posts, likes
				final StatisticWidget activityDoneWidgetContainer = new StatisticWidget(isRoot);
				activityDoneWidgetContainer.setHeader(ACTIVITY_LABEL);

				if(isRoot || isProfilePage)
					activityDoneWidgetContainer.setToolTip(TOOLTIP_ACTIVITY_ROOT_PROFILE);
				else
					activityDoneWidgetContainer.setToolTip(TOOLTIP_ACTIVITY_VRE);


				// add loading image that will be replaced by the incoming values
				Image postsLoader = new Image(imagePath);
				postsLoader.setStyleName("loading-image-center-small");
				activityDoneWidgetContainer.appendToPanel(postsLoader);

				// append widget
				mainPanel.add(activityDoneWidgetContainer);

				// likes & comments got
				final StatisticWidget activityGotWidgetContainer = new StatisticWidget(isRoot);
				activityGotWidgetContainer.setHeader(LIKES_COMMENTS_GOT_LABEL);

				if(isRoot || isProfilePage)
					activityGotWidgetContainer.setToolTip(TOOLTIP_GOT_ROOT_PROFILE);
				else
					activityGotWidgetContainer.setToolTip(TOOLTIP_GOT_VRE);

				// add loading image that will be replaced by the incoming values
				Image commentsLikesLoader = new Image(imagePath);
				commentsLikesLoader.setStyleName("loading-image-center-small");
				activityGotWidgetContainer.appendToPanel(commentsLikesLoader);

				// append widget
				mainPanel.add(activityGotWidgetContainer);

				// the storage and the profile strength(only in root)
				final StatisticWidget storage =  new StatisticWidget(isRoot);
				final StatisticWidget profileStrength = new StatisticWidget(isRoot);

				if(isRoot || isProfilePage){

					storage.setHeader(STORAGE_LABEL);
					storage.setToolTip(TOOLTIP_INFRASTRUCTURE_SPACE);

					// add loading image that will be replaced by the incoming values
					Image totalSpaceLoader = new Image(imagePath);
					totalSpaceLoader.setStyleName("loading-image-center-small");
					storage.appendToPanel(totalSpaceLoader);

					mainPanel.add(storage);

					profileStrength.setHeader(PROFILE_STRENGTH_LABEL);
					profileStrength.setToolTip(TOOLTIP_PROFILE_STRENGHT);

					// add loading image that will be replaced by the incoming values
					Image profileStrengthLoader = new Image(imagePath);
					profileStrengthLoader.setStyleName("loading-image-center-small");
					profileStrength.appendToPanel(profileStrengthLoader);

					// add to the panel 
					mainPanel.add(profileStrength);

					// async requests that must be performed in root context
					statisticsService.getTotalSpaceInUse(userid, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable arg0) {

							appendAlertIcon(storage);

						}

						@Override
						public void onSuccess(String spaceInUse) {

							if(spaceInUse == null){
								appendAlertIcon(storage);
							}else{
								storage.clearPanelValues();
								Button storageValue = new Button();
								storageValue.setType(ButtonType.LINK);
								storageValue.setText(spaceInUse);
								storageValue.addStyleName("buttons-statistics-disabled-events");
								storage.appendToPanel(storageValue);
							}
						}
					});

					statisticsService.getProfileStrength(userid, new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable arg0) {

							appendAlertIcon(profileStrength);

						}

						@Override
						public void onSuccess(Integer profileStrengthInt) {

							if(profileStrengthInt < 0){

								appendAlertIcon(profileStrength);

							}else{

								// clear panel
								profileStrength.clearPanelValues();
								Button profileStrengthLabel = new Button();
								profileStrengthLabel.setType(ButtonType.LINK);
								profileStrengthLabel.setText(profileStrengthInt + "%");
								profileStrengthLabel.addStyleName("buttons-statistics-disabled-events");
								profileStrength.appendToPanel(profileStrengthLabel);

								// in case too low information within the user profile
								if(profileStrengthInt < profileImproveThreshold  && information.isOwner()){

									// Show an alert block in which underline how he can improve the profile
									final AlertBlock improveProfileHint = new AlertBlock();
									improveProfileHint.setType(AlertType.INFO);
									improveProfileHint.addStyleName("improve-profile-hint-message");

									final Button improveProfileButton = new Button(IMPROVE_BUTTON_LABEL);
									improveProfileButton.setType(ButtonType.INFO);
									improveProfileButton.setTitle("Improve your profile");

									improveProfileButton.addClickHandler(new ClickHandler() {

										@Override
										public void onClick(ClickEvent arg0) {

											// set text according current url
											if(isProfilePage)
												improveProfileHint.setHTML(IMPROVE_PROFILE_HINT_MESSAGE_PROFILE);
											else
												improveProfileHint.setHTML(IMPROVE_PROFILE_HINT_MESSAGE_ROOT);
											mainPanel.add(improveProfileHint);

										}
									});

									profileStrengthLabel.setStyleName("statistic-value-inline");
									improveProfileButton.addStyleName("button-improve-profile");
									profileStrength.appendToPanel(improveProfileButton);
								}
							}
						}

					});

				}

				// retrieve other information about number of feeds and post replies/likes done and got
				statisticsService.getPostsStats(userid, new AsyncCallback<PostsStatsBean>(){

					public void onFailure(Throwable arg0) {

						appendAlertIcon(activityDoneWidgetContainer);
						appendAlertIcon(activityGotWidgetContainer);

					}

					public void onSuccess(PostsStatsBean postsBean) {

						// if there are no statistics
						if(postsBean == null){

							appendAlertIcon(activityDoneWidgetContainer);
							appendAlertIcon(activityGotWidgetContainer);
							return;

						}

						// update feeds number, comments and likes done
						activityDoneWidgetContainer.clearPanelValues();
						activityDone = new ActivityWidget();

						// set the event handler if needed
						if(!isProfilePage || getUserToShowId() == null)
							activityDone.setEventBus(eventBus);

						if(isRoot || isProfilePage)
							activityDone.setPosts(
									formattedNumbers(postsBean.getFeedsNumber()), 
									TOOLTIP_POSTS_DONE + " (" + postsBean.getFeedsNumber() + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									information.getCurrentPageLanding());
						else
							activityDone.setPosts(
									formattedNumbers(postsBean.getFeedsNumber()), 
									TOOLTIP_POSTS_DONE_VRE + " (" + postsBean.getFeedsNumber() + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									information.getCurrentPageLanding());

						numberOfWrittenFeeds = postsBean.getFeedsNumber();
						activityDoneWidgetContainer.appendToPanel(activityDone);

						if(isRoot || isProfilePage)
							activityDone.setLikes(
									formattedNumbers(postsBean.getLikesMade()), 
									TOOLTIP_LIKES_DONE + " (" + postsBean.getLikesMade() + ")",
									ShowUserStatisticAction.LIKES_MADE_BY_USER,
									information.getCurrentPageLanding());
						else
							activityDone.setLikes(
									formattedNumbers(postsBean.getLikesMade()), 
									TOOLTIP_LIKES_DONE_VRE + " (" + postsBean.getLikesMade() + ")",
									ShowUserStatisticAction.LIKES_MADE_BY_USER,
									information.getCurrentPageLanding());

						if(isRoot || isProfilePage)
							activityDone.setComments(
									formattedNumbers(postsBean.getCommentsMade()), 
									TOOLTIP_REPLIES_DONE + " (" + postsBean.getCommentsMade() + ")",
									ShowUserStatisticAction.COMMENTS_MADE_BY_USER,
									information.getCurrentPageLanding());
						else
							activityDone.setComments(
									formattedNumbers(postsBean.getCommentsMade()), 
									TOOLTIP_REPLIES_DONE_VRE +" (" + postsBean.getCommentsMade() + ")",
									ShowUserStatisticAction.COMMENTS_MADE_BY_USER,
									information.getCurrentPageLanding());

						activityDoneWidgetContainer.appendToPanel(activityDone);

						// updates comments and likes got
						activityGotWidgetContainer.clearPanelValues();
						activityGot = new ActivityWidget();

						if(!isProfilePage || getUserToShowId() == null)
							activityGot.setEventBus(eventBus);

						if(isRoot || isProfilePage)
							activityGot.setLikes(
									formattedNumbers(postsBean.getLikesReceived()), 
									TOOLTIP_LIKES_GOT+ " (" + postsBean.getLikesReceived() + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									information.getCurrentPageLanding());
						else
							activityGot.setLikes(
									formattedNumbers(postsBean.getLikesReceived()), 
									TOOLTIP_LIKES_GOT_VRE + " (" + postsBean.getLikesReceived() + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									information.getCurrentPageLanding());

						numberOfLikesGot = postsBean.getLikesReceived();

						if(isRoot || isProfilePage)
							activityGot.setComments(
									formattedNumbers(postsBean.getCommentsReceived()), 
									TOOLTIP_REPLIES_GOT + " (" + postsBean.getCommentsReceived() + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									information.getCurrentPageLanding());
						else
							activityGot.setComments(
									formattedNumbers(postsBean.getCommentsReceived()), 
									TOOLTIP_REPLIES_GOT_VRE + " (" + postsBean.getCommentsReceived() + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									information.getCurrentPageLanding());

						numberOfCommentsGot = postsBean.getCommentsReceived();
						activityGotWidgetContainer.appendToPanel(activityGot);
					}

				});

				// check if we need to show the checkbox to allow the user's profile owner to edit privacy options
				// If the user is visiting his profile from within a vre, the checkbox WON'T be shown.
				if(information.isOwner() && getUserToShowId() == null && isProfilePage){

					// add a checkbox with the settable privacy option
					CheckBox privacyOption = new CheckBox(SHOW_STATISTICS_OPTION_LABEL);
					privacyOption.setTitle(SHOW_STATISTICS_OPTION_TOOLTIP);
					privacyOption.setValue(information.isProfileShowable());
					privacyOption.addStyleName("privacy-checkbox-statistics-style");

					privacyOption.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {

							// set this privacy option
							statisticsService.setShowMyOwnStatisticsToOtherPeople(event.getValue(), new AsyncCallback<Void>(){

								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Void result) {
								}

							});

						}
					});

					mainPanel.add(privacyOption);
				}
			}
		});
	}

	/**
	 * Bind for events of increment/decrement of user's posts coming from the news-feed portlet
	 */
	private void bind() {

		try {

			// on statistic value click handler
			eventBus.addHandler(ShowFeedsRelatedToUserStatisticsEvent.TYPE, new ShowFeedsRelatedToUserStatisticsEventHandler() {

				@Override
				public void onShowRelatedFeeds(
						ShowFeedsRelatedToUserStatisticsEvent event) {

					ShowUserStatisticAction actionToTake = event.getAction();

					// get current url
					String currentUrl = Window.Location.getHref();

					// if it is a profile page, we have to move the user to the sitelandingpage
					if(isProfilePage()){
						currentUrl = event.getLandingPage();
					}

					String[] splittedUrl = currentUrl.split("\\?");

					Window.Location.assign(splittedUrl[0] + "?" + Encoder.encode(GCubeSocialNetworking.SHOW_STATISTICS_ACTION_OID) + "=" + Encoder.encode(actionToTake.toString()));

				}
			});  

			// increment post number
			pageBusAdapter.PageBusSubscribe(PageBusEvents.postIncrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.postIncrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){

						GWT.log("Increment number of post message received");

						numberOfWrittenFeeds ++;

						if(isRoot)
							activityDone.setPosts(
									formattedNumbers(numberOfWrittenFeeds), 
									TOOLTIP_POSTS_DONE + " (" + numberOfWrittenFeeds + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityDone.setPosts(
									formattedNumbers(numberOfWrittenFeeds), 
									TOOLTIP_POSTS_DONE_VRE + " (" + numberOfWrittenFeeds + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of written posts changed to " + numberOfWrittenFeeds);
					}
				}
			});

			// decrement post number
			pageBusAdapter.PageBusSubscribe(PageBusEvents.postDecrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.postDecrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){
						GWT.log("Decrement number of post message received");

						// they can't be less than zero...
						numberOfWrittenFeeds --;
						numberOfWrittenFeeds = numberOfWrittenFeeds < 0 ?  0: numberOfWrittenFeeds;

						if(isRoot)
							activityDone.setPosts(
									formattedNumbers(numberOfWrittenFeeds), 
									TOOLTIP_POSTS_DONE + " (" + numberOfWrittenFeeds + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityDone.setPosts(
									formattedNumbers(numberOfWrittenFeeds), 
									TOOLTIP_POSTS_DONE_VRE + " (" + numberOfWrittenFeeds + ")",
									ShowUserStatisticAction.POSTS_MADE_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of written posts changed to " + numberOfWrittenFeeds);
					}
				}});

			// increment likes got number
			pageBusAdapter.PageBusSubscribe(PageBusEvents.likesIncrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.likesIncrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){

						GWT.log("Increment number of likes received");

						numberOfLikesGot ++;

						if(isRoot)
							activityGot.setLikes(
									formattedNumbers(numberOfLikesGot), 

									TOOLTIP_LIKES_GOT + " (" + numberOfLikesGot + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityGot.setLikes(
									formattedNumbers(numberOfLikesGot), 
									TOOLTIP_LIKES_GOT_VRE + " (" + numberOfLikesGot + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of likes got changed to " + numberOfLikesGot);
					}
				}
			});

			// decrement likes got
			pageBusAdapter.PageBusSubscribe(PageBusEvents.likesDecrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.likesDecrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){

						GWT.log("Decrement number of likes received");

						// they can't be less than zero...
						numberOfLikesGot --;
						numberOfLikesGot = numberOfLikesGot < 0 ?  0: numberOfLikesGot;

						if(isRoot)
							activityGot.setLikes(
									formattedNumbers(numberOfLikesGot), 
									TOOLTIP_LIKES_GOT + " (" + numberOfLikesGot + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityGot.setLikes(
									formattedNumbers(numberOfLikesGot), 
									TOOLTIP_LIKES_GOT_VRE + " (" + numberOfLikesGot + ")",
									ShowUserStatisticAction.LIKES_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of likes got changed to " + numberOfLikesGot);
					}
				}
			});

			// increment comments got number
			pageBusAdapter.PageBusSubscribe(PageBusEvents.commentsIncrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.commentsIncrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){

						GWT.log("Increment number of comments received");

						numberOfCommentsGot ++;


						if(isRoot)
							activityGot.setComments(
									formattedNumbers(numberOfCommentsGot), 
									TOOLTIP_REPLIES_GOT  + " (" + numberOfCommentsGot + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityGot.setComments( 
									formattedNumbers(numberOfCommentsGot), 
									TOOLTIP_REPLIES_GOT_VRE + " (" + numberOfCommentsGot + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of comments got changed to " + numberOfCommentsGot);
					}
				}
			});

			// decrement comments got
			pageBusAdapter.PageBusSubscribe(PageBusEvents.commentsDecrement, null, null, null, null);

			pageBusAdapter.addPageBusSubscriptionCallbackListener(new PageBusListener(){

				@Override
				public String getName() {
					return PageBusEvents.commentsDecrement;
				}

				@Override
				public void onPageBusSubscriptionCallback(PageBusEvent event) {

					if(event.getSubject().equals(this.getName())){

						GWT.log("Decrement number of comments received");

						// they can't be less than zero...
						numberOfCommentsGot --;
						numberOfCommentsGot = numberOfCommentsGot < 0 ?  0: numberOfCommentsGot;
						if(isRoot)
							activityGot.setComments(
									formattedNumbers(numberOfCommentsGot), 
									TOOLTIP_REPLIES_GOT  + " (" + numberOfCommentsGot + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());
						else
							activityGot.setComments(
									formattedNumbers(numberOfCommentsGot), 						
									TOOLTIP_REPLIES_GOT_VRE + " (" + numberOfCommentsGot + ")",
									ShowUserStatisticAction.COMMENTS_GOT_BY_USER,
									informationBeanRetrieved.getCurrentPageLanding());

						GWT.log("Number of comments got changed to " + numberOfCommentsGot);
					}
				}
			});

			GWT.log("Subscriptions ok");

		} catch (PageBusAdapterException e) {
			GWT.log(e.toString());
		}

	}

	/**
	 * Show loading image
	 */
	private void showLoader() {
		mainPanel.clear();
		mainPanel.add(loadingImage);
	}

	/**
	 * Error when is not possible to receive data or the received data is null
	 */
	private void showError() {
		mainPanel.clear();
		HTML messageError = new HTML(
				"Sorry but it is not possible to retrieve your statistics at the moment. Retry later."
				);
		messageError.setStyleName("error-msg");
		mainPanel.add(messageError);
	}

	/**
	 * Add alert icon/message for this statistic
	 * @param w
	 */
	private void appendAlertIcon(StatisticWidget w) {

		w.clearPanelValues();
		Button alert = new Button();
		alert.setType(ButtonType.LINK);
		alert.setIcon(IconType.BAN_CIRCLE);
		alert.setTitle(ALERT_MESSAGE);
		alert.addStyleName("alert-icon-center");
		w.appendToPanel(alert);

	}

	/**
	 * Format a given value and append k, M, G
	 * @param value
	 * @return
	 */
	private String formattedNumbers(long value){

		String formattedString = null;

		double v = value;
		double k = (double)value/1000.0;
		double m = (double)value/1_000_000.0;
		double g = (double)value/1_000_000_000.0;

		NumberFormat dec = NumberFormat.getFormat("###.###");

		if ( g >= 1.0 ) {
			formattedString = dec.format(g).concat("G");
		} else if ( m >= 1.0 ) {
			formattedString = dec.format(m).concat("M");
		} else if ( k >= 1.0 ) {
			formattedString = dec.format(k).concat("K");
		} else {
			formattedString = dec.format(v).concat("");
		}

		return formattedString;
	}

	/**
	 * decode the userid from the location param
	 * @return the decoded (base64) userid
	 */
	public static String getUserToShowId() {
		String encodedOid = Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID);
		if (Window.Location.getParameter(encodedOid) == null)
			return null;
		String encodedUserId = Window.Location.getParameter(encodedOid);
		return Encoder.decode(encodedUserId);
	}

	/**
	 * The user-statistics can be deployed in a vre, within the home or in a profile page
	 * @return true if the current page is a profile page, false otherwise
	 */
	private boolean isProfilePage() {
		return Window.Location.getHref().endsWith("profile")
				|| Window.Location.getHref().contains("profile?");
	}
}
