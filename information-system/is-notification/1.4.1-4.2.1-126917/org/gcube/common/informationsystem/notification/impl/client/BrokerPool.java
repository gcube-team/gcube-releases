package org.gcube.common.informationsystem.notification.impl.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BrokerPool {

	private static GCUBELog logger= new GCUBELog(BrokerPool.class);
	
	private static HashMap<String, BrokerPool> brokerPerScope= new HashMap<String, BrokerPool>();
	
	
	public synchronized static BrokerPool getBrokerPool(GCUBEScope scope){
		if (brokerPerScope.get(scope.toString())==null){
			BrokerPool bp= new BrokerPool(scope);
			brokerPerScope.put(scope.toString(), bp);
			return bp;
		}else
			return brokerPerScope.get(scope.toString());
		
	}
	
	
	private List<NotificationBroker> brokerlist;
	private GCUBEScope scope;
	
	private BrokerPool(GCUBEScope scope){
		this.brokerlist= Collections.synchronizedList(new ArrayList<NotificationBroker>());
		this.scope= scope;
	}
	
	public synchronized void registerTopic(GCUBENotificationTopic topic, BaseNotificationConsumer consumer, GCUBESecurityManager manager) {
		logger.debug("the size of notifications broker list is "+this.brokerlist.size());
		
		for (NotificationBroker nb: this.brokerlist){
			if (!nb.containsTopic(topic.getTopicQName())){
				logger.debug("reusing notification broker for topic "+topic.getTopicQName().toString());
				nb.subscribeForAnyTopic(consumer, topic, manager, scope);
				return;
			}
		}
		
		logger.debug("creating a new notification broker for topic "+topic.getTopicQName().toString());
		//the topic is contained in the list or the list is empty
		NotificationBroker nb=null;
		try {
			nb = new NotificationBroker();
			nb.subscribeForAnyTopic(consumer, topic, manager, scope);
			this.brokerlist.add(nb);
		} catch (Exception e) {
			logger.error("error registering the topic ",e);
		}
		
	}
	
	public synchronized void unregisterTopic(GCUBENotificationTopic topic, GCUBESecurityManager manager){
		if (this.brokerlist.size()==0){
			logger.warn("no topics are registered in this scope");
			return;
		}
		
		logger.debug("the size of notifications broker list is "+this.brokerlist.size());
		
		
		List<NotificationBroker> brokerToRemove= new ArrayList<NotificationBroker>();
		
		boolean find= false;
		for (NotificationBroker nb: this.brokerlist){
			if (nb.containsTopic(topic)){
				nb.unsubscribeFromAnyTopic(topic, manager, scope);
				if (nb.relatedTopic.size()==0){
					nb.stopListening();
					brokerToRemove.add(nb);
				}
				find= true;
				break;
			}
		}
		
		logger.trace("removing "+brokerToRemove.size()+" to notification pool in scope "+this.scope);
		for (NotificationBroker nb: brokerToRemove)
			this.brokerlist.remove(nb);
		
		if (!find) logger.warn("the specific topic with QNAME "+topic.getTopicQName()+" is not registered");

	}
	
}
