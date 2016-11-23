package org.gcube.informationsystem.notifier.util;

import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;

public interface RegistrationEventHandler<P extends EPR,C extends EPR> {
		
	public void onProducerExist(P newProducer, P oldProducer, List<C> registeredConsumerList) throws Exception;
	
	public void onNewConsumer(C consumer, List<P> registeredProducerList) throws Exception;
	
	public void onRemoveConsumer(C consumer) throws Exception;
	
	public void onNewProducer(P newProducer, List<C> regsteredConsuerList) throws Exception;
	
	public void onRemoveProducer(P producer) throws Exception;
	
	public List<EndpointReferenceType> getSubscriptionEPRByConsumer(C consumer);
	
	public List<EndpointReferenceType> getSubscriptionEPRByProducer(P producer);
	
}
