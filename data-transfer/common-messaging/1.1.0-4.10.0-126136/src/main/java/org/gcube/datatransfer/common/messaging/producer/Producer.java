package org.gcube.datatransfer.common.messaging.producer;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.datatransfer.common.messaging.ConnectionsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * JMS Client that sends message to ActiveMQ Broker
 * 
 * @author Andrea Manzi( CERN)
 *
 */
public class Producer implements Runnable{

	private static int ackMode;
	private static  boolean transacted;
	private static Producer singleton; 
	private String selectorBase = "MessageType";
	private static Integer MAX_MONITORING_QUEUE_SIZE = 1000;
	private static Integer MAX_ACCOUNTING_QUEUE_SIZE = 1000;
	private static  ConcurrentLinkedQueue<GCUBEMessage> messageForQueue = null;
	

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	static {
		ackMode = Session.AUTO_ACKNOWLEDGE;
		transacted = false;
		singleton = new Producer();
		messageForQueue = new ConcurrentLinkedQueue<GCUBEMessage>();
	
		//starting thread
		Thread t = new Thread(singleton);
		t.start();
	}

	/**
	 * no object instantiation possible
	 */
	private Producer() {}

	

	/**
	 * Sends message to  a QUEUE destination
	 * 	@param message the GCUBEMessage to send
	 */
	public void sendMessageToQueue(GCUBEMessage message) {
		
		QueueConnection connection = ConnectionsManager.getQueueConnection(GCUBEScope.getScope(message.getScope()));
		
		if (connection != null){
			
				try {
					QueueSession session = connection.createQueueSession(transacted, ackMode);
					Queue queue = session.createQueue(message.getTopic());
					QueueSender sender = session.createSender(queue);
					sender.setDeliveryMode(DeliveryMode.PERSISTENT);
					ObjectMessage objMsg = session.createObjectMessage();
					objMsg.setObject(message);
					objMsg.setJMSMessageID(createRandomString());
					sender.send(objMsg);
					logger.debug("Message "+message.toString()+ " SENT");
					return;
				} catch (JMSException e) {
					logger.error("Exception sending message to the Broker",e);
					enqueueMessageForQueue(message);
				} catch (Exception e) {
					logger.error("Exception sending message to the Broker",e);
					enqueueMessageForQueue(message);
			
			}
		} else	ConnectionsManager.reloadConnection(GCUBEScope.getScope(message.getScope()));
	}

	/**
	 * Create a random Long
	 * @return a Random Long-String
	 */
	private  String createRandomString() {
		Random random = new Random(System.currentTimeMillis());
		long randomLong = random.nextLong();
		return Long.toHexString(randomLong);
	}

	public static Producer getSingleton() {
		return singleton;
	}

	public void setSingleton(Producer singleton) {
		this.singleton = singleton;
	}

	/**
	 * 
	 * enqueue the message
	 * @param message the message
	 */
	private void enqueueMessageForQueue(GCUBEMessage message){
		try {
			synchronized (messageForQueue) {
				if (messageForQueue.size() >=MAX_ACCOUNTING_QUEUE_SIZE){
					logger.error("Reached Maximum queue size, message discarded");
					logger.error(message.toString());
				} else messageForQueue.add(message);	
			}
		}catch  (Exception e) {
			logger.error("Error enqueuing Message : "+ message.toString(),e);
		}

	}
	

	public void run() {
		int sizeQueue = 0;
		int sizeTopic = 0;
		while (true){
			
			synchronized(messageForQueue){
				sizeQueue = messageForQueue.size();
			}
			
			
			if ((sizeQueue ==0) && (sizeTopic == 0)){
				try {
					Thread.sleep(1000*600);
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
			else {
				if (sizeQueue >0){
					GCUBEMessage message = null;
					synchronized (messageForQueue) 
					{
						 message = messageForQueue.poll();
					}
					this.sendMessageToQueue(message);
					try {
						Thread.sleep(1000*60);
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
				}
			
			}
		}
		
	}
	
}
