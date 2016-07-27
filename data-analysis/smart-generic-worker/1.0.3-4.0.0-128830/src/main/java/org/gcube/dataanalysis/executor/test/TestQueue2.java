package org.gcube.dataanalysis.executor.test;

import org.apache.activemq.ActiveMQConnection;
import org.gcube.dataanalysis.executor.messagequeue.Consumer;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.dataanalysis.executor.messagequeue.SimpleListener;

public class TestQueue2 {
	public static void main(String[] args) throws Exception {
		String queueName = "SMQService";// + session;
		String queueURL = "tcp://ui.grid.research-infrastructures.eu:6166";
		// String jmxqueue = "service:jmx:rmi:///jndi/rmi://ui.grid.research-infrastructures.eu:6166/jmxrmi";
		String queueUSER = ActiveMQConnection.DEFAULT_USER;
		String queuePWD = ActiveMQConnection.DEFAULT_PASSWORD;
		QueueManager qm = new QueueManager();
		qm.createAndConnect(queueUSER, queuePWD, queueURL, queueName);

		Producer p = new Producer(qm, queueName);

		/*
		 * QueueManager qm1 = new QueueManager(); qm1.createAndConnect(queueUSER, queuePWD, queueURL);
		 */
		Consumer c1 = new Consumer(qm, new SimpleListener("1"), new SimpleListener("1"), queueName);

		
		for (int i = 0; i < 2; i++) {
			p.sendTextMessage(new String("hello world" + (i + 1)), 0);
			// Thread.sleep(2000);
		}
		
		
		
		
	}

	/*
	 * public class ThreadGenerator implements Runnable {
	 * 
	 * QueueManager qm; String queueName; public ThreadGenerator() {
	 * 
	 * }
	 * 
	 * public void run() { try { Consumer c2 = new Consumer(manager, consumerCallback, errorCallback, topic);
	 * 
	 * } catch (Exception e) { } }
	 * 
	 * }
	 */

}
