package org.gcube.portlet.user.userstatisticsportlet.client;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;

import org.gcube.portlet.user.userstatisticsportlet.client.resources.Images;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.CommentsAndLikesWidget;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.StatisticWidget;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	 * alert icon to be show if some statistic is not available
	 */
	public static final String alertIconPath = GWT.getModuleBaseURL() + "../images/icon-alert.png";

	/**
	 * tooltip for the above image
	 */
	private static final String ALERT_MESSAGE = "Information not available at the moment.";

	/**
	 * Title of the page
	 */
	public static final String DISPLAY_NAME =  "Your Stats ";

	/**
	 * Labels
	 */
	private final static String POSTS_LABEL = "Posts";
	private final static String STORAGE_LABEL = "Total Space Used";
	private final static String LIKES_COMMENTS_LABEL = "You got";
	private final static String PROFILE_STRENGTH_LABEL = "Profile Strength";

	/**
	 * profile url
	 */
	private final static String profileAccount = "/group/data-e-infrastructure-gateway/profile";

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
	 * Since the number of feeds(posts) can be manipulated when the user posts, we need a class reference
	 */
	private Label numberOfFeedsLabel;

	/**
	 * Since the number of likes/comments got can be manipulated, we need a class reference
	 */
	private CommentsAndLikesWidget content;

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

	public StatisticsPanel() {

		//init this object
		super();
		initWidget(mainPanel);

		// bind pagebus events
		bind();

		// set style of the main panel
		mainPanel.setStyleName("user-stats-frame");

		//init image loader
		loadingImage = new Image(imagePath);
		loadingImage.setStyleName("loading-image-center");

		//show loader, waiting for the answer coming by the server
		showLoader();

		// request user's information
		statisticsService.getUserSettings(new AsyncCallback<UserInformation>() {

			@Override
			public void onFailure(Throwable arg0) {

				showError();

			}

			@Override
			public void onSuccess(UserInformation information) {

				// remove loading image
				mainPanel.remove(loadingImage);

				// check which kind of information we have to show
				isRoot = information.isRoot();

				if(!isRoot){

					// add the border to the panel and the VRE name (check for VRE name lenght)
					mainPanel.addStyleName("user-stats-frame-border");
					String nameToShow = DISPLAY_NAME + " in " + information.getActualVre();

					// cut it if it's too long
					nameToShow = nameToShow.length() > 30 ? 
							nameToShow.substring(0, 27) + "..." : nameToShow;
					final HTML name = new HTML(nameToShow);	
					name.setTitle(DISPLAY_NAME + " in " + information.getActualVre());
					name.setStyleName("user-stats-title");
					mainPanel.add(name);

				}

				// user image 
				Images image = GWT.create(Images.class);
				Image userImage = new Image(image.avatarLoader());

				// check if the user has an avatar
				if(information.getUrlAvatar() == null)
					userImage.setResource(image.avatarDefaultImage());
				else
					userImage.setUrl(information.getUrlAvatar());

				// set the style for the user image
				userImage.setStyleName("user-image");

				// set the right margin according isRoot variable value
				if(isRoot)
					userImage.addStyleName("user-image-margin-right-root");
				else
					userImage.addStyleName("user-image-margin-right-vre");

				// set url to change avatar
				final String urlAccount = information.getAccountURL();

				if(urlAccount != null){
					userImage.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent ev) {
							Window.Location.assign(urlAccount);
						}
					});
					userImage.setTitle("Edit your avatar");
					userImage.addStyleName("user-image-editable");
				}

				// add image to mainPanel
				mainPanel.add(userImage);

				// feeds
				final StatisticWidget feeds = new StatisticWidget(isRoot);
				feeds.setHeader(POSTS_LABEL);

				if(isRoot)
					feeds.setToolTip("Your posts during the last year.");
				else
					feeds.setToolTip("Your posts during the last year in this VRE.");


				// add loading image that will be replaced by the incoming values
				Image postsLoader = new Image(imagePath);
				postsLoader.setStyleName("loading-image-center-small");
				feeds.appendToPanel(postsLoader);

				// append widget
				mainPanel.add(feeds);

				// likes & comments
				final StatisticWidget likesAndComments = new StatisticWidget(isRoot);
				likesAndComments.setHeader(LIKES_COMMENTS_LABEL);

				if(isRoot)
					likesAndComments.setToolTip("Likes and post replies you got during the last year.");
				else
					likesAndComments.setToolTip("Likes and post replies you got during the last year in this VRE.");

				// add loading image that will be replaced by the incoming values
				Image commentsLikesLoader = new Image(imagePath);
				commentsLikesLoader.setStyleName("loading-image-center-small");
				likesAndComments.appendToPanel(commentsLikesLoader);

				// append widget
				mainPanel.add(likesAndComments);

				// the storage and the profile strength(only in root)
				final StatisticWidget storage =  new StatisticWidget(isRoot);
				final StatisticWidget profileStrength = new StatisticWidget(isRoot);

				if(isRoot){

					storage.setHeader(STORAGE_LABEL);
					storage.setToolTip("Total amount of space used in the infrastructure.");

					// add loading image that will be replaced by the incoming values
					Image totalSpaceLoader = new Image(imagePath);
					totalSpaceLoader.setStyleName("loading-image-center-small");
					storage.appendToPanel(totalSpaceLoader);

					mainPanel.add(storage);

					profileStrength.setHeader(PROFILE_STRENGTH_LABEL);
					profileStrength.setToolTip("Your profile strength.");

					// add loading image that will be replaced by the incoming values
					Image profileStrengthLoader = new Image(imagePath);
					profileStrengthLoader.setStyleName("loading-image-center-small");
					profileStrength.appendToPanel(profileStrengthLoader);

					// add to the panel 
					mainPanel.add(profileStrength);

					// async requests that must be performed in root context
					statisticsService.getTotalSpaceInUse(new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable arg0) {

							appendAlertIcon(storage);

						}

						@Override
						public void onSuccess(String spaceInUse) {

							if(spaceInUse == null){
								appendAlertIcon(storage);
								return;
							}

							storage.clearPanelValues();
							Label storageValue = new Label(spaceInUse);
							storageValue.setStyleName("statistic-value");
							storage.appendToPanel(storageValue);

						}
					});

					statisticsService.getProfileStrength(new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable arg0) {

							appendAlertIcon(profileStrength);

						}

						@Override
						public void onSuccess(Integer profileStrengthInt) {

							if(profileStrengthInt < 0){

								appendAlertIcon(profileStrength);
								return;

							}

							// clear panel
							profileStrength.clearPanelValues();
							final Label profileStrengthLabel = new Label(profileStrengthInt + "%");
							profileStrengthLabel.setStyleName("statistic-value");

							profileStrength.appendToPanel(profileStrengthLabel);

							// in case too low information within the user profile
							if(profileStrengthInt < profileImproveThreshold){

								final Button improveProfileButton = new Button(IMPROVE_BUTTON_LABEL);
								improveProfileButton.setTitle("Improve your profile.");

								improveProfileButton.addClickHandler(new ClickHandler() {

									@Override
									public void onClick(ClickEvent arg0) {

										// redirect
										Window.Location.assign(profileAccount);

									}
								});

								profileStrengthLabel.setStyleName("statistic-value-inline");
								improveProfileButton.setStyleName("button-improve-profile");
								profileStrength.appendToPanel(improveProfileButton);
							}

						}

					});

				}

				// retrieve othe information about number of feeds and post replies/likes
				statisticsService.getPostsStats(new AsyncCallback<PostsStatsBean>(){

					public void onFailure(Throwable arg0) {

						appendAlertIcon(feeds);
						appendAlertIcon(likesAndComments);

					}

					public void onSuccess(PostsStatsBean postsBean) {

						// if there are no statistics
						if(postsBean == null){

							appendAlertIcon(feeds);
							appendAlertIcon(likesAndComments);
							return;

						}

						// update feeds number
						feeds.clearPanelValues();
						numberOfFeedsLabel = new Label(formattedNumbers(postsBean.getFeedsNumber()));
						if(isRoot)
							numberOfFeedsLabel.setTitle("Your posts during the last year (" + postsBean.getFeedsNumber() + ")."); 
						else
							numberOfFeedsLabel.setTitle("Your posts during the last year in this VRE (" + postsBean.getFeedsNumber() + ").");

						numberOfWrittenFeeds = postsBean.getFeedsNumber();
						numberOfFeedsLabel.setStyleName("statistic-value");
						feeds.appendToPanel(numberOfFeedsLabel);

						// updates comments and likes
						likesAndComments.clearPanelValues();
						content = new CommentsAndLikesWidget();

						String urlLikesIcon = GWT.getModuleBaseURL() + "../images/star_blue.png";
						if(isRoot)
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(postsBean.getLikesReceived()), 
									"Likes you got during the last year.",
									"Likes you got during the last year (" + postsBean.getLikesReceived() + ").");
						else
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(postsBean.getLikesReceived()), 
									"Likes you got during the last year in this VRE",
									"Likes you got during the last year in this VRE (" + postsBean.getLikesReceived() + ").");

						numberOfLikesGot = postsBean.getLikesReceived();

						String urlCommentsIcon = GWT.getModuleBaseURL() + "../images/comment_edit.png";
						if(isRoot)
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(postsBean.getCommentsReceived()), 
									"Post replies you got during the last year.",
									"Post replies you got during the last year (" + postsBean.getCommentsReceived() + ").");
						else
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(postsBean.getCommentsReceived()), 
									"Post replies you got during the last year in this VRE.",
									"Post replies you got during the last year in this VRE (" + postsBean.getCommentsReceived() + ").");

						numberOfCommentsGot = postsBean.getCommentsReceived();
						likesAndComments.appendToPanel(content);
					}

				});

			}
		});

	}

	/**
	 * Bind for events of increment/decrement of user's posts coming from the news-feed portlet
	 */
	private void bind() {

		try {
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
						numberOfFeedsLabel.setText(formattedNumbers(numberOfWrittenFeeds));
						if(isRoot)
							numberOfFeedsLabel.setTitle("Your posts during the last year (" + numberOfWrittenFeeds + ")."); 
						else
							numberOfFeedsLabel.setTitle("Your posts during the last year in this VRE (" + numberOfWrittenFeeds + ").");
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
						numberOfFeedsLabel.setText(formattedNumbers(numberOfWrittenFeeds));
						if(isRoot)
							numberOfFeedsLabel.setTitle("Your posts during the last year (" + numberOfWrittenFeeds + ")."); 
						else
							numberOfFeedsLabel.setTitle("Your posts during the last year in this VRE (" + numberOfWrittenFeeds + ").");
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
						String urlLikesIcon = GWT.getModuleBaseURL() + "../images/star_blue.png";

						if(isRoot)
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(numberOfLikesGot), 
									"Likes you got during the last year.",
									"Likes you got during the last year (" + numberOfLikesGot + ").");
						else
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(numberOfLikesGot), 
									"Likes you got during the last year in this VRE",
									"Likes you got during the last year in this VRE (" + numberOfLikesGot + ").");



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
						String urlLikesIcon = GWT.getModuleBaseURL() + "../images/star_blue.png";

						if(isRoot)
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(numberOfLikesGot), 
									"Likes you got during the last year.",
									"Likes you got during the last year (" + numberOfLikesGot + ").");
						else
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(numberOfLikesGot), 
									"Likes you got during the last year in this VRE",
									"Likes you got during the last year in this VRE (" + numberOfLikesGot + ").");

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

						String urlCommentsIcon = GWT.getModuleBaseURL() + "../images/comment_edit.png";

						if(isRoot)
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(numberOfCommentsGot), 
									"Post replies you got during the last year.",
									"Post replies you got during the last year (" + numberOfCommentsGot + ").");
						else
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(numberOfCommentsGot), 
									"Post replies you got during the last year in this VRE.",
									"Post replies you got during the last year in this VRE (" + numberOfCommentsGot + ").");

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
						String urlCommentsIcon = GWT.getModuleBaseURL() + "../images/comment_edit.png";
						if(isRoot)
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(numberOfCommentsGot), 
									"Post replies you got during the last year.",
									"Post replies you got during the last year (" + numberOfCommentsGot + ").");
						else
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(numberOfCommentsGot), 
									"Post replies you got during the last year in this VRE.",
									"Post replies you got during the last year in this VRE (" + numberOfCommentsGot + ").");

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
		Image alert = new Image(alertIconPath);
		alert.setTitle(ALERT_MESSAGE);
		alert.setStyleName("alert-icon-center");
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
}
