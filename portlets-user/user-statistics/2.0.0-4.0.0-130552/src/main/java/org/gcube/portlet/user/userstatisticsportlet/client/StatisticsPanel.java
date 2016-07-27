package org.gcube.portlet.user.userstatisticsportlet.client;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusEvent;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusListener;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portlet.user.userstatisticsportlet.client.resources.Images;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.CommentsAndLikesWidget;
import org.gcube.portlet.user.userstatisticsportlet.client.ui.StatisticWidget;
import org.gcube.portlet.user.userstatisticsportlet.shared.PostsStatsBean;
import org.gcube.portlet.user.userstatisticsportlet.shared.UserInformation;

import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	private final static String STORAGE_LABEL = "Space Used";
	private final static String LIKES_COMMENTS_LABEL = "Got";
	private final static String PROFILE_STRENGTH_LABEL = "Profile Strength";

	/**
	 * improve profile hints
	 */
	public static String IMPROVE_PROFILE_HINT_MESSAGE_ROOT = "You can improve your profile strength by: <ul>"
			+ "<li> adding a job title, your current position or your professional summary;" 
			+ "<li> adding your contact information (facebook, linkedin, skype, google mail and so on).</ul>";

	/**
	 * improve profile hints
	 */
	public static final String IMPROVE_PROFILE_HINT_MESSAGE_PROFILE = "You can improve your profile strength by: <ul>"
			+ "<li> adding a job title, your current position or your professional summary;" 
			+ "<li> adding your contact information (facebook, linkedin, skype, google mail and so on).</ul>";

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

					mainPanel.add(new HTML("Sorry but the user set his Statistics to private."));
					return;

				}

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

				// feeds
				final StatisticWidget feeds = new StatisticWidget(isRoot);
				feeds.setHeader(POSTS_LABEL);

				if(isRoot || isProfilePage)
					feeds.setToolTip("Posts during the last year.");
				else
					feeds.setToolTip("Posts during the last year in this VRE.");


				// add loading image that will be replaced by the incoming values
				Image postsLoader = new Image(imagePath);
				postsLoader.setStyleName("loading-image-center-small");
				feeds.appendToPanel(postsLoader);

				// append widget
				mainPanel.add(feeds);

				// likes & comments
				final StatisticWidget likesAndComments = new StatisticWidget(isRoot);
				likesAndComments.setHeader(LIKES_COMMENTS_LABEL);

				if(isRoot || isProfilePage)
					likesAndComments.setToolTip("Likes and post replies got during the last year.");
				else
					likesAndComments.setToolTip("Likes and post replies got during the last year in this VRE.");

				// add loading image that will be replaced by the incoming values
				Image commentsLikesLoader = new Image(imagePath);
				commentsLikesLoader.setStyleName("loading-image-center-small");
				likesAndComments.appendToPanel(commentsLikesLoader);

				// append widget
				mainPanel.add(likesAndComments);

				// the storage and the profile strength(only in root)
				final StatisticWidget storage =  new StatisticWidget(isRoot);
				final StatisticWidget profileStrength = new StatisticWidget(isRoot);

				if(isRoot || isProfilePage){

					storage.setHeader(STORAGE_LABEL);
					storage.setToolTip("Total amount of space used in the infrastructure.");

					// add loading image that will be replaced by the incoming values
					Image totalSpaceLoader = new Image(imagePath);
					totalSpaceLoader.setStyleName("loading-image-center-small");
					storage.appendToPanel(totalSpaceLoader);

					mainPanel.add(storage);

					profileStrength.setHeader(PROFILE_STRENGTH_LABEL);
					profileStrength.setToolTip("Profile strength.");

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
								return;
							}

							storage.clearPanelValues();
							Label storageValue = new Label(spaceInUse);
							storageValue.setStyleName("statistic-value");
							storage.appendToPanel(storageValue);

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
								return;

							}

							// clear panel
							profileStrength.clearPanelValues();
							final Label profileStrengthLabel = new Label(profileStrengthInt + "%");
							profileStrengthLabel.setStyleName("statistic-value");

							profileStrength.appendToPanel(profileStrengthLabel);

							// in case too low information within the user profile
							if(profileStrengthInt < profileImproveThreshold  && information.isOwner()){

								// Show an alert block in which underline how he can improve the profile
								final AlertBlock improveProfileHint = new AlertBlock();
								improveProfileHint.setType(AlertType.INFO);
								improveProfileHint.addStyleName("improve-profile-hint-message");

								final Button improveProfileButton = new Button(IMPROVE_BUTTON_LABEL);
								improveProfileButton.setType(ButtonType.INFO);
								improveProfileButton.setTitle("Improve your profile.");

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

					});

				}

				// retrieve othe information about number of feeds and post replies/likes
				statisticsService.getPostsStats(userid, new AsyncCallback<PostsStatsBean>(){

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
						if(isRoot || isProfilePage)
							numberOfFeedsLabel.setTitle("Posts during the last year (" + postsBean.getFeedsNumber() + ")."); 
						else
							numberOfFeedsLabel.setTitle("Posts during the last year in this VRE (" + postsBean.getFeedsNumber() + ").");

						numberOfWrittenFeeds = postsBean.getFeedsNumber();
						numberOfFeedsLabel.setStyleName("statistic-value");
						feeds.appendToPanel(numberOfFeedsLabel);

						// updates comments and likes
						likesAndComments.clearPanelValues();
						content = new CommentsAndLikesWidget();

						String urlLikesIcon = GWT.getModuleBaseURL() + "../images/star_blue.png";
						if(isRoot || isProfilePage)
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(postsBean.getLikesReceived()), 
									"Likes got during the last year.",
									"Likes got during the last year (" + postsBean.getLikesReceived() + ").");
						else
							content.setLikes(
									urlLikesIcon, 
									formattedNumbers(postsBean.getLikesReceived()), 
									"Likes got during the last year in this VRE",
									"Likes got during the last year in this VRE (" + postsBean.getLikesReceived() + ").");

						numberOfLikesGot = postsBean.getLikesReceived();

						String urlCommentsIcon = GWT.getModuleBaseURL() + "../images/comment_edit.png";
						if(isRoot || isProfilePage)
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(postsBean.getCommentsReceived()), 
									"Post replies got during the last year.",
									"Post replies got during the last year (" + postsBean.getCommentsReceived() + ").");
						else
							content.setComments(
									urlCommentsIcon, 
									formattedNumbers(postsBean.getCommentsReceived()), 
									"Post replies got during the last year in this VRE.",
									"Post replies got during the last year in this VRE (" + postsBean.getCommentsReceived() + ").");

						numberOfCommentsGot = postsBean.getCommentsReceived();
						likesAndComments.appendToPanel(content);
					}

				});

				// check if we need to show the checkbox to allow the user's profile owner to edit privacy options
				// If the user is visiting his profile from within a vre, the checkbox WON'T be shown.
				if(information.isOwner() && getUserToShowId() == null && isProfilePage){

					// add a checkbox with the settable privacy option
					CheckBox privacyOption = new CheckBox("Show my statistics to VRE Members");
					privacyOption.setTitle("Show Statistics to members viewing your profile");
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
