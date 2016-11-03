package org.gcube.application.framework.search.library.exception;

public class ReadingUserProfileException extends Exception {
	
	public ReadingUserProfileException(Throwable cause) {
		super("Problem while trying to retrieve UserProfile from Personalization Service", cause);
	}

}
