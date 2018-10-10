package org.gcube.portlets.user.socialprofile.client.ui;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portal.databook.shared.UserInfo;
import org.gcube.portlets.user.socialprofile.client.SocialProfile;
import org.gcube.portlets.user.socialprofile.client.SocialService;
import org.gcube.portlets.user.socialprofile.client.SocialServiceAsync;
import org.gcube.portlets.user.socialprofile.shared.UserContext;

import com.github.gwtbootstrap.client.ui.Alert;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DisplayProfile extends Composite {
	protected final static String HEADLINE_TEXT = "Professional Headline";
	protected final static String HEADLINE_ERROR = "Your Headline please";
	protected final static String ISTI_TEXT = "Location and/or industry";
	protected final static String ISTI_ERROR = "Your Company please";
	private final static String OAUTH2_SERVICE = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code"; 
	private final static String D4S_APP_ID = "77n7r4c9nwuwk2";

	private static DisplayProfileUiBinder uiBinder = GWT
			.create(DisplayProfileUiBinder.class);

	interface DisplayProfileUiBinder extends UiBinder<Widget, DisplayProfile> {
	}
	public static final String avatar_default = GWT.getModuleBaseURL() + "../images/Avatar_default.png";
	public static final String loading = GWT.getModuleBaseURL() + "../images/avatarLoader.gif";
	public static final String savingImage = GWT.getModuleBaseURL() + "../images/saving.gif";
	public static final String GET_OID_PARAMETER = "oid";
	public static final String CONTROL_SEQUENCE_COOKIE = "CSRF-check-d4science";

	private final SocialServiceAsync socialService = GWT.create(SocialService.class);


	@UiField HTMLPanel mainPanel;
	@UiField Image avatarImage;	
	@UiField HTML userFullName;
	@UiField TextBox headlineBox;
	@UiField TextBox institutionBox;

	@UiField HTML editHeadline;
	@UiField HTML editIsti;

	@UiField HTML headlineLabel;
	@UiField HTML institutionLabel;

	@UiField Image savingHeadline;
	@UiField Button saveHead;
	@UiField Button cancelHead;

	@UiField Button saveIsti;
	@UiField Button cancelIsti;

	@UiField Button messageButton;
	@UiField Button editButton;
	@UiField Button importButton;
	@UiField Alert ownerSeeingProfileAsVreMemberAlert;


	private String currHeadLine;
	private String currInstitution;

	private UserInfo myUserInfo;
	private String messageAppURL;
	private DisplaySummary summarySibling;

	public DisplayProfile() {
		initWidget(uiBinder.createAndBindUi(this));
		avatarImage.setUrl(loading);
		mainPanel.addStyleName("profile-section");	
		savingHeadline.setUrl(savingImage);
	}
	/**
	 * 
	 * @param result
	 */
	public void show(UserContext result) {		
		myUserInfo = result.getUserInfo();				
		avatarImage.setUrl(myUserInfo.getAvatarId());	
		userFullName.setText(myUserInfo.getFullName());
		messageAppURL = result.getSendMessageURL();
		/*
		 * Here we check that:
		 * 1) the user is the owner of the profile;
		 * 2) if the current user to show is null or not (that is, the parameter GCubeSocialNetworking.USER_PROFILE_OID within the url);
		 * 3) if such parameter is equal to the current user we show the profile as if it belong to someone else
		 */
		if (showAsOwner(result)) { 

			String head = (result.getHeadline() == null || result.getHeadline().compareTo("") == 0) ? HEADLINE_TEXT : result.getHeadline();
			String isti = (result.getInstitution() == null ||result.getInstitution().compareTo("") == 0) ? ISTI_TEXT : result.getInstitution();
			headlineLabel.setText(head);
			institutionLabel.setText(isti);

			editHeadline.setStyleName("editImage");
			editHeadline.setTitle("Edit your Professional Headline (e.g. Researcher at University of ...");

			editIsti.setStyleName("editImage");
			editIsti.setTitle("Edit location or industry");

			headlineBox.setMaxLength(100);
			institutionBox.setMaxLength(100);

			messageButton.removeFromParent();
			currHeadLine = head;
			if  (result.getHeadline() == null || result.getHeadline().compareTo("") == 0) {
				headlineLabel.getElement().getStyle().setOpacity(0.5);
				institutionLabel.getElement().getStyle().setOpacity(0.5);						
			}

			editButton.addStyleName("import-edit-buttons-style");
			editButton.setVisible(true);
			editButton.addClickHandler(new ClickHandler() {						
				@Override
				public void onClick(ClickEvent event) {

					// enable editing
					summarySibling.enableEditing();

				}
			});

			importButton.addStyleName("import-edit-buttons-style");
			importButton.setVisible(true);
			importButton.addClickHandler(new ClickHandler() {						
				//TODO: make it a runtime resource
				@Override
				public void onClick(ClickEvent event) {
					String controlSequence = getRandomString();

					//needed to prevent Cross Site Request Forgery attacks
					Cookies.setCookie(CONTROL_SEQUENCE_COOKIE, controlSequence);

					String url = OAUTH2_SERVICE + ""
							+ "&client_id="+D4S_APP_ID
							+ "&state="+controlSequence
							+ "&redirect_uri="+getRedirectURI();

					Location.assign(url);
				}
			});

		} else { //its someone else
			String head = (result.getHeadline() == null || result.getHeadline().compareTo("") == 0) ? "" : result.getHeadline();
			String isti = (result.getInstitution() == null || result.getInstitution().compareTo("") == 0) ? "" : result.getInstitution();
			headlineLabel.setText(head);
			institutionLabel.setText(isti);

			avatarImage.getElement().getParentElement().setAttribute("href", "");
			avatarImage.setTitle(myUserInfo.getFullName());				
			messageButton.setVisible(true);
			// check if the current user is the owner but he is looking at his own profile as if it was the profile of another vre member
			if(result.isOwner()){
				// TODO get the address of the My Profile page and add a link to redirect the user
				ownerSeeingProfileAsVreMemberAlert.setText("You are viewing your profile as other VRE members view it.");
				ownerSeeingProfileAsVreMemberAlert.setVisible(true);
			}
		}
	}

	/**
	 * Check if the profile must be shown as belonging to the user or not
	 * @param result
	 * @return
	 */
	private boolean showAsOwner(UserContext result) {
		if((SocialProfile.getUserToShowId(true) == null  && result.isOwner())) 
			return true;

		return false;
	}
	/**
	 * 	
	 * @return the redirect uri when authorized (or not) by LinkedIn via oAuth2
	 */
	public static String getRedirectURI() {
		String redirectURI = Window.Location.getProtocol()+"//"+Window.Location.getHost()+Window.Location.getPath();
		//development case
		if (Window.Location.getParameter("gwt.codesvr") != null) 
			return redirectURI+"?gwt.codesvr=127.0.0.1:9997";
		return redirectURI;		
	}

	@UiHandler("editHeadline") 
	void onEditHeadlineClick(ClickEvent e) {
		headlineLabel.setVisible(false);
		headlineBox.setText(headlineLabel.getText());
		headlineBox.addStyleName("edit");		
		headlineBox.setVisible(true);
		headlineBox.setFocus(true);
		headlineBox.selectAll();		

		editHeadline.setVisible(false);
		saveHead.setVisible(true);		
		cancelHead.setVisible(true);		
	}

	@UiHandler("editIsti") 
	void onEditInstitutionClick(ClickEvent e) {
		institutionLabel.setVisible(false);
		institutionBox.setText(institutionLabel.getText());
		institutionBox.addStyleName("edit");		
		institutionBox.setVisible(true);
		institutionBox.setFocus(true);
		institutionBox.selectAll();		

		editIsti.setVisible(false);
		saveIsti.setVisible(true);		
		cancelIsti.setVisible(true);		
	}

	@UiHandler("cancelHead") 
	void onCancelHeadlineClick(ClickEvent e) {
		cancelHeadline();
	}

	@UiHandler("cancelIsti") 
	void onCancelIstiClick(ClickEvent e) {
		cancelInsti();
	}


	@UiHandler("saveHead")
	void onSaveHeadlineClick(ClickEvent e) {
		String toShare = escapeHtml(headlineBox.getText());
		if (toShare.equals("") || toShare.equals(HEADLINE_TEXT) || toShare.equals(HEADLINE_ERROR)) {
			headlineBox.setText(HEADLINE_ERROR);
		} else {
			currHeadLine = toShare;
			saveHead.setVisible(false);		
			cancelHead.setVisible(false);
			savingHeadline.setVisible(true);
			socialService.saveHeadline(currHeadLine, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Sorry, an error occurred");
					cancelHeadline();	
					savingHeadline.setVisible(false);		
				}

				@Override
				public void onSuccess(Boolean result) {
					headlineLabel.setText(currHeadLine);
					editHeadline.setVisible(true);
					headlineLabel.getElement().getStyle().setOpacity(1.0);	
					headlineBox.setVisible(false);
					headlineLabel.setVisible(true);		
					savingHeadline.setVisible(false);		
				}
			});

		}
	}

	@UiHandler("saveIsti")
	void onSaveInstitutionClick(ClickEvent e) {
		String toShare = escapeHtml(institutionBox.getText());
		if (toShare.equals("") || toShare.equals(ISTI_TEXT) || toShare.equals(ISTI_ERROR)) {
			institutionBox.setText(ISTI_ERROR);
		} else {
			currInstitution = toShare;
			saveIsti.setVisible(false);		
			cancelIsti.setVisible(false);
			savingHeadline.setVisible(true);
			socialService.saveIsti(currInstitution, new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Sorry, an error occurred");
					cancelHeadline();	
					savingHeadline.setVisible(false);		
				}

				@Override
				public void onSuccess(Boolean result) {
					institutionLabel.setText(currInstitution);
					editIsti.setVisible(true);
					institutionLabel.getElement().getStyle().setOpacity(1.0);	
					institutionBox.setVisible(false);
					institutionLabel.setVisible(true);		
					savingHeadline.setVisible(false);		
				}
			});

		}
	}

	@UiHandler("messageButton")
	void onSendPrivateMessageClick(ClickEvent e) {
		String encodedOid = Encoder.encode(GCubeSocialNetworking.USER_PROFILE_OID);
		String urlToOpen = messageAppURL + "?"+ encodedOid + "=" + SocialProfile.getUserToShowId(false);
		Window.open(urlToOpen,"_blank","");
	}

	private void cancelHeadline() {
		editHeadline.setVisible(true);
		saveHead.setVisible(false);		
		cancelHead.setVisible(false);	
		headlineBox.setVisible(false);
		headlineLabel.setVisible(true);
	}

	private void cancelInsti() {
		editIsti.setVisible(true);
		saveIsti.setVisible(false);		
		cancelIsti.setVisible(false);	
		institutionBox.setVisible(false);
		institutionLabel.setVisible(true);
	}

	public void showError(String message) {
		Window.alert("Failure: " + message);
		avatarImage.setSize("100px", "100px");
		avatarImage.setUrl(avatar_default);				
	}


	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}	

	private String getRandomString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<20;i++) {
			sb.append('a'+Random.nextInt(26));
		}
		return sb.toString();
	}

	/**
	 * Set the summary sibling object
	 * @param summary
	 */
	public void setDisplaySummarySibling(DisplaySummary summary) {
		this.summarySibling = summary;
	}

}
