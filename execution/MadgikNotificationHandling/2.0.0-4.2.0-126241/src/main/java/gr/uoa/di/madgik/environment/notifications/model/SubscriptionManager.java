package gr.uoa.di.madgik.environment.notifications.model;

public interface SubscriptionManager {
	
	public void destroy();
	
	public void pauseSubscription();
	
	public void resumeSubscription();
	
	public void setTerminationTime(long timeInMillis);

}
