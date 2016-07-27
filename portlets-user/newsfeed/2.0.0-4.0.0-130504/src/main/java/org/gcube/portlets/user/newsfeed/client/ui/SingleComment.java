package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.Comment;
import org.gcube.portlets.user.gcubewidgets.client.ClientScopeHelper;
import org.gcube.portlets.user.newsfeed.client.event.DeleteCommentEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class SingleComment extends Composite {

	private static SingleCommentUiBinder uiBinder = GWT
			.create(SingleCommentUiBinder.class);

	interface SingleCommentUiBinder extends UiBinder<Widget, SingleComment> {
	}
	private static final int MAX_SHOWTEXT_LENGTH = 450;
	
	private boolean isUsers = false;
	private TweetTemplate owner;
	private String myCommentid;
	private Comment myComment;

	@UiField HTMLPanel mainPanel;
	@UiField Image avatarImage;
	@UiField AvatarReplacement4Comments avatarReplacement;
	@UiField HTML commentText;
	@UiField HTML timeArea;
	@UiField HTML closeImage;
	@UiField HTML editImage;
	@UiField HTML seeMore;

	public SingleComment(Comment toShow, TweetTemplate owner, boolean isUsers) {
		initWidget(uiBinder.createAndBindUi(this));
		sinkEvents(Event.ONPASTE);
		this.owner = owner;
		this.isUsers = isUsers;
		this.myComment = toShow;
		myCommentid = toShow.getKey();
		avatarImage.setPixelSize(30, 30);
		avatarImage.setUrl(toShow.getThumbnailURL());	
		
		//check if the user has his own avatar
		if (toShow.getThumbnailURL().endsWith("img_id=0") || !toShow.getThumbnailURL().contains("?")) { //it means no avatar is set
			avatarImage.setVisible(false);
			String f = "A";
			String s = "Z";
			if (toShow.getFullName() != null) {
				String[] parts = toShow.getFullName().split("\\s");
				if (parts.length > 0) {
					f = parts[0].toUpperCase();
					s = parts[parts.length-1].toUpperCase();
				} else {
					f = toShow.getFullName().substring(0,1);
					s = toShow.getFullName().substring(1,2);
				}
			}
			avatarReplacement.setInitials(toShow.getUserid(), f, s);
			avatarReplacement.setVisible(true);
		}
		
		String commentToShow = toShow.getText();
		
		//replace the < & and >
		commentToShow = commentToShow.replaceAll("&lt;","<").replaceAll("&gt;",">");
		commentToShow = commentToShow.replaceAll("&amp;","&");
		

		
		if (commentToShow.length() > MAX_SHOWTEXT_LENGTH) {
			final int TEXT_TO_SHOW_LENGHT = (commentToShow.length() < 700) ? (commentToShow.length() - (commentToShow.length() / 3)) : 700;
			commentToShow = commentToShow.substring(0, TEXT_TO_SHOW_LENGHT) + "...";
			seeMore.setHTML("<a class=\"seemore\"> See More </a>");
		}
		final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + ClientScopeHelper.extractOrgFriendlyURL(Location.getHref()) +GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;

		commentText.setHTML("<a class=\"link\" href=\"" + profilePageURL + "?"+
					Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
					Encoder.encode(toShow.getUserid())+"\">"+toShow.getFullName()+
					"</a> " + commentToShow);
		if(toShow.isEdit())
			timeArea.setHTML(DateTimeFormat.getFormat("MMMM dd, h:mm a").format(toShow.getTime()) + 
					" (Last edit on " + DateTimeFormat.getFormat("MMMM dd, h:mm a").format(toShow.getLastEditTime()) + ")");
		else
		timeArea.setHTML(DateTimeFormat.getFormat("MMMM dd, h:mm a").format(toShow.getTime()));
		if (isUsers) {
			closeImage.setStyleName("closeImage");
			closeImage.setTitle("Delete");
			editImage.setStyleName("editImage");
			editImage.setTitle("Edit");
		}
	}
	
	@UiHandler("seeMore") 
	void onSeeMoreClick(ClickEvent e) {
		String commentToShow = myComment.getText();
		//replace the < & and >
		commentToShow = commentToShow.replaceAll("&lt;","<").replaceAll("&gt;",">");
		commentToShow = commentToShow.replaceAll("&amp;","&");
		
		final String profilePageURL = GCubePortalConstants.PREFIX_GROUP_URL + ClientScopeHelper.extractOrgFriendlyURL(Location.getHref()) +GCubePortalConstants.USER_PROFILE_FRIENDLY_URL;

		commentText.setHTML("<a class=\"link\" href=\"" + profilePageURL + "?"+
					Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID)+"="+
					Encoder.encode(myComment.getUserid())+"\">"+
					myComment.getFullName()+"</a> " + commentToShow);
		seeMore.setHTML("");
	}	
	
	

	@UiHandler("closeImage") 
	void onDeleteCommentClick(ClickEvent e) {
		if (isUsers) 
			owner.getEventBus().fireEvent(new DeleteCommentEvent(owner, myCommentid));
	}	

	@UiHandler("editImage") 
	void onEditCommentClick(ClickEvent e) {
		if (isUsers) {
			AddCommentTemplate addComm = new AddCommentTemplate(owner, myComment, mainPanel);
			mainPanel.getElement().setInnerHTML("");
			mainPanel.add(addComm);
		}
	}	


	@UiHandler("commentText")
	public void onHover(MouseOverEvent event) {
		if (isUsers) {
			closeImage.addStyleName("uiCloseButton");
			editImage.addStyleName("uiEditButton");
		}
	}

	@UiHandler("commentText")
	public void onHover(MouseOutEvent event) {
		if (isUsers) {
			closeImage.removeStyleName("uiCloseButton");
			editImage.removeStyleName("uiEditButton");
		}
	}

	public String getCommentKey() {
		return myCommentid;
	}
	
	
}
