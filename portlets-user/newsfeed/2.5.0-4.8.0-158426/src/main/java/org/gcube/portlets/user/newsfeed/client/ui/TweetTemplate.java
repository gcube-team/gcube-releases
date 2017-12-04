/**
 * 
 */
package org.gcube.portlets.user.newsfeed.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.Attachment;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.newsfeed.client.event.AddLikeEvent;
import org.gcube.portlets.user.newsfeed.client.event.DeleteFeedEvent;
import org.gcube.portlets.user.newsfeed.client.event.OpenFeedEvent;
import org.gcube.portlets.user.newsfeed.client.event.SeeCommentsEvent;
import org.gcube.portlets.user.newsfeed.client.event.SeeLikesEvent;
import org.gcube.portlets.user.newsfeed.client.event.UnLikeEvent;
import org.gcube.portlets.user.newsfeed.client.panels.NewsFeedPanel;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.EnhancedImage;
import org.gcube.portlets.widgets.imagepreviewerwidget.client.ui.Carousel;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Massimiliano Assante at ISTI-CNR
 * @author Costantino Perciante at ISTI-CNR
 *
 */
public class TweetTemplate extends Composite {

	private static TweetTemplateUiBinder uiBinder = GWT
			.create(TweetTemplateUiBinder.class);

	interface TweetTemplateUiBinder extends UiBinder<Widget, TweetTemplate> {
	}


	public static final String loading = GWT.getModuleBaseURL() + "../images/loading-comments.gif";
	
	private static final int MAX_SHOWTEXT_LENGTH = 256;

	private EnhancedFeed myFeed;
	private UserInfo myUserInfo;

	private HandlerManager eventBus;
	private ArrayList<SingleComment> myComments;
	private boolean commentingDisabled = false;
	private boolean commentsFetched = false;
	private int totalComments = 0;
	private HTML showAllComments = new HTML();
	private boolean isAppFeed = false;
	private HTML submitCommentPreloader = new HTML("<div class=\"more-comment\"><img style=\"padding-right:15px;\"src=\""+ loading +"\" /></div>");
	private TweetTemplate myInstance;

	// Carousel from the image-previewer widget
	private Carousel carousel;

	/**
	 * tell if this tweet is belonging to the current user
	 */
	private boolean isUsers = false;

	@UiField
	HTML contentArea;
	@UiField
	HTML seeMore;
	@UiField
	HTML timeArea;
	@UiField
	HTML likeArea;
	@UiField
	HTML commentArea;
	@UiField
	HTML sharePostArea;
	@UiField
	Image avatarImage;
	@UiField
	AvatarReplacement avatarReplacement;
	@UiField
	HTMLPanel mainHTML;
	@UiField
	Button likesNo;
	@UiField
	Button commentsNo;
	@UiField
	VerticalPanel commentsPanel;
	@UiField 
	HTML closeImage;
	@UiField 
	HTML openImage;
	@UiField 
	HTML vreSource;	
	@UiField 
	VerticalPanel previewPanel;
	@UiField 
	Placeholder attachmentPreviewPanel;
	@UiField
	Label messageSeparator;
	/**
	 *  used when fetching tweets from server
	 * @param myUserInfo
	 * @param myFeed
	 * @param isUsers
	 * @param displaySingle tells if you're displaying a single fedd or  not
	 * @param eventBus
	 */
	public TweetTemplate(boolean displaySingle, boolean showTimelineSource, UserInfo myUserInfo, EnhancedFeed myFeed, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		commentsNo.getElement().getStyle().setPaddingTop(0, Unit.PX);
		likesNo.getElement().getStyle().setPaddingTop(0, Unit.PX);
		likesNo.getElement().getStyle().setPaddingRight(2, Unit.PX);
		commentsNo.getElement().getStyle().setPaddingRight(2, Unit.PX);
		likesNo.getElement().getStyle().setPaddingLeft(2, Unit.PX);
		commentsNo.getElement().getStyle().setPaddingLeft(2, Unit.PX);
		
		myInstance = this;
		this.myUserInfo = myUserInfo;
		this.vreSource.setVisible(false);
		this.myFeed = myFeed;
		isAppFeed = myFeed.getFeed().isApplicationFeed();
		Feed feed = myFeed.getFeed();
		this.eventBus = eventBus;
		this.isUsers = myFeed.isUsers();
		this.carousel = new Carousel();
		myComments = new ArrayList<SingleComment>();

		if (isUsers || myUserInfo.isAdmin()) {
			closeImage.setStyleName("closeImage");
			closeImage.setTitle(myUserInfo.isAdmin() ? "Delete (Administrator Mode)" : "delete");
		} else {
			closeImage.removeFromParent();
		}

		// if there is one attachment or a link preview, maintain backward compatibility
		if (feed.getUri() != null && feed.getUri().compareTo("") != 0 && feed.getLinkTitle() != null && feed.getLinkTitle().compareTo("") != 0  && !feed.isMultiFileUpload()) {

			// hide the attachments panel
			attachmentPreviewPanel.setVisible(false);

			LinkPreviewer linkPreviewer = new LinkPreviewer(feed.getLinkTitle(), feed.getLinkDescription(), feed.getLinkHost(), feed.getUriThumbnail(), feed.getUri());

			// enable the image previewer if it is an image (mime)
			if(feed.getLinkHost().contains("image/")){

				ArrayList<EnhancedImage> listOfEnhancedImages;

				EnhancedImage enhancedImage = new EnhancedImage(
								feed.getUri(), 
								feed.getLinkTitle() + 
								" (" + feed.getLinkDescription() +  ", type:" + feed.getLinkHost()  +")", 
								feed.getLinkTitle(), 
								feed.getUri()
						);

				listOfEnhancedImages = new ArrayList<EnhancedImage>();
				listOfEnhancedImages.add(enhancedImage);
				carousel.updateImages(listOfEnhancedImages);

				// set handler on the linkpreviewer image to show this carousel and on the image title too
				linkPreviewer.onImageClickOpenCarousel(carousel);
				linkPreviewer.onFileNameClickOpenCarousel(carousel);

				// remove next and prev buttons of the carousel since we have only an image
				carousel.hideArrows();

			}

			// add link preview to the preview panel
			previewPanel.add(linkPreviewer);
		}

		// in case there are attachments, we have to fill attachmentPreviewPanel instead of the previewPanel
		if(feed.isMultiFileUpload()){

			// set style to the attachment container
			attachmentPreviewPanel.setStyleName("attachment-preview-container");

			// hide link preview panel
			previewPanel.setVisible(false);

			// prepare the carousel
			ArrayList<EnhancedImage> listOfEnhancedImages = new ArrayList<EnhancedImage>();

			// remember that one attachment is stored in the fields: uri, uriThumbnail, linkTitle,  linkDescription, linkHost
			Attachment firstAttachment = new Attachment(
					feed.getKey(), // it is meaningless but it's needed
					feed.getUri(), 
					feed.getLinkTitle(), 
					feed.getLinkDescription(), 
					feed.getUriThumbnail(), 
					feed.getLinkHost());

			// create first attachment previewer and pass it the carousel
			AttachmentPreviewer firstAttachmentPreviewer = new AttachmentPreviewer(firstAttachment);

			// determine if the left/right arrows must be removed
			int imagesAvailableInCarousel = 0;

			// check if it is an image
			if(firstAttachment.getMimeType().contains("image/")){

				EnhancedImage enhancedImage = new EnhancedImage(
								feed.getUri(), 
								feed.getLinkTitle() + 
								" (" + feed.getLinkDescription() +  ", type:" + feed.getLinkHost()  +")",  
								feed.getLinkTitle(), 
								feed.getUri()
						);

				listOfEnhancedImages.add(enhancedImage);
				firstAttachmentPreviewer.onImageClickOpenCarousel(carousel, enhancedImage);

				// increment the images
				imagesAvailableInCarousel ++;

			}

			// add the first attachment to the panel
			attachmentPreviewPanel.add(firstAttachmentPreviewer);

			// check the others
			for (Attachment otherAttachment : myFeed.getAttachments()) {

				AttachmentPreviewer attachmentPreviewer = new AttachmentPreviewer(otherAttachment);

				if(otherAttachment.getMimeType().contains("image/")){

					EnhancedImage enhancedImage = new EnhancedImage(
									otherAttachment.getUri(), 
									otherAttachment.getName() + 
									" (" + otherAttachment.getDescription() +  ", type:" + feed.getLinkHost()  +")", 
									otherAttachment.getName(), 
									otherAttachment.getUri()
							);

					listOfEnhancedImages.add(enhancedImage);

					// pass the carousel
					attachmentPreviewer.onImageClickOpenCarousel(carousel, enhancedImage);

					// increment the images
					imagesAvailableInCarousel ++;

				}

				// try to build the attachment viewer
				attachmentPreviewPanel.add(attachmentPreviewer);

				// hide arrows if there is no more than 1 image
				if(imagesAvailableInCarousel <= 1)
					carousel.hideArrows();

			}

			// update the carousel's images
			carousel.updateImages(listOfEnhancedImages);

			// invoke append label
			attachmentPreviewPanel.appendShowMoreLabel();
		}

		openImage.setStyleName("openImage");
		openImage.setTitle("Open this feed separately");
		//show if the user has already liked this or not
		setFavoritedUI(myFeed.isLiked());

		commentArea.setHTML("<a>" + NewsFeedPanel.COMMENT_LABEL + "</a>");

		String feedText = feed.getDescription();
		String descWithoutHTML = new HTML(feedText).getText();

		if ( (! feedText.startsWith("<span")) && descWithoutHTML.length() > MAX_SHOWTEXT_LENGTH && !displaySingle) {
			final int TEXT_TO_SHOW_LENGHT = (descWithoutHTML.length() < 500) ? (feedText.length() - (feedText.length() / 3)) : 500;
			feedText = feedText.substring(0, TEXT_TO_SHOW_LENGHT) + "...";
			seeMore.setHTML("<a class=\"seemore\"> See More </a>");
		}

		avatarImage.setUrl(feed.getThumbnailURL());
		avatarImage.setPixelSize(50, 50);

		//replace the < & and >
		feedText = feedText.replaceAll("&lt;","<").replaceAll("&gt;",">");
		feedText = feedText.replaceAll("&amp;","&");

		final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + NewsFeedPanel.extractOrgFriendlyURL(Location.getHref()) +GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;

		if (! isAppFeed) {
			//			sharePostArea.setHTML("<a>" + NewsFeedPanel.SHARE_FWD_LABEL + "</a>");
			contentArea.setHTML("<a class=\"link\" href=\""+profilePageURL
					+"?"+
					Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
					Encoder.encode(feed.getEntityId())+"\">"+feed.getFullName()+"</a> " + feedText);

			this.vreSource.setVisible(true);
			//show the vreid iff the info is present
			if (showTimelineSource && feed.getVreid() != null && feed.getVreid().compareTo("") != 0) {
				String vreName = feed.getVreid().substring(feed.getVreid().lastIndexOf("/")+1);
				vreSource.setHTML("<a class=\"link\" style=\"font-size: 10px; white-space: nowrap;\" href=\"/group/"+vreName.toLowerCase()+"\">[" +vreName + "]</a>");
			} 
			//check if the user has his own avatar
			if (feed.getThumbnailURL().contains("img_id=0") || !feed.getThumbnailURL().contains("?")) { //it means no avatar is set
				avatarImage.setVisible(false);
				String f = "A";
				String s = "Z";
				if (feed.getFullName() != null) {
					String[] parts = feed.getFullName().split("\\s");
					if (parts.length > 0) {
						f = parts[0].toUpperCase();
						s = parts[parts.length-1].toUpperCase();
					} else {
						f = feed.getFullName().substring(0,1);
						s = feed.getFullName().substring(1,2);
					}
				}
				avatarReplacement.setInitials(feed.getEntityId(), f, s);
				avatarReplacement.setVisible(true);
			}
		}
		else {
			//			messageSeparator.setVisible(false);
			contentArea.setHTML("<a class=\"link\" href=\""+feed.getUri()+"\">"+feed.getFullName()+"</a> " + feedText);
			if (isAppFeed) {
				if (myUserInfo.isAdmin()) 
					closeImage.setTitle("Delete this Application feed (Administrator Only)");
				else
					closeImage.removeFromParent();
				try{
					String vreName = feed.getVreid().substring(feed.getVreid().lastIndexOf("/")+1);
					sharePostArea.setHTML("<a class=\"link\" style=\"white-space: nowrap;\" href=\""+feed.getUri()+"\"> - go App [" +vreName + "] - </a>");
				}
				catch (Exception e) {}
			}
		}


		try {
			Date now = new Date();
			String formattedTime;
			// TODO java.util.Calendar is not yet available in GWT
			if(now.getYear() != feed.getTime().getYear())
				formattedTime =	DateTimeFormat.getFormat("MMMM dd yyyy, h:mm a").format(feed.getTime());
			else
				formattedTime =	DateTimeFormat.getFormat("MMMM dd, h:mm a").format(feed.getTime());

			timeArea.setHTML(formattedTime);
			String formattedTimeWithYear = DateTimeFormat.getFormat("dd MMMM yyyy h:mm a ").format(feed.getTime());
			timeArea.setTitle(formattedTimeWithYear);
			if (! feed.getCommentsNo().equals("0")) {		
				commentsNo.setVisible(true);
				commentsNo.setText(feed.getCommentsNo());
				commentsNo.setTitle(feed.getCommentsNo() + " people commented this.");
			} 
			if (! feed.getLikesNo().equals("0")) {
				likesNo.setVisible(true);
				likesNo.setText(feed.getLikesNo());
				likesNo.setTitle("Show People who have " + NewsFeedPanel.LIKED_LABEL + " this.");
			}
			totalComments = Integer.parseInt(feed.getCommentsNo());
		}
		catch (NumberFormatException e) {
			totalComments = 0;
		}
		catch (Exception e) {
			timeArea.setHTML("just now");
		}
		commentsPanel.setStyleName("commentsPanel");
		if (myFeed.getComments() != null && myFeed.getComments().size() > 0) {
			if (totalComments > 2 && !displaySingle) {
				showAllComments = getShowAllCommentsLink(totalComments);
				commentsPanel.add(showAllComments);
				commentsNo.setVisible(true);
			}
			for (Comment comment : myFeed.getComments()) {
				addComment(new SingleComment(comment, this, (comment.getUserid().equals(myUserInfo.getUsername()))));
			}
			showAddCommentForm(false);
		} 
	}

	/**
	 * used when getting tweets from the client
	 * @param myUserInfo
	 * @param feed
	 * @param eventBus
	 * @param hidden
	 */
	public TweetTemplate(UserInfo myUserInfo, EnhancedFeed feed, HandlerManager eventBus, boolean hidden) {
		this(false, false, myUserInfo, feed, eventBus);
		contentArea.getElement().getParentElement().getParentElement().setClassName("div-table-col content hidden");
	}

	@UiHandler("contentArea")
	public void onHover(MouseOutEvent event) {
		if (isUsers) 
			closeImage.removeStyleName("uiCloseButton");
		openImage.removeStyleName("uiOpenButton");
	}

	@UiHandler("contentArea")
	public void onHover(MouseOverEvent event) {
		if (isUsers) {
			closeImage.addStyleName("uiCloseButton");
			GWT.log("this belong to user");
		}
		openImage.addStyleName("uiOpenButton");
	}

	@UiHandler("closeImage") 
	void onDeleteFeedClick(ClickEvent e) {
		if (isUsers || myUserInfo.isAdmin()){ 
			eventBus.fireEvent(new DeleteFeedEvent(this));
		}
		else {
			GWT.log("not belong to user");
		}
	}	

	@UiHandler("openImage") 
	void onOpenFeedClick(ClickEvent e) {
		eventBus.fireEvent(new OpenFeedEvent(this));
	}	




	@UiHandler("seeMore") 
	void onSeeMoreClick(ClickEvent e) {
		String feedText =  myFeed.getFeed().getDescription();
		//replace the < & and >
		feedText = feedText.replaceAll("&lt;","<").replaceAll("&gt;",">");
		feedText = feedText.replaceAll("&amp;","&");

		final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + NewsFeedPanel.extractOrgFriendlyURL(Location.getHref()) +GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;


		contentArea.setHTML("<a class=\"link\"href=\"" + profilePageURL + "?"+
				Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
				Encoder.encode(myFeed.getFeed().getEntityId())+"\">"+
				myFeed.getFeed().getFullName()+"</a> " + feedText);

		seeMore.setHTML("");
	}	

	private void setFavoritedUI(boolean favorited) {
		if (favorited) {
			likeArea.setHTML("<a style=\"color:#6E8CCC;\">" + NewsFeedPanel.LIKED_LABEL + "</a>");
			likeArea.setTitle("Unlike this");
		}
		else {
			likeArea.setHTML("<a>" + NewsFeedPanel.LIKE_LABEL + "</a>");
		}
	}



	@UiHandler("likeArea")
	void onLikeClick(ClickEvent e) {
		//if is not liked
		if (!likeArea.getText().equals(NewsFeedPanel.LIKED_LABEL)) {
			try {
				int cur = Integer.parseInt(myFeed.getFeed().getLikesNo());
				cur++;
				if (cur == 1) {
					myFeed.getFeed().setLikesNo("1");
					likesNo.setText("1");
					likesNo.setTitle("People who have " + NewsFeedPanel.LIKED_LABEL + " this");
					likesNo.setVisible(true);
				} else {
					myFeed.getFeed().setLikesNo(""+cur);
					likesNo.setText(""+cur);
					likesNo.setVisible(true);
				}
				eventBus.fireEvent(new AddLikeEvent(this, myFeed.getFeed().getKey()));
				setFavoritedUI(true);
			}
			catch (NumberFormatException ex) {
				likeArea.setHTML("Error on the server");
			}
		} else {
			//it is liked
			int cur = Integer.parseInt(myFeed.getFeed().getLikesNo());
			cur--;
			if (cur == 0) {			
				myFeed.getFeed().setLikesNo("0");
				likesNo.setText("");
				likesNo.setVisible(false);
				likesNo.setTitle("");
			} else {
				myFeed.getFeed().setLikesNo(""+cur);
				likesNo.setText(""+cur);
				likesNo.setVisible(true);
			}
			eventBus.fireEvent(new UnLikeEvent(this, myFeed.getFeed().getKey()));
			setFavoritedUI(false);
		}
	}

	@UiHandler("commentArea")
	void onAddCommentClick(ClickEvent e) {
		if (! commentingDisabled) {
			if (! commentsFetched && totalComments > 2) { //if so, need to load all comments before adding a comment
				fireSeeComments(true);	
			}			
			else {
				showAddCommentForm(true);			
			}
		}
		else
			GWT.log("Commenting disabled");
	}

	@UiHandler("sharePostArea")
	void onMessageClick(ClickEvent e) {
		if (! isAppFeed) {
			final List<String> listToLogin = new ArrayList<String>();
			listToLogin.add(myFeed.getFeed().getEntityId());
			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onSuccess() {
					SharePostDialog dlg = new SharePostDialog(myInstance);
					dlg.openModal();
				}
				public void onFailure(Throwable reason) {
					Window.alert("Could not load this component: " + reason.getMessage());
				}   
			});

		}
	}

	public void showAddCommentForm(boolean focus) {
		final AddCommentTemplate toAdd = new AddCommentTemplate(this, myUserInfo, eventBus);			
		commentsPanel.add(toAdd);
		commentingDisabled = true;
		final Timer t = new Timer() {
			@Override
			public void run() {
				toAdd.setStyleName("comment-show");
			}
		};
		if (focus)
			toAdd.setFocus();
		t.schedule(10);
	}

	private HTML getShowAllCommentsLink(int commentsNo) {
		final HTML toReturn = new HTML("<div class=\"more-comment\"><a class=\"link\" style=\"font-size:11px;\">Show all " + commentsNo + " comments<a/></div>");
		toReturn.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				fireSeeComments(false);			
			}
		});
		return toReturn;
	}

	private void fireSeeComments(boolean commentForm2Add) {
		eventBus.fireEvent(new SeeCommentsEvent(this, commentForm2Add));
	}

	@UiHandler("likesNo")
	void onSeeLikes(ClickEvent e) {
		eventBus.fireEvent(new SeeLikesEvent(myFeed.getFeed().getKey()));
	}
	
	@UiHandler("commentsNo")
	void onSeeComments(ClickEvent e) {
		fireSeeComments(false);
	}	

	public void setcontentAreaStyle(String cssclass) {
		contentArea.getElement().getParentElement().getParentElement().setClassName("div-table-col content visible");
	}

	public boolean isCommenting() {
		return commentingDisabled;
	}

	public void setCommentingDisabled(boolean commenting) {
		this.commentingDisabled = commenting;
	}
	public String getFeedKey() {
		return myFeed.getFeed().getKey();
	}

	public void remove(Widget w) {
		mainHTML.remove(w);
	}

	public void addComment(SingleComment comment) {
		commentsPanel.add(comment);
		myComments.add(comment);

	}
	/**
	 * 
	 * @param show true to show a preloader, false to hide it.
	 * display a preloader userful when the user is wating for the comment operation to be confirmed by the server
	 */
	public void showCommentingPreloader(boolean show) {
		if (show)
			commentsPanel.add(submitCommentPreloader);
		else
			commentsPanel.remove(submitCommentPreloader);

	}

	public void updateSingleComment(Comment edited, HTMLPanel commentPanel){

		commentPanel.clear();
		SingleComment sc = new SingleComment(edited, this, true);
		commentPanel.add(sc);

		// replace the new SingleComment in the list
		int index = 0;
		Iterator<SingleComment> iterator = this.myComments.iterator();

		for (;iterator.hasNext();) {
			SingleComment singleComment = (SingleComment) iterator.next();

			if(singleComment.getCommentKey().equals(edited.getKey())){

				iterator.remove();
				this.myComments.add(index, sc);
				break;

			}
			index ++;
		}

	}

	public void clearComments() {
		myComments.clear();
		commentsPanel.clear();
	}

	public void showLoadingComments() {
		showAllComments.setHTML("<div class=\"more-comment\"><img style=\"padding-right:15px;\"src=\""+ loading +"\" /></div>");
	}

	public boolean isCommentsFetched() {
		return commentsFetched;
	}

	public void setCommentsFetched(boolean commentsFetched) {
		this.commentsFetched = commentsFetched;
	}
	public HandlerManager getEventBus() {
		return eventBus;
	}
	public void updateCommentsNumberCount() {
		if (myComments.size() == 1) {
			//commentsNo.setStyleName("show-comments-number");
			commentsNo.setTitle("Persons who have commented this.");
		}			
		//commentsNo.setHTML(commentIcon.getElement().toString()+"<span>&nbsp;</span>"+myComments.size());
		commentsNo.setText(""+myComments.size());
	}
	public UserInfo getMyUserInfo() {
		return myUserInfo;
	}

	public String getMyFeedUserId() {
		return myFeed.getFeed().getEntityId();
	}

	public String getMyFeedText() {
		return myFeed.getFeed().getDescription();
	}

	public boolean isAppFeed() {
		return isAppFeed;
	}

	public boolean isUser() {
		return isUsers;
	}

	/**
	 * Returns the number of comments this post has
	 * @return
	 */
	public int numberOfComments(){

		// quite easy..
		return myComments.size();

	}

	/**
	 * Returns the number of likes this post has
	 * @return
	 */
	public int numberOfLikes(){

		// not so easy
		int ret = 0;

		try{

			ret = Integer.parseInt(likesNo.getText());

		}catch(NumberFormatException e){

			GWT.log(e.toString());
		}

		return ret;
	}

}
