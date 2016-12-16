package org.gcube.dataanalysis.executor.plugin;

/*
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.lifetime.State.Done;
import org.gcube.common.core.utils.handlers.lifetime.State.Failed;
import org.gcube.common.core.utils.handlers.lifetime.State.Running;
import org.gcube.dataanalysis.executor.messagequeue.ATTRIBUTE;
import org.gcube.dataanalysis.executor.messagequeue.Consumer;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QCONSTANTS;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.vremanagement.executor.plugin.ExecutorTask;
import org.gcube.vremanagement.executor.state.TaskRuntime;

public class GenericWorkerPlugin1 extends GCUBEHandler<TaskRuntime> implements ExecutorTask {

	@Override
	public void stop() throws UnsupportedOperationException, Exception {
		this.getLogger().trace("GenericWorkerPlugin: Stopped");
	}

	public static Hashtable<String, Consumer> activeTs;
	public static Hashtable<String, Producer> activePs;
	public static Hashtable<String, QueueWatcher> qWatchers;
	public static Hashtable<String, String> qBlackList;
	public static Boolean processing;

	public static synchronized void setProcessing(boolean state) {
		processing = state;
	}

	@Override
	public void run() throws Exception {
		this.setState(Running.INSTANCE);
		this.getLogger().trace("GenericWorkerPlugin: Start");
		TaskRuntime runtime = this.getHandled();
		String nodeAddress = GHNContext.getContext().getHostname();

		Map<String, Object> inputs = runtime.getInputs();
		this.getLogger().trace("GenericWorkerPlugin: Inputs: " + inputs + " on node: " + nodeAddress);

		try {
			String uniqueTopicName = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.TOPIC_NAME.name()));
			String user = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_USER.name()));
			String password = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_PASSWORD.name()));
			String queueURL = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QUEUE_URL.name()));
			String topicResponseName = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.TOPIC_RESPONSE_NAME.name()));
			String session = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.QSESSION.name()));
			String erase = ScriptIOWorker.getString((String) inputs.get(ATTRIBUTE.ERASE.name()));

			if ((uniqueTopicName != null) && (qWatchers != null)) {
				long tq = System.currentTimeMillis();
				this.getLogger().trace("GenericWorkerPlugin: Controlling old queues");

				// controlling old queues
				List<String> qToErase = new ArrayList<String>();
				for (String qkey : qWatchers.keySet()) {
					if (qWatchers.get(qkey).isTooMuch()) {
						this.getLogger().trace("GenericWorkerPlugin: Erasing inactive queue " + qkey);
						qToErase.add(qkey);
					}
				}

				int qSize = qToErase.size();
				for (int i = 0; i < qSize; i++){
					this.getLogger().trace("GenericWorkerPlugin: Performing Erasing ..." + qToErase.get(i));
					eraseTopic(qToErase.get(i));
					this.getLogger().trace("GenericWorkerPlugin: Erased ..." + qToErase.get(i));
				}
				
				this.getLogger().trace("GenericWorkerPlugin: Controlled old queues in " + (System.currentTimeMillis() - tq)+" ms");
			}

			if (session == null) {
				this.getLogger().trace("GenericWorkerPlugin: Session is null ignoring message");
			} else {
				if ((erase != null) && (erase.equals("true"))) {
					this.getLogger().trace("GenericWorkerPlugin: purging session " + session+ " on queue "+uniqueTopicName);
//					eraseTopic(uniqueTopicName);
					if (qBlackList!=null)
						qBlackList.put(session, uniqueTopicName);
					this.getLogger().trace("GenericWorkerPlugin: topic " + session + "on queue "+uniqueTopicName+" has been purged");
				} else {
					if (getProcessing()) {
						this.getLogger().trace("GenericWorkerPlugin: Worker is Computing - Ignoring Request");
						if ((qWatchers!=null)&&(qWatchers.get(uniqueTopicName)!=null)){
								qWatchers.get(uniqueTopicName).reset();
								this.getLogger().trace("GenericWorkerPlugin: Queue yet managed - Queue LifeTime Reset");
						}
					} else {
						this.getLogger().trace("GenericWorkerPlugin: Adding Topic " + uniqueTopicName + " with session " + session);
						// activate queue manager
						if (activeTs == null) {
							this.getLogger().trace("GenericWorkerPlugin: Active Queues are null - recreating");
							activeTs = new Hashtable<String, Consumer>();
							activePs = new Hashtable<String, Producer>();
							qWatchers = new Hashtable<String, QueueWatcher>();
						}
						if (activeTs.get(uniqueTopicName) == null) {
							this.getLogger().trace("GenericWorkerPlugin: Creating new topic producer for topic " + topicResponseName);
							QueueManager qm1 = new QueueManager();
							qm1.createAndConnect(user, password, queueURL, topicResponseName);
							activePs.put(uniqueTopicName, new Producer(qm1, topicResponseName));
							this.getLogger().trace("GenericWorkerPlugin: Creating new queue consumer for queue " + uniqueTopicName);
							QueueManager qm = new QueueManager();
							qm.createAndConnect(user, password, queueURL, uniqueTopicName);
							QueueListener ql = new QueueListener(qm, uniqueTopicName, nodeAddress, this.logger);
							activeTs.put(uniqueTopicName, new Consumer(qm, ql, ql, uniqueTopicName));
							qWatchers.put(uniqueTopicName, new QueueWatcher(QCONSTANTS.QueueLifeTime));
							this.getLogger().trace("GenericWorkerPlugin: New Queue added!");
						} else {
							qWatchers.get(uniqueTopicName).reset();
							this.getLogger().trace("GenericWorkerPlugin: Queue yet managed - Queue LifeTime Reset");
						}
					}
				}

				if (activeTs != null)
					this.getLogger().trace("GenericWorkerPlugin: Queues size is currently " + activeTs.size());
				else
					this.getLogger().trace("GenericWorkerPlugin: Queue is currently null");
			}
			this.getLogger().trace("GenericWorkerPlugin: End . Stopping");
			stop();
			this.getLogger().trace("GenericWorkerPlugin: Finished");
			this.setState(Done.INSTANCE);

		} catch (Exception e) {
			e.printStackTrace();
			this.getLogger().error("GenericWorkerPlugin: Error " + e.getLocalizedMessage());
			this.getLogger().trace("GenericWorkerPlugin: End");
			stop();
			this.getLogger().trace("GenericWorkerPlugin: Completely Finished");
			this.setState(Failed.INSTANCE);
		}
	}

	public static  void eraseTopic(String topicName) throws Exception {
		if (activeTs != null) {
			System.out.println("->getting consumer");
			Consumer consumer = GenericWorkerPlugin1.activeTs.get(topicName);
			System.out.println("->got consumer");
			try {
				if (consumer != null) {
					System.out.println("->stopping consumer");
					consumer.stop();
					System.out.println("->closing consumer session");
					consumer.closeSession();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("->removing consumer from list");
			GenericWorkerPlugin1.activeTs.remove(topicName);
			
			System.out.println("->getting producer");
			Producer producer = GenericWorkerPlugin1.activePs.get(topicName);
			try {
				if (producer != null) {
					System.out.println("->stopping producer");
					producer.stop();
					System.out.println("->closing producer session");
					producer.closeSession();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("->removing producer from list");
			GenericWorkerPlugin1.activePs.remove(topicName);
			System.out.println("->removing watcher from list");
			GenericWorkerPlugin1.qWatchers.remove(topicName);
			System.out.println("->all done");
		}
		else
			System.out.println("->activeTS is null");
	}

	public static synchronized boolean getProcessing() {
		if (processing == null)
			processing = false;

		return processing;
	}

}
*/

public class GenericWorkerPlugin1 {
	
}
