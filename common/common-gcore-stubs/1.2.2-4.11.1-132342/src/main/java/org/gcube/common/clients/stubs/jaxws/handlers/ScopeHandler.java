package org.gcube.common.clients.stubs.jaxws.handlers;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * A {@link CallHandler} that sets the current scope on outgoing calls.
 * @author Fabio Simeoni
 *
 */
public class ScopeHandler extends AbstractHandler {

	/** Namespace of scope-related headers */
	public static final String SCOPE_NS = "http://gcube-system.org/namespaces/scope";
	
	/** Name of the scope call header. */
	public static final String SCOPE_HEADER_NAME = "scope";
	public static final QName SCOPE_QNAME = new QName(SCOPE_NS,SCOPE_HEADER_NAME);
	
	@Override
	public void handleRequest(GCoreService<?> target, SOAPHeader header, SOAPMessageContext context) throws Exception {
		String scope = ScopeProvider.instance.get();
		addHeader(header,SCOPE_QNAME, scope);
	}
}
