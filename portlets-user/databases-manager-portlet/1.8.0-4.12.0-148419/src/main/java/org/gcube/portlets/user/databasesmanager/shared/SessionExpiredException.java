package org.gcube.portlets.user.databasesmanager.shared;

public class SessionExpiredException extends Exception {

	private static final long serialVersionUID = 1L;

	public SessionExpiredException() {
		super("Session expired");
	}

}
