package org.gcube.portlets.user.socialprofile.client;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.PersonJsonizer;

import org.gcube.portal.databook.client.GCubeSocialNetworking;
import org.gcube.portal.databook.client.util.Encoder;
import org.gcube.portlets.user.socialprofile.client.ui.DisplayProfile;
import org.gcube.portlets.user.socialprofile.client.ui.DisplaySummary;
import org.gcube.portlets.user.socialprofile.client.ui.ErrorAlert;
import org.gcube.portlets.user.socialprofile.client.ui.OkAlert;
import org.gcube.portlets.user.socialprofile.shared.UserContext;
import org.jsonmaker.gwt.client.Jsonizer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SocialProfile implements EntryPoint {

	private final SocialServiceAsync socialService = GWT.create(SocialService.class);
	private final PageBusAdapter pageBusAdapter = new PageBusAdapter();

	private VerticalPanel mainPanel = new VerticalPanel();
	private DisplayProfile dispProfile = new DisplayProfile();
	private DisplaySummary summary = new DisplaySummary();

	public void onModuleLoad() {

		// set main panel width
		mainPanel.setWidth("100%");

		if (isUserAuthZFromLinkedIn()) {
			String authorizationCode = checkLinkedInAuthZ();
			if (authorizationCode != null) {
				mainPanel.add(new OkAlert("Authorization OK! Please wait while we import from LinkedIn ... ", false));
				socialService.fetchUserProfile(authorizationCode, DisplayProfile.getRedirectURI(), new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if (result == null) {
							mainPanel.clear();
							mainPanel.add(new ErrorAlert("Something went wrong while parsing your professional summary from LinkedIn, please report the issue.", true));	
							displayProfile();
						}
						else {
							mainPanel.clear();				
							mainPanel.add(new OkAlert("Your data have been imported successfully, anything you want to edit or add? Please use Edit Profile Manually.", true));
							displayProfile();
							//result contain the publicProfileLinkedInUrl
							sendRefreshClientEvent(result);
							// force refresh to reload updated content
							setUrlBase();
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						mainPanel.clear();
						mainPanel.add(new ErrorAlert("Something went wrong while communicating with LinkedIn service, please report us the issue.", true));
						displayProfile();
					}
				});

			}
		} else {
			displayProfile();
		}

		RootPanel.get("SocialProfileDiv").add(mainPanel);
	}

	protected void sendRefreshClientEvent(String inPublicProfileURL) {
		//create the Contact bean data
		Person person = new Person();
		person.setName(inPublicProfileURL);
		// publish a message with Contact bean data
		try {
			pageBusAdapter.PageBusPublish("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", person, (Jsonizer)GWT.create(PersonJsonizer.class));
		} catch (PageBusAdapterException e) {
			e.printStackTrace();
		}

	}
	/**
	 * display the profile of the user
	 */
	private void displayProfile() {
		socialService.getUserContext(getUserToShowId(), new AsyncCallback<UserContext>() {
			@Override
			public void onSuccess(UserContext result) {
				mainPanel.add(dispProfile);
				dispProfile.show(result);
				if (result.getSummary() != null && result.getSummary().compareTo("") != 0) {
					summary.setSummary(result.getSummary());
				}
				mainPanel.add(summary);
				dispProfile.setDisplaySummarySibling(summary);
			}
			@Override
			public void onFailure(Throwable caught) {
				mainPanel.add(dispProfile);
				//				dispProfile.showError(caught.getMessage());
			}
		});
	}

	/** 
	 * Removes any get parameter (such as code, state)
	 */
	private void setUrlBase() {

		String baseUrl = Window.Location.getHref().split("\\?")[0];
		GWT.log(baseUrl);
		Location.assign(baseUrl);
	}

	/**
	 * 
	 * @return the token if everything goers ok, null otherwise
	 */
	private String checkLinkedInAuthZ() {
		if (Window.Location.getParameter("error") != null) {
			mainPanel.add(new ErrorAlert("it seems you denied our request to import your professional summary from LinkedIn.", true));
			return null;
		}	
		String code = Window.Location.getParameter("code");
		String controlSequence = Window.Location.getParameter("state");
		String cSeq2Compare = Cookies.getCookie(DisplayProfile.CONTROL_SEQUENCE_COOKIE);
		if (controlSequence.compareTo(cSeq2Compare) != 0) {
			mainPanel.add(new ErrorAlert("Something went wrong when importing your professional summary from LinkedIn, please try again.", true));
			return null;
		}

		GWT.log("key="+code+" state="+controlSequence);
		GWT.log("state="+controlSequence);		
		return code;
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
	 * 
	 * @return true if the user has clicked import from LinkedIn 
	 */
	private boolean isUserAuthZFromLinkedIn() {
		if (Window.Location.getParameter("error") != null || Window.Location.getParameter("code") != null)
			return true;
		return false;
	}
}
