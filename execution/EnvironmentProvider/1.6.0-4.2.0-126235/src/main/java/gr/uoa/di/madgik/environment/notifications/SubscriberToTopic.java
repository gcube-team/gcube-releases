package gr.uoa.di.madgik.environment.notifications;

public class SubscriberToTopic {
	
	private Object topicSubscriber;
	
	private Object connection;
	
	public SubscriberToTopic() {
		
	}

	public Object getTopicSubscriber() {
		return topicSubscriber;
	}

	public void setTopicSubscriber(Object topicSubscriber) {
		this.topicSubscriber = topicSubscriber;
	}

	public Object getConnection() {
		return connection;
	}

	public void setConnection(Object connection) {
		this.connection = connection;
	}
	
	

}
