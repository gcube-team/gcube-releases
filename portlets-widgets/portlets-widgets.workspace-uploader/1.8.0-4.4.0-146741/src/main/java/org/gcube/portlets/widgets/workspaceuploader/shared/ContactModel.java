package org.gcube.portlets.widgets.workspaceuploader.shared;

import java.io.Serializable;
import java.util.Comparator;



/**
 * The Class ContactModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class ContactModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3715519940338008890L;
	
	private String id;
	private String login;
	private boolean isGroup;
	private String fullName;
	
	/**
	 * Instantiates a new info contact model.
	 */
	public ContactModel() {}

	/**
	 * Instantiates a new info contact model.
	 *
	 * @param id the id
	 * @param login the login
	 * @param isGroup the is group
	 * @param fullName the full name
	 */
	public ContactModel(String id, String login, boolean isGroup, String fullName) {
		super();
		this.id = id;
		this.login = login;
		this.isGroup = isGroup;
		this.fullName = fullName;
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
	 * @return the isGroup
	 */
	public boolean isGroup() {
		return isGroup;
	}

	/**
	 * Gets the full name.
	 *
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the login.
	 *
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Sets the group.
	 *
	 * @param isGroup the isGroup to set
	 */
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	/**
	 * Sets the full name.
	 *
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public static Comparator<ContactModel> COMPARATORFULLNAME = new Comparator<ContactModel>() {
		// This is where the sorting happens.
		public int compare(ContactModel o1, ContactModel o2) {
			if(o1==null)
				return -1;
			if(o2==null)
				return 1;
			return o1.getFullName().compareToIgnoreCase(o2.getFullName());
		}
	};
	
	
	public static Comparator<ContactModel> COMPARATORLOGINS = new Comparator<ContactModel>() {
		// This is where the sorting happens.
		public int compare(ContactModel o1, ContactModel o2) {
			if(o1==null)
				return -1;
			if(o2==null)
				return 1;
			return o1.getLogin().compareToIgnoreCase(o2.getLogin());
		}
	};

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InfoContactModel [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", isGroup=");
		builder.append(isGroup);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append("]");
		return builder.toString();
	}
}