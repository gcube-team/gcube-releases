package org.gcube.portlets.user.contactinformation.client.ui;

import org.gcube.portlets.user.contactinformation.shared.ContactType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
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

	public InfoPart(String val, ContactType type) {
		initWidget(uiBinder.createAndBindUi(this));
		String title = "";
		String href = "";
		switch (type) {
		case AIM:
			icon.setUrl(AIM);
			title = "AOL Instant Messenger identifier";
			break;
		case EMAIL:
			icon.setUrl(EMAIL);
			title = "e-mail";
			href = "mailto:"+val;
			break;
		case FB:
			icon.setUrl(FB);
			title = "Go Facebook Profile";
			href = "http://www.facebook.com/"+val;
			break;
		case IN:
			icon.setUrl(IN);
			title = "LinkedIn Public Profile";
			if (! val.startsWith("http")) {
				href = "http://"+val;
			} else
				href = val;
			break;
		case GOOGLE:
			icon.setUrl(GOOGLE);
			title = "Google Hangout";
			//icon.getParent().getElement().setInnerHTML("<div title=\""+ val +"\" id=\"placeholder-gplus\"></div>");
			Element el = DOM.createElement("div");
			el.setId("placeholder-gplus");
			value.getParent().getElement().appendChild(el);
			break;
		case PHONE:
			icon.setUrl(PHONE);
			title = "Phone Number";
			href = "callto://"+val;
			break;
		case TWITTER:
			icon.setUrl(TWITTER);
			title = "Twitter Profile";
			href= "https://twitter.com/"+val;
			val = "@"+val;
			break;
		case SKYPE:
			icon.setUrl(SKYPE);
			title = "Call on Skype";
			href = "callto://"+val;
			break;
		case WEBSITE:
			icon.setUrl(WEBSITE);
			if (! val.startsWith("http")) {
				href = "http://"+val;
			} else
				href = val;
			title = "Personal web site";		
			break;
		default:
			break;
		}
		icon.setTitle(title);
		value.setTitle(title);
		value.setHTML("<a href=\""+href+"\" target=\"_blank\" class=\"contact-link\">" + val + "</a>");
		final String googleContactId = val;
		if (type == ContactType.GOOGLE) { 
			//timer needed to let the google api js load before
			Timer t = new Timer() {
				@Override
				public void run() {
					renderButton(googleContactId);
				}
			};
			t.schedule(500);
			//value.setHTML("");
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

}
