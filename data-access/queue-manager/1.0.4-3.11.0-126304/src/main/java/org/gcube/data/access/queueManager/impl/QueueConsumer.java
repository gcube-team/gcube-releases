package org.gcube.data.access.queueManager.impl;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.model.QueueItem;
import org.gcube.data.access.queueManager.utils.QueueXStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueConsumer<T extends QueueItem> implements MessageListener{

	private static Logger logger = LoggerFactory.getLogger(QueueConsumer.class);
	
	private QueueItemHandler<T> callback;
	private Session session;
	private Connection connection;
	
	QueueConsumer(QueueItemHandler<T> callback,Session session,Connection connection) throws JMSException{
		this.callback=callback;
		this.session=session;
		this.connection=connection;
	}
	
	public void onMessage(Message arg0) {
		T item=null;
		try {
			item=(T) QueueXStream.get().fromXML(((TextMessage)arg0).getText());
			callback.handleQueueItem(item);
			arg0.acknowledge();
		} catch (JMSException e) {
			recoverMessage();
			logger.error("Unable to get item from message "+arg0,e);
		}catch (ClassCastException e){
			recoverMessage();
			logger.error("Unexpected type of item "+item,e);
		} catch (Exception e) {
			recoverMessage();
			logger.error("Unable to handle item from message "+item,e);
		}
	} 
	
	public void close() throws JMSException{
		callback.close();
		this.session.close();
		this.connection.close();
	}
	
	
	private void recoverMessage(){
		try{
			logger.debug("Trying to recover message..");
			session.recover();
		}catch(Exception e){
			logger.error("Unable to recover, consumer will be stalled..");
		}
	}
}
