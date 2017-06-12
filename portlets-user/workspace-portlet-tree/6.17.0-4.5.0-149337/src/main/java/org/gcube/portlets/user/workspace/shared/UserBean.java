package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

public class UserBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6779963164440480883L;

	private String username;
	private String firstName;
	private String lastName;
	private String email;

	public UserBean() {
	}

	public UserBean(String username, String firstName, String lastName, String email) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}


	/**
	 * @return the username
	 */
	public String getUsername() {

		return username;
	}


	/**
	 * @return the firstName
	 */
	public String getFirstName() {

		return firstName;
	}


	/**
	 * @return the lastName
	 */
	public String getLastName() {

		return lastName;
	}


	/**
	 * @return the email
	 */
	public String getEmail() {

		return email;
	}


	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {

		this.username = username;
	}


	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {

		this.firstName = firstName;
	}


	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {

		this.lastName = lastName;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {

		this.email = email;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("UserBean [username=");
		builder.append(username);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append("]");
		return builder.toString();
	}


}
