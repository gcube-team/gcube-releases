package org.gcube.datatransfer.common.messaging;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.messaging.endpoints.BrokerNotConfiguredInScopeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 * @param <LISTENER>
 */
public abstract class BrokerSubscription<LISTENER extends MessageListener> extends Thread implements ExceptionListener {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected String subscriberEndpoint;
	protected DestinationPair pair;
	protected ArrayList<String> messageSelectors = new ArrayList<String>();
	protected LISTENER listener;
	
	protected ArrayList<Connection> connections = new ArrayList<Connection>();
	protected static boolean transacted = false;
	protected static int ackMode = Session.AUTO_ACKNOWLEDGE;

	/**
	 * set the  scope 
	 * 
	 * @param scope the scope to set
	 */
	public abstract void setScope(GCUBEScope scope);


	/**
	 * subscribe
	 * @throws Exception
	 */
	public void subscribe() throws Exception{
		
		if (this.getDestinationPair().isQueue()){
			setupQueueSubscription();
		}
	}

	/**
	 * Get the list of connections
	 * @return the list of connections
	 */
	public ArrayList<Connection> getConnections() {
		return connections;
	}

	/**
	 * Set the list of connections
	 * @param connections the list of connections
	 */
	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	} 

	

	/**
	 * setupQueueSubscription
	 * @throws Exception
	 */
	public void setupQueueSubscription() throws Exception{


		if (ConnectionsManager.getBrokerRetriever(this.getDestinationPair().getScope()) != null)
		{
			for (String address: ConnectionsManager.getBrokerRetriever(this.getDestinationPair().getScope()).getEndpoints())
			{
		
				ActiveMQConnectionFactory connectionFactory = 
						new ActiveMQConnectionFactory(address);

				QueueConnection connection; 
				QueueSession session;

				try {
					connection = ((QueueConnectionFactory)connectionFactory).createQueueConnection();
					connection.setClientID(this.getDestinationPair().getDestinationName());
					connection.start();
					connection.setExceptionListener(this);

					session = connection.createQueueSession(transacted, ackMode);
					Queue queue = session.createQueue(this.getDestinationPair().getDestinationName());
					QueueReceiver consumer = null;

					consumer = session.createReceiver(queue);

					consumer.setMessageListener(listener);
					connections.add(connection);

				} catch (JMSException e) {
					logger.error("Error creating Queue Receiver",e);
					throw e;
				}
				catch (Exception e) {
					logger.error("Error creating Queue Receiver",e);
					throw e;
				}
			}
		} else logger.warn("Impossible to setup Queue Receiver, Broker addrress not specified for the scope: "+this.getDestinationPair().getScope().toString());

		logger.info("Started Queue receveiver for topic: "+this.getDestinationPair().getDestinationName());

	}


	@Override
	public void run() {

		try {
			//reset previous connections;	
			for (Connection connection: connections){
				connection.stop();
				connection.close();	
			}
			connections.clear();
		}catch (JMSException e) {
			logger.debug("Exception stopping the connection",e);
			connections.clear();
		}

		while (true){
			try {
				this.subscribe();
				return;
			}catch (InvalidClientIDException ex) {
				logger.error("Subscription has not been reset",ex);
				return;

			}
			catch (Exception e) {
				logger.error("Error on subscription",e);
				try {
					Thread.sleep(6000*2);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}


	/**
	 * {@inheritDoc}
	 */
	public void onException(JMSException exce) {

		logger.error(exce.getMessage());
		logger.error(exce.toString());
		this.run();
	}
	/**
	 * default constructor
	 */
	public BrokerSubscription(String subscriberEndpoint){
		this.subscriberEndpoint=subscriberEndpoint;
	}


	/**
	 * get the DestinationPair info
	 * @return the DestinationPair info
	 */
	public DestinationPair getDestinationPair() {
		return pair;
	}

	/**
	 * set the DestinationPair info
	 * @param pair the DestinationPair info
	 */
	public void setDestinationPair(DestinationPair pair) {
		this.pair = pair;
	}

	/**
	 * Get the listener associated to the subscription
	 * @return the listener
	 */
	public LISTENER getListener() {
		return listener;
	}

	/**
	 * set the listener associated to the connection
	 * @param listener the listener
	 */
	public void setListener(LISTENER listener) {
		this.listener = listener;
	}

	/**
	 * Get the message Selectors for this subscription
	 * @return the message selectors
	 */
	public ArrayList<String> getMessageSelectors() {
		return messageSelectors;
	}

	/**
	 * Set the message selectors for this subscription
	 * @param messageSelectors the message selector
	 */
	public void setMessageSelectors(ArrayList<String> messageSelectors) {
		this.messageSelectors = messageSelectors;
	}

	/**
	 * destination pair
	 * @author Andrea Manzi(CERN)
	 *
	 */
	public class  DestinationPair {
		GCUBEScope scope;
		String destinationName;
		boolean queue = false;

		/**
		 * get the scope
		 * @return the scope
		 */
		public GCUBEScope getScope() {
			return scope;
		}
		/**
		 * set the scope
		 * @param scope the scope
		 * @throws GCUBEScopeNotSupportedException 
		 * @throws BrokerNotConfiguredInScopeException 
		 */
		public void setScope(GCUBEScope scope)  {
			try {
				ConnectionsManager.addScope(scope);
			} catch (BrokerNotConfiguredInScopeException e) {
				e.printStackTrace();
			} catch (GCUBEScopeNotSupportedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.scope = scope;
		}
		/**
		 * get the destinationName name
		 * @return the destinationName name
		 */
		public String getDestinationName() {
			return destinationName;
		}
		/**
		 * set the destinationName name
		 * 
		 * @param destinationName the destinationName name
		 */
		public void setDestinationName(String destinationName) {
			this.destinationName = destinationName;
		}
		
		public boolean isQueue() {
			return queue;
		}


		public void setQueue(boolean queue) {
			this.queue = queue;
		}

	}
	
}