package org.gcube.informationsystem.notifier.thread;

import javax.xml.namespace.QName;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.impl.NotifierContext;
import org.gcube.informationsystem.notifier.impl.NotifierResource;
import org.gcube.informationsystem.notifier.impl.ServiceContext;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.impl.entities.Producer;
import org.gcube.informationsystem.notifier.util.RegistrationEventHandlerImpl;
import org.gcube.informationsystem.notifier.util.TopicMapping;

public class RegisterTopicThread extends Thread {
	
	
	protected final GCUBELog logger = new GCUBELog(this);
	
	private EndpointReferenceType notifierEpr;
	private String[] topicVector;
		
	public RegisterTopicThread(String[] topicVector, EndpointReferenceType notifierEpr){
		this.notifierEpr= notifierEpr;
		this.topicVector= topicVector;
		
	}
	
	public void run() {
		Object resource = null;
		try {
			resource = NotifierContext.getPortTypeContext().getWSHome().find(NotifierContext.getContext().makeKey("NotifierResource"+"_"+ServiceContext.getContext().getScope().toString().replace("/", "_")));
		} catch (Exception e) {
			logger.error(" Unable to access resource", e);
			return;
		}
		Producer producer=new Producer(this.notifierEpr); 
		NotifierResource notifierResource = (NotifierResource) resource;
		for (String topicString: topicVector){
			try{
							
				//Endpoint Reference of notifiers
				QName topic= QName.valueOf(topicString);
				logger.debug("the topic to register is "+topic);
							
				TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping;
				
				if (notifierResource.isTopicPresent(topic)){
					logger.debug("the topic "+topicString+" already exists");
					topicMapping=notifierResource.getTopicMappingByQName(topic);
					topicMapping.addProducer(producer);
				} else{
					logger.debug("creating new topicMapping "+topicString);
					topicMapping= new TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl>(topic);
					topicMapping.setRegistrationEventHandler(new RegistrationEventHandlerImpl(topic));
					topicMapping.addProducer(producer);
					notifierResource.addTopicMapping(topicMapping);
				}
				logger.trace("adding the producer to the topic mapping");
				logger.info("topic "+ topicString+ " registered ");
			}catch(Exception e){
				logger.error("error registering topic", e);
			}
		}
		producer.setSubscriptionFinished();
		notifierResource.store();
	}

}
