package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * Handles outgoing calls and their responses.
 * 
 * @author Fabio Simeoni
 *
 */
public interface CallHandler {

	/**
	 * Handles an outgoing call.
	 * @param target information about the  target service
	 * @param header the SOAP header of the call
	 * @param context the JAX-WS call context
	 * @throws Exception if the call cannot be handled
	 */
	void handleRequest(GCoreService<?> target,SOAPHeader header,SOAPMessageContext context) throws Exception;
	
	/**
	 * Handles the response to a call.
	 * @param target information about the target service
	 * @param context the JAX-WS call context
	 * @throws Exception if the response cannot be handled
	 */
	void handleResponse(GCoreService<?> target,SOAPMessageContext context) throws Exception;
}
