package org.gcube.dataanalysis.executor.plugin;

import java.net.Inet4Address;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.dataanalysis.executor.messagequeue.ATTRIBUTE;
import org.gcube.dataanalysis.executor.messagequeue.Consumer;
import org.gcube.dataanalysis.executor.messagequeue.QCONSTANTS;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericWorkerPlugin extends Plugin<GenericWorkerPluginDeclaration> {

	/**
	 * Logger
	 */
	private static Logger workerLogger = LoggerFactory.getLogger(GenericWorkerPlugin.class);

	public GenericWorkerPlugin(GenericWorkerPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
		workerLogger.debug("contructor");
	}

	@Override
	protected void onStop() throws Exception {
		workerLogger.trace("GenericWorkerPlugin: Stopped");
	}

	public static Consumer activeT;
	// public static Producer activeP;
	public static QueueWatcher qWatcher;
	public static Hashtable<String, String> sessionBlackList = new Hashtable<String, String>();
	public static Boolean processing;
	public static Boolean creating;
	public static ConsumerWatcher2 consumerwatcher;
	public static Timer consumerWatcherTimer;

	@Override
	public void launch(Map<String, Object> inputs) throws Exception {

		workerLogger.trace("GenericWorkerPlugin: Start");

		// TODO check
		String nodeAddress = Inet4Address.getLocalHost().getHostName();
		
		workerLogger.trace(
				"GenericWorkerPlugin: Inputs: " + inputs + " on node: "
						+ nodeAddress);

		try {
			String uniqueTopicName = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.TOPIC_NAME.name()));
			String user = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_USER.name()));
			String password = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_PASSWORD.name()));
			String queueURL = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_URL.name()));
			String topicResponseName = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.TOPIC_RESPONSE_NAME.name()));
			String session = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QSESSION.name()));
			String erase = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.ERASE.name()));

			// the consumer watcher must always be checked
			if (consumerwatcher == null || consumerWatcherTimer == null) {
				workerLogger.trace("GenericWorkerPlugin: Starting consumer watcher");
				consumerWatcherTimer = new Timer();
				consumerwatcher = new ConsumerWatcher2();
				consumerWatcherTimer.schedule(consumerwatcher, QCONSTANTS.QueueLifeTime, QCONSTANTS.QueueLifeTime);
			}

			if (session == null) {
				workerLogger.trace("GenericWorkerPlugin: Session is null ignoring message");
			} 
			else 
				if (GenericWorkerPlugin.sessionBlackList != null
					&& GenericWorkerPlugin.sessionBlackList.contains(session))
					workerLogger.trace("GenericWorkerPlugin: Session is black listed ... ignoring message");
			else if (getProcessing())
				workerLogger.trace("GenericWorkerPlugin: The worker is processing... ignoring message");
			else if (getCreating())
				workerLogger.trace("GenericWorkerPlugin: The worker is creating... ignoring message");
			else if ((erase != null) && (erase.equals("true"))) {
				workerLogger.trace("GenericWorkerPlugin: Erasing queue command");
				workerLogger.trace("GenericWorkerPlugin: purging session " + session
								+ " on queue " + uniqueTopicName);
				sessionBlackList.put(session, uniqueTopicName);
				workerLogger.trace("GenericWorkerPlugin: topic " + session + "on queue "
								+ uniqueTopicName + " has been blacklisted");
			} else {
				workerLogger.trace("GenericWorkerPlugin: The worker is available");
				setCreating(true);
				boolean eraseCon = eraseConsumer();
				if (eraseCon)
					createConsumer(uniqueTopicName, session, user, password, queueURL, nodeAddress);
				else
					workerLogger.trace("GenericWorkerPlugin: could not erase che consumer ... ignoring message");
				setCreating(false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			workerLogger.error("GenericWorkerPlugin: Error " + e.getLocalizedMessage());
			workerLogger.trace("GenericWorkerPlugin: Completely Finished");
			
		} 
	}

	public static void setProcessing(boolean state) {
		processing = state;
		/*
		 * if (processing) resetWatcher();
		 */
	}

	public static boolean getProcessing() {
		if (processing == null)
			processing = false;

		return processing;
	}

	public static boolean getCreating() {
		if (creating == null)
			creating = false;

		return creating;
	}

	public static void setCreating(boolean state) {
		creating = state;
		/*
		 * if (creating) resetWatcher();
		 */
	}

	public static boolean eraseConsumer() {
		try {
			if (activeT != null) {
				workerLogger
						.trace("GenericWorkerPlugin: deleting the previous consumer");
				activeT.stop();
				activeT.closeSession();
				activeT = null;
				System.gc();
				Thread.sleep(2000);
			}
			workerLogger
					.trace("GenericWorkerPlugin: Previous consumer is offline!");
		} catch (Throwable e) {
			workerLogger
					.trace("GenericWorkerPlugin: Error could not erase Consumer! "
							+ e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void createConsumer(String uniqueTopicName, String session,
			String user, String password, String queueURL, String nodeAddress) {
		try {
			workerLogger.trace("GenericWorkerPlugin: Adding Topic "+ uniqueTopicName + " with session " + session);
			workerLogger.trace("GenericWorkerPlugin: Active Queue is null - creating");
			workerLogger.trace("GenericWorkerPlugin: Creating Consumer");
			QueueManager qm = new QueueManager();
			qm.createAndConnect(user, password, queueURL, uniqueTopicName);
			QueueListener ql = new QueueListener(qm, uniqueTopicName, nodeAddress, workerLogger);
			activeT = new Consumer(qm, ql, ql, uniqueTopicName);
			workerLogger.trace(
					"GenericWorkerPlugin: Active Queue Consumer was created!");
			workerLogger.trace(
					"GenericWorkerPlugin: Creation was set to FALSE");
		} catch (Throwable e) {
			workerLogger.trace(
					"GenericWorkerPlugin: Error could not create Consumer! "
							+ e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	public static void resetGenericWorker() {
		setCreating(true);
		eraseConsumer();
		if (sessionBlackList != null && sessionBlackList.size() > 10) {
			sessionBlackList = null;
			sessionBlackList = new Hashtable<String, String>();
			workerLogger.trace("GenericWorkerPlugin: Refreshing the black list!");
		}
		setCreating(false);
		purgeConsumerWatcher();
		System.gc();
		workerLogger.trace("GenericWorkerPlugin: Reset GW!");
	}

	public static void purgeConsumerWatcher() {
		workerLogger.trace("GenericWorkerPlugin: Stopping - closing all watchers");
		if (consumerwatcher != null) {
			consumerwatcher.cancel();
			if (consumerWatcherTimer != null) {
				consumerWatcherTimer.cancel();
				consumerWatcherTimer.purge();
			}
			consumerwatcher = null;
			consumerWatcherTimer = null;
		}
		workerLogger.trace("GenericWorkerPlugin: Stopping - closed all watchers");
	}

	public class ConsumerWatcher2 extends TimerTask {

		public ConsumerWatcher2() {

		}

		@Override
		public void run() {
			try {
				if (!getProcessing() && !getCreating()) {
					resetGenericWorker();
				}
			} catch (Exception e) {
				System.out.println("GenericWorkerPlugin: ERROR IN RESETTING WATCHER!");
				e.printStackTrace();
			}
		}

	}

}
