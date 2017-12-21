package org.gcube.data.analysis.statisticalmanager.persistence;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSException;

import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.data.access.queueManager.FactoryConfiguration;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumer;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducer;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;
import org.gcube.data.access.queueManager.model.QueueItem;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.experimentspace.ComputationFactoryResource;
import org.gcube.data.analysis.statisticalmanager.experimentspace.ServiceQueueConsumer;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceQueueManager {

	private static Logger logger = LoggerFactory.getLogger(ServiceQueueManager.class);

	//Map JMS endpoint - configuration
	//SCOPE -> Producer
	private static ConcurrentHashMap<String,ServiceQueueManager> producerMap=new ConcurrentHashMap<String,ServiceQueueManager>();

	//Map endpoint per scope chache
	//SCOPE -> Endpoint
	private static ConcurrentHashMap<String,String> endpointMap=new ConcurrentHashMap<String,String>();


	/**
	 * Returns the producer for that scope, initializing it if not present
	 * @return 
	 * 
	 * @throws MalformedScopeExpressionException
	 * @throws GCUBEScopeNotSupportedException
	 * @throws JMSException
	 * @throws StatisticalManagerException
	 */
	public static synchronized ServiceQueueManager getProducer() throws MalformedScopeExpressionException, GCUBEScopeNotSupportedException, JMSException, StatisticalManagerException{
		String toConsiderScope=ScopeUtils.getCurrentScope();		
		logger.debug("Scope is "+toConsiderScope);
		
		//VRES are not initialized at service init, thus we consider only VOs
		if(toConsiderScope.split("/").length>3){
			toConsiderScope=toConsiderScope.substring(0,toConsiderScope.lastIndexOf("/"));
			logger.debug("Reducing scope to VO : "+toConsiderScope);
		}
		if(!producerMap.containsKey(toConsiderScope))
			producerMap.put(toConsiderScope, new ServiceQueueManager(toConsiderScope));
		return producerMap.get(toConsiderScope);
	}

	public synchronized static void sendItem(RequestItem item) throws JMSException, MalformedScopeExpressionException, GCUBEScopeNotSupportedException, StatisticalManagerException{
		logger.debug("Sending item "+item);
		
		ServiceQueueManager queueManager=getProducer();
		logger.debug("got producer "+queueManager);
		
		logger.debug("Sent item ID : "+queueManager.producer().send(item));				
	}


	private static String endPointByScope() throws MalformedScopeExpressionException, GCUBEScopeNotSupportedException, StatisticalManagerException{		
		String currentScope=ScopeUtils.getCurrentScope();
		if(!endpointMap.containsKey(currentScope)){
			logger.debug("Getting JMS endpoint for new scope "+currentScope);
			//			String infra = (String) GHNContext.getContext().getProperty(GHNContext.INFRASTRUCTURE_NAME, true);
			//			Set<EndpointReferenceType> set = GCUBEScope.getScope("/" + infra).getServiceMap().getEndpoints(GHNContext.MSGBROKER);
			//			String url_broker = ((EndpointReferenceType) set.toArray()[0]).getAddress().toString();

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Category/text() eq '"+
					Configuration.getProperty(Configuration.JMS_SERVICE_ENDPOINT_CATEGORY)+
					"' and $resource/Profile/Name eq '"+
					Configuration.getProperty(Configuration.JMS_SERVICE_ENDPOINT_NAME)+"' ");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			List<ServiceEndpoint> resources = client.submit(query);
			if (resources==null || resources.size()==0)	throw new StatisticalManagerException("No resources found in scope "+currentScope);
			else{
				AccessPoint ap = resources.get(0).profile().accessPoints().iterator().next();
				endpointMap.put(currentScope, ap.address());		
			}
		}
		return endpointMap.get(currentScope);
	}



	// INSTANCE 


	private QueueProducer<QueueItem> producer;
	private QueueConsumer<QueueItem> consumer;
	private String operatingScope;
	private String topicName;
	private FactoryConfiguration queueConfig;
	
	private String endpoint;

	private ServiceQueueManager(String toConsiderScope) throws JMSException, MalformedScopeExpressionException, GCUBEScopeNotSupportedException, StatisticalManagerException {
		operatingScope=toConsiderScope;		
		topicName=Configuration.getProperty(Configuration.JMS_TOPIC)+"_"+operatingScope.replaceAll("/", "_");
		endpoint=endPointByScope();
		logger.debug("Instantiating queue manager under scope "+operatingScope+", found endpoint"+endpoint+".Topic name : "+topicName);
		

		queueConfig = new FactoryConfiguration(Configuration.getProperty(Configuration.JMS_SERVICE_CLASS),
				Configuration.getProperty(Configuration.JMS_SERVICE_NAME), endpoint, null, null);
		queueConfig.setInitialRedeliveryDelay(5000);
		queueConfig.setMaximumRedeliveries(-1);
		queueConfig.setUseExponentialRedelivery(false);				

		logger.debug("Configuration is "+queueConfig);


		QueueConsumerFactory consumerFactory = QueueConsumerFactory.get(queueConfig);
		ServiceQueueConsumer handler= new ServiceQueueConsumer(topicName,consumerFactory);
		consumer=consumerFactory.register(topicName, QueueType.REQUEST, handler);
		logger.debug("Consumer registered");

		initProducer();

	}

	/**
	 * @return the consumer
	 */
	public QueueConsumer<QueueItem> consumer() {
		return consumer;
	}

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	public QueueProducer<QueueItem> producer() throws JMSException{
		if(!producer.isActive()) {
			try{
				producer.close();
			}catch(Exception e){
				logger.warn("Unabel to close JMS producer ",e);
			}			
			initProducer();
		}
		return producer;
	}
	
	private void initProducer() throws JMSException{
		QueueProducerFactory factory = QueueProducerFactory.get(queueConfig);

		producer=factory.getSubmitter(topicName, QueueType.REQUEST);
	}
	
	public String toString() {
		return "QueueManager for Scope "+operatingScope+". Configuration is :"+queueConfig.toString();
	};
}
