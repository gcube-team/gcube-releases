package gr.uoa.di.madgik.execution.engine.monitoring.consumer;

import gr.uoa.di.madgik.environment.notifications.Message;
import gr.uoa.di.madgik.environment.notifications.NotificationMessageListenerI;
import gr.uoa.di.madgik.environment.notifications.SubscriberToTopic;
import gr.uoa.di.madgik.environment.notifications.TextMessage;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.monitoring.resource.ExecutionNodesLoad;
import gr.uoa.di.madgik.notificationhandling.NotificationHandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer class for execution node notifications
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class ExecutionNodesLoadConsumer {
	public static final String EXECUTIONNODEMONITOR = "ExecutionNodeMonitor";

	private Logger log = LoggerFactory.getLogger(ExecutionNodesLoadConsumer.class.getName());

	private String resourceID = null;
	private SubscriberToTopic subscriberToExecutionNodeLoad = null;
	private ExecutionNodesLoad executionNodesLoad;

	private NotificationMessageListenerI executionNodeListener = new LoadConsumerNotification();

	public ExecutionNodesLoadConsumer(String resourceID, ExecutionNodesLoad executionNodesLoad) {
		this.resourceID = resourceID;
		this.executionNodesLoad = executionNodesLoad;
	}

	/**
	 * Subscribes this ExecutionNodeConsumer for notifications concerning the
	 * execution node load.
	 */
	public void subscribeForExecutionNodeLoad() {
		try {
			// Subscribe to Execution node load notifications
			String topicId = NotificationHandling.RegisterNotificationTopic(ExecutionEngine.LOADTOPICNAME, ExecutionEngine.PRODUCERID, null);
			subscriberToExecutionNodeLoad = NotificationHandling.RegisterToNotificationTopic(topicId, resourceID, topicId + resourceID, "", subscriberToExecutionNodeLoad,
					executionNodeListener, null);

			log.debug("Consumer with id " + resourceID + " subscribed for execution node load reporting, to the following topic ID: " + topicId);
		} catch (Exception e) {
			log.error("Failed to subscribe for execution node load reporting with id: " + resourceID, e);
		}
	}

	/**
	 * Unregister this ExecutionNodeConsumer from notifications involving the
	 * execution node load.
	 */
	public void UnregisterFromExecutionNodeLoad() {
		try {
			// Subscribe to index manager creation notifications
			String topicId = NotificationHandling.RegisterNotificationTopic(ExecutionEngine.LOADTOPICNAME, ExecutionEngine.PRODUCERID, null);
			NotificationHandling.UnregisterFromNotificationTopic(topicId, topicId + resourceID, subscriberToExecutionNodeLoad, null);

			log.debug("Consumer unsubscribed from execution node load reporting, with the following topic ID: " + topicId);
		} catch (Exception e) {
			log.error("Failed to unsubscribe for execution node load reporting", e);
		}
	}

	/**
	 * Class that handles the consuming of received execution node load
	 * notifications
	 */
	public class LoadConsumerNotification implements NotificationMessageListenerI {

		@Override
		public void onMessage(Message message) {
			TextMessage textMsg = (TextMessage) message;
			String strMsg = textMsg.getText();
			handleUpdateNotification(strMsg);
		}
	}

	/**
	 * Handles a notification regarding a load update
	 * 
	 * @param strMsg
	 *            the message data
	 */
	private void handleUpdateNotification(String strMsg) {
		String hostName = null;
		Float load = null;

		log.info("Received load notification.");
		if (strMsg != null) {
			String[] parsed = strMsg.split(",");
			for (String token : parsed) {
				String attr = token.split("=")[0];
				String value = token.split("=")[1];

				if (attr.equals("load"))
					load = Float.parseFloat(value);
				else if (attr.equals("hostname"))
					hostName = value;
			}
		}

		if (hostName != null && load != null) {
			String[] parts = hostName.split(":");
			if(parts.length > 1 && parts[1].compareTo("null") == 0)
				log.warn("Received notification from node with port null. Hostname: " + hostName);
			log.info("Hostname " + hostName + " reported load: " + load);
			executionNodesLoad.put(hostName, load);
		}
	}
}
