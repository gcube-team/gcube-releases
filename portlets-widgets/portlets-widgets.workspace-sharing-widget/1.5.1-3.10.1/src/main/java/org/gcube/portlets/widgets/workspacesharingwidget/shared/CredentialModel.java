/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 3, 2014
 *
 */
public class CredentialModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4265499048265553603L;
	
	private String id;
	private String login;
	private boolean isGroup;
	
	public CredentialModel(){
		
	}
	/**
	 * @param id
	 * @param login
	 * @param isGroup
	 */
	public CredentialModel(String id, String login, boolean isGroup) {
		this.id = id;
		this.login = login;
		this.isGroup = isGroup;
	}
	
	public String getId() {
		return id;
	}
	public String getLogin() {
		return login;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CredentialModel [id=");
		builder.append(id);
		builder.append(", login=");
		builder.append(login);
		builder.append(", isGroup=");
		builder.append(isGroup);
		builder.append("]");
		return builder.toString();
	}

}
