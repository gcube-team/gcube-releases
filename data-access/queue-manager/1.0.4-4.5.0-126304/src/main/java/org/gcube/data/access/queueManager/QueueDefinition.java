package org.gcube.data.access.queueManager;

public class QueueDefinition {

	private FactoryConfiguration factoryConfiguration;
	private String topic;
	private QueueType type;
	
	public QueueDefinition(FactoryConfiguration factoryConfiguration,
			String topic, QueueType type) {
		super();
		this.factoryConfiguration = factoryConfiguration;
		this.topic = topic;
		this.type = type;
	}

	/**
	 * @return the factoryConfiguration
	 */
	public FactoryConfiguration getFactoryConfiguration() {
		return factoryConfiguration;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @return the type
	 */
	public QueueType getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueueDefinition [factoryConfiguration=");
		builder.append(factoryConfiguration);
		builder.append(", topic=");
		builder.append(topic);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
