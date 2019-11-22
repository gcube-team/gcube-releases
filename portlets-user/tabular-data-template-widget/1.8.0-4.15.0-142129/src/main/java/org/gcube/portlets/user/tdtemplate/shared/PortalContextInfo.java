/**
 *
 */
package org.gcube.portlets.user.tdtemplate.shared;

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
	private static final long serialVersionUID = 317264788673825917L;
	private String username;
	private String userFullName;
	private String userEmail;
	private String userAvatarID;
	private String userAvatarURL;
	private String currentScope;
	private String userToken;
	private long currGroupId;


	/**
	 * Instantiates a new portal context info.
	 */
	public PortalContextInfo() {
	}



	/**
	 * Instantiates a new portal context info.
	 *
	 * @param username the username
	 * @param currentScope the current scope
	 * @param userToken the user token
	 */
	public PortalContextInfo(String username, String currentScope, String userToken) {
		this.username = username;
		this.currentScope = currentScope;
		this.userToken = userToken;
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
		this(username, currentScope, userToken);
		this.userFullName = userFullName;
		this.userEmail = userEmail;
		this.userAvatarID = userAvatarID;
		this.userAvatarURL = userAvatarURL;
		this.currGroupId = currGroupId;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {

		return username;
	}

	/**
	 * Gets the user full name.
	 *
	 * @return the userFullName
	 */
	public String getUserFullName() {

		return userFullName;
	}



	/**
	 * Gets the user email.
	 *
	 * @return the userEmail
	 */
	public String getUserEmail() {

		return userEmail;
	}



	/**
	 * Gets the user avatar id.
	 *
	 * @return the userAvatarID
	 */
	public String getUserAvatarID() {

		return userAvatarID;
	}



	/**
	 * Gets the user avatar url.
	 *
	 * @return the userAvatarURL
	 */
	public String getUserAvatarURL() {

		return userAvatarURL;
	}



	/**
	 * Gets the current scope.
	 *
	 * @return the currentScope
	 */
	public String getCurrentScope() {

		return currentScope;
	}



	/**
	 * Gets the user token.
	 *
	 * @return the userToken
	 */
	public String getUserToken() {

		return userToken;
	}



	/**
	 * Gets the curr group id.
	 *
	 * @return the currGroupId
	 */
	public long getCurrGroupId() {

		return currGroupId;
	}



	/**
	 * Sets the username.
	 *
	 * @param username the username to set
	 */
	public void setUsername(String username) {

		this.username = username;
	}



	/**
	 * Sets the user full name.
	 *
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName(String userFullName) {

		this.userFullName = userFullName;
	}



	/**
	 * Sets the user email.
	 *
	 * @param userEmail the userEmail to set
	 */
	public void setUserEmail(String userEmail) {

		this.userEmail = userEmail;
	}



	/**
	 * Sets the user avatar id.
	 *
	 * @param userAvatarID the userAvatarID to set
	 */
	public void setUserAvatarID(String userAvatarID) {

		this.userAvatarID = userAvatarID;
	}



	/**
	 * Sets the user avatar url.
	 *
	 * @param userAvatarURL the userAvatarURL to set
	 */
	public void setUserAvatarURL(String userAvatarURL) {

		this.userAvatarURL = userAvatarURL;
	}



	/**
	 * Sets the current scope.
	 *
	 * @param currentScope the currentScope to set
	 */
	public void setCurrentScope(String currentScope) {

		this.currentScope = currentScope;
	}



	/**
	 * Sets the user token.
	 *
	 * @param userToken the userToken to set
	 */
	public void setUserToken(String userToken) {

		this.userToken = userToken;
	}



	/**
	 * Sets the curr group id.
	 *
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
