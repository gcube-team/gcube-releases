package gr.uoa.di.madgik.environment.notifications.model;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralMessageListener implements MessageListener {
	
	NotificationMessageListenerI messageListener;
	Logger logger = LoggerFactory.getLogger(GeneralMessageListener.class.getName());
	
	public void setNotificationMessageListener(NotificationMessageListenerI ml) {
		messageListener = ml;
	}

	@Override
	public void onMessage(Message message) {
		// Make the converstion from jms message to our message
		if (message instanceof TextMessage) {
			TextMessage textJmsMsg = (TextMessage) message;
			gr.uoa.di.madgik.environment.notifications.TextMessage msg = new gr.uoa.di.madgik.environment.notifications.TextMessage();
			
			// Copy message text content
			try {
				msg.setText(textJmsMsg.getText());
			} catch (JMSException e) {
				logger.error("Error while getting text from JMS message");
			}
			
			// Copy message properties
			Enumeration propertyNames;
			try {
				propertyNames = textJmsMsg.getPropertyNames();
				while (propertyNames.hasMoreElements()) {
					String name = (String)propertyNames.nextElement();
					
					try {
						msg.addStringProperty(name, textJmsMsg.getStringProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addBooleanProperty(name, textJmsMsg.getBooleanProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addByteProperty(name, textJmsMsg.getByteProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addDoubleProperty(name, textJmsMsg.getDoubleProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addFloatProperty(name, textJmsMsg.getFloatProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addIntProperty(name, textJmsMsg.getIntProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
					try {
						msg.addObjectProperty(name, textJmsMsg.getObjectProperty(name));
					} catch (JMSException e) {
						logger.error(e.getMessage());
					}
				}
			} catch (JMSException e) {
				logger.error("Error while getting JMS message property names.");
			}
			
			// Set redelivered
			try {
				msg.setRedelivered(textJmsMsg.getJMSRedelivered());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			// Set delivery mode
			try {
				msg.setDeliveryMode(textJmsMsg.getJMSDeliveryMode());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			// Set expiration
			try {
				msg.setExpiration(textJmsMsg.getJMSExpiration());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			// Set message id
			try {
				msg.setMessageID(textJmsMsg.getJMSMessageID());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			try {
				msg.setMessageType(textJmsMsg.getJMSType());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			try {
				msg.setPriority(textJmsMsg.getJMSPriority());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			
			try {
				msg.setTimestamp(textJmsMsg.getJMSTimestamp());
			} catch (JMSException e) {
				logger.error(e.getMessage());
			}
			messageListener.onMessage(msg);
		} else {
			// TODO: ..........
		}
		
	}


}
