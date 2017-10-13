package org.gcube.portlets.user.contactinformation.client.ui;

import org.gcube.portlets.user.contactinformation.shared.ContactType;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


public class InfoPart extends Composite {

	private static InfoPartUiBinder uiBinder = GWT
			.create(InfoPartUiBinder.class);

	interface InfoPartUiBinder extends UiBinder<Widget, InfoPart> {
	}

	public static final String IN = GWT.getModuleBaseURL() + "../images/in.png";
	public static final String AIM = GWT.getModuleBaseURL() + "../images/aim.png";
	public static final String EMAIL = GWT.getModuleBaseURL() + "../images/email.png";
	public static final String FB = GWT.getModuleBaseURL() + "../images/fb.png";
	public static final String GOOGLE = GWT.getModuleBaseURL() + "../images/google.png";
	public static final String PHONE = GWT.getModuleBaseURL() + "../images/phone.png";
	public static final String SKYPE = GWT.getModuleBaseURL() + "../images/skype.png";
	public static final String TWITTER = GWT.getModuleBaseURL() + "../images/twitter.png";
	public static final String WEBSITE = GWT.getModuleBaseURL() + "../images/website.png";

	public InfoPart() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField HTMLPanel panel;
	@UiField Image icon;
	@UiField HTML value;
	@UiField TextBox valueEdit;
	
	private ContactType type;
	private String currentValue;
	
	public InfoPart(String val, ContactType type) {
		initWidget(uiBinder.createAndBindUi(this));
		setFieldInformation(val, type);
	}
	
	/**
	 * Update contact information
	 * @param val
	 * @param type
	 */
	public void updateInformation(String val, ContactType type){
		setFieldInformation(val, type);
	}
	
	/**
	 * Private method to set this InfoPart value and type information
	 * @param val
	 * @param type
	 */
	private void setFieldInformation(String val, ContactType type){
		this.type = type;
		this.currentValue = val;
		String title = "";
		String href = "";
		switch (type) {
		case AIM:
			icon.setUrl(AIM);
			title = "AOL Instant Messenger identifier";
			valueEdit.setPlaceholder("AOL Instant Messenger identifier");
			break;
		case EMAIL:
			icon.setUrl(EMAIL);
			title = "e-mail";
			href = "mailto:"+val;
			valueEdit.setPlaceholder("Email");
			break;
		case FB:
			icon.setUrl(FB);
			title = "Go Facebook Profile";
			href = "http://www.facebook.com/"+val;
			valueEdit.setPlaceholder("Facebook");
			break;
		case IN:
			icon.setUrl(IN);
			title = "LinkedIn Public Profile";
			if (! val.startsWith("http")) {
				href = "http://"+val;
			} else
				href = val;
			valueEdit.setPlaceholder("Linkedin");
			break;
		case GOOGLE:
			icon.setUrl(GOOGLE);
			title = "Google Hangout";
			Element el = DOM.createElement("div");
			el.setId("placeholder-gplus");
			value.getParent().getElement().appendChild(el);
			valueEdit.setPlaceholder("Google");
			break;
		case PHONE:
			icon.setUrl(PHONE);
			title = "Phone Number";
			href = "callto://"+val;
			valueEdit.setPlaceholder("Phone");
			break;
		case TWITTER:
			icon.setUrl(TWITTER);
			title = "Twitter Profile";
			href= "https://twitter.com/"+val;
			valueEdit.setPlaceholder("Twitter");
			break;
		case SKYPE:
			icon.setUrl(SKYPE);
			title = "Call on Skype";
			href = "callto://"+val;
			valueEdit.setPlaceholder("Skype");
			break;
		case WEBSITE:
			icon.setUrl(WEBSITE);
			if (! val.startsWith("http")) {
				href = "http://"+val;
			} else
				href = val;
			title = "Personal web site";	
			valueEdit.setPlaceholder("Personal web site");
			break;
		default:
			break;
		}
		
		icon.setTitle(title);
		value.setTitle(title);
		value.setHTML("<a href=\""+href+"\" target=\"_blank\" class=\"contact-link\">" + val + "</a>");
		valueEdit.setText(val); // save the same value into the textbox element
		
		// show the button for google hangout after a while
		final String googleContactId = val;
		if (type == ContactType.GOOGLE && !googleContactId.isEmpty()) { 
			//timer needed to let the google api js load before
			Timer t = new Timer() {
				@Override
				public void run() {
					renderButton(googleContactId);
				}
			};
			t.schedule(500);
		}
	}

	/**
	 * construct the Google plus Hangout button
	 * @param googleContactId the email or the google plus id
	 */
	public static native void renderButton(String googleContactId) /*-{
		var myInvites = [];
		var id = "id";
		var email = googleContactId;

		var invite = "invite_type";
		var type = "EMAIL";

		var obj1 = {};
		obj1[id] = email;
		obj1[invite] = type;

		myInvites.push(obj1)
	   	$wnd.gapi.hangout.render('placeholder-gplus', {
	    	'render': 'createhangout',
	    	'invites': JSON.stringify(myInvites),
	    	'widget_size': 136
	  	});

	  	console.log("invites: %s", JSON.stringify(myInvites))
	}-*/;

	/**
	 * Show editable areas and hide value ones or viceversa.
	 */
	public void showTextBox(boolean show){
		value.setVisible(!show);
		valueEdit.setVisible(show);
	}
	
	/**
	 * Return current textbox value
	 * @return
	 */
	public String getTextBoxValue(){
		return valueEdit.getText();
	}
	
	/**
	 * Return current contact type
	 * @return
	 */
	public ContactType getContactType(){
		return type;
	}
	
	/**
	 * When the user cancels changes, textboxes are reset with previous values
	 */
	public void replaceTextBoxValueWithLabel(){
		valueEdit.setText(currentValue);
	}
}
