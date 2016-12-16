package gr.uoa.di.madgik.environment.notifications.model;

import gr.uoa.di.madgik.environment.notifications.exceptions.IllegalTopicIdGivenException;

public class TopicData {
	
	String topicName;
	String producerId;
	
	private TopicData() {
		
	}
	
	public TopicData(String topicName, String producerId) {
		this.topicName = topicName;
		this.producerId = producerId;
	}
	
	public String getTopicName() {
		return topicName;
	}
	
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public String getProducerId() {
		return producerId;
	}
	
	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}
	
	public String createTopicId() {
		return topicName + "__" + producerId;
	}
	
	public static String getTopicNameFromId(String id) throws IllegalTopicIdGivenException {
		String[] name_provider = id.split("__");
		if (name_provider == null || name_provider.length != 2) {
			throw new IllegalTopicIdGivenException();
		}
		return name_provider[0];
	}
	
	public static String getProviderIdFromId(String id) throws IllegalTopicIdGivenException {
		String[] name_provider = id.split("__");
		if (name_provider == null || name_provider.length != 2) {
			throw new IllegalTopicIdGivenException();
		}
		return name_provider[1];
	}
}
