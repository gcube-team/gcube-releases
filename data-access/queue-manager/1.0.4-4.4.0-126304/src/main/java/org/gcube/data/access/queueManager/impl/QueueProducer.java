package org.gcube.data.access.queueManager.impl;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQSession;
import org.gcube.data.access.queueManager.model.QueueItem;
import org.gcube.data.access.queueManager.utils.QueueXStream;

public class QueueProducer<T extends QueueItem>{

	
	protected MessageProducer publisher;
	protected Session session;
	protected Connection connection;
	
	public QueueProducer(MessageProducer publisher, Session session, Connection connection) throws JMSException{
		super();
		this.publisher=publisher;
		this.session=session;
		this.connection=connection;
	}
	
	public String send(T item) throws JMSException {
		Message msg=session.createTextMessage(QueueXStream.get().toXML(item));
		publisher.send(msg);
		return msg.getJMSMessageID();
	}
	
	public void close()throws JMSException{
			publisher.close();
			session.close();
			connection.close();
	}
	
	public boolean isActive(){
		return ((publisher!=null)&&(session!=null)&&(connection!=null)
				&&(!((ActiveMQSession)session).isClosed()));
	}
}
