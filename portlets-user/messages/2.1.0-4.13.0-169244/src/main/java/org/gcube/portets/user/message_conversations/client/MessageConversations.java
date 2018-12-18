package org.gcube.portets.user.message_conversations.client;

import org.gcube.portets.user.message_conversations.client.ui.ApplicationView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Massimiliano Assante, CNR-ISTI
 */
public class MessageConversations implements EntryPoint {
	public static final String DIV_CONTAINER_ID = "create-users-container";
	public static final String ARTIFACT_ID = "messages";
	public static final String USER_PROFILE_OID = "userIdentificationParameter";
	private ApplicationView ap;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		String[] usernamesToSendTo = getUserToShowId();
		//we check whether there exists usernames to send to in the params, if not regular Messages else we show the new message panel to send the message to the users passed
		ap = (usernamesToSendTo != null && usernamesToSendTo.length > 0) ? 
				new ApplicationView(usernamesToSendTo) : new ApplicationView();
		RootPanel.get(DIV_CONTAINER_ID).add(ap);
	}

	/**
	 * decode the usernames from the location param (the list of usernames decoded should be comma separated e.g. username1,username2)
	 * @return the decoded (base64) userid
	 */
	public static String[] getUserToShowId() {
		String encodedOid = Encoder.encode(USER_PROFILE_OID);
		if (Window.Location.getParameter(encodedOid) == null)
			return null;
		try {
			String encodedUsernames = Window.Location.getParameter(encodedOid);
			String decodedUserNames = Encoder.decode(encodedUsernames);
			if (decodedUserNames.contains(","))
				return decodedUserNames.split(",");
			else {
				String[] usernames = {decodedUserNames};
				return usernames;
			}
		} catch (Exception e) {
			GWT.log("Something wring in parsing username list");
			return null;
		}
	}
}
