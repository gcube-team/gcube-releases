package org.gcube.portlets.user.questions.client.ui;

import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.questions.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DisplayBadge extends Composite {
	private static DisplayBadgeUiBinder uiBinder = GWT.create(DisplayBadgeUiBinder.class);

	interface DisplayBadgeUiBinder extends UiBinder<Widget, DisplayBadge> {	}
		
	@UiField HTMLPanel mainPanel;
	@UiField Image avatarImage;	

	@UiField HTML userFullName;

	@UiField HTML headlineLabel;
	@UiField HTML institutionLabel;
	@UiField AnchorElement imageRedirect;
		
	private UserInfo myUserInfo;

	public DisplayBadge(UserInfo user) {
		initWidget(uiBinder.createAndBindUi(this));
		String profileURL = "";
		String location = Window.Location.getHref();
		if (location.split("/").length == 5)
			profileURL = location + "/" + user.getAccountURL();
		else 
			profileURL = user.getAccountURL();
		
		
		
		Images images = GWT.create(Images.class);
	
		avatarImage.setUrl(images.avatarLoader().getSafeUri());
		mainPanel.addStyleName("profile-section");
	
		myUserInfo = user;		
		avatarImage.getElement().getParentElement().setAttribute("href", myUserInfo.getAvatarId());
		if (myUserInfo.getAvatarId() == null)
			avatarImage.setUrl(images.avatarDefaultImage().getSafeUri());
		else 
			avatarImage.setUrl(myUserInfo.getAvatarId());	
		
		userFullName.setHTML("<a class=\"manager-person-link\" href=\""+profileURL+"\">"+myUserInfo.getFullName()+"</a>");


		headlineLabel.setText(user.getEmailaddress()); //it is the headline
		imageRedirect.setHref(profileURL);
		String title = "See profile of " + myUserInfo.getFullName();
		avatarImage.setTitle(title);	
		userFullName.setTitle(title);
	}
	

}
