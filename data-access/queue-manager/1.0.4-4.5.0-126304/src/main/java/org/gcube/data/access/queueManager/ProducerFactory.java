package org.gcube.data.access.queueManager;

import javax.jms.JMSException;

import org.gcube.data.access.queueManager.impl.QueueProducer;

public interface ProducerFactory {

	
	public QueueProducer getSubmitter(String topic,QueueType type) throws JMSException;
	public void close();
}
