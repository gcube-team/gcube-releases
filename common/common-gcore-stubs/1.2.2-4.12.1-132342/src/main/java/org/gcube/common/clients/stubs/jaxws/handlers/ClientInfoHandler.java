package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * A {@link CallHandler} that sets client identification on outgoing calls.
 * 
 * @author Fabio Simeoni
 *
 */
public class ClientInfoHandler extends AbstractHandler {

	
	/** Name of the scope call header. */
	public static final String CALLER_HEADER_NAME = "caller";
	/** Namespace of scope-related headers */
	public static final String CALLER_NS = "http://gcube-system.org/namespaces/caller";

	public static final QName CALLER_QNAME = new QName(CALLER_NS,CALLER_HEADER_NAME);

	public static final String CALLED_METHOD_HEADER_NAME = "gcube-method";
	/** Namespace of scope-related headers */
	public static final String CALLED_METHOD_NS = "http://gcube-system.org/namespaces/method";

	public static final QName CALLED_METHOD_QNAME = new QName(CALLED_METHOD_NS,CALLED_METHOD_HEADER_NAME);
	
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
		addHeader(header,CALLER_QNAME, target.clientId());
		addHeader(header, CALLED_METHOD_QNAME, CalledMethodProvider.instance.get());
	}
}
