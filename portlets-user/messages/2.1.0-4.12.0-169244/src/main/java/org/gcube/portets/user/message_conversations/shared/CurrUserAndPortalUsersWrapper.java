package org.gcube.portets.user.message_conversations.shared;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class CurrUserAndPortalUsersWrapper implements Serializable {
	
	private WSUser currentUser;
	private ArrayList<WSUser> users;
	
	public CurrUserAndPortalUsersWrapper() {
		super();	
	}
	
	public CurrUserAndPortalUsersWrapper(WSUser currentUser, ArrayList<WSUser> users) {
		super();
		this.currentUser = currentUser;
		this.users = users;
	}

	public WSUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(WSUser currentUser) {
		this.currentUser = currentUser;
	}

	public ArrayList<WSUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<WSUser> users) {
		this.users = users;
	}	
}
