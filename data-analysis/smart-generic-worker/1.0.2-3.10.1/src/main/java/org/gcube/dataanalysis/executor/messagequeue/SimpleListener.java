package org.gcube.dataanalysis.executor.messagequeue;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class SimpleListener implements MessageListener, ExceptionListener {
	String id;
	
	synchronized public void onException(JMSException ex) {
		System.out.println("JMS Exception occured.  Shutting down client.");
	}
	public SimpleListener(String id){
		this.id = id;
	}
	public void onMessage(Message message) {
		try {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			
				message.acknowledge();
				System.out.println("Received message: " + textMessage.getText() + " id "+id);
				Thread.sleep(5000);
				
			
		} else {
			System.out.println("Received: " + message+ " id "+id);
		}
		} catch (Exception ex) {
			System.out.println("Error reading message: " + ex+ " id "+id);
		}
		
	}

}
