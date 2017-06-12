package org.gcube.common.informationsystem.notification.impl.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifierException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.notification.impl.handlers.CheckTopicRegistrationOperationHandler;
import org.gcube.common.informationsystem.notification.impl.handlers.RegisterTopicHandler;
import org.gcube.common.informationsystem.notification.impl.handlers.UnregisterTopicHandler;
import org.gcube.informationsystem.notifier.stubs.IsOngoingRequest;
import org.gcube.informationsystem.notifier.stubs.RegisterTopicMessage;
import org.gcube.informationsystem.notifier.stubs.TopicItem;
import org.globus.wsrf.Topic;

public class GCUBENotifier implements ISNotifier{

		
		
	private final GCUBELog logger= new GCUBELog(this);
		
	/**
	 * {@inheritDoc}
	 */
	public void unregisterISNotification(EndpointReferenceType epr, List<? extends Topic> topics, GCUBESecurityManager man, GCUBEScope... scopes) throws ISNotifierException {
	
		logger.trace("unregisterISNotification method");
		if (scopes==null || scopes.length==0) throw new ISNotifierExceptionImpl("no scopes are passed"); 
				
		for (GCUBEScope scope: scopes){
			try{
				
				RegisterTopicMessage rtm= new RegisterTopicMessage();
				rtm.setEndpointReference(epr);
				String[] topicArray= new String[topics.size()];
				int i=0;
				for (Topic topic: topics)
					topicArray[i++]=topic.getName().toString();
				
				rtm.setVectorTopic(topicArray);
				UnregisterTopicHandler uth= new UnregisterTopicHandler(rtm, scope, man);
				uth.run();
			} catch (Exception e) {	logger.warn("Error unregistering Topics for scope "+scope, e);	}
			
		}
	}

	
	/**
	 * {@inheritDoc}
	 */
	public void registerISNotification(EndpointReferenceType epr, List<? extends Topic> topics, GCUBESecurityManager man, GCUBEScope... scopes) throws ISNotifierException {
		logger.trace("registerISNotification method");
		
		if (scopes==null || scopes.length==0) throw new ISNotifierExceptionImpl("no scopes are passed");
		long start= System.currentTimeMillis();
		for (GCUBEScope scope: scopes){
			try{
				logger.debug("trying to register "+epr.toString()+" in scope "+scope.toString());
				
				RegisterTopicMessage rtm= new RegisterTopicMessage();
				rtm.setEndpointReference(epr);
				String[] topicArray= new String[topics.size()];
				int i=0;
				for (Topic topic: topics)
					topicArray[i++]=topic.getName().toString();
				rtm.setVectorTopic(topicArray);	
				
				RegisterTopicHandler rth= new RegisterTopicHandler(rtm,scope, man);
				rth.run();
			} catch (Exception e) {	logger.warn("Error registering Topics for scope "+scope, e); }
		}
		long end= System.currentTimeMillis();
		logger.trace("registerISNotification finished in "+(end-start)+" ms");
	}

	
	/**
	 * {@inheritDoc}
	 */
	public <T extends BaseNotificationConsumer> void registerToISNotification( T consumer, List<GCUBENotificationTopic> topics, GCUBESecurityManager man, GCUBEScope... scopes) throws ISNotifierException {
		logger.trace("registerToISNotification method");
		if (scopes==null || scopes.length==0) throw new ISNotifierExceptionImpl("the scopes parameter is empty or null");
		for (GCUBEScope scope: scopes){
				BrokerPool bp= BrokerPool.getBrokerPool(scope);
				for (GCUBENotificationTopic topic: topics)
					bp.registerTopic(topic, consumer, man);
		}
		logger.trace("registerToISNotification finished");
	}
		
	

	
	/**
	 * {@inheritDoc}
	 */
	public void unregisterFromISNotification( GCUBESecurityManager man, List<GCUBENotificationTopic> topics,GCUBEScope... scopes) throws ISNotifierException {
		
		if (scopes==null || scopes.length==0) throw new ISNotifierExceptionImpl("no scopes are passed");
		
		for (GCUBEScope scope: scopes){
			BrokerPool bp= BrokerPool.getBrokerPool(scope);
			for (GCUBENotificationTopic topic: topics){
				bp.unregisterTopic(topic, man);
			}
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean[] isTopicRegistered(GCUBESecurityManager securityManager, GCUBEScope scope, List<TopicData> topics)
			throws ISNotifierException {
		IsOngoingRequest request= new IsOngoingRequest();
		List<TopicItem> topicItems=new ArrayList<TopicItem>();
		for (TopicData topic: topics)
			topicItems.add(new TopicItem(topic.getEpr(), topic.getTopic().toString()));
		
		request.setItemList(topicItems.toArray(new TopicItem[0]));
		CheckTopicRegistrationOperationHandler checkTopicRegHandler=new CheckTopicRegistrationOperationHandler(request, scope, securityManager);
		try {
			checkTopicRegHandler.run();
		} catch (Exception e) {
			logger.error("Error checking for the registration operation ",e);
			throw new ISNotifierExceptionImpl("Error checking for the registration operation");
		}
		return checkTopicRegHandler.getReturnValue();
	}
	
	
	private class ISNotifierExceptionImpl extends ISNotifierException{
		
		/**
		 * generated serial Version ID
		 */
		private static final long serialVersionUID = 3451898024491226477L;

		public ISNotifierExceptionImpl(String message){
			super(message);
		}
	}

}
