/**
 *
 */
package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;


/**
 * The Class CredentialModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 14, 2016
 */
public class CredentialModel implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5312392409290548813L;

	private String id;
	private String login;
	private boolean isGroup;
	private String name;

	/**
	 * Instantiates a new credential model.
	 */
	public CredentialModel(){

	}

	/**
	 * Instantiates a new credential model.
	 *
	 * @param id the id
	 * @param login the login
	 * @param isGroup the is group
	 */
	public CredentialModel(String id, String login, boolean isGroup) {
		this.id = id;
		this.login = login;
		this.isGroup = isGroup;
	}

	/**
	 * Instantiates a new credential model.
	 *
	 * @param id the id
	 * @param login the login
	 * @param name the name
	 * @param isGroup the is group
	 */
	public CredentialModel(String id, String login, String name, boolean isGroup) {
		this(id,login,isGroup);
		this.name = name;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {

		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the login.
	 *
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Checks if is group.
	 *
	 * @return true, if is group
	 */
	public boolean isGroup() {
		return isGroup;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the login.
	 *
	 * @param login the new login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Sets the group.
	 *
	 * @param isGroup the new group
	 */
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CredentialModel [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", isGroup=");
		builder.append(isGroup);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
