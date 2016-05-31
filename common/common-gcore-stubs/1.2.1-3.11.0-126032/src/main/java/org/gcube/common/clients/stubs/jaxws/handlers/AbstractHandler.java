package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * Adapter implementation of {@link CallHandler} for selective implementations (handle only outgoing calls, or only responses).
 * 
 * @author Fabio Simeoni
 *
 */
public class AbstractHandler implements CallHandler {

	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
	}

	@Override
	public void handleResponse(GCoreService<?> target, SOAPMessageContext context) throws Exception {
	}

	/**
	 * Helper to set an element on the SOAP header of the outgoing call
	 * @param header the SOAP header
	 * @param name the name the element's name
	 * @param value the element's value
	 * @throws SOAPException if the element cannot be added to the header 
	 */
	protected final void addHeader(SOAPHeader header,QName name, String value) throws SOAPException {
		header.addHeaderElement(name).addTextNode(value);
	}
}
