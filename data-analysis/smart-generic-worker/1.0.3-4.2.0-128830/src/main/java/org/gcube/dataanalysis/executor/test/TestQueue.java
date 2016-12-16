package org.gcube.dataanalysis.executor.test;

import org.apache.activemq.ActiveMQConnection;
import org.gcube.dataanalysis.executor.messagequeue.Consumer;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.dataanalysis.executor.messagequeue.SimpleListener;

public class TestQueue {
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

		QueueManager qm2 = new QueueManager();
		qm2.createAndConnect(queueUSER, queuePWD, queueURL, queueName);
		QueueManager qm3 = new QueueManager();
		qm3.createAndConnect(queueUSER, queuePWD, queueURL, queueName);
		QueueManager qm4 = new QueueManager();
		qm4.createAndConnect(queueUSER, queuePWD, queueURL, queueName);
		QueueManager qm5 = new QueueManager();
		qm5.createAndConnect(queueUSER, queuePWD, queueURL, queueName);

		/*
		 * QueueManager qm2 = new QueueManager(); qm2.createAndConnect(queueUSER, queuePWD, queueURL);
		 */
		Consumer c2 = new Consumer(qm2, new SimpleListener("2"), new SimpleListener("2"), queueName);
		Consumer c3 = null;

		for (int i = 0; i < 550; i++) {
			p.sendTextMessage(new String("hello world" + (i + 1)), 0);
			// Thread.sleep(2000);
		}

		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		c3 = new Consumer(qm3, new SimpleListener("3"), new SimpleListener("3"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c4 = new Consumer(qm3, new SimpleListener("4"), new SimpleListener("4"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c5 = new Consumer(qm3, new SimpleListener("5"), new SimpleListener("5"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c6 = new Consumer(qm3, new SimpleListener("6"), new SimpleListener("6"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c7 = new Consumer(qm3, new SimpleListener("7"), new SimpleListener("7"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c8 = new Consumer(qm3, new SimpleListener("8"), new SimpleListener("8"), queueName);
		Thread.sleep(10000);
		System.out.println("ADDING NEW CONSUMER");
		Consumer c9 = new Consumer(qm3, new SimpleListener("9"), new SimpleListener("9"), queueName);

		/*
		 * p.stop(); c1.stop(); c2.stop(); c3.stop();
		 * 
		 * p.closeSession();
		 */
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
