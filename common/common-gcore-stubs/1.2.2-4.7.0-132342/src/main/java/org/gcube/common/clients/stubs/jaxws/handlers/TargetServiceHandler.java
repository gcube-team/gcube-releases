package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * A {@link CallHandler} that sets the coordinates of the target service on outgoing calls. 
 * 
 * @author Fabio Simeoni
 *
 */
public class TargetServiceHandler extends AbstractHandler {

	/** Namespace of scope-related headers */
	private static final String SCOPE_NS = "http://gcube-system.org/namespaces/scope";
	
	/** Name of the service class call header. */
	private static final String SERVICECLASS_HEADER_NAME = "serviceClass";
	private static final QName SERVICECLASS_QNAME = new QName(SCOPE_NS,SERVICECLASS_HEADER_NAME);
	
	/** Name of the service name call header. */
	private static final String SERVICENAME_HEADER_NAME = "serviceName";
	private static final QName SERVICENAME_QNAME = new QName(SCOPE_NS,SERVICENAME_HEADER_NAME);
	
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
		addHeader(header,SERVICECLASS_QNAME, target.gcubeClass());
		addHeader(header,SERVICENAME_QNAME, target.gcubeName());
	}
}
