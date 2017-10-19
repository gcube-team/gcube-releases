/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.io.Serializable;


/**
 * The Class PortalContextInfo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 25, 2016
 */
public class PortalContextInfo implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -7960309866466555863L;
	private String username;
	private String userFullName;
	private String userEmail;
	private String userAvatarID;
	private String userAvatarURL;
	private String currentScope;
	private String userToken;
	private long currGroupId;

	/**
	 *
	 */
	public PortalContextInfo() {

	}


	/**
	 * Instantiates a new portal context info.
	 *
	 * @param username the username
	 * @param userFullName the user full name
	 * @param userEmail the user email
	 * @param userAvatarID the user avatar id
	 * @param userAvatarURL the user avatar url
	 * @param currentScope the current scope
	 * @param userToken the user token
	 * @param currGroupId the curr group id
	 */
	public PortalContextInfo(String username, String userFullName, String userEmail, String userAvatarID, String userAvatarURL, String currentScope, String userToken, long currGroupId) {
		this.username = username;
		this.userFullName = userFullName;
		this.userEmail = userEmail;
		this.userAvatarID = userAvatarID;
		this.userAvatarURL = userAvatarURL;
		this.currentScope = currentScope;
		this.userToken = userToken;
		this.currGroupId = currGroupId;

	}



	/**
	 * @return the username
	 */
	public String getUsername() {

		return username;
	}



	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {

		return userFullName;
	}



	/**
	 * @return the userEmail
	 */
	public String getUserEmail() {

		return userEmail;
	}



	/**
	 * @return the userAvatarID
	 */
	public String getUserAvatarID() {

		return userAvatarID;
	}



	/**
	 * @return the userAvatarURL
	 */
	public String getUserAvatarURL() {

		return userAvatarURL;
	}



	/**
	 * @return the currentScope
	 */
	public String getCurrentScope() {

		return currentScope;
	}



	/**
	 * @return the userToken
	 */
	public String getUserToken() {

		return userToken;
	}



	/**
	 * @return the currGroupId
	 */
	public long getCurrGroupId() {

		return currGroupId;
	}



	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {

		this.username = username;
	}



	/**
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName(String userFullName) {

		this.userFullName = userFullName;
	}



	/**
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {

		this.userEmail = userEmail;
	}



	/**
	 * @param userAvatarID the userAvatarID to set
	 */
	public void setUserAvatarID(String userAvatarID) {

		this.userAvatarID = userAvatarID;
	}



	/**
	 * @param userAvatarURL the userAvatarURL to set
	 */
	public void setUserAvatarURL(String userAvatarURL) {

		this.userAvatarURL = userAvatarURL;
	}



	/**
	 * @param currentScope the currentScope to set
	 */
	public void setCurrentScope(String currentScope) {

		this.currentScope = currentScope;
	}



	/**
	 * @param userToken the userToken to set
	 */
	public void setUserToken(String userToken) {

		this.userToken = userToken;
	}



	/**
	 * @param currGroupId the currGroupId to set
	 */
	public void setCurrGroupId(long currGroupId) {

		this.currGroupId = currGroupId;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PortalContextInfo [username=");
		builder.append(username);
		builder.append(", userFullName=");
		builder.append(userFullName);
		builder.append(", userEmail=");
		builder.append(userEmail);
		builder.append(", userAvatarID=");
		builder.append(userAvatarID);
		builder.append(", userAvatarURL=");
		builder.append(userAvatarURL);
		builder.append(", currentScope=");
		builder.append(currentScope);
		builder.append(", userToken=");
		builder.append(userToken);
		builder.append(", currGroupId=");
		builder.append(currGroupId);
		builder.append("]");
		return builder.toString();
	}

}
