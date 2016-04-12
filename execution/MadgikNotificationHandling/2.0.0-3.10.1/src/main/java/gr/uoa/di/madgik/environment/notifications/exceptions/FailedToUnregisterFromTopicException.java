package gr.uoa.di.madgik.environment.notifications.exceptions;

public class FailedToUnregisterFromTopicException extends Exception {
	
	public FailedToUnregisterFromTopicException(Throwable cause) {
		super("Failed to unregister from topic.", cause);
	}

}
