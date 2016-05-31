package org.gcube.data.access.queueManager;

import javax.jms.JMSException;

import org.apache.activemq.transport.TransportListener;
import org.gcube.data.access.queueManager.impl.QueueConsumer;

public interface ConsumerFactory {

	/**
	 * 
	 * 
	 * @param topic
	 * @param type
	 * @param callback
	 * @return
	 * @throws JMSException
	 */
	public QueueConsumer register(String topic,QueueType type,QueueItemHandler callback)
			throws JMSException;
	public void close();
}
