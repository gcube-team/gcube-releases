package org.gcube.datatransfer.common.messaging;

import java.io.IOException;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.datatransfer.common.messaging.messages.TransferMessage;
import org.gcube.datatransfer.common.messaging.messages.TransferRequestMessage;
import org.gcube.datatransfer.common.messaging.messages.TransferResponseMessage;
import org.gcube.datatransfer.common.messaging.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/** 		
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class MSGClient {

	public Logger logger= LoggerFactory.getLogger(this.getClass());

	protected MSGClient() throws IOException, GCUBEScopeNotSupportedException{
	}

	/**
	 * The method create Messages and send them to the D4science MessageBroker configured in the Infrastructure.
	 *
	 * This method is intended to be executed by Service running on a GHN container.
	 * 
	 * The information about GCUBEScope, ServiceClass and ServiceName are automatically retrieved from the 
	 * ServiceContext. 
	 * 
	 * By default a message for each service scope is generated, otherwise is it possible to specify the GCUBEScope as optional parameter. 
	 * 
	 * 
	 * @param context the Service Context 
	 * @param message the map of parameters to send
	 * @param scope, if the service is running in more than one scope and we want to specify the caller scope, is it possible to use this optional parameters
	 * @throws GHNClientModeException the Exception is thrown when the method is invoked from a client
	 * @throws ReservedFieldException if one or more parameters clash the ones already reserved by the system
	 * @throws IllegalArgumentException if one or more parameters type are not supported by the system
	 * @throws Exception 
	 */
	public void sendRequestMessage(GCUBEServiceContext context,TransferRequestMessage message,
			GCUBEScope ...scope) 	throws GHNClientModeException  ,IllegalArgumentException, Exception {

		try {
			message.setTimeNow();
			if (scope.length > 0) {
				message.createTopicName(scope[0]);
				message.setScope(scope[0].toString());	
				this.sendMessage(message);

			}
			else 
			{//a message  for each scope is sent

				for (GCUBEScope sco : context.getInstance().getScopes().values()){
					message.createTopicName(sco);
					message.setScope(sco.toString());	
					this.sendMessage(message);
				}
			}

		}catch (Exception e){
			logger.error("Error Sending Transfer message",e);
			e.printStackTrace();
			throw e;
		}

	}

	public void sendResponseMessage(GCUBEServiceContext context,TransferResponseMessage message,
			GCUBEScope ...scope) 	throws GHNClientModeException  ,IllegalArgumentException, Exception {

		try {
			message.setTimeNow();
			if (scope.length > 0) {
				message.createTopicName(scope[0]);
				message.setScope(scope[0].toString());	
				this.sendMessage(message);

			}
			else 
			{//a message  for each scope is sent

				for (GCUBEScope sco : context.getInstance().getScopes().values()){

					message.createTopicName(sco);
					message.setScope(sco.toString());	
					this.sendMessage(message);
				}
			}

		}catch (Exception e){
			logger.error("Error Sending Transfer message",e);
			e.printStackTrace();
			throw e;
		}

	}

	public void sendMessage(GCUBEServiceContext context,TransferMessage message,
			GCUBEScope ...scope) 	throws GHNClientModeException  ,IllegalArgumentException, Exception {

		try {
			message.setTimeNow();
			if (scope.length > 0) {
				message.createTopicName(scope[0]);
				message.setScope(scope[0].toString());	
				this.sendMessage(message);

			}
			else 
			{//a message  for each scope is sent

				for (GCUBEScope sco : context.getInstance().getScopes().values()){

					message.createTopicName(sco);
					message.setScope(sco.toString());	
					this.sendMessage(message);
				}
			}

		}catch (Exception e){
			logger.error("Error Sending Transfer message",e);
			e.printStackTrace();
			throw e;
		}

	}



	/**
	 * {@inheritDoc}
	 */
	private void sendMessage(GCUBEMessage message) {
		String source=getSource(message);	
		String destination=getDestination(message);	
		logger.debug("MSGClient is going to send a message from "+source +" to "+destination);
		Producer.getSingleton().sendMessageToQueue(message);
	}




	public class GHNClientModeException extends Exception {

		public GHNClientModeException(String string) {
			super (string);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2107643850291448530L;
	};

	public String getSource(GCUBEMessage message){
		String source="";
		try{
			if (message instanceof TransferRequestMessage){
				TransferRequestMessage req_msg = (TransferRequestMessage ) message;
				source=req_msg.getSourceEndpoint();
			}
			else if (message instanceof TransferResponseMessage){
				TransferResponseMessage res_msg = (TransferResponseMessage ) message;
				source=res_msg.getSourceEndpoint();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return source;
	}
	public String getDestination(GCUBEMessage message){
		String destination="";
		try{
			if (message instanceof TransferRequestMessage){
				TransferRequestMessage req_msg = (TransferRequestMessage ) message;
				destination=req_msg.getDestEndpoint();
			}
			else if (message instanceof TransferResponseMessage){
				TransferResponseMessage res_msg = (TransferResponseMessage ) message;
				destination=res_msg.getDestEndpoint();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return destination;
	}
}
