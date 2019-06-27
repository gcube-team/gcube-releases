package org.gcube.datatransfer.common.messaging;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.messaging.endpoints.BrokerEndpoints;
import org.gcube.common.messaging.endpoints.BrokerNotConfiguredInScopeException;
import org.gcube.common.messaging.endpoints.ScheduledRetriever;
import org.gcube.common.scope.api.ScopeProvider;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class ConnectionsManager {

	private static long refreshTime = 1800;

	private static long waitingTime = 60;



	static Logger logger = LoggerFactory.getLogger(ConnectionsManager.class);


	/**ActiveMQ Connection factory**/
	private static HashMap<GCUBEScope,ActiveMQConnectionFactory> connectionFactoryMap = null;

	private static HashMap<GCUBEScope,QueueConnection> queueConnectionMap = null;

	static {

		connectionFactoryMap = new HashMap<GCUBEScope,ActiveMQConnectionFactory>();

		queueConnectionMap = new HashMap<GCUBEScope, QueueConnection>();

	}


	public void init(long waitingTime, long refreshTime)
	{
		ConnectionsManager.refreshTime= refreshTime;
		ConnectionsManager.waitingTime = waitingTime;

	}


	/**
	 * Get the QueueConnection list for the given scope
	 * @return the queue Connection list for the given scope
	 */
	public static QueueConnection getQueueConnection(GCUBEScope scope){
		QueueConnection connection = queueConnectionMap.get(scope);
		if( connection == null ) {
			logger.debug("CONNECTION MAP NULL");
		}
		return connection;
	}

	/**
	 * Add the Scope to the monitoredMap
	 * @return the scope to add
	 * @throws org.gcube.common.messaging.endpoints.BrokerNotConfiguredInScopeException 
	 * @throws Exception 
	 */
	public synchronized static void addScope (GCUBEScope scope) throws BrokerNotConfiguredInScopeException, Exception{

		ScopeProvider.instance.set(scope.toString());
		BrokerEndpoints.getRetriever(waitingTime, refreshTime);
		reloadConnection(scope);

	}

	/**
	 * 
	 * @return 
	 * @throws Exception 
	 * @throws BrokerNotConfiguredInScopeException 
	 */
	public  static ScheduledRetriever getBrokerRetriever (GCUBEScope scope) throws BrokerNotConfiguredInScopeException, Exception 
	{
		ScopeProvider.instance.set(scope.toString());
		return BrokerEndpoints.getRetriever(waitingTime, refreshTime);
	}



	/**
	 * Reload the scope connection
	 * @param scope the scope to reload
	 */
	public synchronized static void reloadConnection(GCUBEScope scope) {

		//close current connections:
		stopConnections(scope);
		ActiveMQConnectionFactory factory =null;
		
		QueueConnection queueConnection = null;

		logger.debug("Reload JMS connections");

		try {
			logger.info("MSG-Broker failover endpoint found: "+getBrokerRetriever(scope).getFailoverEndpoint()+" for scope: "+scope.toString());

			factory = new ActiveMQConnectionFactory(getBrokerRetriever(scope).getFailoverEndpoint());


			queueConnection = ((QueueConnectionFactory)factory).createQueueConnection();
			queueConnection.start();

		} catch (JMSException e1) {
			logger.error("Error creating Topic Connection",e1);
		} catch (Exception e) {
			logger.error("Error creating Topic Connection",e);
		}

		queueConnectionMap.put(scope,queueConnection);
		connectionFactoryMap.put(scope, factory);

	}



	/**
	 * Check if the given scope belongs to start scopes
	 * @param scope the scope to check
	 * @return true/false
	 */
	public static boolean checkStartScope(GCUBEScope scope) {
		boolean isStartScope = false;
		for (GCUBEScope scopeStart:GHNContext.getContext().getStartScopes())
		{
			if (scope.equals(scopeStart) || scope.isInfrastructure())
				isStartScope = true;
		}
		return isStartScope;
	}


	private static void stopConnections(GCUBEScope scope){


		if (queueConnectionMap != null  && queueConnectionMap.get(scope) != null) {
			//close current connections:
			QueueConnection con =queueConnectionMap.get(scope);
			try {
				con.stop();
				con.close();
			}
			catch (JMSException e ){
				logger.error("Error stopping queueConnections",e);
			}
		}

	}


}

