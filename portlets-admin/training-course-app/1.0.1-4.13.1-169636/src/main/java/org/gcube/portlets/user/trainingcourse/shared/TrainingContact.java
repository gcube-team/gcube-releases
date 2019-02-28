package org.gcube.portlets.user.trainingcourse.shared;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingContact.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 31, 2018
 */
public class TrainingContact implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 32630745939606286L;

	/** The username. */
	private String username;
	
	/** The fullname. */
	private String fullname;
	
	/** The email. */
	private String email;

	private String avatarURL;

	
	/** The is group. */
	private boolean isGroup;
	
	
	/**
	 * Instantiates a new training user.
	 */
	public TrainingContact() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * Instantiates a new training user.
	 *
	 * @param username the username
	 * @param fullname the fullname
	 * @param email the email
	 * @param avatarURL the avatar URL
	 */
	public TrainingContact(String username, String fullname, String email, String avatarURL, boolean isGroup) {
		super();
		this.username = username;
		this.fullname = fullname;
		this.email = email;
		this.avatarURL = avatarURL;
		this.isGroup = isGroup;
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
	 * Sets the username.
	 *
	 * @param username the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}


	/**
	 * Gets the fullname.
	 *
	 * @return the fullname
	 */
	public String getFullname() {
		return fullname;
	}


	/**
	 * Sets the fullname.
	 *
	 * @param fullname the new fullname
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}


	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	


	public String getAvatarURL() {
		return avatarURL;
	}


	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}


	public boolean isGroup() {
		return isGroup;
	}


	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainingContact [username=");
		builder.append(username);
		builder.append(", fullname=");
		builder.append(fullname);
		builder.append(", email=");
		builder.append(email);
		builder.append(", avatarURL=");
		builder.append(avatarURL);
		builder.append(", isGroup=");
		builder.append(isGroup);
		builder.append("]");
		return builder.toString();
	}




	

}
