package org.apache.jackrabbit.j2ee.workspacemanager;

import org.apache.jackrabbit.core.SessionImpl;

public class MySession {
	
	String user;
	SessionImpl session;

	public MySession(String user, SessionImpl session) {
		this.user = user;
		this.session = session;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the session
	 */
	public SessionImpl getSession() {
		return session;
	}


}
