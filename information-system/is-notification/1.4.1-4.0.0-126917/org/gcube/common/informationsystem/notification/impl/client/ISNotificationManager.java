package org.gcube.common.informationsystem.notification.impl.client;

import javax.xml.namespace.QName;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.notification.impl.handlers.RemoveSubscriptionHandler;
import org.gcube.common.informationsystem.notification.impl.handlers.SubscribeToTopicHandler;
import org.gcube.informationsystem.notifier.stubs.SubscribeMessage;
import org.gcube.informationsystem.notifier.stubs.SubscribeToTopicResponse;
import org.globus.wsrf.WSNConstants;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsn.NotificationProducer;
import org.oasis.wsn.TopicExpressionType;
import org.oasis.wsn.WSBaseNotificationServiceAddressingLocator;


/**
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class ISNotificationManager{

	private GCUBEScope scope ;
	
	/**
	 *  ISNotificationManager Constructor
	 */
	public ISNotificationManager(GCUBEScope scope){this.scope=scope;}
	
	protected static  final GCUBELog logger=new GCUBELog(ISNotificationManager.class);
	
	/**
	 * 
	 * Subscribes for given Topic using IS-Notifier notification mechanism 
	 * 
	 * @param clientEPR EndpointReferenceType
	 * @param topic the Qualified Name of the topic
	 * @param manager GCUBESecurityManager
	 * @return EndpointReferenceType[]
	 */
	public  EndpointReferenceType [] subscribeEPRToTopic(EndpointReferenceType clientEPR,
			GCUBENotificationTopic topic,GCUBESecurityManager manager ) {
		
		SubscribeToTopicResponse response = null;
		try {
			// I create the message to interact with the Notifier
			SubscribeMessage mess = new SubscribeMessage();
			mess.setEndpointReference(clientEPR);
			mess.setTopic(topic.getTopicQName().toString());
			mess.setPrecondition(topic.getPrecondition());
			mess.setSelection(topic.getSelector());
			//interaction by service handler
			SubscribeToTopicHandler sth= new SubscribeToTopicHandler(mess, this.getScope(),manager);
			int attempt=0;
			boolean repeat=true;
			while(repeat && attempt<=4){
				if (attempt>0)Thread.sleep(20000);
				try{
					sth.run();
					repeat= false;
				}catch (Exception e) {
					logger.warn("error trying to register retrying in 20 secs",e);
				}
				attempt++;
			}
			response= sth.getReturnValue();
			logger.trace("Trying to subscribe to topic" + topic.getTopicQName() + " in "+ this.getScope() + " with ERP "+ clientEPR.toString());
		} catch (Exception e) {logger.error("General Exception ",e);}
		return  response.getEndpointReference();
	}
			
	/**
	 * This method allows to UNsubscribe the client (or another client, the one
	 * specified by the EPR) to a generic "standard" TOPIC (WS-Resource
	 * properties): be careful because the observer EPR should be correct and
	 * should be exactly the same EPR used for subscription
	 *
	 * 
	 * 
	 * @param clientEpr
	 *            the EPR of the WS-Resource that was waiting for a topic change
	 *            notification
	 * @param topic
	 *            the topic to Subscribe to
	 *            
	 * @param manager GCUBESecurityManager
	 *
	 */
	public void  unsubscribeEPRFromTopic(EndpointReferenceType clientEpr,GCUBENotificationTopic topic,GCUBESecurityManager manager) {
			
		try {
			// I create the message to interact with the DISBroker
			SubscribeMessage mess = new SubscribeMessage();
			mess.setEndpointReference(clientEpr);
			mess.setTopic(topic.getTopicQName().toString());
			mess.setPrecondition(topic.getPrecondition());
			mess.setSelection(topic.getSelector());
			new RemoveSubscriptionHandler(mess, this.getScope(),manager).run();
			logger.trace("Trying to unsubscribe from topic" + topic.toString());
		} catch (Exception e) {
			logger.error("General Exception " + e);
		}
	}
	
	/**
	 * This method allows retrieving last notification message for the given topic <br>
	 * this method returns null if not notification messages are present
	 * 
	 * @param notifierEPR EndpointReferenceType
	 * @param topic QName
	 * @param manager GCUBESecurityManager
	 * @return String
	 * @throws Exception generic exception
	 */
	public String getCurrentNotificationMessageFromNotifier (EndpointReferenceType notifierEPR, QName topic,GCUBESecurityManager manager) throws Exception{
		
		org.oasis.wsn.GetCurrentMessageResponse response = null;
		
		try {
			
			TopicExpressionType topicExpression = new TopicExpressionType();
			
			topicExpression.setDialect(WSNConstants.SIMPLE_TOPIC_DIALECT);
			topicExpression.setValue(topic);
			WSBaseNotificationServiceAddressingLocator locator =
				new WSBaseNotificationServiceAddressingLocator();
			NotificationProducer port =
				locator.getNotificationProducerPort(notifierEPR);
			
			org.oasis.wsn.GetCurrentMessage request =
				new org.oasis.wsn.GetCurrentMessage();
			request.setTopic(topicExpression);
			
			response = port.getCurrentMessage(request);
			
		}catch (Exception e ) {
			logger.error("Error trying to get Current Notification message ");
			return null;
		}
		return AnyHelper.toSingleString(response);
	}
	
	/**
	 * Subscribe to the given topic and retries also last notification messages from the existing notifiers
	 * 
	 * @param consumerEPR EndpointReferenceType
	 * @param topic QName
	 * @param manager GCUBESecurityManager
	 * @return String []
	 * @throws Exception generic exception
	 */
	/*
	public String [] getCurrentNotificationMessageFromNotifiers ( EndpointReferenceType consumerEPR,QName topic,GCUBESecurityManager manager) throws Exception{
		
		String [] result  = null;
		
		try {
			EndpointReferenceType [] eprs = subscribeEPRToTopic(consumerEPR,topic,manager);
			TopicExpressionType topicExpression = new TopicExpressionType();	
			topicExpression.setDialect(WSNConstants.SIMPLE_TOPIC_DIALECT);
			topicExpression.setValue(topic);
			
			
			if (eprs.length != 0) {
				
				result = new String[eprs.length];
				
				for  (int i = 0; i < eprs.length; i++)  {
					String resultElement = null;
					if ((resultElement = getCurrentNotificationMessageFromNotifier(eprs[i],topic,manager))!= null)
						result[i] = resultElement;
				}
			} 
		}catch (Exception e ) {
			logger.error("Error trying to get Current Notification message ");
		}
		return result;
	}
	

	/**
	 * @return
	 */
	private   GCUBEScope getScope(){
		 return this.scope;

	}

	
}
