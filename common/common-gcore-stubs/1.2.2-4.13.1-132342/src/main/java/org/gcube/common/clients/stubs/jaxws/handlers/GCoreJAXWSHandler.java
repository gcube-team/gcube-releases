package org.gcube.common.clients.stubs.jaxws.handlers;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * A {@link SOAPHandler} that adds gCube headers to outgoing calls.
 * 
 * @author Fabio Simeoni
 * 
 */
public class GCoreJAXWSHandler implements SOAPHandler<SOAPMessageContext> {

	private final GCoreService<?> target;

	public GCoreJAXWSHandler(GCoreService<?> target) {
		this.target = target;
	}

	public boolean handleMessage(SOAPMessageContext context) {

		Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound)
			try {

				SOAPHeader header = context.getMessage().getSOAPPart().getEnvelope().getHeader();

				if (header == null)
					header = context.getMessage().getSOAPPart().getEnvelope().addHeader();

				for (CallHandler handler : HandlerRegistry.handlers())
					handler.handleRequest(target, header, context);

			} catch (Exception e) {
				throw new RuntimeException("cannot configure outgoing message", e);
			}
		else
			try {
				
				for (CallHandler handler : HandlerRegistry.handlers())
					handler.handleResponse(target, context);
				
			} catch (Exception e) {
				throw new RuntimeException("cannot configure outgoing message", e);
			}

		return true;
	};

	public Set<QName> getHeaders() {
		return null;
	}

	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	public void close(MessageContext context) {
	}

}
