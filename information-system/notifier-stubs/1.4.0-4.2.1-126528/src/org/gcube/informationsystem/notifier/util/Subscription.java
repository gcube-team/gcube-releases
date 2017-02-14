package org.gcube.informationsystem.notifier.util;


import javax.xml.namespace.QName;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.thread.RemoveSubscriptionThread;
import org.globus.wsrf.WSNConstants;
import org.globus.wsrf.WSRFConstants;
import org.oasis.wsn.NotificationProducer;
import org.oasis.wsn.Subscribe;
import org.oasis.wsn.SubscribeResponse;
import org.oasis.wsn.TopicExpressionType;
import org.oasis.wsn.WSBaseNotificationServiceAddressingLocator;
import org.oasis.wsrf.properties.QueryExpressionType;


public class Subscription extends SubscriptionObject<EPR, Consumer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4637067173535597897L;
	public static GCUBELog logger= new GCUBELog(Subscription.class);
	
				
	public Subscription(Consumer consumer, EPR producer){
		this.setConsumer(consumer);
		this.setProducer(producer);
		this.setSubscribed(false);
	}
	
	@Override
	public synchronized void removeSubscription() throws Exception {
		// Get a reference to the NotificationProducer portType
		if (this.isSubscribed()){
			try {
				logger.trace("subscription EPR  "+this.getNotificationResourceEnpoint().getEpr());
				RemoveSubscriptionThread rst= new RemoveSubscriptionThread(this.getNotificationResourceEnpoint().getEpr());
				rst.start();
				this.setSubscribed(false);
			}catch(Exception e){
				logger.error("error deleting the subscription ");
				throw e;
			}
		}
	}

	
	
	@Override
	public synchronized void subscribe(QName topic) throws Exception {
		if (!this.isSubscribed()){
			try {
				logger.debug("subscribing for the topic "+topic.toString()+" consumer:"+this.getConsumer().getEpr()+" producer:"+this.getProducer().getEpr());
				long start= System.currentTimeMillis();
				// Create the request to the remote Subscribe() call
				Subscribe request = new Subscribe();
				// Must the notification be delivered using the Notify operation?
				request.setUseNotify(Boolean.TRUE);
				// Indicate what the client's EPR is
				request.setConsumerReference(this.getConsumer().getEpr());
				// The TopicExpression specifies what topic we want to subscribe to
				TopicExpressionType topicExpression = new TopicExpressionType();
				topicExpression.setDialect(WSNConstants.SIMPLE_TOPIC_DIALECT);
				topicExpression.setValue(new QName (topic.getNamespaceURI(), topic.getLocalPart()));
				request.setTopicExpression(topicExpression);
				
				if (this.getConsumer().getPrecondition()!=null)
					request.setPrecondition(new QueryExpressionType(WSRFConstants.XPATH_1_DIALECT, this.getConsumer().getPrecondition()));
								
				
				if (this.getConsumer().getSelection()!=null){
					
				}
				
				if (!Util.isEndpointReachable(this.getProducer().getEpr())){
					logger.error("the producer is unreachable");
					throw new Exception("the producer "+this.getProducer().getEpr().toString()+" is unreachable");
				}
				logger.info("the prducer is reachable");
				if (!Util.isEndpointReachable(this.getConsumer().getEpr())){
					logger.error("the consumer is unreachable");
					throw new Exception("the consumer "+this.getConsumer().getEpr().toString()+" is unreachable");
				}
				logger.info("the consumer is reachable");
				logger.trace("consumer EPR is "+this.getConsumer().getEpr());
				
				// Get a reference to the NotificationProducer portType
				WSBaseNotificationServiceAddressingLocator notifLocator =
					new WSBaseNotificationServiceAddressingLocator();
				NotificationProducer producerPort = notifLocator.getNotificationProducerPort(this.getProducer().getEpr());
				
				logger.trace("near the creation of the new subscription resource");
												
				SubscribeResponse response =producerPort.subscribe(request);
				this.setNotificationResourceEnpoint(new EPR(response.getSubscriptionReference()));
				long end= System.currentTimeMillis();
				logger.trace("time to subscribe a cosumer to a producer is "+(end-start)+" ms");
				logger.info("subscribed client " +this.getConsumer().getEpr().toString() + " to producer "+this.getProducer().getEpr().toString());
			}catch (Exception e) {
				logger.error(" Error subscribing client :" +this.getConsumer().getEpr().toString() + " to producer "+this.getProducer().getEpr().toString());
				this.setSubscribed(false);
				throw e;
			}
			this.setSubscribed(true);
		}
	}
	

}
