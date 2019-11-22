package org.gcube.common.queueManager.test.model;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.gcube.common.queueManager.test.TestCommon;

public class ExtractedConsumerLogic {

	private static boolean received=false;
	
	public static void main(String[] args) throws JMSException{
		ActiveMQConnectionFactory factory=new ActiveMQConnectionFactory(TestCommon.config.getBrokerEndpoint());
		MessageListener exec=new MessageListener() {
			
			public void onMessage(Message arg0) {
				System.out.println("Received msg :D :D "+arg0);
				received=true;
			}
		};  
		QueueConnection conn=factory.createQueueConnection();
		conn.setClientID(exec.toString());
		conn.start();
		QueueSession session= conn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		String topic=TestCommon.config.getServiceClass()+"."+TestCommon.config.getServiceName()+"."+TestCommon.topics[0];
		System.out.println("Creating queue "+topic);
		Queue queue=session.createQueue(topic);
		
		QueueReceiver receiver =session.createReceiver(queue);
		receiver.setMessageListener(exec);
//		((ActiveMQMessageConsumer)receiver).start();
		System.out.println("Receiver setted");
		while(!received){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		System.out.println("DONE");
	}
	
}
