package org.gcube.informationsystem.notifier.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;

public class TopicMapping<PRODUCER extends EPR, CONSUMER extends EPR, RE extends RegistrationEventHandler<PRODUCER,CONSUMER>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final GCUBELog logger = new GCUBELog(TopicMapping.class);
	
	private RE registrationEventHandler;
	
	private List<PRODUCER> producerList;
	private List<CONSUMER> consumerList;
		
	private QName topic;
	
	public TopicMapping(QName topic){
		producerList= Collections.synchronizedList(new ArrayList<PRODUCER>());
		consumerList= Collections.synchronizedList(new ArrayList<CONSUMER>());
		this.topic= topic;
		logger.info("creating new TopicMapping for the topic "+topic.toString());
	}
	
	public void setTopic(QName topic) {
		this.topic = topic;
	}
	
	public QName getTopic(){
		return this.topic;
	}
		
	public synchronized void setProducerList(List<PRODUCER> producerList){
		this.producerList=producerList;
	}
	
	public synchronized List<PRODUCER> getProducerList(){
		return this.producerList;
	}
	
	public List<CONSUMER> getConsumerList() {
		return consumerList;
	}

	public synchronized void setConsumerList(List<CONSUMER> consumerList) {
		this.consumerList = consumerList;
	}

	
	public void setRegistrationEventHandler(RE registrationEventHandler) throws Exception{
		this.registrationEventHandler= registrationEventHandler;
	}
	
	public RE getRegistrationEventHandler() {
		return registrationEventHandler;
	}

		
	public synchronized void addConsumer(CONSUMER consumer) throws Exception{
		synchronized (consumerList) {
			if (!consumerList.contains(consumer)){
				logger.debug("the consumer is not already registered");
				registrationEventHandler.onNewConsumer(consumer, producerList);
				consumerList.add(consumer);
			}else logger.warn("the consumer is already registered for this topic");
		}
	}
	
	public synchronized void removeConsumer(CONSUMER consumer) throws Exception{
		try{
			registrationEventHandler.onRemoveConsumer(consumer);
			logger.trace("has the consumer been removed correctly? "+consumerList.remove(consumer));
		}catch(Exception e) {logger.warn("error removing subscription",e); e.printStackTrace();}
	}
	
	public synchronized void removeProducer(PRODUCER producer)  throws Exception{
		registrationEventHandler.onRemoveProducer(producer);
		producerList.remove(producer);
		logger.debug("producer "+producer.getEpr()+" removed");
	}
	
	public synchronized void addProducer(PRODUCER producer)  throws Exception{
		synchronized (producerList) {
			if (!producerList.contains(producer)){
				logger.debug("the producer does not exist, adding it");
				registrationEventHandler.onNewProducer(producer, consumerList);
				logger.debug("producer created, trying to add it to the prodcuer List");
				producerList.add(producer);
			} else {
				logger.debug("trying to deleate the old subscription and creating a new one");
				int oldProducerIndex= producerList.indexOf(producer);
				PRODUCER oldProducer= producerList.get(oldProducerIndex);
				registrationEventHandler.onProducerExist(producer, oldProducer, consumerList);
				producerList.remove(oldProducerIndex);
				producerList.add(producer);
			}
		}
		
	}
	
	public synchronized List<EndpointReferenceType> getProducers(){
		List<EndpointReferenceType> eprs= new ArrayList<EndpointReferenceType>();
		for (PRODUCER prod :this.producerList)
			eprs.add(prod.getEpr());
		return eprs;
	}
	
	public synchronized List<EndpointReferenceType> getConsumers(){
		List<EndpointReferenceType> eprs= new ArrayList<EndpointReferenceType>();
		for (CONSUMER cons :this.consumerList)
			eprs.add(cons.getEpr());
		return eprs;
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		TopicMapping tp= (TopicMapping) o;
		return tp.getTopic().equals(this.getTopic());
	}
	
}
