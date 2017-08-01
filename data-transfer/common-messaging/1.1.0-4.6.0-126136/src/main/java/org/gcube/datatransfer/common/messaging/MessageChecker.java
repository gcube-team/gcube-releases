package org.gcube.datatransfer.common.messaging;


import org.gcube.common.core.monitoring.GCUBEMessage;
import org.gcube.common.core.scope.GCUBEScope;

/**
 *  
 * @author Andrea Manzi(CERN) 
 *
 * @param <MESSAGE>
 */
public abstract class MessageChecker<MESSAGE extends GCUBEMessage>   {

	protected GCUBEScope scope = null;
	protected String subscriberEndpoint=null;
	
	/**
	 * create a messageChecker for the given scope
	 * @param scope the scope
	 */
	public MessageChecker(GCUBEScope scope,String subscriberEndpoint ){
		this.scope = scope;
		this.subscriberEndpoint=subscriberEndpoint;
	}
	
	/**
	 * Check the incoming message
	 * @param msg the message
	 * @throws Exception exception
	 */
	public abstract void check(MESSAGE msg) throws Exception ;
}