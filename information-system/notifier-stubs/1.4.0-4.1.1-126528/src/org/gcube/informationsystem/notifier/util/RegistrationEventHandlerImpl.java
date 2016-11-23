package org.gcube.informationsystem.notifier.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.impl.entities.Producer;
import org.globus.wsrf.NoSuchResourceException;

public class RegistrationEventHandlerImpl implements RegistrationEventHandler<Producer, Consumer>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5127492772936157364L;


	public static GCUBELog logger= new GCUBELog(RegistrationEventHandlerImpl.class);
	
	
	private List<Subscription> subscriptionList;
	private QName topic;
	
	public RegistrationEventHandlerImpl(QName topic){
		this.subscriptionList= Collections.synchronizedList(new ArrayList<Subscription>());
		this.topic= topic;
	}
	
	public void setSubscriptionList(List<Subscription> subscriptionList){
		this.subscriptionList= subscriptionList;
	}
	
	public List<Subscription> getSubscriptionList(){
		return this.subscriptionList;
	}
	
	public QName getTopic(){
		return this.topic;
	}
	
	public void setTopic(QName topic){
		this.topic= topic;
	}
	
	public synchronized void onNewConsumer(Consumer consumer, List<Producer> registeredProducerList) throws Exception{
		logger.trace("onNewConsumer method with "+registeredProducerList.size()+" producers");
		for (EPR producer: registeredProducerList){
			try{
				logger.debug("creating subscription for consumer "+consumer.getEpr()+" to prducer "+producer.getEpr());
				Subscription subscription =new Subscription(consumer, producer);
				subscription.subscribe(this.topic);
				logger.debug("creating subscription, is consumer null? "+(subscription.getConsumer()==null));
				subscriptionList.add(subscription);
			}catch(Exception e){
				logger.error("error registering consumer "+consumer.getEpr()+" to the producer");
			}
		}
	}

	public synchronized void onNewProducer(Producer newProducer, List<Consumer> registeredConsumerList) throws Exception {
		logger.trace("onNewProducer method with "+registeredConsumerList.size()+" consumers");
		for (Consumer consumer: registeredConsumerList){
			try{
			logger.debug("creating subscription for consumer "+consumer.getEpr()+" to prducer "+newProducer.getEpr());
			Subscription subscription =new Subscription(consumer, newProducer);
			logger.debug("subscription created");
			subscription.subscribe(this.topic);
			logger.debug("adding it to the subscription list");
			subscriptionList.add(subscription);
			}catch(Exception e){
				logger.error("error registering producer "+newProducer.getEpr()+" with the consumer", e);
			}
		}
		
	}

	public synchronized void onProducerExist(Producer newProducer, Producer oldProducer,
			List<Consumer> registeredConsumerList) throws Exception {
		logger.trace("onProducerExist method");
		this.onRemoveProducer(oldProducer);
		this.onNewProducer(newProducer, registeredConsumerList);
	}

	public synchronized void onRemoveConsumer(Consumer consumer) throws Exception {
		logger.trace("onRemoveConsumer method");
		List<Subscription> subscriptionToRemove= new ArrayList<Subscription>();
		synchronized (this.subscriptionList) {
			for (Subscription subscription : this.subscriptionList){
				if (subscription.getConsumer()==null){
					subscriptionToRemove.add(subscription);
					logger.trace("a subscription has a null consumer, will be removed");
				}else if (subscription.getConsumer().equals(consumer)){
					logger.trace("stopping subscription from "+subscription.getProducer().getEpr() +" to "+subscription.getConsumer().getEpr());
					try{
						subscription.removeSubscription();
					}catch(NoSuchResourceException re){
						logger.error("resource not found  for subscription: "+consumer.getEpr());
					}catch(Exception e){
						logger.error("failed stopping subscription from consumer "+consumer.getEpr(), e);
					}finally{
						subscriptionToRemove.add(subscription);
					}
				}
			}
			this.subscriptionList.removeAll(subscriptionToRemove);
		}
		
	}

	public synchronized void onRemoveProducer(Producer producer) throws Exception {
		logger.trace("onRemoveProducer method");
		List<Subscription> subscriptionToRemove= new ArrayList<Subscription>();
		synchronized (this.subscriptionList) {
			for (Subscription subscription : this.subscriptionList){
				if (subscription.getProducer() !=null && subscription.getProducer().equals(producer)){
					logger.trace("stopping subscription from "+producer.getEpr() +" to "+subscription.getConsumer().getEpr());
					try{
						subscription.removeSubscription();
					}catch(Exception e){
						logger.error("failed stopping subscription from producer "+producer.getEpr(), e);
					}finally{
						subscriptionToRemove.add(subscription);
						logger.trace("removing the subscription");
					}
				}else logger.warn("the producer "+producer.getEpr()+ " seems not exist ");
			}
			this.subscriptionList.removeAll(subscriptionToRemove);
		}
	}
	
	public List<EndpointReferenceType> getSubscriptionEPRByProducer(Producer producer){
		List<EndpointReferenceType> eprSubscriptions= new ArrayList<EndpointReferenceType>(); 
		for (Subscription subscription: this.subscriptionList)
			if (subscription.isSubscribed() && subscription.getProducer().equals(producer))
				eprSubscriptions.add(subscription.getNotificationResourceEnpoint().getEpr());
		return eprSubscriptions;
		
	}

	public List<EndpointReferenceType> getSubscriptionEPRByConsumer(Consumer consumer){
		List<EndpointReferenceType> eprSubscriptions= new ArrayList<EndpointReferenceType>();
		for (Subscription subscription: this.subscriptionList)
			if (subscription.isSubscribed() && subscription.getConsumer().equals(consumer))
				eprSubscriptions.add(subscription.getNotificationResourceEnpoint().getEpr());
		return eprSubscriptions;
	}
	
}
