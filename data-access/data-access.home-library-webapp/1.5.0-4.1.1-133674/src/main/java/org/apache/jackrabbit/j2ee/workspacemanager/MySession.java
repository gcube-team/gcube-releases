package org.apache.jackrabbit.j2ee.workspacemanager;

import javax.jcr.Session;


public class MySession {
	
	String user;
	Session session;

	public MySession(String user, Session session) {
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
	public Session getSession() {
		return session;
	}


}
