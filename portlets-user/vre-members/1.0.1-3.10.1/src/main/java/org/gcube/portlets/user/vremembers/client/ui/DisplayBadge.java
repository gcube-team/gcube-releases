package org.gcube.portlets.user.vremembers.client.ui;

import org.gcube.portlets.user.vremembers.shared.BelongingUser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DisplayBadge extends Composite {
	protected final static String HEADLINE_TEXT = "Professional Headline";
	protected final static String ISTI_TEXT = "Company";

	private static DisplayBadgeUiBinder uiBinder = GWT.create(DisplayBadgeUiBinder.class);

	interface DisplayBadgeUiBinder extends UiBinder<Widget, DisplayBadge> {
	}
	public static final String avatar_default = GWT.getModuleBaseURL() + "../images/Avatar_default.png";
	public static final String loading = GWT.getModuleBaseURL() + "../images/avatarLoader.gif";

	@UiField HTMLPanel mainPanel;
	@UiField Image avatarImage;	
	@UiField HTML userFullName;

	@UiField HTML headlineLabel;
	@UiField HTML institutionLabel;
	@UiField AnchorElement imageRedirect;
	
	
	private BelongingUser myUserInfo;

	public DisplayBadge(BelongingUser user) {
		initWidget(uiBinder.createAndBindUi(this));
		avatarImage.setUrl(loading);
		mainPanel.addStyleName("profile-section");
	
		myUserInfo = user;		
		avatarImage.getElement().getParentElement().setAttribute("href", myUserInfo.getAvatarId());
		avatarImage.setUrl(myUserInfo.getAvatarId());	
		userFullName.setHTML("<a class=\"person-link\" href=\""+user.getProfileLink()+"\">"+myUserInfo.getFullName()+"</a>");


		String head = (user.getHeadline() == null || user.getHeadline().compareTo("") == 0) ? "" : user.getHeadline();
		String isti = (user.getInstitution() == null || user.getInstitution().compareTo("") == 0) ? "" : user.getInstitution();
		headlineLabel.setText(head);
		institutionLabel.setText(isti);
		imageRedirect.setHref(user.getProfileLink());
		String title = "See profile of " + myUserInfo.getFullName();
		avatarImage.setTitle(title);	
		userFullName.setTitle(title);
	}
	
	
	
	public void showError(String message) {
		Window.alert("Failure: " + message);
		avatarImage.setSize("100px", "100px");
		avatarImage.setUrl(avatar_default);				
	}

	

}
