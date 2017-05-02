package it.eng.edison.usersurvey_portlet.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The Class UserDTO.
 */
public class UserDTO implements IsSerializable {

	/** The group id. */
	private long userId, contactId, groupId;
	
	/** The roles id. */
	private long[] rolesId;
	
	/** The email address. */
	private String screenName, fullName, emailAddress;
	
	/** The list user map. */
	private HashMap listUserMap; 
	
	/** The manage survey user. */
	private boolean adminUser, vreManager, manageSurveyUser;
	
	/**
	 * Instantiates a new user DTO.
	 */
	public UserDTO() {
		listUserMap  = new HashMap();
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * Sets the user id.
	 *
	 * @param userId the new user id
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * Gets the contact id.
	 *
	 * @return the contact id
	 */
	public long getContactId() {
		return contactId;
	}

	/**
	 * Sets the contact id.
	 *
	 * @param contactId the new contact id
	 */
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}

	/**
	 * Gets the screen name.
	 *
	 * @return the screen name
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * Sets the screen name.
	 *
	 * @param screenName the new screen name
	 */
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	/**
	 * Gets the full name.
	 *
	 * @return the full name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the full name.
	 *
	 * @param fullName the new full name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets the email address.
	 *
	 * @return the email address
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address.
	 *
	 * @param emailAddress the new email address
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the list user map.
	 *
	 * @return the list user map
	 */
	public HashMap getListUserMap() {
		return listUserMap;
	}

	/**
	 * Sets the list user map.
	 *
	 * @param listUserMap the new list user map
	 */
	public void setListUserMap(HashMap listUserMap) {
		this.listUserMap = listUserMap;
	}

	/**
	 * Gets the roles id.
	 *
	 * @return the roles id
	 */
	public long[] getRolesId() {
		return rolesId;
	}

	/**
	 * Sets the roles id.
	 *
	 * @param rolesId the new roles id
	 */
	public void setRolesId(long[] rolesId) {
		this.rolesId = rolesId;
	}

	/**
	 * Checks if is admin user.
	 *
	 * @return true, if is admin user
	 */
	public boolean isAdminUser() {
		return adminUser;
	}

	/**
	 * Sets the admin user.
	 *
	 * @param adminUser the new admin user
	 */
	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}

	/**
	 * Checks if is manage survey user.
	 *
	 * @return true, if is manage survey user
	 */
	public boolean isManageSurveyUser() {
		return manageSurveyUser;
	}

	/**
	 * Sets the manage survey user.
	 *
	 * @param manageSurveyUser the new manage survey user
	 */
	public void setManageSurveyUser(boolean manageSurveyUser) {
		this.manageSurveyUser = manageSurveyUser;
	}

	/**
	 * Checks if is vre manager.
	 *
	 * @return true, if is vre manager
	 */
	public boolean isVreManager() {
		return vreManager;
	}

	/**
	 * Sets the vre manager.
	 *
	 * @param vreManager the new vre manager
	 */
	public void setVreManager(boolean vreManager) {
		this.vreManager = vreManager;
	}

	/**
	 * Gets the group id.
	 *
	 * @return the group id
	 */
	public long getGroupId() {
		return groupId;
	}

	/**
	 * Sets the group id.
	 *
	 * @param groupId the new group id
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

}
