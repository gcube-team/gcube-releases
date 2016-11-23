package org.gcube.dataanalysis.executor.messagequeue;

import java.util.UUID;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class Producer {

	public MessageProducer producer;
	public QueueManager manager;
	public String topic;
	public String identifier;
	public Producer(QueueManager manager,String topic) throws JMSException {
		this.manager = manager;
		this.topic = topic;
		this.identifier = "" + UUID.randomUUID();
		create();
	}

	private void create() throws JMSException {
//		Topic ProducerTopic = manager.session.createTopic(topic);
		producer = manager.session.createProducer(manager.destination);
//		producer = manager.session.createProducer(ProducerTopic);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
	}

	public void sendTextMessage(String text, long timeToLive) throws JMSException {
		TextMessage message = manager.session.createTextMessage(text);
		producer.setTimeToLive(timeToLive);
		producer.send(message);
	}

	public void sendMessage(Object toSend, long timeToLive) throws JMSException {
		Message message = manager.session.createMessage();
		message.setObjectProperty(ATTRIBUTE.CONTENT.name(), toSend);
		producer.setTimeToLive(timeToLive);
		producer.send(message);
	}

	public void standBy() throws JMSException {
		producer.close();
	}

	public void wake() throws Exception {
		this.create();
	}

	public void stop() throws JMSException {
		if (producer != null){
			producer.close();
			
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
