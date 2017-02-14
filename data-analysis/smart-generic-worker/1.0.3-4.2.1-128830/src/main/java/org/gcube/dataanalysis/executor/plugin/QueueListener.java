package org.gcube.dataanalysis.executor.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.gcube.dataanalysis.executor.messagequeue.ATTRIBUTE;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QCONSTANTS;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.dataanalysis.executor.scripts.ExecuteScript;
import org.slf4j.Logger;

public class QueueListener implements MessageListener, ExceptionListener {
	private String topicName;

	private Logger logger;
	public static int refreshStatusTime = QCONSTANTS.refreshStatusTime;
	public QueueManager qm;
	public String nodeaddress;
	public static long timeToLive = 0;

	public QueueListener(QueueManager qm, String topicName, String nodeaddress, Logger logger) {

		this.topicName = topicName;
		this.logger = logger;
		this.qm = qm;
		this.nodeaddress = nodeaddress;

	}

	synchronized public void onException(JMSException ex) {
		try {
			logger.trace("GenericWorkerPlugin: AN ERROR OCCURRED ", ex);
		} catch (Exception e) {
			logger.trace("GenericWorkerPlugin: Exception", e);
		}
		logger.trace("GenericWorkerPlugin: JMS Exception occured.  Shutting down client.", ex);
		ex.printStackTrace();
	}

	private void sendStatus(Producer p, String order, String status, String session, Throwable e) throws Exception {
		logger.trace("GenericWorkerPlugin: SENDING THE FOLLOWING : status " + status + " order " + order + " address " + nodeaddress+" session "+session);
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ATTRIBUTE.STATUS.name(), status);
		props.put(ATTRIBUTE.ORDER.name(), order);
		props.put(ATTRIBUTE.NODE.name(), nodeaddress);
		props.put(ATTRIBUTE.QSESSION.name(), session);
		if (e!=null)
			props.put(ATTRIBUTE.ERROR.name(), e.getLocalizedMessage());
		else
			props.put(ATTRIBUTE.ERROR.name(), null);
		if ((status != null) && (order != null) && (nodeaddress != null) && (p != null) && (session != null))
			p.sendMessage(props, timeToLive);
	}

	public void onMessage(Message message) {
		String session=null;
		String order=null;
		Timer statusSenderScheduler = null;
		Producer producer = null;
		try {
			logger.trace("GenericWorkerPlugin:  Incoming message on queue " + topicName);
			if (!GenericWorkerPlugin.getProcessing()) {
				logger.trace("GenericWorkerPlugin:  Ack message ");
				message.acknowledge();
				// if (!processing) {
				logger.trace("GenericWorkerPlugin:  Set Processing to True");
				GenericWorkerPlugin.setProcessing(true);
				logger.trace("GenericWorkerPlugin: Received Message on queue " + topicName);
				logger.trace("GenericWorkerPlugin: ack message");
				HashMap<String, Object> inputs = (HashMap<String, Object>) message.getObjectProperty(ATTRIBUTE.CONTENT.name());
				logger.trace("GenericWorkerPlugin: Getting contents : " + inputs.size());
				session = (String) inputs.get(ATTRIBUTE.QSESSION.name());
				if ((session != null)&&(GenericWorkerPlugin.sessionBlackList!=null)&&(!GenericWorkerPlugin.sessionBlackList.contains(session))) {
					logger.trace("GenericWorkerPlugin: managing session: " + session);
					
					//execute script
					List<String> filenames = (List<String>) inputs.get(ATTRIBUTE.FILE_NAMES.name());
					List<String> fileurls = (List<String>) inputs.get(ATTRIBUTE.FILE_URLS.name());
					String nodeConfigurationString = (String) inputs.get(ATTRIBUTE.CONFIGURATION.name());
					String outputDir = (String) inputs.get(ATTRIBUTE.OUTPUTDIR.name());
					String owner = (String) inputs.get(ATTRIBUTE.OWNER.name());
					String remoteDir = (String) inputs.get(ATTRIBUTE.REMOTEDIR.name());
					String serviceClass = (String) inputs.get(ATTRIBUTE.SERVICE_CLASS.name());
					String serviceName = (String) inputs.get(ATTRIBUTE.SERVICE_NAME.name());
					String scope = (String) inputs.get(ATTRIBUTE.SCOPE.name());
					String script = (String) inputs.get(ATTRIBUTE.SCRIPT.name());
					String arguments = (String) inputs.get(ATTRIBUTE.ARGUMENTS.name());
					String delFiles = (String) inputs.get(ATTRIBUTE.CLEAN_CACHE.name());
					String responseQueue = (String) inputs.get(ATTRIBUTE.TOPIC_RESPONSE_NAME.name());
					String quser = (String) inputs.get(ATTRIBUTE.QUEUE_USER.name());
					String qpwd = (String) inputs.get(ATTRIBUTE.QUEUE_PASSWORD.name());
					String qurl = (String) inputs.get(ATTRIBUTE.QUEUE_URL.name());
					
					boolean deletefiles = delFiles == null ? true : Boolean.parseBoolean(delFiles);
					ExecuteScript scripter = new ExecuteScript(logger);

					logger.trace("GenericWorkerPlugin: Building producer...");

					QueueManager qmR = new QueueManager();
					qmR.createAndConnect(quser, qpwd, qurl, responseQueue);
					producer = new Producer(qmR, responseQueue);
					
					logger.trace("GenericWorkerPlugin: ...Producer built");
					
					// get inputs
					order = "" + inputs.get(ATTRIBUTE.ORDER.name());
					// take producer and ack the status running
					sendStatus(producer,order, ATTRIBUTE.STARTED.name(), session,null);
					statusSenderScheduler = new Timer();
					statusSenderScheduler.schedule(new StatusSender(order,session,producer), 0, refreshStatusTime);

					
					logger.trace("GenericWorkerPlugin: Executing Script");
					// execute script
					scripter.executeScript(filenames, fileurls, outputDir, script, arguments, order, scope, serviceClass, serviceName, owner, remoteDir, session, nodeConfigurationString, deletefiles);
					// executed script
					logger.trace("GenericWorkerPlugin: Sending Back Message");
					// Produce a termination message
					purgeStatusScheduler(statusSenderScheduler);
					sendStatus(producer,order, ATTRIBUTE.FINISHED.name(), session,null);
					logger.trace("GenericWorkerPlugin: message correctly processed");
				} else {
					logger.trace("GenericWorkerPlugin: ignoring message with session " + session );
				}
			} else {
				logger.trace("GenericWorkerPlugin: Processing .. discarding message on queue " + topicName);
				Thread.sleep(2000);
			}
		} catch (Throwable ex) {
			logger.error("GenericWorkerPlugin: An Error occurred - Fatal error reporting ");
			try {
				sendStatus(producer,order, ATTRIBUTE.FATAL_ERROR.name(), session, ex);
			} catch (Exception e2) {
				e2.printStackTrace();
				logger.error("GenericWorkerPlugin: Error in sending fault message: ", e2);
			}
			ex.printStackTrace();
			logger.error("GenericWorkerPlugin: Error in message: ", ex);
		} finally {
			purgeStatusScheduler(statusSenderScheduler);
			purgeProducer(producer);
			GenericWorkerPlugin.setProcessing(false);
		}
	}

	private void purgeProducer(Producer producer){
		if (producer!=null){
			try {
				logger.debug("GenericWorkerPlugin: Stopping producer");
				producer.stop();
				producer.closeSession();
				logger.debug("GenericWorkerPlugin: Producer Stopped");
				producer = null;
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("GenericWorkerPlugin: Error in stopping producer: ", e);
			}
		}
		System.gc();
	}
	private void purgeStatusScheduler(Timer statusSenderScheduler) {
			try {
				if (statusSenderScheduler != null) {
					logger.debug("GenericWorkerPlugin: purging timer task ");
					statusSenderScheduler.cancel();
					statusSenderScheduler.purge();
					statusSenderScheduler = null;
				}
			} catch (Exception e) {
				logger.error("GenericWorkerPlugin: Error purging timer task!!!");
				e.printStackTrace();
			}
	}

	private class StatusSender extends TimerTask {
		String session;
		String order;
		Producer producer;
		public StatusSender(String order,String session,Producer p){
			this.session = session;
			this.order = order;
			this.producer=p;
		}
		@Override
		public void run() {
			try {
				sendStatus(producer,order, ATTRIBUTE.PROCESSING.name(), session,null);
			} catch (Exception e) {
				e.printStackTrace();
				logger.trace("GenericWorkerPlugin: Error Sending Status message");
			}
		}

	}

}
