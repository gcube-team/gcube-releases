package org.gcube.informationsystem.notifier.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.gcube.common.core.faults.GCUBEFault;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.impl.entities.Producer;
import org.gcube.informationsystem.notifier.stubs.IsOngoingRequest;
import org.gcube.informationsystem.notifier.stubs.IsOngoingResponse;
import org.gcube.informationsystem.notifier.stubs.ListTopics;
import org.gcube.informationsystem.notifier.stubs.ListTopicsResponse;
import org.gcube.informationsystem.notifier.stubs.RegisterTopicMessage;
import org.gcube.informationsystem.notifier.stubs.RemoveNotifier;
import org.gcube.informationsystem.notifier.stubs.RemoveNotifierResponse;
import org.gcube.informationsystem.notifier.stubs.RemoveSubscriber;
import org.gcube.informationsystem.notifier.stubs.RemoveSubscriberResponse;
import org.gcube.informationsystem.notifier.stubs.RemoveSubscriptionResponse;
import org.gcube.informationsystem.notifier.stubs.SubscribeMessage;
import org.gcube.informationsystem.notifier.stubs.SubscribeToTopicResponse;
import org.gcube.informationsystem.notifier.stubs.UnregisterTopicResponse;
import org.gcube.informationsystem.notifier.thread.RegisterTopicThread;
import org.gcube.informationsystem.notifier.util.EPR;
import org.gcube.informationsystem.notifier.util.RegistrationEventHandlerImpl;
import org.gcube.informationsystem.notifier.util.TopicMapping;
import org.globus.wsrf.security.SecurityManager;


/**
 *  notifier Service Class
 * 
 * @author Lucio Lelii
 *
 */
public class Notifier {


	protected final GCUBELog logger = new GCUBELog(this);

		
	//private SubscriptionManagerServiceAddressingLocator subscriptLocator = new SubscriptionManagerServiceAddressingLocator();
	//private SubscriptionManager subscriptPort = null;

	/** Default Constructor
	 * 
	 * @throws RemoteException Exception
	 */
	public Notifier() throws RemoteException {}


	/**
	 * Subscribes the given Consumer to the notifier(s) that expose the given topic<br>
	 * This methods directly subscribe the Consumer to the available notifiers and maintains <br>
	 * association btw subscriber and topic in order to subscribe it to if other Consumers expose <br>
	 * the same topic  
	 * 
	 * 
	 * @param message Contains the Consumer Epr and the topic to subscribe to
	 * @return SubscribeToTopicResponse Contains an array of NotifierEPR that already espose the topic 
	 * @throws GCUBEFault Exception
	 */

	public SubscribeToTopicResponse subscribeToTopic(SubscribeMessage message) throws GCUBEFault {
		logger.info("subscribeToTopic method");
		long start= System.currentTimeMillis();
		NotifierResource resource;
		List<EndpointReferenceType> notifiersEprs; 
		try{
			TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping;
			resource = this.getResource();
			if (resource.isTopicPresent(QName.valueOf(message.getTopic()))){
				topicMapping= resource.getTopicMappingByQName(QName.valueOf(message.getTopic()));
			}else {
				topicMapping= new TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl>(QName.valueOf(message.getTopic()));
				topicMapping.setRegistrationEventHandler(new RegistrationEventHandlerImpl(QName.valueOf(message.getTopic())));
				resource.addTopicMapping(topicMapping);
			}
			Consumer consumer = new Consumer(message.getEndpointReference());
			consumer.setPrecondition(message.getPrecondition());
			consumer.setSelection(message.getSelection());
			topicMapping.addConsumer(consumer);
			resource.store();
			notifiersEprs= topicMapping.getProducers();// .getRegistrationEventHandler().getSubscriptionEPRByConsumer(consumer);
			logger.info("subscribed "+message.getEndpointReference() +" for the topic "+ message.getTopic());
			logger.debug("there are "+resource.getTopicMappingList().size()+" in the array");
			logger.debug("there are "+notifiersEprs.size() +"producer for this topic");
		}catch(Exception e){
			logger.error("error subscribing "+message.getEndpointReference()+" for the topic "+message.getTopic(),e );
			throw new GCUBEFault(e, "error subscribing "+message.getEndpointReference()+" for the topic "+message.getTopic() );
		}
		long end= System.currentTimeMillis();
		logger.debug("SubscribeToTopic finished his work with topic "+message.getTopic()+" in "+(end-start)+" ms");
		EndpointReferenceType[] notifierEprArray= notifiersEprs.toArray(new EndpointReferenceType[notifiersEprs.size()]);
		logger.debug("producer address returned are "+notifierEprArray.length);
		
		//only for logging
		for (EndpointReferenceType epr: notifierEprArray)
			logger.debug("returned subscription resource is: "+epr);
		//end logging
		return new SubscribeToTopicResponse(notifierEprArray); 

	}

	/**
	 * This method allows Consumer to unsubscribe from a topic. In order to unsubscribe <br>
	 * from a topic it destroys subscription Resources on the Notifiers side ( that are collected <br>
	 * into the stateful resource.
	 * 
	 * @param message Contains the Consumer Epr and the topic to unsubscribe to
	 * @return RemoveSubscriptionResponse
	 * @throws GCUBEFault Exception
	 */
	public  RemoveSubscriptionResponse removeSubscription (SubscribeMessage message) throws GCUBEFault{
		try {
			logger.debug("removing subscription "+message.getTopic().toString()+" with precondition"+message.getPrecondition());
			
			EndpointReferenceType subscriberEPR= message.getEndpointReference();
			if (subscriberEPR == null || message.getTopic()== null ||  message.getTopic().compareTo("")==0) {
				throw ServiceContext.getContext().getDefaultException("MissingInputParamters",null).toFault("MissingInputParamters");
			}
			QName topic = QName.valueOf(message.getTopic());
			NotifierResource resource = this.getResource();
			TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping = resource.getTopicMappingByQName(topic);
			Consumer consumer= new Consumer(subscriberEPR);
			consumer.setPrecondition(message.getPrecondition());
			consumer.setSelection(message.getSelection());
			topicMapping.removeConsumer(consumer);
			if (topicMapping.getConsumerList().size()==0 && topicMapping.getProducerList().size()==0)
				resource.getTopicMappingList().remove(topicMapping.getTopic().toString());
			logger.debug("the subscription has been removed without errors");
		}
		catch (Exception e) {
			logger.error(" Error removing subscription "+ e);
			throw ServiceContext.getContext().getDefaultException(" Error removing subscription",e).toFault(" Error removing subscription");
		}
		
		return new RemoveSubscriptionResponse();
	}

	/**
	 * Allows Notifiers to register their topics to Notifier. This method controls also <br>
	 * wheather Subscribers exist for this topics and automatically subscribes them to the topics <br> 
	 * 
	 * @param message Contains the EnpointReferenceType of the Notifier and the list of Topis to exposes
	 * @return RegisterTopicResponse
	 * @throws GCUBEFault Exception
	 */

	public  String registerTopic(RegisterTopicMessage message) throws GCUBEFault {
		logger.debug("registerTopic("+message.getVectorTopic().length+" topics) called  ");	
		EndpointReferenceType notifierEPR  = message.getEndpointReference();
		RegisterTopicThread rtt=new RegisterTopicThread(message.getVectorTopic(), notifierEPR);
		ServiceContext.getContext().setScope(rtt, ServiceContext.getContext().getScope());
		rtt.start();
		return "";
	}

	
	/**
	 * Method called by a notifier that have to unregister its topics from Notifier
	 * 
	 * 
	 * @param message the Message definied in the Service WSDl
	 * @return UnregisterTopicResponse 
	 * @throws GCUBEFault Exception
	 */
	public synchronized UnregisterTopicResponse unregisterTopic(RegisterTopicMessage message) throws GCUBEFault {
		logger.info("unregister topic method");
		//taking parameters
		EndpointReferenceType notifierEpr = message.getEndpointReference();
		boolean hasErrors=false;
		String errorsString="";
		NotifierResource resource;
		try{
			resource= this.getResource();
		}catch(Exception e){
			logger.error("error retrieving resource");
			throw new GCUBEFault(e,"error retrieving resource");
		}
		if (notifierEpr == null)
			throw ServiceContext.getContext().getDefaultException(" Error taking parameters",null).toFault("Error taking parameters");
		for (String topicString: message.getVectorTopic() ){
			try{
				TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping=resource.getTopicMappingByQName(QName.valueOf(topicString));
				topicMapping.removeProducer(new Producer(notifierEpr));
				logger.debug("removed producer with epr "+notifierEpr+" from "+topicMapping.getTopic().toString());
				if (topicMapping.getConsumerList().size()==0 && topicMapping.getProducerList().size()==0)
					resource.getTopicMappingList().remove(topicMapping.getTopic().toString());
			}catch(Exception e){
				logger.error("error unregistering the producer to the topic "+topicString+" for "+notifierEpr,e);
				hasErrors=true;
				errorsString+=topicString+" for "+notifierEpr;
			}
		}
		
		resource.store();
		if (hasErrors) throw new GCUBEFault("error unregistering the producer for the topic "+errorsString);		
		return new UnregisterTopicResponse();
	}

	/**
	 * Retrieve the list of topics published in the Notifier
	 * 
	 * @param topics same as Void
	 * @return the list of topics
	 * @throws GCUBEFault  Exception
	 */
	public ListTopicsResponse listTopics (ListTopics topics) throws GCUBEFault{

		ListTopicsResponse topicList = new ListTopicsResponse();
		try {
			List<String> listTopic=this.getResource().getListTopic();
			topicList.setTopicList(listTopic.toArray(new String[listTopic.size()]));
			logger.info("the topic registered are "+topicList.getTopicList().length);
		} catch (Exception e) {
			throw ServiceContext.getContext().getDefaultException(" Error Listing Topics ",e).toFault("Error Listing Topics");
		}
		return topicList;

	}

	/**
	 *
	 * @param epr the notifier epr
	 * @return a list of topics
	 * @throws GCUBEFault Exception 
	 */
	public String[] listTopicForNotifier (EndpointReferenceType epr) throws GCUBEFault{
		//TODO
		ArrayList<String> topicList = new ArrayList<String>();
		return topicList.toArray(new String[0]);
	}


	/**
	 * Removes the notifiers from the topics
	 * 
	 * @param notifiers array of EPR
	 * @return same as void 
	 * @throws GCUBEFault Exception
	 */
	public RemoveNotifierResponse removeNotifier(RemoveNotifier notifiers)throws GCUBEFault {

		try {
			NotifierResource res= this.getResource();
			for (TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topic: res.getTopicMappingList().values())
				for (EndpointReferenceType epr: notifiers.getEndpointReference())
					topic.removeProducer(new Producer(epr)); 
		} catch (Exception e) {
			logger.error("error removing notifiers",e);
			throw ServiceContext.getContext().getDefaultException(" Error Removing Notifiers ",e).toFault("Error Removing Notifiers");
		}
		return new RemoveNotifierResponse();

	}

	/**
	 * 
	 * 
	 * @param subscribers same as void
	 * @return same as void
	 * @throws GCUBEFault Exception
	 */
	public RemoveSubscriberResponse removeSubscriber(RemoveSubscriber subscribers)throws GCUBEFault {
		//this method is unused (review it)
		try {
			NotifierResource res= this.getResource();
			for (TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topic: res.getTopicMappingList().values())
				for (EndpointReferenceType epr: subscribers.getEndpointReference())
					topic.removeConsumer(new Consumer(epr)); 
		} catch (Exception e) {
			logger.error("error removing subscribers",e);
			throw ServiceContext.getContext().getDefaultException(" Error Removing subscribers ",e).toFault("Error Removing subscribers");
		}
		return new RemoveSubscriberResponse();

	}

	
	
	
	/**
	 *  
	 * @param topic the topic
	 * @return array of EndpointRefenfence
	 * @throws GCUBEFault   Exception
	 */
	public EndpointReferenceType [] getSubscribersForTopic (String topic) throws GCUBEFault {

		EndpointReferenceType[] subscribers;
		try {
			TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping= this.getResource().getTopicMappingByQName(QName.valueOf(topic));
			subscribers =topicMapping.getConsumers().toArray(new EndpointReferenceType[0]);
			return subscribers;
		} catch (RemoteException e) {
			logger.error("error retrieving subscribers for this topic",e);
			throw ServiceContext.getContext().getDefaultException(" Error Getting Subscribers ",e).toFault("Error Getting Subscribers ");

		} catch (Exception e) {
			logger.error("error retrieving subscribers for this topic",e);
			throw ServiceContext.getContext().getDefaultException(" Error Getting Subscribers ",e).toFault("Error Getting Subscribers ");
		}


	}

	/**
	 * return the resource
	 * 
	 * @return NotifierResource
	 * @throws RemoteException Exception
	 */
	protected NotifierResource getResource() throws RemoteException {
		Object resource = null;
		try {
			resource = NotifierContext.getPortTypeContext().getWSHome().find(NotifierContext.getContext().makeKey("NotifierResource"+"_"+ServiceContext.getContext().getScope().toString().replace("/", "_")));
		} catch (Exception e) {
			logger.error(" Unable to access resource", e);
		}
		NotifierResource notifierResource = (NotifierResource) resource;
		return notifierResource;
	}

	/**
	 * 
	 * Log the caller 
	 * @param methodName the method name
	 */
	public void logSecurityInfo(String methodName) {
		logger.info(" Security info for method "+ methodName );
		String identity = SecurityManager.getManager().getCaller();
		logger.info(" The caller is: " + identity);
	}
	
	/**
	 * controls if the register topic operation is still onGoing
	 * 
	 * @param isOngoingRequest
	 * @return IsOngoingResponse
	 */
	public IsOngoingResponse isOngoing(IsOngoingRequest request) throws RemoteException{
		logger.debug("isOngoing call - START");
		NotifierResource resource= this.getResource();
		
		boolean[] mask= new boolean[request.getItemList().length];
		for (int i=0; i<request.getItemList().length; i++){
			try{
				//logger.trace("-TEST- isOngoing "+request.getItemList(i).getTopic()+" "+request.getItemList(i).getEndpointReference());
				//logger.trace("-TEST- isOngoing call started["+Thread.currentThread().getId()+"]");
				TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl>  topicMapping = resource.getTopicMappingByQName(QName.valueOf(request.getItemList(i).getTopic()));
				Producer p= topicMapping.getProducerList().get(topicMapping.getProducerList().indexOf(new EPR(request.getItemList(i).getEndpointReference())));
				mask[i]=p.isSubscribed();
			}catch (Exception e){
				logger.warn("problem checking the ongoing registration/unregistration (the topic isn't in the map)");
				mask[i]=false;
			}
		}
		for (boolean b:mask)
			logger.trace(b);
		
		logger.debug("isOngoing call - END");
		return new IsOngoingResponse(mask);
	}
	
}

