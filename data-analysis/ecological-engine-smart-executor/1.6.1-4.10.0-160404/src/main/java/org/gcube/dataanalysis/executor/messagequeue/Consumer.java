package org.gcube.dataanalysis.executor.messagequeue;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

public class Consumer {
	public QueueManager manager;
	public MessageConsumer consumer;
	private MessageListener consumerCallback;
	private ExceptionListener errorCallback;
	private String topic;
	

	public Consumer(QueueManager manager, MessageListener consumerCallback, ExceptionListener errorCallback, String topic) throws JMSException, InterruptedException {
		this.manager = manager;
		this.consumerCallback = consumerCallback;
		this.errorCallback = errorCallback;
		this.topic = topic;
		
		create();
	}

	private void create() throws JMSException, InterruptedException {

//		Topic ConsumerTopic = manager.session.createTopic(topic);
		
		 MessageConsumer consumer = manager.session.createConsumer(manager.destination);
//		MessageConsumer consumer = manager.session.createDurableSubscriber(ConsumerTopic, "Consumer."+topic);
//		MessageConsumer consumer = manager.session.createConsumer(ConsumerTopic);
		manager.connection.setExceptionListener(errorCallback);
		consumer.setMessageListener(consumerCallback);
		
	}

	public void standBy() throws JMSException {
		if (consumer != null)
			consumer.close();
	}

	public void wake() throws Exception {
		this.create();
	}

	public void stop() throws JMSException {
		if (consumer != null) {
			consumer.close();
			
		}
//		closeSession();
	}

	public void closeSession() throws JMSException {
		try {
			manager.closeSession();
			manager.connection.close();
		} catch (Exception e) {

		}
	}
}
